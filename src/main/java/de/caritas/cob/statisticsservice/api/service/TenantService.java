package de.caritas.cob.statisticsservice.api.service;

import de.caritas.cob.statisticsservice.config.apiclient.TenantServiceApiControllerFactory;
import de.caritas.cob.statisticsservice.config.cache.CacheManagerConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import de.caritas.cob.statisticsservice.tenantservice.generated.web.TenantControllerApi;
import de.caritas.cob.statisticsservice.tenantservice.generated.web.model.RestrictedTenantDTO;

@Service
@RequiredArgsConstructor
public class TenantService {

  private final @NonNull TenantServiceApiControllerFactory tenantServiceApiControllerFactory;

  @Cacheable(cacheNames = CacheManagerConfig.TENANT_CACHE, key = "#subdomain")
  public RestrictedTenantDTO getRestrictedTenantDataBySubdomain(String subdomain) {
    return tenantServiceApiControllerFactory
            .createControllerApi()
            .getRestrictedTenantDataBySubdomainWithHttpInfo(subdomain).getBody();
  }

}
