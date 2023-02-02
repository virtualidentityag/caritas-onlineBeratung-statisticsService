package de.caritas.cob.statisticsservice.api.service;

import de.caritas.cob.statisticsservice.api.service.securityheader.SecurityHeaderSupplier;
import de.caritas.cob.statisticsservice.api.service.securityheader.TenantHeaderSupplier;
import de.caritas.cob.statisticsservice.config.apiclient.ApplicationSettingsApiControllerFactory;
import de.caritas.cob.statisticsservice.config.cache.CacheManagerConfig;
import de.caritas.cob.statisticsservice.applicationsettingsservice.generated.web.model.ApplicationSettingsDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import de.caritas.cob.statisticsservice.applicationsettingsservice.generated.ApiClient;

/** Service class to communicate with the ConsultingTypeService. */
@Component
@RequiredArgsConstructor
public class ApplicationSettingsService {

  private final @NonNull ApplicationSettingsApiControllerFactory
      applicationSettingsApiControllerFactory;
  private final @NonNull SecurityHeaderSupplier securityHeaderSupplier;
  private final @NonNull TenantHeaderSupplier tenantHeaderSupplier;

  //@Cacheable(value = CacheManagerConfig.APPLICATION_SETTINGS_CACHE)
  public de.caritas.cob.statisticsservice.applicationsettingsservice.generated.web.model.ApplicationSettingsDTO getApplicationSettings() {
    de.caritas.cob.statisticsservice.applicationsettingsservice.generated.web.ApplicationsettingsControllerApi controllerApi =
        applicationSettingsApiControllerFactory.createControllerApi();
    addDefaultHeaders(controllerApi.getApiClient());
    return controllerApi.getApplicationSettings();
  }

  private void addDefaultHeaders(
      de.caritas.cob.statisticsservice.applicationsettingsservice.generated.ApiClient apiClient) {
    var headers = this.securityHeaderSupplier.getCsrfHttpHeaders();
    tenantHeaderSupplier.addTenantHeader(headers);
    headers.forEach((key, value) -> apiClient.addDefaultHeader(key, value.iterator().next()));
  }
}
