package cz.metacentrum.perun.spRegistration.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import cz.metacentrum.perun.spRegistration.Utils;
import cz.metacentrum.perun.spRegistration.common.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestManager;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestSignatureManager;
import cz.metacentrum.perun.spRegistration.common.models.Request;
import cz.metacentrum.perun.spRegistration.common.models.RequestSignature;
import cz.metacentrum.perun.spRegistration.common.models.User;
import cz.metacentrum.perun.spRegistration.service.RequestSignaturesService;
import cz.metacentrum.perun.spRegistration.service.UtilsService;
import cz.metacentrum.perun.spRegistration.common.exceptions.ExpiredCodeException;
import cz.metacentrum.perun.spRegistration.common.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.common.exceptions.MalformedCodeException;
import cz.metacentrum.perun.spRegistration.common.exceptions.UnauthorizedActionException;
import cz.metacentrum.perun.spRegistration.service.mails.MailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.util.List;

import static cz.metacentrum.perun.spRegistration.service.mails.MailsService.REQUEST_SIGNED;

@Service("requestSignaturesService")
public class RequestSignaturesServiceImpl implements RequestSignaturesService {

    private static final Logger log = LoggerFactory.getLogger(RequestSignaturesServiceImpl.class);

    private static final String REQUEST_ID_KEY = "requestId";

    private final RequestSignatureManager requestSignatureManager;
    private final RequestManager requestManager;
    private final MailsService mailsService;
    private final AppConfig appConfig;
    private final UtilsService utilsService;

    @Autowired
    public RequestSignaturesServiceImpl(RequestSignatureManager requestSignatureManager, RequestManager requestManager,
                                        MailsService mailsService, AppConfig appConfig, UtilsService utilsService) {
        this.requestSignatureManager = requestSignatureManager;
        this.requestManager = requestManager;
        this.mailsService = mailsService;
        this.appConfig = appConfig;
        this.utilsService = utilsService;
    }

    @Override
    public boolean addSignature(User user, String code, boolean approved)
            throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, MalformedCodeException,
            ExpiredCodeException, InternalErrorException
    {
        log.trace("signTransferToProduction(user: {}, code: {})", user, code);

        if (Utils.checkParamsInvalid(user, code)) {
            log.error("Wrong parameters passed: (user: {}, code: {})", user, code);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        JsonNode decryptedCode = utilsService.decryptRequestCode(code);
        boolean isExpired = utilsService.isExpiredCode(decryptedCode);

        if (isExpired) {
            log.error("User trying to approve request with expired code: {}", decryptedCode);
            throw new ExpiredCodeException("Code has expired");
        }

        Long requestId = decryptedCode.get(REQUEST_ID_KEY).asLong();
        boolean signed = requestSignatureManager.addSignature(requestId, user.getId(), user.getName(), approved, code);
        Request req = requestManager.getRequestById(requestId);

        log.info("Sending mail notification");
        mailsService.notifyUser(req, REQUEST_SIGNED);
        mailsService.notifyAppAdmins(req, REQUEST_SIGNED);

        log.trace("signTransferToProduction returns: {}", signed);
        return signed;
    }

    @Override
    public List<RequestSignature> getSignaturesForRequest(Long requestId, Long userId)
            throws UnauthorizedActionException, InternalErrorException
    {
        log.trace("getApprovalsOfProductionTransfer(requestId: {}, userId: {})", requestId, userId);

        if (Utils.checkParamsInvalid(requestId, userId)) {
            log.error("Wrong parameters passed: (requestId: {}, userId: {})" , requestId, userId);
            throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
        }

        Request request = requestManager.getRequestById(requestId);
        if (request == null) {
            log.error("Could not retrieve request for id: {}", requestId);
            throw new InternalErrorException(Utils.GENERIC_ERROR_MSG);
        } else if (!appConfig.isAppAdmin(userId) && !utilsService.isAdminInRequest(request.getReqUserId(), userId)) {
            log.error("User is not authorized to view approvals for request: {}", requestId);
            throw new UnauthorizedActionException(Utils.GENERIC_ERROR_MSG);
        }

        List<RequestSignature> result = requestSignatureManager.getRequestSignatures(requestId);

        log.trace("getApprovalsOfProductionTransfer returns: {}", result);
        return result;
    }
}
