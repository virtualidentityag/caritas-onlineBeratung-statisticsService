package de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StatisticEventsContainer {

  private Collection<StatisticsEvent> archiveSessionEvents;
  private Collection<StatisticsEvent> deleteAccountEvents;
  private Collection<StatisticsEvent> videoCallStartedEvents;
  private Collection<StatisticsEvent> bookingCreatedEvents;
  private Collection<StatisticsEvent> consultantMessageCreatedEvents;

}
