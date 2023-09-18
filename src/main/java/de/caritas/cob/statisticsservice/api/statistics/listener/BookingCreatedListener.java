package de.caritas.cob.statisticsservice.api.statistics.listener;

import de.caritas.cob.statisticsservice.api.model.BookingCreatedStatisticsEventMessage;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.User;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.BookingCreatedMetaData;
import java.time.Instant;
import lombok.NonNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * AMQP Listener for create message statistics event.
 */
@Service
public class BookingCreatedListener extends BookingListener {

  public BookingCreatedListener(@NonNull MongoTemplate mongoTemplate) {
    super(mongoTemplate);
  }

  /**
   * Consumer for create message statics statistics event.
   *
   * @param eventMessage the {@link BookingCreatedStatisticsEventMessage} instance
   */
  @RabbitListener(
      id = "booking-created-event-listener",
      queues = "#{rabbitMqConfig.QUEUE_NAME_BOOKING_CREATED}",
      containerFactory = "simpleRabbitListenerContainerFactory")
  public void receiveMessage(BookingCreatedStatisticsEventMessage eventMessage) {

    StatisticsEvent statisticsEvent = StatisticsEvent.builder()
        .eventType(eventMessage.getEventType())
        .timestamp(eventMessage.getTimestamp().toInstant())
        .user(User.builder().userRole(eventMessage.getUserRole()).id(eventMessage.getUserId())
            .build())
        .metaData(buildMetaData(eventMessage))
        .build();

    mongoTemplate.insert(statisticsEvent);
  }

  private BookingCreatedMetaData buildMetaData(BookingCreatedStatisticsEventMessage eventMessage) {
    return BookingCreatedMetaData.builder()
        .type(eventMessage.getType())
        .title(eventMessage.getTitle())
        .startTime(Instant.parse(eventMessage.getStartTime()))
        .endTime(Instant.parse(eventMessage.getEndTime()))
        .uid(eventMessage.getUid())
        .bookingId(eventMessage.getBookingId())
        .currentBookingId(eventMessage.getBookingId())
        .adviceSeekerId(eventMessage.getAdviceSeekerId())
        .tenantId(eventMessage.getTenantId())
        .build();
  }
}
