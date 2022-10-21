package de.caritas.cob.statisticsservice.api.helper;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;

import de.caritas.cob.statisticsservice.api.model.RegistrationStatisticsResponseDTO;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.ArchiveMetaData;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.RegistrationMetaData;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class RegistrationStatisticsDTOConverter {

  public RegistrationStatisticsResponseDTO convertStatisticsEvent(
      StatisticsEvent rawEvent, List<StatisticsEvent> archiveSessionEvents) {
    RegistrationMetaData metadata = (RegistrationMetaData) rawEvent.getMetaData();
    return new RegistrationStatisticsResponseDTO()
        .userId(rawEvent.getUser().getId())
        .registrationDate(metadata.getRegistrationDate())
        .age(metadata.getAge())
        .gender(metadata.getGender())
        .counsellingRelation(metadata.getCounsellingRelation())
        .mainTopicInternalAttribute(metadata.getMainTopicInternalAttribute())
        .topicsInternalAttributes(metadata.getTopicsInternalAttributes())
        .endDate(findEndDate(rawEvent.getSessionId(), archiveSessionEvents))
        .postalCode(metadata.getPostalCode());
  }

  private String findEndDate(Long sessionId, List<StatisticsEvent> archiveSessionEvents) {
    var maxArchiveEvent = findMaxArchiveSessionEvent(sessionId, archiveSessionEvents);
    if (maxArchiveEvent.isPresent()) {
      ArchiveMetaData metaData = (ArchiveMetaData) maxArchiveEvent.get().getMetaData();
      return metaData.getEndDate();
    }
    return null;
  }

  private Optional<StatisticsEvent> findMaxArchiveSessionEvent(Long sessionId, List<StatisticsEvent> archiveSessionEvents) {
    return nonNull(archiveSessionEvents) ? archiveSessionEvents.stream()
        .filter(event -> event.getSessionId() == sessionId)
        .max(comparing(StatisticsEvent::getTimestamp)) : Optional.empty();
  }
}
