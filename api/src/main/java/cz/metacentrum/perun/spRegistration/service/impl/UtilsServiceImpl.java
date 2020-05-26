package cz.metacentrum.perun.spRegistration.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.metacentrum.perun.spRegistration.Utils;
import cz.metacentrum.perun.spRegistration.common.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.common.exceptions.CodeNotStoredException;
import cz.metacentrum.perun.spRegistration.common.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.common.exceptions.ExpiredCodeException;
import cz.metacentrum.perun.spRegistration.common.exceptions.MalformedCodeException;
import cz.metacentrum.perun.spRegistration.common.exceptions.UnauthorizedActionException;
import cz.metacentrum.perun.spRegistration.common.models.Facility;
import cz.metacentrum.perun.spRegistration.common.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.connectors.PerunConnector;
import cz.metacentrum.perun.spRegistration.persistence.managers.LinkCodeManager;
import cz.metacentrum.perun.spRegistration.service.MailsService;
import cz.metacentrum.perun.spRegistration.service.ServiceUtils;
import cz.metacentrum.perun.spRegistration.service.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@Service("utilsService")
public class UtilsServiceImpl implements UtilsService {

    private static final Logger log = LoggerFactory.getLogger(UtilsServiceImpl.class);

    private static final String CREATED_AT_KEY = "createdAt";

    private final LinkCodeManager linkCodeManager;
    private final PerunConnector perunConnector;
    private final AppConfig appConfig;
    private final MailsService mailsService;

    @Autowired
    public UtilsServiceImpl(LinkCodeManager linkCodeManager, PerunConnector perunConnector, AppConfig appConfig, MailsServiceImpl mailsService) {
        this.linkCodeManager = linkCodeManager;
        this.perunConnector = perunConnector;
        this.appConfig = appConfig;
        this.mailsService = mailsService;
    }

    @Override
    public boolean validateCode(String code) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
            MalformedCodeException, ExpiredCodeException, CodeNotStoredException {
        log.trace("validateCode({})", code);

        if (Utils.checkParamsInvalid(code)) {
            log.error("Wrong parameters passed: (code: {})", code);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        } else if (isExpiredCode(decryptRequestCode(code))) {
            throw new ExpiredCodeException("Code expired");
        } else if (!linkCodeManager.validateCode(code)) {
            throw new CodeNotStoredException("Code not found");
        }
        boolean isValid = (!isExpiredCode(decryptRequestCode(code)) && linkCodeManager.validateCode(code));

        log.trace("validateCode() returns: {}", isValid);
        return isValid;
    }

    @Override
    public PerunAttribute regenerateClientSecret(Long userId, Long facilityId) throws UnauthorizedActionException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, ConnectorException {
        log.trace("regenerateClientSecret({}, {})", userId, facilityId);

        if (Utils.checkParamsInvalid(userId, facilityId)) {
            log.error("Wrong parameters passed: (userId: {}, facilityId: {})", userId, facilityId);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        } else if (!appConfig.isAppAdmin(userId) && !isFacilityAdmin(facilityId, userId)) {
            log.error("User is not authorized to regenerate client secret");
            throw new UnauthorizedActionException("User is not authorized to regenerate client secret");
        }

        PerunAttribute clientSecret = generateClientSecretAttribute();
        perunConnector.setFacilityAttribute(facilityId, clientSecret.toJson());

        String decrypted = ServiceUtils.decrypt(clientSecret.valueAsString(), appConfig.getSecret());
        clientSecret.setValue(decrypted);

        Facility facility = perunConnector.getFacilityById(facilityId);
        Map<String, PerunAttribute> attrs = perunConnector.getFacilityAttributes(facilityId, Arrays.asList(
                appConfig.getServiceNameAttributeName(), appConfig.getServiceDescAttributeName()));

        facility.setName(attrs.get(appConfig.getServiceNameAttributeName()).valueAsMap());
        facility.setDescription(attrs.get(appConfig.getServiceDescAttributeName()).valueAsMap());

        mailsService.notifyClientSecretChanged(facility);

        log.trace("regenerateClientSecret({}, {}) returns: {}", userId, facilityId, clientSecret);
        return clientSecret;
    }

    @Override
    public boolean isFacilityAdmin(Long facilityId, Long userId) throws ConnectorException {
        log.trace("isFacilityAdmin(facilityId: {}, userId: {})", facilityId, userId);

        if (Utils.checkParamsInvalid(facilityId, userId)) {
            log.error("Wrong parameters passed: (facility: {}, userId: {})", facilityId, userId);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        Set<Long> whereAdmin = perunConnector.getFacilityIdsWhereUserIsAdmin(userId);

        if (whereAdmin == null || whereAdmin.isEmpty()) {
            log.debug("isFacilityAdmin returns: {}", false);
            return false;
        }

        boolean result = whereAdmin.contains(facilityId);
        log.debug("isFacilityAdmin returns:  {}", result);
        return result;
    }

    @Override
    public PerunAttribute generateClientSecretAttribute() throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        log.trace("generateClientIdAttribute()");

        PerunAttribute attribute = new PerunAttribute();
        attribute.setDefinition(appConfig.getAttrDefinition(appConfig.getClientSecretAttribute()));

        String clientSecret = ServiceUtils.generateClientSecret();
        String encryptedClientSecret = ServiceUtils.encrypt(clientSecret, appConfig.getSecret());

        attribute.setValue(encryptedClientSecret);

        log.trace("generateClientIdAttribute() returns: {}", attribute);
        return attribute;
    }

    @Override
    public boolean isExpiredCode(JsonNode codeInJson) {
        log.trace("isExpiredCode({})", codeInJson);

        if (Utils.checkParamsInvalid(codeInJson)) {
            log.error("Wrong parameters passed: (codeInJson: {})", codeInJson);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        long daysValidPeriod = appConfig.getConfirmationPeriodDays();
        long hoursValidPeriod = appConfig.getConfirmationPeriodHours();

        LocalDateTime createdAt = LocalDateTime.parse(codeInJson.get(CREATED_AT_KEY).textValue());
        LocalDateTime validUntil = createdAt.plusDays(daysValidPeriod).plusHours(hoursValidPeriod);

        boolean isExpired = LocalDateTime.now().isAfter(validUntil);

        log.trace("isExpiredCode() returns: {}", isExpired);
        return isExpired;
    }

    @Override
    public boolean isAdminInRequest(Long reqUserId, Long userId) {
        log.debug("isAdminInRequest(reqUserId: {}, userId: {})", reqUserId, userId);

        if (Utils.checkParamsInvalid(reqUserId, userId)) {
            log.error("Wrong parameters passed: (reqUserId: {}, userId: {})", reqUserId, userId);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        boolean res = reqUserId.equals(userId) || appConfig.isAppAdmin(userId);

        log.debug("isAdminInRequest returns: {}", res);
        return res;
    }

    @Override
    public JsonNode decryptRequestCode(String code) throws InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, MalformedCodeException
    {
        log.trace("decryptRequestCode({})", code);

        if (Utils.checkParamsInvalid(code)) {
            log.error("Wrong parameters passed: (code: {})", code);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        String decrypted = ServiceUtils.decrypt(code, appConfig.getSecret());

        try {
            JsonNode decryptedAsJson = new ObjectMapper().readTree(decrypted);

            log.trace("decryptRequestCode() returns: {}", decryptedAsJson);
            return decryptedAsJson;
        } catch (JsonProcessingException e) {
            throw new MalformedCodeException();
        }
    }

}
