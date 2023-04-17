package de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent;

import de.caritas.cob.statisticsservice.api.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "statistics_event")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsEvent {

  @Id
  private String id;
  private EventType eventType;
  private Long sessionId;
  private Instant timestamp;
  private ConsultingType consultingType;
  private User user;
  private Agency agency;
  private Diocese diocese;
  private Object metaData;

}
