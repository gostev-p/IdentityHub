package de.adesso.dataspaces.extension;

import de.adesso.dataspaces.controller.IssuanceApiController;
import de.adesso.dataspaces.service.IssuanceService;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;

public class IssuanceApiExtension implements ServiceExtension {

  @Inject
  WebService webService;

  @Inject
  Monitor monitor;

  @Override
  public void initialize(ServiceExtensionContext context) {
    monitor.info("Initializing Issuance API Extension");

    IssuanceService issuanceService = new IssuanceService();
    context.registerService(IssuanceService.class, issuanceService);

    IssuanceApiController controller = new IssuanceApiController(issuanceService);
    webService.registerResource(controller);

    monitor.info("Issuance API Extension initialized successfully");
  }
}
