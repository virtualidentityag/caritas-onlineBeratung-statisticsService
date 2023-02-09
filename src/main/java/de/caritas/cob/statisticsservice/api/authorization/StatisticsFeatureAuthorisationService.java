package de.caritas.cob.statisticsservice.api.authorization;

import de.caritas.cob.statisticsservice.api.exception.httpresponses.StatisticsDisabledException;
import de.caritas.cob.statisticsservice.api.service.ApplicationSettingsService;
import de.caritas.cob.statisticsservice.api.service.TenantService;
import de.caritas.cob.statisticsservice.api.tenant.SubdomainExtractor;
import de.caritas.cob.statisticsservice.api.tenant.SubdomainTenantResolver;
import de.caritas.cob.statisticsservice.api.tenant.TenantContext;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import de.caritas.cob.statisticsservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import de.caritas.cob.statisticsservice.tenantservice.generated.web.model.Settings;

@RequiredArgsConstructor
@Slf4j
@Service
public class StatisticsFeatureAuthorisationService {

  @Value("${multitenancy.enabled}")
  private boolean multitenancy;

  @Value("${feature.multitenancy.with.single.domain.enabled}")
  private boolean multitenancyWithSingleDomainEnabled;

  private final @NonNull TenantService tenantService;

  private final @NonNull SubdomainExtractor subdomainExtractor;

  private final @NonNull ApplicationSettingsService applicationSettingsService;

  public void assertStatisticsFeatureIsEnabled() throws StatisticsDisabledException {
    if (!determineIfStatisticsFeatureIsEnabled()) {
      throw new StatisticsDisabledException("feature statistics disabled");
    }
  }

  private boolean determineIfStatisticsFeatureIsEnabled() {
    if (multitenancy) {
      return isStatisticsFeatureEnabledForMultitenancy();
    }
    if (multitenancyWithSingleDomainEnabled) {
      return isStatisticsFeatureEnabledForSingleDomainMultitenancy();
    }
    return true;
  }

  private boolean isStatisticsFeatureEnabledForSingleDomainMultitenancy() {
    String mainTenantSubdomain = getMainTenantSubdomain();
    RestrictedTenantDTO restrictedTenantDataBySubdomain =
        tenantService.getRestrictedTenantDataBySubdomainNonCached(mainTenantSubdomain);
    return isStatisticsFeatureEnabled(restrictedTenantDataBySubdomain);
  }

  private String getMainTenantSubdomain() {
    var applicationSettings =
        applicationSettingsService.getApplicationSettings();
    String mainTenantSubdomain =
        applicationSettings.getMainTenantSubdomainForSingleDomainMultitenancy().getValue();
    return mainTenantSubdomain;
  }

  private boolean isStatisticsFeatureEnabledForMultitenancy() {
    Optional<String> currentSubdomain = subdomainExtractor.getCurrentSubdomain();
    RestrictedTenantDTO restrictedTenantData = currentSubdomain.isPresent() ? tenantService.getRestrictedTenantDataBySubdomainNonCached(
          currentSubdomain.get()) : tenantService.getRestrictedTenantDataNonCached(TenantContext.getCurrentTenant());
    return isStatisticsFeatureEnabled(restrictedTenantData);
  }

  private boolean isStatisticsFeatureEnabled(RestrictedTenantDTO restrictedTenantData) {
    Settings settings = restrictedTenantData.getSettings();
    if (settings == null) {
      log.warn("Tenant has no settings!");
      return false;
    }
    Boolean featureStatisticsEnabled = settings.getFeatureStatisticsEnabled();
    return featureStatisticsEnabled != null && featureStatisticsEnabled;
  }
}
