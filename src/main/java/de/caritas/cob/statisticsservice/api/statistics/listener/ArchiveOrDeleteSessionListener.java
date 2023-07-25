package de.caritas.cob.statisticsservice.api.statistics.listener;

import de.caritas.cob.statisticsservice.api.model.ArchiveOrDeleteSessionStatisticsEventMessage;
import de.caritas.cob.statisticsservice.api.service.UserStatisticsService;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEventBuilder;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.ArchiveMetaData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * AMQP Listener for archive session statistics event.
 */
@Service
@RequiredArgsConstructor
public class ArchiveOrDeleteSessionListener {

  private final @NonNull MongoTemplate mongoTemplate;
  private final @NonNull UserStatisticsService userStatisticsService;

  /**
   * Consumer for archive session message statistics event.
   *
   * @param eventMessage the {@link ArchiveOrDeleteSessionStatisticsEventMessage} instance
   */
  @RabbitListener(
      id = "archive-session-event-listener",
      queues = "#{rabbitMqConfig.QUEUE_NAME_ARCHIVE_SESSION}",
      containerFactory = "simpleRabbitListenerContainerFactory")
  public void receiveMessage(ArchiveOrDeleteSessionStatisticsEventMessage eventMessage) {

    StatisticsEvent statisticsEvent =
        StatisticsEventBuilder.getInstance(
            () -> userStatisticsService.retrieveSessionViaSessionId(eventMessage.getSessionId()))
            .withEventType(eventMessage.getEventType())
            .withTimestamp(eventMessage.getTimestamp().toInstant())
            .withUserId(eventMessage.getUserId())
            .withUserRole(eventMessage.getUserRole())
            .withMetaData(buildMetaData(eventMessage))
            .build();

    mongoTemplate.insert(statisticsEvent);
  }

  private ArchiveMetaData buildMetaData(ArchiveOrDeleteSessionStatisticsEventMessage eventMessage) {
    return ArchiveMetaData.builder()
        .endDate(eventMessage.getEndDate())
        .tenantId(eventMessage.getTenantId())
        .build();
  }
}
