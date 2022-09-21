package de.caritas.cob.statisticsservice.api.helper;

import de.caritas.cob.statisticsservice.api.model.RegistrationStatisticsResponseDTO;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.RegistrationMetaData;
import org.springframework.stereotype.Component;

@Component
public class RegistrationStatisticsDTOConverter {

  public RegistrationStatisticsResponseDTO convertStatisticsEvent(
      StatisticsEvent rawEvent) {
    RegistrationMetaData metadata = (RegistrationMetaData) rawEvent.getMetaData();
    return new RegistrationStatisticsResponseDTO()
        .userId(rawEvent.getUser().getId())
        .registrationDate(metadata.getRegistrationDate())
        .age(metadata.getAge())
        .gender(metadata.getGender())
        .counsellingRelation(metadata.getCounsellingRelation())
        .mainTopicInternalAttribute(metadata.getMainTopicInternalAttribute())
        .topicsInternalAttributes(metadata.getTopicsInternalAttributes())
        .postalCode(metadata.getPostalCode());
  }

}
