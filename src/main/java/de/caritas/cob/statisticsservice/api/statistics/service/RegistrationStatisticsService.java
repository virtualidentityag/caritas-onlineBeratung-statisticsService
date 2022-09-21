package de.caritas.cob.statisticsservice.api.statistics.service;

import de.caritas.cob.statisticsservice.api.helper.RegistrationStatisticsDTOConverter;
import de.caritas.cob.statisticsservice.api.model.RegistrationStatisticsListResponseDTO;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.repository.StatisticsEventRepository;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationStatisticsService {

  private final @NonNull StatisticsEventRepository statisticsEventRepository;
  private final @NonNull RegistrationStatisticsDTOConverter registrationStatisticsDTOConverter;

  public RegistrationStatisticsListResponseDTO fetchRegistrationStatisticsData() {
    return buildResponseDTO();
  }

  private RegistrationStatisticsListResponseDTO buildResponseDTO() {
    RegistrationStatisticsListResponseDTO registrationStatisticsList = new RegistrationStatisticsListResponseDTO();
    List<StatisticsEvent> registrationStatisticsRaw = statisticsEventRepository.getAllRegistrationStatistics();
    for (StatisticsEvent rawEvent : registrationStatisticsRaw) {
      registrationStatisticsList.addRegistrationStatisticsItem(
          registrationStatisticsDTOConverter.convertStatisticsEvent(rawEvent));
    }
    return registrationStatisticsList;
  }
}