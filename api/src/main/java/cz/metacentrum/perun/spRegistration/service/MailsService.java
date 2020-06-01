package cz.metacentrum.perun.spRegistration.service;

import cz.metacentrum.perun.spRegistration.common.models.Facility;
import cz.metacentrum.perun.spRegistration.common.models.Request;
import cz.metacentrum.perun.spRegistration.common.models.User;

import java.util.Map;

public interface MailsService {
    void notifyAuthorities(Request req, Map<String, String> authoritiesLinksMap);

    boolean notifyNewAdmins(Facility facility, Map<String, String> adminsLinksMap, User user);

    boolean authoritiesApproveProductionTransferNotify(String approvalLink, Request req, String recipient);

    boolean adminAddRemoveNotify(String approvalLink, Facility facility, String recipient, User user);

    void notifyUser(Request req, String action);

    void notifyAppAdmins(Request req, String action);

    void notifyClientSecretChanged(Facility facility);
}
