package de.caritas.cob.statisticsservice.api.statistics.service;

import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.AGENCY_ID;
import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.ASKER_ID;
import static de.caritas.cob.statisticsservice.api.testhelper.TestConstants.CONSULTING_TYPE_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.statisticsservice.api.helper.RegistrationStatisticsDTOConverter;
import de.caritas.cob.statisticsservice.api.model.EventType;
import de.caritas.cob.statisticsservice.api.model.UserRole;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.Agency;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.ConsultingType;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.User;
import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.meta.RegistrationMetaData;
import de.caritas.cob.statisticsservice.api.statistics.repository.StatisticsEventRepository;
import de.caritas.cob.statisticsservice.api.statistics.repository.StatisticsEventTenantAwareRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationStatisticsServiceTest {

  RegistrationStatisticsService registrationStatisticsService;
  @Mock
  StatisticsEventRepository statisticsEventRepository;

  @Mock
  StatisticsEventTenantAwareRepository statisticsEventTenantAwareRepository;
  RegistrationStatisticsDTOConverter registrationStatisticsDTOConverter;

  @Before
  public void setup() {

    registrationStatisticsDTOConverter = new RegistrationStatisticsDTOConverter();
    registrationStatisticsService = new RegistrationStatisticsService(statisticsEventRepository,
        statisticsEventTenantAwareRepository,
        registrationStatisticsDTOConverter);
    ReflectionTestUtils.setField(registrationStatisticsService, "multitenancyEnabled", false);
  }

  @Test
  public void fetchRegistrationStatisticsData_Should_RetrieveRegistrationStatisticsDataViaRepository() {
    registrationStatisticsService.fetchRegistrationStatisticsData();

    verify(statisticsEventRepository, times(1))
        .getAllRegistrationStatistics();
  }

  @Test
  public void fetchRegistrationStatisticsData_Should_RetrieveExpectedData_When_matchingStatisticsAreAvailable() {
    List<StatisticsEvent> testData = new ArrayList<>();
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
    testData.add(StatisticsEvent.builder()
        .eventType(EventType.REGISTRATION)
        .user(User.builder().userRole(UserRole.ASKER).id(ASKER_ID).build())
        .consultingType(ConsultingType.builder().id(CONSULTING_TYPE_ID).build())
        .agency(Agency.builder().id(AGENCY_ID).build())
        .timestamp(Instant.now())
        .metaData(metaData)
        .build()
    );
    when(this.statisticsEventRepository.getAllRegistrationStatistics()).thenReturn(testData);

    var result = registrationStatisticsService.fetchRegistrationStatisticsData();

    assertThat(result.getRegistrationStatistics().get(0).getUserId(), is(ASKER_ID));
    assertThat(result.getRegistrationStatistics().get(0).getRegistrationDate(),
        is("2022-09-15T09:14:45Z"));
    assertThat(result.getRegistrationStatistics().get(0).getAge(), is(26));
    assertThat(result.getRegistrationStatistics().get(0).getGender(), is("FEMALE"));
    assertThat(result.getRegistrationStatistics().get(0).getMainTopicInternalAttribute(),
        is("alk"));
    assertThat(result.getRegistrationStatistics().get(0).getTopicsInternalAttributes(),
        is(List.of("alk", "drogen")));
    assertThat(result.getRegistrationStatistics().get(0).getPostalCode(), is("12345"));
    assertThat(result.getRegistrationStatistics().get(0).getCounsellingRelation(),
        is("SELF_COUNSELLING"));
  }
}