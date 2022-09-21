package de.caritas.cob.statisticsservice.api.statistics.listener;

import de.caritas.cob.statisticsservice.api.model.RegistrationStatisticsEventMessage;
import de.caritas.cob.statisticsservice.api.service.UserStatisticsService;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEventBuilder;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.RegistrationMetaData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * AMQP Listener for registration statistics event.
 */
@Service
@RequiredArgsConstructor
public class RegistrationListener {

  private final @NonNull MongoTemplate mongoTemplate;
  private final @NonNull UserStatisticsService userStatisticsService;

  /**
   * Consumer for registration statics statistics event.
   *
   * @param eventMessage the {@link RegistrationStatisticsEventMessage} instance
   */
  @RabbitListener(
      id = "registration-event-listener",
      queues = "#{rabbitMqConfig.QUEUE_NAME_REGISTRATION}",
      containerFactory = "simpleRabbitListenerContainerFactory")
  public void receiveMessage(RegistrationStatisticsEventMessage eventMessage) {

    StatisticsEvent statisticsEvent =
        StatisticsEventBuilder.getInstance(
            () ->
                userStatisticsService.retrieveSessionViaSessionId(eventMessage.getSessionId()))
            .withEventType(eventMessage.getEventType())
            .withTimestamp(eventMessage.getTimestamp().toInstant())
            .withUserId(eventMessage.getUserId())
            .withUserRole(eventMessage.getUserRole())
            .withMetaData(buildMetaData(eventMessage))
            .build();

    mongoTemplate.insert(statisticsEvent);
  }

  private RegistrationMetaData buildMetaData(RegistrationStatisticsEventMessage eventMessage) {
    return RegistrationMetaData.builder()
        .tenantId(eventMessage.getTenantId())
        .registrationDate(eventMessage.getRegistrationDate())
        .age(eventMessage.getAge())
        .gender(eventMessage.getGender())
        .counsellingRelation(eventMessage.getCounsellingRelation())
        .topicsInternalAttributes(eventMessage.getTopicsInternalAttributes())
        .mainTopicInternalAttribute(eventMessage.getMainTopicInternalAttribute())
        .postalCode(eventMessage.getPostalCode())
        .build();
  }
}
