package de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCanceledMetaData {

  private String uid;
  private Integer bookingId;
  private Integer currentBookingId;

}
