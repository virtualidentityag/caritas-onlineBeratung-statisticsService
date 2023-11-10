package de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageMetaData {

  private boolean hasAttachment;
  private String receiverId;

  private Long tenantId;

}
