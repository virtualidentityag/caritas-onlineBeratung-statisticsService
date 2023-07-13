package de.caritas.cob.statisticsservice.api.helper;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;

import de.caritas.cob.statisticsservice.api.model.RegistrationStatisticsResponseDTO;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.ArchiveMetaData;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.DeleteAccountMetaData;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.RegistrationMetaData;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class RegistrationStatisticsDTOConverter {

  public RegistrationStatisticsResponseDTO convertStatisticsEvent(
      StatisticsEvent rawEvent, List<StatisticsEvent> archiveSessionEvents, List<StatisticsEvent> deleteAccountEvents) {
    RegistrationMetaData metadata = (RegistrationMetaData) rawEvent.getMetaData();
    String maxArchiveDate = findMaxArchiveDate(rawEvent.getSessionId(), archiveSessionEvents);
    String deleteAccountDate = findDeleteAccountDate(rawEvent.getUser().getId(), deleteAccountEvents);
    return new RegistrationStatisticsResponseDTO()
        .userId(rawEvent.getUser().getId())
        .registrationDate(metadata.getRegistrationDate())
        .age(metadata.getAge())
        .tenantName(metadata.getTenantName())
        .agencyName(metadata.getAgencyName())
        .gender(metadata.getGender())
        .counsellingRelation(metadata.getCounsellingRelation())
        .mainTopicInternalAttribute(metadata.getMainTopicInternalAttribute())
        .topicsInternalAttributes(metadata.getTopicsInternalAttributes())
        .endDate(getEndDate(maxArchiveDate, deleteAccountDate))
        .postalCode(metadata.getPostalCode())
        .referer(metadata.getReferer());
  }

  private String getEndDate(String maxArchiveDate, String deleteAccountDate) {
    return deleteAccountDate != null ? deleteAccountDate : maxArchiveDate;
  }

  private String findDeleteAccountDate(String userId, List<StatisticsEvent> deleteAccountEvents) {
    return deleteAccountEvents != null ? deleteAccountEvents.stream()
        .filter(event -> event.getUser() != null && event.getUser().getId().equals(userId))
        .map(event -> ((DeleteAccountMetaData) event.getMetaData()).getDeleteDate())
        .findFirst().orElse(null) : null;
  }

  private String findMaxArchiveDate(Long sessionId, List<StatisticsEvent> archiveSessionEvents) {
    var maxArchiveEvent = findMaxArchiveSessionEvent(sessionId, archiveSessionEvents);
    if (maxArchiveEvent.isPresent()) {
      ArchiveMetaData metaData = (ArchiveMetaData) maxArchiveEvent.get().getMetaData();
      return metaData.getEndDate();
    }
    return null;
  }

  private Optional<StatisticsEvent> findMaxArchiveSessionEvent(Long sessionId, List<StatisticsEvent> archiveSessionEvents) {
    return nonNull(archiveSessionEvents) ? archiveSessionEvents.stream()
        .filter(event -> event.getSessionId() != null && event.getSessionId().equals(sessionId))
        .max(comparing(StatisticsEvent::getTimestamp)) : Optional.empty();
  }
}
