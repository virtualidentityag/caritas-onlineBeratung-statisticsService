package de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartVideoCallMetaData implements AdviceSeekerAwareMetaData {

  private String videoCallUuid;
  private long duration;
  private Instant timestampStop;
  private VideoCallStatus status;
  private String adviceSeekerId;

  private Long tenantId;

}
