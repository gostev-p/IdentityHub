package org.eclipse.edc.identityhub.seed;

import org.eclipse.edc.identityhub.spi.authentication.ServicePrincipal;
import org.eclipse.edc.identityhub.spi.participantcontext.ParticipantContextService;
import org.eclipse.edc.identityhub.spi.participantcontext.model.KeyDescriptor;
import org.eclipse.edc.identityhub.spi.participantcontext.model.ParticipantManifest;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ParticipantContextSeedExtension implements ServiceExtension {

  public static final String NAME = "ParticipantContext Seed Extension";
  public static final String DEFAULT_SUPERUSER_ID = "super-user";

  @Setting(description = "API key for the initial super user")
  public static final String SUPERUSER_APIKEY_PROPERTY = "edc.ih.api.superuser.key";

  @Setting(description = "Participant ID for the initial super user", defaultValue = DEFAULT_SUPERUSER_ID)
  public static final String SUPERUSER_ID_PROPERTY = "edc.ih.api.superuser.id";

  @Inject
  private ParticipantContextService participantContextService;

  private Monitor monitor;
  private String superUserId;
  private String superUserApiKey;

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    monitor = context.getMonitor();
    superUserId = context.getSetting(SUPERUSER_ID_PROPERTY, DEFAULT_SUPERUSER_ID);
    superUserApiKey = context.getSetting(SUPERUSER_APIKEY_PROPERTY, null);
  }

  @Override
  public void start() {
    // schon vorhanden? dann nicht nochmal seeden
    if (participantContextService.getParticipantContext(superUserId).succeeded()) {
      monitor.info("Super user '%s' already exists, skipping seeding.".formatted(superUserId));
      return;
    }

    // 1) Manifest bauen – hier gibt es nur .active(...)
    var manifest = ParticipantManifest.Builder.newInstance()
        .participantId(superUserId)
        .did("did:web:%s".formatted(superUserId))
        .active(true) // sagt: soll eigentlich aktiviert werden
        .key(KeyDescriptor.Builder.newInstance()
            .keyId(superUserId + "-key")
            .privateKeyAlias(superUserId + "-alias")
            .keyGeneratorParams(Map.of("algorithm", "EdDSA", "curve", "Ed25519"))
            .build())
        .roles(List.of(ServicePrincipal.ROLE_ADMIN))
        .build();

    // 2) anlegen
    var createResult = participantContextService.createParticipantContext(manifest)
        .orElseThrow(f -> new EdcException("Could not create super user: " + f.getFailureDetail()));

    // 3) explizit aktivieren, weil dein Service es offenbar nicht automatisch macht
    participantContextService.updateParticipant(superUserId, pc -> {
      pc.activate();          // <-- das gibt es in deinem ParticipantContext
      pc.updateLastModified();
    }).orElseThrow(f -> new EdcException("Could not activate super user: " + f.getFailureDetail()));

    // 4) Key aus dem create-Result nehmen, wenn keiner per -D gesetzt wurde
    String effectiveKey = Optional.ofNullable(superUserApiKey).orElse(createResult.apiKey());

    monitor.info("Created and ACTIVATED super user '%s'. Use this API key for Identity API requests: %s"
        .formatted(superUserId, effectiveKey));
  }
}
