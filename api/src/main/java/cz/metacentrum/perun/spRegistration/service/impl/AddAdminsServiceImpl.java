package cz.metacentrum.perun.spRegistration.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.metacentrum.perun.spRegistration.Utils;
import cz.metacentrum.perun.spRegistration.common.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.common.exceptions.CodeNotStoredException;
import cz.metacentrum.perun.spRegistration.common.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.common.exceptions.ExpiredCodeException;
import cz.metacentrum.perun.spRegistration.common.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.common.exceptions.MalformedCodeException;
import cz.metacentrum.perun.spRegistration.common.exceptions.UnauthorizedActionException;
import cz.metacentrum.perun.spRegistration.common.models.Facility;
import cz.metacentrum.perun.spRegistration.common.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.common.models.User;
import cz.metacentrum.perun.spRegistration.persistence.connectors.PerunConnector;
import cz.metacentrum.perun.spRegistration.persistence.managers.LinkCodeManager;
import cz.metacentrum.perun.spRegistration.service.AddAdminsService;
import cz.metacentrum.perun.spRegistration.service.MailsService;
import cz.metacentrum.perun.spRegistration.service.ServiceUtils;
import cz.metacentrum.perun.spRegistration.service.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("addAdminsService")
public class AddAdminsServiceImpl implements AddAdminsService {

    private static final Logger log = LoggerFactory.getLogger(AddAdminsServiceImpl.class);

    private static final String FACILITY_ID_KEY = "facilityId";
    private static final String CREATED_AT_KEY = "createdAt";
    private static final String REQUESTED_MAIL_KEY = "requestedMail";

    private final PerunConnector perunConnector;
    private final AppConfig appConfig;
    private final MailsService mailsService;
    private final LinkCodeManager linkCodeManager;
    private final UtilsService utilsService;

    @Autowired
    public AddAdminsServiceImpl(PerunConnector perunConnector, AppConfig appConfig, MailsService mailsService,
                                LinkCodeManager linkCodeManager, UtilsService utilsService) {
        this.perunConnector = perunConnector;
        this.appConfig = appConfig;
        this.mailsService = mailsService;
        this.linkCodeManager = linkCodeManager;
        this.utilsService = utilsService;
    }

    @Override
    public boolean addAdminsNotify(User user, Long facilityId, List<String> admins)
            throws UnauthorizedActionException, ConnectorException, BadPaddingException, InvalidKeyException,
            IllegalBlockSizeException, UnsupportedEncodingException, InternalErrorException
    {
        log.trace("addAdminsNotify(user: {}, facilityId: {}, admins: {}", user, facilityId, admins);

        if (Utils.checkParamsInvalid(user, facilityId, admins) || admins.isEmpty()) {
            log.error("Wrong parameters passed (user: {}, facilityId: {}, admins: {})", user, facilityId, admins);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        } else if (! utilsService.isFacilityAdmin(facilityId, user.getId())) {
            log.error("User cannot request adding admins to facility, user is not an admin");
            throw new UnauthorizedActionException("User cannot request adding admins to facility, user is not an admin");
        }

        Facility facility = perunConnector.getFacilityById(facilityId);
        if (facility == null) {
            log.error("Could not fetch facility for id: {}", facilityId);
            throw new InternalErrorException("Could not find facility for id: " + facilityId);
        }

        Map<String, PerunAttribute> attrs = perunConnector.getFacilityAttributes(facility.getId(),
                Arrays.asList(appConfig.getServiceNameAttributeName(), appConfig.getServiceDescAttributeName()));

        facility.setName(attrs.get(appConfig.getServiceNameAttributeName()).valueAsMap());
        facility.setDescription(attrs.get(appConfig.getServiceDescAttributeName()).valueAsMap());

        Map<String, String> adminCodeMap = generateCodesForAdmins(admins, facilityId);
        Map<String, String> adminLinkMap = generateLinksForAdmins(adminCodeMap);
        boolean successful = mailsService.notifyNewAdmins(facility, adminLinkMap, user);

        log.debug("addAdminsNotify returns: {}", successful);
        return successful;
    }

    @Override
    public boolean confirmAddAdmin(User user, String code)
            throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, MalformedCodeException,
            ExpiredCodeException, ConnectorException, InternalErrorException, CodeNotStoredException {
        log.debug("confirmAddAdmin({})", code);

        if (Utils.checkParamsInvalid(user, code)) {
            log.error("Wrong parameters passed: (user: {}, code: {})", user, code);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        JsonNode decrypted = decryptAddAdminCode(code);
        boolean isValid = (!utilsService.isExpiredCode(decrypted) && utilsService.validateCode(code));

        if (! isValid) {
            log.error("User trying to become admin with invalid code: {}", decrypted);
            throw new ExpiredCodeException("Code is invalid");
        }

        Long facilityId = decrypted.get(FACILITY_ID_KEY).asLong();
        boolean added = perunConnector.addFacilityAdmin(facilityId, user.getId());
        boolean deletedCode = linkCodeManager.deleteUsedCode(code);
        boolean successful = (added && deletedCode);

        if (!successful) {
            log.error("some operations failed: added: {}, deletedCode: {}", added, deletedCode);
        } else {
            log.info("Admin added, code deleted");
        }

        log.debug("confirmAddAdmin returns: {}", successful);
        return added;
    }

    @Override
    public boolean rejectAddAdmin(User user, String code)
            throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, MalformedCodeException,
            ExpiredCodeException, InternalErrorException, CodeNotStoredException {
        log.debug("rejectAddAdmin(user: {}, code: {})", user, code);

        if (Utils.checkParamsInvalid(user, code)) {
            log.error("Wrong parameters passed: (user: {}, code: {})", user, code);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        JsonNode decrypted = decryptAddAdminCode(code);
        boolean isValid = (!utilsService.isExpiredCode(decrypted) && utilsService.validateCode(code));

        if (! isValid) {
            log.error("User trying to become reject becoming with invalid code: {}", decrypted);
            throw new ExpiredCodeException("Code is invalid");
        }

        boolean deletedCode = linkCodeManager.deleteUsedCode(code);

        log.debug("rejectAddAdmin() returns: {}", deletedCode);
        return deletedCode;
    }


    private JsonNode decryptAddAdminCode(String code)
            throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, MalformedCodeException
    {
        log.trace("decryptAddAdminCode({})", code);

        if (Utils.checkParamsInvalid(code)) {
            log.error("Wrong parameters passed: (code: {})", code);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        String decrypted = ServiceUtils.decrypt(code, appConfig.getSecret());

        try {
            JsonNode decryptedAsJson = new ObjectMapper().readTree(decrypted);
            log.trace("decryptAddAdminCode() returns: {}", decryptedAsJson);
            return decryptedAsJson;
        } catch (JsonProcessingException e) {
            throw new MalformedCodeException();
        }
    }

    private String createAddAdminCode(Long facilityId, String requestedMail)
            throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException
    {
        log.trace("createRequestCode(facilityId: {}, requestedMail: {})", facilityId, requestedMail);

        if (Utils.checkParamsInvalid(facilityId, requestedMail)) {
            log.error("Wrong parameters passed: (facilityId: {}, requestedMail: {})", facilityId, requestedMail);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        ObjectNode object = JsonNodeFactory.instance.objectNode();
        object.put(FACILITY_ID_KEY, facilityId);
        object.put(CREATED_AT_KEY, LocalDateTime.now().toString());
        object.put(REQUESTED_MAIL_KEY, requestedMail);

        String strToEncrypt = object.toString();
        String encoded = ServiceUtils.encrypt(strToEncrypt, appConfig.getSecret());

        log.trace("createRequestCode() returns: {}", encoded);
        return encoded;
    }

    private Map<String, String> generateCodesForAdmins(List<String> admins, Long facilityId)
            throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, InternalErrorException
    {
        log.trace("generateCodesForAdmins(facilityId: {}, admins: {})", facilityId, admins);

        if (Utils.checkParamsInvalid(admins, facilityId)) {
            log.error("Wrong parameters passed: (admins: {}, facilityId: {})", admins, facilityId);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        List<String> codes = new ArrayList<>();
        Map<String, String> adminCodesMap = new HashMap<>();

        for (String admin : admins) {
            String code = createAddAdminCode(facilityId, admin);
            codes.add(code);
            adminCodesMap.put(admin, code);
        }

        linkCodeManager.storeCodes(codes);

        log.trace("generateCodesForAdmins() returns: {}", adminCodesMap);
        return adminCodesMap;
    }

    private Map<String, String> generateLinksForAdmins(Map<String, String> adminCodeMap)
            throws UnsupportedEncodingException
    {
        log.trace("generateLinksForAdmins(adminCodeMap: {})", adminCodeMap);

        if (Utils.checkParamsInvalid(adminCodeMap)) {
            log.error("Wrong parameters passed: (adminCodeMap: {})", adminCodeMap);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        Map<String, String> linksMap = new HashMap<>();

        for (Map.Entry<String, String> entry : adminCodeMap.entrySet()) {
            String code = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
            String link = appConfig.getAdminsEndpoint()
                    .concat("?code=").concat(code);
            linksMap.put(entry.getKey(), link);
            log.debug("Generated code: {}", code); //TODO: remove
        }

        log.trace("generateLinksForAdmins() returns: {}", linksMap);
        return linksMap;
    }
}
