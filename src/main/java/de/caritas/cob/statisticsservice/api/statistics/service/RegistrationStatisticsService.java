package de.caritas.cob.statisticsservice.api.statistics.service;

import de.caritas.cob.statisticsservice.api.helper.RegistrationStatisticsDTOConverter;
import de.caritas.cob.statisticsservice.api.model.RegistrationStatisticsListResponseDTO;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.repository.StatisticsEventRepository;
import de.caritas.cob.statisticsservice.api.statistics.repository.StatisticsEventTenantAwareRepository;
import de.caritas.cob.statisticsservice.api.tenant.TenantContext;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationStatisticsService {


  @Value("${multitenancy.enabled}")
  private Boolean multitenancyEnabled;

  private static final Long TECHNICAL_TENANT_ID = 0L;

  private final @NonNull StatisticsEventRepository statisticsEventRepository;

  private final @NonNull StatisticsEventTenantAwareRepository statisticsEventTenantAwareRepository;

  private final @NonNull RegistrationStatisticsDTOConverter registrationStatisticsDTOConverter;

  public RegistrationStatisticsListResponseDTO fetchRegistrationStatisticsData() {
    return buildResponseDTO();
  }

  private RegistrationStatisticsListResponseDTO buildResponseDTO() {
    List<StatisticsEvent> archiveSessionEvents = getArchiveSessionEvents();

    RegistrationStatisticsListResponseDTO registrationStatisticsList = new RegistrationStatisticsListResponseDTO();
    getRegistrationStatistics()
        .stream()
        .map(rawEvent -> registrationStatisticsDTOConverter.convertStatisticsEvent(rawEvent, archiveSessionEvents))
        .forEach(registrationStatisticsList::addRegistrationStatisticsItem);

    return registrationStatisticsList;
  }

  private List<StatisticsEvent> getRegistrationStatistics() {
    if (isAllTenantAccessContext()) {
      return getRegistrationStatisticsForAllTenants();
    } else {
      return getRegistrationStatisticsForCurrentTenant();
    }
  }

  private List<StatisticsEvent> getArchiveSessionEvents() {
    if (isAllTenantAccessContext()) {
      return getArchiveSessionEventsForAllTenants();
    } else {
      return getArchiveSessionEventsForCurrentTenant();
    }
  }

  private List<StatisticsEvent> getRegistrationStatisticsForCurrentTenant() {
    log.info("Gathering registration statistics for tenant with id {}", TenantContext.getCurrentTenant());
    return statisticsEventTenantAwareRepository.getAllRegistrationStatistics(
        TenantContext.getCurrentTenant());
  }

  private List<StatisticsEvent> getRegistrationStatisticsForAllTenants() {
    log.info("Gathering registration statistics for all tenants");
    return statisticsEventRepository.getAllRegistrationStatistics();
  }

  private List<StatisticsEvent> getArchiveSessionEventsForAllTenants() {
    log.info("Gathering archive sessions events for all tenants");
    return statisticsEventRepository.getAllArchiveSessionEvents();
  }

  private List<StatisticsEvent> getArchiveSessionEventsForCurrentTenant() {
    log.info("Gathering archive session events for tenant with id {}", TenantContext.getCurrentTenant());
    return statisticsEventTenantAwareRepository.getAllArchiveSessionEvents(TenantContext.getCurrentTenant());
  }

  private boolean isAllTenantAccessContext() {
    return multitenancyIsDisabled() || TECHNICAL_TENANT_ID.equals(TenantContext.getCurrentTenant());
  }

  private boolean multitenancyIsDisabled() {
    return !multitenancyEnabled;
  }
}
