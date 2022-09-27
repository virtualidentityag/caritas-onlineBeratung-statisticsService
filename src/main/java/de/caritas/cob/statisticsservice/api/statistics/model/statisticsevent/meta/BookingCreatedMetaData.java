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
public class BookingCreatedMetaData {

  private String type;
  private String title;
  private Instant startTime;
  private Instant endTime;
  private String uid;
  private Integer bookingId;
  private Integer currentBookingId;

}
