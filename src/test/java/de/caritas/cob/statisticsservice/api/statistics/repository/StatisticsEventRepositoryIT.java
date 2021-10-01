package de.caritas.cob.statisticsservice.api.statistics.repository;

import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.CONSULTANT_ID;
import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.DATE_FROM;
import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.DATE_TO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.caritas.cob.statisticsservice.StatisticsServiceApplication;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@DataMongoTest()
@ContextConfiguration(classes = StatisticsServiceApplication.class)
@RunWith(SpringRunner.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
public class StatisticsEventRepositoryIT {

  public static final String MONGODB_STATISTICS_EVENTS_JSON_FILENAME =
      "mongodb/StatisticsEvents.json";
  private final Instant dateFromConverted =
      OffsetDateTime.of(DATE_FROM, LocalTime.MIN, ZoneOffset.UTC).toInstant();
  private final Instant dateToConverted =
      OffsetDateTime.of(DATE_TO, LocalTime.MAX, ZoneOffset.UTC).toInstant();
  private final String MONGO_COLLECTION_NAME = "statistics_event";
  @Autowired StatisticsEventRepository statisticsEventRepository;
  @Autowired MongoTemplate mongoTemplate;

  @Before
  public void createDataSet() throws IOException {
    mongoTemplate.dropCollection(MONGO_COLLECTION_NAME);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    List<StatisticsEvent> statisticEvents =
        objectMapper.readValue(
            new ClassPathResource(MONGODB_STATISTICS_EVENTS_JSON_FILENAME).getFile(),
            new TypeReference<>() {});
    mongoTemplate.insert(statisticEvents, MONGO_COLLECTION_NAME);
  }

  @Test
  public void calculateNumberOfAssignedSessionsForUser_Should_ReturnCorrectNumberOfSessions() {
    assertThat(
        statisticsEventRepository.calculateNumberOfAssignedSessionsForUser(
            CONSULTANT_ID, dateFromConverted, dateToConverted),
        is(3L));
  }

  @Test
  public void calculateNumbersOfSessionsWhereUserWasActive_Should_ReturnCorrectNumberOfSessions() {
    assertThat(
        statisticsEventRepository.calculateNumbersOfSessionsWhereUserWasActive(
            CONSULTANT_ID, dateFromConverted, dateToConverted),
        is(5L));
  }

  @Test
  public void calculateNumberOfSentMessagesForUser_Should_ReturnCorrectNumberOfMessages() {
    assertThat(
        statisticsEventRepository.calculateNumberOfSentMessagesForUser(
            CONSULTANT_ID, dateFromConverted, dateToConverted),
        is(4L));
  }

  @Test
  public void calculateTimeInVideoCallsForUser_Should_ReturnCorrectTime() {
    assertThat(
        statisticsEventRepository.calculateTimeInVideoCallsForUser(
            CONSULTANT_ID, dateFromConverted, dateToConverted),
        is(1800L));
  }
}
