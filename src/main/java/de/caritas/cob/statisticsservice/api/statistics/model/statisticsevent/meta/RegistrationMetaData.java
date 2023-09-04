package de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationMetaData {

  private Long tenantId;
  private String tenantName;
  private String agencyName;
  private String registrationDate;
  private Integer age;
  private String gender;
  private String counsellingRelation;
  private List<String> topicsInternalAttributes;
  private String mainTopicInternalAttribute;
  private String postalCode;
  private String referer;

}
