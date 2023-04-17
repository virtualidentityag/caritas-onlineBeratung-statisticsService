package de.caritas.cob.statisticsservice.api.statistics.model;

import de.caritas.cob.statisticsservice.api.model.EventType;
import de.caritas.cob.statisticsservice.api.model.UserRole;
import de.caritas.cob.statisticsservice.api.service.UserStatisticsService;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEventBuilder;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.CreateMessageMetaData;
import de.caritas.cob.statisticsservice.userstatisticsservice.generated.web.model.SessionStatisticsResultDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;

import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.AGENCY_ID;
import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.CONSULTANT_ID;
import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.CONSULTING_TYPE_ID;
import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.RC_GROUP_ID;
import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.SESSION_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsEventBuilderTest {

  @Mock UserStatisticsService userStatisticsService;

  @Test(expected = NullPointerException.class)
  public void build_Should_ThrowNullPointerException_WhenEventTypeIsNull() {

    StatisticsEventBuilder builder =
        StatisticsEventBuilder.getInstance(
            () -> userStatisticsService.retrieveSessionViaSessionId(SESSION_ID));
    builder
        .withTimestamp(Instant.now())
        .withUserId(CONSULTANT_ID)
        .withUserRole(UserRole.CONSULTANT)
        .build();
  }

  @Test(expected = NullPointerException.class)
  public void build_Should_ThrowNullPointerException_WhenTimestampIsNull() {

    StatisticsEventBuilder builder =
        StatisticsEventBuilder.getInstance(
            () -> userStatisticsService.retrieveSessionViaSessionId(SESSION_ID));
    builder
        .withEventType(EventType.ASSIGN_SESSION)
        .withUserId(CONSULTANT_ID)
        .withUserRole(UserRole.CONSULTANT)
        .build();
  }

  @Test(expected = NullPointerException.class)
  public void build_Should_ThrowNullPointerException_WhenUserIdIsNull() {

    StatisticsEventBuilder builder =
        StatisticsEventBuilder.getInstance(
            () -> userStatisticsService.retrieveSessionViaSessionId(SESSION_ID));
    builder
        .withEventType(EventType.ASSIGN_SESSION)
        .withTimestamp(Instant.now())
        .withUserRole(UserRole.CONSULTANT)
        .build();
  }

  @Test(expected = NullPointerException.class)
  public void build_Should_ThrowNullPointerException_WhenUserRoleIsNull() {

    StatisticsEventBuilder builder =
        StatisticsEventBuilder.getInstance(
            () -> userStatisticsService.retrieveSessionViaSessionId(SESSION_ID));
    builder
        .withEventType(EventType.ASSIGN_SESSION)
        .withTimestamp(Instant.now())
        .withUserId(CONSULTANT_ID)
        .build();
  }

  @Test(expected = NullPointerException.class)
  public void build_Should_ThrowNullPointerException_WhenRetrievedSessionHasNoId() {

    SessionStatisticsResultDTO session = buildSessionStatisticsResultDto();
    session.id(null);

    when(userStatisticsService.retrieveSessionViaSessionId(SESSION_ID))
        .thenReturn(session);

    StatisticsEventBuilder builder =
        StatisticsEventBuilder.getInstance(
            () -> userStatisticsService.retrieveSessionViaSessionId(SESSION_ID));
    builder
        .withEventType(EventType.ASSIGN_SESSION)
        .withTimestamp(Instant.now())
        .withUserId(CONSULTANT_ID)
        .withUserRole(UserRole.CONSULTANT)
        .build();
  }

  @Test
  public void build_Should_Build_ValidStatisticEventsModel() {

    Instant timestamp = Instant.now();
    Object metaData = buildMetaData();
    EventType eventType = EventType.CREATE_MESSAGE;
    String userId = CONSULTANT_ID;
    UserRole userRole = UserRole.CONSULTANT;

    when(userStatisticsService.retrieveSessionViaSessionId(SESSION_ID))
        .thenReturn(buildSessionStatisticsResultDto());

    StatisticsEventBuilder builder =
        StatisticsEventBuilder.getInstance(
            () -> userStatisticsService.retrieveSessionViaSessionId(SESSION_ID));
    StatisticsEvent result = builder
        .withEventType(eventType)
        .withTimestamp(timestamp)
        .withUserId(userId)
        .withUserRole(userRole)
        .withMetaData(metaData)
        .build();

    assertThat(result.getEventType(), is(eventType));
    assertThat(result.getTimestamp(), is(timestamp));
    assertThat(result.getMetaData(), notNullValue());
    assertThat(result.getMetaData(), is(metaData));
    assertThat(result.getUser(), notNullValue());
    assertThat(result.getUser().getId(), is(userId));
    assertThat(result.getUser().getUserRole(), is(userRole));
    assertThat(result.getAgency(), notNullValue());
    assertThat(result.getAgency().getId(), is(AGENCY_ID));
    assertThat(result.getConsultingType(), notNullValue());
    assertThat(result.getConsultingType().getId(), is(CONSULTING_TYPE_ID));
  }

  @Test
  public void build_Should_RequestSessionFromUserService() {

    when(userStatisticsService.retrieveSessionViaSessionId(SESSION_ID))
        .thenReturn(buildSessionStatisticsResultDto());

    StatisticsEventBuilder builder =
        StatisticsEventBuilder.getInstance(
            () -> userStatisticsService.retrieveSessionViaSessionId(SESSION_ID));
    builder
        .withEventType(EventType.ASSIGN_SESSION)
        .withTimestamp(Instant.now())
        .withUserId(CONSULTANT_ID)
        .withUserRole(UserRole.CONSULTANT)
        .build();

    verify(userStatisticsService).retrieveSessionViaSessionId(SESSION_ID);
  }

  @Test
  public void buildShouldNotRequestSessionFromUserServiceOnStartVideoCallEvent() {
    Instant now = Instant.now();
    Object metaData = buildMetaData();

    var result = StatisticsEventBuilder.getInstance()
            .withEventType(EventType.START_VIDEO_CALL)
            .withTimestamp(now)
            .withUserId(CONSULTANT_ID)
            .withUserRole(UserRole.CONSULTANT)
            .withMetaData(metaData)
            .build();

    assertThat(result.getEventType(), is(EventType.START_VIDEO_CALL));
    assertThat(result.getTimestamp(), is(now));
    assertThat(result.getMetaData(), notNullValue());
    assertThat(result.getMetaData(), is(metaData));
    assertThat(result.getUser(), notNullValue());
    assertThat(result.getUser().getId(), is(CONSULTANT_ID));
    assertThat(result.getUser().getUserRole(), is(UserRole.CONSULTANT));
    assertThat(result.getAgency(), nullValue());
    assertThat(result.getConsultingType(), nullValue());

    verifyNoInteractions(userStatisticsService);
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildShouldIllegalArgExceptionOnMissingSessionAndNotStartVideoCallEvent() {
    StatisticsEventBuilder.getInstance()
            .withEventType(EventType.ASSIGN_SESSION)
            .withTimestamp(Instant.now())
            .withUserId(CONSULTANT_ID)
            .withUserRole(UserRole.CONSULTANT)
            .withMetaData(new Object())
            .build();
  }

  private SessionStatisticsResultDTO buildSessionStatisticsResultDto() {
    return new SessionStatisticsResultDTO()
        .id(SESSION_ID)
        .isTeamSession(false)
        .agencyId(AGENCY_ID)
        .consultingType(CONSULTING_TYPE_ID)
        .rcGroupId(RC_GROUP_ID);
  }

  private CreateMessageMetaData buildMetaData() {
    return CreateMessageMetaData
        .builder()
        .hasAttachment(true)
        .build();
  }
}
