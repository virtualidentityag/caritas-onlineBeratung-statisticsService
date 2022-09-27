package de.caritas.cob.statisticsservice.api.statistics.service;

import static java.util.Objects.nonNull;

import de.caritas.cob.statisticsservice.api.exception.httpresponses.BadRequestException;
import de.caritas.cob.statisticsservice.api.helper.AuthenticatedUser;
import de.caritas.cob.statisticsservice.api.model.ConsultantStatisticsResponseDTO;
import de.caritas.cob.statisticsservice.api.statistics.repository.StatisticsEventRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for statistical requests.
 */
@Service
@RequiredArgsConstructor
public class StatisticsService {

  private final @NonNull StatisticsEventRepository statisticsEventRepository;
  private final @NonNull AuthenticatedUser authenticatedUser;

  /**
   * Returns statistical data for a consultant.
   *
   * @param dateFrom the start date of the period
   * @param dateTo   the end date of the period
   * @return a {@link ConsultantStatisticsResponseDTO} instance with the statistical data.
   */
  public ConsultantStatisticsResponseDTO fetchStatisticsData(LocalDate dateFrom, LocalDate dateTo) {
    validateDates(dateFrom, dateTo);
    return buildResponseDTO(dateFrom, dateTo);
  }

  private void validateDates(LocalDate dateFrom, LocalDate dateTo) {
    if (dateTo.isBefore(dateFrom)) {
      throw new BadRequestException("dateFrom must be before dateTo");
    }
  }

  private ConsultantStatisticsResponseDTO buildResponseDTO(LocalDate dateFrom, LocalDate dateTo) {

    Instant dateFromConverted = convertDateWithMinimumTimeAndUtc(dateFrom);
    Instant dateToConverted = convertDateWithMaximumTimeAndUtc(dateTo);

    return new ConsultantStatisticsResponseDTO()
        .numberOfAssignedSessions(statisticsEventRepository
            .calculateNumberOfAssignedSessionsForUser(
                authenticatedUser.getUserId(),
                dateFromConverted,
                dateToConverted))
        .numberOfSentMessages(statisticsEventRepository
            .calculateNumberOfSentMessagesForUser(
                authenticatedUser.getUserId(),
                dateFromConverted,
                dateToConverted))
        .numberOfSessionsWhereConsultantWasActive(
            extractActiveNumberOfSessions(dateFromConverted, dateToConverted))
        .videoCallDuration(extractVideoCallDuration(dateFromConverted, dateToConverted))
        .numberOfAppointments(extractNumberOfDoneAppointments(dateFromConverted, dateToConverted))
        .startDate(dateFrom)
        .endDate(dateTo);
  }

  private long extractActiveNumberOfSessions(Instant dateFromConverted, Instant dateToConverted) {
    var result = statisticsEventRepository
        .calculateNumbersOfSessionsWhereUserWasActive(
            authenticatedUser.getUserId(),
            dateFromConverted,
            dateToConverted);
    return nonNull(result) ? result.getTotalCount() : 0L;
  }

  private long extractNumberOfDoneAppointments(Instant dateFromConverted, Instant dateToConverted) {
    var result = statisticsEventRepository
        .calculateNumbersOfDoneAppointments(
            authenticatedUser.getUserId(),
            dateFromConverted,
            dateToConverted,
            Instant.now());
    return nonNull(result) ? result.getTotalCount() : 0L;
  }

  private long extractVideoCallDuration(Instant dateFromConverted, Instant dateToConverted) {
    var result = statisticsEventRepository
        .calculateTimeInVideoCallsForUser(
            authenticatedUser.getUserId(),
            dateFromConverted,
            dateToConverted);
    return nonNull(result) ? result.getTotal() : 0L;
  }

  private Instant convertDateWithMinimumTimeAndUtc(LocalDate dateFrom) {
    return OffsetDateTime.of(dateFrom, LocalTime.MIN, ZoneOffset.UTC).toInstant();
  }

  private Instant convertDateWithMaximumTimeAndUtc(LocalDate dateTo) {
    return OffsetDateTime.of(dateTo, LocalTime.MAX, ZoneOffset.UTC).toInstant();
  }

}
