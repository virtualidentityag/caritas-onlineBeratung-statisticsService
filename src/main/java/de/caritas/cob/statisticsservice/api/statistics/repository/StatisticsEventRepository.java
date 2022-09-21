package de.caritas.cob.statisticsservice.api.statistics.repository;

import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface StatisticsEventRepository extends MongoRepository<StatisticsEvent, String> {

  @Data
  @AllArgsConstructor
  class Count {
    long totalCount;
  }

  @Data
  @AllArgsConstructor
  class Duration {
    long total;
  }

  /**
   * Calculate the number of sessions in which the user was active.
   * Active means that the user has either sent a message or initiated a video call.
   *
   * @param userId the user id
   * @param dateFrom the start date of the period
   * @param dateTo the end date of the period
   * @return the number of sessions in which the user was active. Could also return null if the
   * mongo query returns no results.
   */
  @Aggregation(
      pipeline = {
          "{$match:{'user._id': ?0,'eventType': {'$in': ['START_VIDEO_CALL','CREATE_MESSAGE']},'timestamp':{$gte:?1,$lte:?2}}}",
          "{$group:{'_id': '$sessionId', 'count': { '$sum': 1 }}}",
          "{$project:{'_id': 0}}",
          "{$count:'totalCount'}"
      })
  Count calculateNumbersOfSessionsWhereUserWasActive(String userId, Instant dateFrom, Instant dateTo);

  /**
   * Calculate the number of sent messages in the given period.
   *
   * @param userId the user id
   * @param dateFrom the start date of the period
   * @param dateTo the end date of the period
   * @return the number of sent messages in the given period
   */
  @Query(value = "{'user._id': ?0, 'eventType': 'CREATE_MESSAGE', 'timestamp':{$gte:?1,$lte:?2}}", count = true)
  long calculateNumberOfSentMessagesForUser(String userId, Instant dateFrom, Instant dateTo);

  /**
   * Calculate the time a user has spent in video calls in the given time period.
   *
   * @param userId the user id
   * @param dateFrom the start date of the period
   * @param dateTo the end date of the period
   * @return the total time in seconds. Could also return null if the mongo query returns no
   * results.
   */
  @Aggregation(pipeline = {
      "{'$match':{'user._id': ?0,'eventType': 'START_VIDEO_CALL','timestamp':{$gte:?1,$lte:?2}}}",
      "{'$group':{'_id':'','total':{'$sum':'$metaData.duration'}}}"
      })
  Duration calculateTimeInVideoCallsForUser(String userId, Instant dateFrom, Instant dateTo);

  /**
   * Calculate the number of sessions assigned to a user in the given time period.
   *
   * @param userId the user id
   * @param dateFrom the start date of the period
   * @param dateTo the end date of the period
   * @return the number of the new sessions
   */
  @Query(value = "{'user._id': ?0, 'eventType': 'ASSIGN_SESSION', 'timestamp':{$gte:?1,$lte:?2}}", count = true)
  long calculateNumberOfAssignedSessionsForUser(String userId, Instant dateFrom, Instant dateTo);

  @Query(value = "{'eventType': 'REGISTRATION'}")
  List<StatisticsEvent> getAllRegistrationStatistics();
}
