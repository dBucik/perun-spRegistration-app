package cz.metacentrum.perun.spRegistration.common.configs;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode
@Slf4j
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    @NotEmpty
    private Set<Long> adminIds;

    @NotBlank
    private String proxyIdentifier;

    @NotEmpty
    private Set<String> protocolsEnabled;

    @NotEmpty private Set<String> languagesEnabled;

    @ToString.Exclude
    @NotBlank
    private String secretKey;

    @NotBlank
    private String hostUrl;

    @NotBlank
    private String logoutUrl;

    @NotNull
    private AttributesProperties attributesProperties;

    @NotNull
    private ApprovalsProperties approvalsProperties;

    @NotNull
    private FrontendProperties frontendProperties;

    @NotNull
    private Long spManagersVoId;

    @NotNull
    private Long spManagersParentGroupId;

    @NotBlank
    private String mailsConfigFilePath;

    @NotNull
    private boolean startupSyncEnabled = false;

    private String devUserIdentifier;

    private long propagationInterval = 1800; //seconds

    @PostConstruct
    public void postInit() {
        log.info("Initialized application properties");
        log.debug("{}", this);
    }

    public boolean isAppAdmin(@NonNull Long id) {
        return adminIds.contains(id);
    }

}
