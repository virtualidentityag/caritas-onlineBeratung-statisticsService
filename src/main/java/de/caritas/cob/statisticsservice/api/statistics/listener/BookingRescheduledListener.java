package de.caritas.cob.statisticsservice.api.statistics.listener;

import de.caritas.cob.statisticsservice.api.model.BookingRescheduledStatisticsEventMessage;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.User;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.BookingRescheduledMetaData;
import java.time.Instant;
import lombok.NonNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * AMQP Listener for create message statistics event.
 */
@Service
public class BookingRescheduledListener extends BookingListener {

  public BookingRescheduledListener(
      @NonNull MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  /**
   * Consumer for create message statics statistics event.
   *
   * @param eventMessage the {@link BookingRescheduledStatisticsEventMessage} instance
   */
  @RabbitListener(
      id = "booking-rescheduled-event-listener",
      queues = "#{rabbitMqConfig.QUEUE_NAME_BOOKING_RESCHEDULED}",
      containerFactory = "simpleRabbitListenerContainerFactory")
  public void receiveMessage(BookingRescheduledStatisticsEventMessage eventMessage) {

    StatisticsEvent statisticsEvent = StatisticsEvent.builder()
        .eventType(eventMessage.getEventType())
        .timestamp(eventMessage.getTimestamp().toInstant())
        .user(User.builder().userRole(eventMessage.getUserRole()).id(eventMessage.getUserId())
            .build())
        .metaData(buildMetaData(eventMessage))
        .build();

    mongoTemplate.insert(statisticsEvent);
    this.updateRelatedBookings(eventMessage.getPrevBookingId(), eventMessage.getBookingId());
  }

  private BookingRescheduledMetaData buildMetaData(
      BookingRescheduledStatisticsEventMessage eventMessage) {
    return BookingRescheduledMetaData.builder()
        .startTime(Instant.parse(eventMessage.getStartTime()))
        .endTime(Instant.parse(eventMessage.getEndTime()))
        .uid(eventMessage.getUid())
        .bookingId(eventMessage.getBookingId())
        .currentBookingId(eventMessage.getBookingId())
        .build();
  }
}
