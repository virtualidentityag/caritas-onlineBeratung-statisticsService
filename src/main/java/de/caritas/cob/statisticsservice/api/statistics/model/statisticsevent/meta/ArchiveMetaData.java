package de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ArchiveMetaData {

  private String endDate;
  private Long tenantId;
}
