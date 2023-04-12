package de.caritas.cob.statisticsservice.api.authorization;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.statisticsservice.api.exception.httpresponses.StatisticsDisabledException;
import de.caritas.cob.statisticsservice.api.service.ApplicationSettingsService;
import de.caritas.cob.statisticsservice.api.service.TenantService;
import de.caritas.cob.statisticsservice.api.tenant.SubdomainExtractor;
import de.caritas.cob.statisticsservice.api.tenant.TenantContext;
import de.caritas.cob.statisticsservice.applicationsettingsservice.generated.web.model.ApplicationSettingsDTO;
import de.caritas.cob.statisticsservice.applicationsettingsservice.generated.web.model.ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy;
import de.caritas.cob.statisticsservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import de.caritas.cob.statisticsservice.tenantservice.generated.web.model.Settings;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StatisticsFeatureAuthorisationServiceTest {

  private static final String MULTIDOMAIN_MULTITENANCY_SUBDOMAIN = "subdomain";
  private static final String SINGLE_DOMAIN_MULTITENANCY_SUBDOMAIN = "app";
  @InjectMocks
  StatisticsFeatureAuthorisationService statisticsFeatureAuthorisationService;

  @Mock
  ApplicationSettingsService applicationSettingsService;

  @Mock
  SubdomainExtractor subdomainExtractor;
  @Mock
  TenantService tenantService;

  @Test
  public void assertStatisticsFeatureIsEnabled_ShouldNotThrowExceptionIfFeatureIsEnabled_When_Multitenancy() {
    // given
    TenantContext.setCurrentTenant(1L);
    givenTenantSettingStatisticsFeatureEnabled(true);
    ReflectionTestUtils.setField(statisticsFeatureAuthorisationService, "multitenancy", true);
    // when
    statisticsFeatureAuthorisationService.assertStatisticsFeatureIsEnabled();
    // then
    verify(tenantService).getRestrictedTenantDataNonCached(1L);
  }

  @Test
  public void assertStatisticsFeatureIsEnabled_ShouldThrowException_WhenFeatureIsEnabledAndMultitenanancyOn() {
    // given
    TenantContext.setCurrentTenant(1L);
    givenTenantSettingStatisticsFeatureEnabled(false);
    ReflectionTestUtils.setField(statisticsFeatureAuthorisationService, "multitenancy", true);

    // when, then
    assertThrows(StatisticsDisabledException.class, () -> statisticsFeatureAuthorisationService.assertStatisticsFeatureIsEnabled());
    verify(tenantService).getRestrictedTenantDataNonCached(1L);
  }

  @Test
  public void assertStatisticsFeatureIsEnabled_ShouldThrowException_WhenFeatureIsEnabledAndMultitenanancyOnAndResolvingBySubdomain() {
    // given
    TenantContext.setCurrentTenant(1L);
    Mockito.when(subdomainExtractor.getCurrentSubdomain()).thenReturn(Optional.of(
        MULTIDOMAIN_MULTITENANCY_SUBDOMAIN));
    when(tenantService.getRestrictedTenantDataBySubdomainNonCached(
        MULTIDOMAIN_MULTITENANCY_SUBDOMAIN)).thenReturn(new RestrictedTenantDTO().settings(new Settings().featureStatisticsEnabled(false)));
    ReflectionTestUtils.setField(statisticsFeatureAuthorisationService, "multitenancy", true);

    // when, then
    assertThrows(StatisticsDisabledException.class, () -> statisticsFeatureAuthorisationService.assertStatisticsFeatureIsEnabled());
    verify(tenantService).getRestrictedTenantDataBySubdomainNonCached(
        MULTIDOMAIN_MULTITENANCY_SUBDOMAIN);
  }

  @Test
  public void assertStatisticsFeatureIsEnabled_ShouldNotThrowExceptionIfFeatureIsEnabled_When_MultitenancySingleDomain() {
    // given
    TenantContext.setCurrentTenant(1L);
    givenTenantSettingStatisticsFeatureEnabledForSingleDomain(true);
    when(applicationSettingsService.getApplicationSettings()).thenReturn(new ApplicationSettingsDTO()
        .mainTenantSubdomainForSingleDomainMultitenancy(new ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy().value(
            SINGLE_DOMAIN_MULTITENANCY_SUBDOMAIN)));
    ReflectionTestUtils.setField(statisticsFeatureAuthorisationService, "multitenancy", false);
    ReflectionTestUtils.setField(statisticsFeatureAuthorisationService, "multitenancyWithSingleDomainEnabled", true);
    // when
    statisticsFeatureAuthorisationService.assertStatisticsFeatureIsEnabled();
    // then
    verify(tenantService).getRestrictedTenantDataBySubdomainNonCached(
        SINGLE_DOMAIN_MULTITENANCY_SUBDOMAIN);

  }

  @Test
  public void assertStatisticsFeatureIsEnabled_ShouldThrowExceptionIfFeatureIsDisabled_When_MultitenancySingleDomain() {
    // given
    TenantContext.setCurrentTenant(1L);
    givenTenantSettingStatisticsFeatureEnabledForSingleDomain(false);
    when(applicationSettingsService.getApplicationSettings()).thenReturn(new ApplicationSettingsDTO()
        .mainTenantSubdomainForSingleDomainMultitenancy(new ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy().value(
            SINGLE_DOMAIN_MULTITENANCY_SUBDOMAIN)));
    ReflectionTestUtils.setField(statisticsFeatureAuthorisationService, "multitenancy", false);
    ReflectionTestUtils.setField(statisticsFeatureAuthorisationService, "multitenancyWithSingleDomainEnabled", true);
    // when, then
    assertThrows(StatisticsDisabledException.class, () -> statisticsFeatureAuthorisationService.assertStatisticsFeatureIsEnabled());
  }

  private void givenTenantSettingStatisticsFeatureEnabled(boolean statisticsFeatureEnabled) {
    when(tenantService.getRestrictedTenantDataNonCached(1L)).thenReturn(new RestrictedTenantDTO().settings(new Settings().featureStatisticsEnabled(statisticsFeatureEnabled)));
  }

  private void givenTenantSettingStatisticsFeatureEnabledForSingleDomain(boolean statisticsFeatureEnabled) {
    when(tenantService.getRestrictedTenantDataBySubdomainNonCached(
        SINGLE_DOMAIN_MULTITENANCY_SUBDOMAIN)).thenReturn(new RestrictedTenantDTO().settings(new Settings().featureStatisticsEnabled(statisticsFeatureEnabled)));
  }
}