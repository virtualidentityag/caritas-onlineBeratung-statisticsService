package de.caritas.cob.statisticsservice.api.statistics.listener;

import de.caritas.cob.statisticsservice.api.model.BookingCanceledStatisticsEventMessage;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.User;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.BookingCanceledMetaData;
import lombok.NonNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * AMQP Listener for create message statistics event.
 */
@Service
public class BookingCanceledListener extends BookingListener {

  public BookingCanceledListener(@NonNull MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  /**
   * Consumer for create message statics statistics event.
   *
   * @param eventMessage the {@link BookingCanceledStatisticsEventMessage} instance
   */
  @RabbitListener(
      id = "booking-canceled-event-listener",
      queues = "#{rabbitMqConfig.QUEUE_NAME_BOOKING_CANCELED}",
      containerFactory = "simpleRabbitListenerContainerFactory")
  public void receiveMessage(BookingCanceledStatisticsEventMessage eventMessage) {

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

  private BookingCanceledMetaData buildMetaData(
      BookingCanceledStatisticsEventMessage eventMessage) {
    return BookingCanceledMetaData.builder()
        .uid(eventMessage.getUid())
        .bookingId(eventMessage.getBookingId())
        .currentBookingId(eventMessage.getBookingId())
        .build();
  }
}
