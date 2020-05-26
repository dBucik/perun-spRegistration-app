package cz.metacentrum.perun.spRegistration.persistence;

import cz.metacentrum.perun.spRegistration.persistence.managers.LinkCodeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private final LinkCodeManager linkCodeManager;

    @Autowired
    public ScheduledTasks(LinkCodeManager linkCodeManager) {
        this.linkCodeManager = linkCodeManager;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Prague")
    public void removeExpiredCodes() {
        log.info("removeExpiredCodes() starts");
        linkCodeManager.deleteExpired();
        log.info("removeExpiredCodes() ends");
    }
}
