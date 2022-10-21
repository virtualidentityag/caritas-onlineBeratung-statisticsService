package de.caritas.cob.statisticsservice.api.helper;

import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.AGENCY_ID;
import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.ASKER_ID;
import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.CONSULTING_TYPE_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import de.caritas.cob.statisticsservice.api.model.EventType;
import de.caritas.cob.statisticsservice.api.model.RegistrationStatisticsResponseDTO;
import de.caritas.cob.statisticsservice.api.model.UserRole;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.Agency;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.ConsultingType;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.User;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.ArchiveMetaData;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.RegistrationMetaData;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrationStatisticsDTOConverterTest {

  @InjectMocks
  RegistrationStatisticsDTOConverter registrationStatisticsDTOConverter;

  private StatisticsEvent testEvent;

  private List<StatisticsEvent> archiveSessionEvents;

  @AfterEach
  void teardownEach() {
    testEvent = null;
    archiveSessionEvents = null;
  }

  @Test
  void convertStatisticsEvent_Should_convertToRegistrationStatisticResponse() {
    // given
    givenValidStatisticEvent(1L);

    // when
    RegistrationStatisticsResponseDTO result = registrationStatisticsDTOConverter.convertStatisticsEvent(
        testEvent, null);

    // then
    assertThat(result.getUserId(), is(ASKER_ID));
    assertThat(result.getRegistrationDate(),
        is("2022-09-15T09:14:45Z"));
    assertThat(result.getAge(), is(26));
    assertThat(result.getGender(), is("FEMALE"));
    assertThat(result.getMainTopicInternalAttribute(),
        is("alk"));
    assertThat(result.getTopicsInternalAttributes(),
        is(List.of("alk", "drogen")));
    assertThat(result.getPostalCode(), is("12345"));
    assertThat(result.getCounsellingRelation(),
        is("SELF_COUNSELLING"));
  }

  @Test
  void convertStatisticsEvent_Should_notFail_When_archiveSessionEventsAreNull() {
    // given
    givenValidStatisticEvent(1L);

    // when
    RegistrationStatisticsResponseDTO result = registrationStatisticsDTOConverter.convertStatisticsEvent(
        testEvent, null);

    // then
    assertThat(result.getEndDate(), is(nullValue()));
  }

  @Test
  void convertStatisticsEvent_Should_addNewestArchiveSessionEndDate_When_multipleArchiveSessionEventsAreAvailable() {
    // given
    givenValidStatisticEvent(1L);
    givenValidArchiveStatisticEvents();

    // when
    RegistrationStatisticsResponseDTO result = registrationStatisticsDTOConverter.convertStatisticsEvent(
        testEvent, archiveSessionEvents);

    // then
    assertThat(result.getEndDate(), is("2 end date for session 1"));
  }

  @Test
  void convertStatisticsEvent_Should_addArchiveSessionEndDate_When_onlyOneArchiveSessionEventIsAvailable() {
    // given
    givenValidStatisticEvent(2L);
    givenValidArchiveStatisticEvents();

    // when
    RegistrationStatisticsResponseDTO result = registrationStatisticsDTOConverter.convertStatisticsEvent(
        testEvent, archiveSessionEvents);

    // then
    assertThat(result.getEndDate(), is("end date for session 2"));
  }

  @Test
  void convertStatisticsEvent_Should_notAddArchiveSessionEndDate_When_noMatchingArchiveSessionEventIsAvailable() {
    // given
    givenValidStatisticEvent(99L);
    givenValidArchiveStatisticEvents();

    // when
    RegistrationStatisticsResponseDTO result = registrationStatisticsDTOConverter.convertStatisticsEvent(
        testEvent, archiveSessionEvents);

    // then
    assertThat(result.getEndDate(), is(nullValue()));
  }

  private void givenValidStatisticEvent(Long sessionId) {
    Object metaData = RegistrationMetaData.builder()
        .registrationDate("2022-09-15T09:14:45Z")
        .age(26)
        .gender("FEMALE")
        .mainTopicInternalAttribute("alk")
        .topicsInternalAttributes(List.of("alk", "drogen"))
        .postalCode("12345")
        .tenantId(1L)
        .counsellingRelation("SELF_COUNSELLING")
        .build();
    testEvent = StatisticsEvent.builder()
        .sessionId(sessionId)
        .eventType(EventType.REGISTRATION)
        .user(User.builder().userRole(UserRole.ASKER).id(ASKER_ID).build())
        .consultingType(ConsultingType.builder().id(CONSULTING_TYPE_ID).build())
        .agency(Agency.builder().id(AGENCY_ID).build())
        .timestamp(Instant.now())
        .metaData(metaData)
        .build();
  }

  private void givenValidArchiveStatisticEvents() {
    archiveSessionEvents = List.of(archiveEvent(1L, "2022-10-17T10:00:00.00Z", "1 end date for session 1"),
        archiveEvent(1L, "2022-10-18T10:00:00.00Z", "2 end date for session 1"),
        archiveEvent(2L, "2022-10-18T10:00:00.00Z", "end date for session 2"),
        archiveEvent(999L, "2022-10-19T10:00:00.00Z", "dummy end date"));
  }

  private StatisticsEvent archiveEvent(Long sessionId, String timestampString, String endDate) {
    Object metaData = ArchiveMetaData.builder().endDate(endDate).build();
    return StatisticsEvent.builder().timestamp(Instant.parse(timestampString)).sessionId(sessionId).metaData(metaData).build();
  }
}
