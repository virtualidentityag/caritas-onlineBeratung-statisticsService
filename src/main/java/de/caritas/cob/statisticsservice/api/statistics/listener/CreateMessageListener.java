package de.caritas.cob.statisticsservice.api.statistics.listener;

import de.caritas.cob.statisticsservice.api.model.CreateMessageStatisticsEventMessage;
import de.caritas.cob.statisticsservice.api.service.UserStatisticsService;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEventBuilder;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.CreateMessageMetaData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/** AMQP Listener for create message statistics event. */
@Service
@RequiredArgsConstructor
public class CreateMessageListener {

  private final @NonNull MongoTemplate mongoTemplate;
  private final @NonNull UserStatisticsService userStatisticsService;

  /**
   * Consumer for create message statics statistics event.
   *
   * @param eventMessage the {@link CreateMessageStatisticsEventMessage} instance
   */
  @RabbitListener(
      id = "create-message-event-listener",
      queues = "#{rabbitMqConfig.QUEUE_NAME_CREATE_MESSAGE}",
      containerFactory = "simpleRabbitListenerContainerFactory")
  public void receiveMessage(CreateMessageStatisticsEventMessage eventMessage) {

    StatisticsEvent statisticsEvent =
        StatisticsEventBuilder.getInstance(
            () ->
                userStatisticsService.retrieveSessionViaRcGroupId(eventMessage.getRcGroupId()))
            .withEventType(eventMessage.getEventType())
            .withTimestamp(eventMessage.getTimestamp().toInstant())
            .withUserId(eventMessage.getUserId())
            .withUserRole(eventMessage.getUserRole())
            .withMetaData(buildMetaData(eventMessage))
            .build();

    mongoTemplate.insert(statisticsEvent);
  }

  private CreateMessageMetaData buildMetaData(CreateMessageStatisticsEventMessage eventMessage) {
    return CreateMessageMetaData.builder()
        .receiverId(eventMessage.getReceiverId())
        .hasAttachment(eventMessage.getHasAttachment())
        .tenantId(eventMessage.getTenantId())
        .build();
  }
}
