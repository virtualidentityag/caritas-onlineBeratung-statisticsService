package de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent;

import de.caritas.cob.statisticsservice.api.model.EventType;
import de.caritas.cob.statisticsservice.api.model.UserRole;
import de.caritas.cob.statisticsservice.userstatisticsservice.generated.web.model.SessionStatisticsResultDTO;

import java.time.Instant;
import java.util.function.Supplier;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/** Builder for a {@link StatisticsEvent} instance. */
public class StatisticsEventBuilder {

  private final Supplier<SessionStatisticsResultDTO> sessionSupplier;
  private EventType eventType;
  private Instant timestamp;
  private String userId;
  private UserRole userRole;
  private Object metaData;

  private StatisticsEventBuilder() {
    sessionSupplier = null;
  }

  private StatisticsEventBuilder(Supplier<SessionStatisticsResultDTO> sessionSupplier) {
    this.sessionSupplier = sessionSupplier;
  }

  /**
   * Creates the {@link StatisticsEventBuilder} instance.
   *
   * @param sessionSupplier A (@link {@link Supplier} for a {@link SessionStatisticsResultDTO}
   *     instance
   * @return a instance of {@link StatisticsEventBuilder}
   */
  public static StatisticsEventBuilder getInstance(
      Supplier<SessionStatisticsResultDTO> sessionSupplier) {
    return new StatisticsEventBuilder(sessionSupplier);
  }

  public static StatisticsEventBuilder getInstance() {
    return new StatisticsEventBuilder();
  }

  /**
   * Sets the {@link EventType}.
   *
   * @param eventType the {@link EventType}
   * @return the current {@link StatisticsEventBuilder}
   */
  public StatisticsEventBuilder withEventType(EventType eventType) {
    this.eventType = eventType;
    return this;
  }

  /**
   * Sets the timestamp of the event.
   *
   * @param timestamp the timestamp as {@link Instant}
   * @return the current {@link StatisticsEventBuilder}
   */
  public StatisticsEventBuilder withTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Sets the user id.
   *
   * @param userId the id of the user to whom the event should be assigned
   * @return the current {@link StatisticsEventBuilder}
   */
  public StatisticsEventBuilder withUserId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Sets the {@link UserRole}.
   *
   * @param userRole the {@link UserRole} of the user to whom the event should be assigned
   * @return the current {@link StatisticsEventBuilder}
   */
  public StatisticsEventBuilder withUserRole(UserRole userRole) {
    this.userRole = userRole;
    return this;
  }

  /**
   * Sets the additional meta data. It must be a serializable {@link Object}.
   *
   * @param metaData additional meta data informationen for the event
   * @return the current {@link StatisticsEventBuilder}
   */
  public StatisticsEventBuilder withMetaData(Object metaData) {
    this.metaData = metaData;
    return this;
  }

  /**
   * Generates the {@link StatisticsEvent} instance with additional information from the session,
   * etc.
   *
   * @return the {@link StatisticsEvent} instance.
   */
  public StatisticsEvent build() {

    validateAttributes();

    var eventBuilder = StatisticsEvent.builder()
            .eventType(eventType)
            .timestamp(timestamp)
            .user(buildUser())
            .metaData(metaData);

    if (isNull(sessionSupplier)) {
      if (eventType != EventType.START_VIDEO_CALL) {
        throw new IllegalArgumentException("Mandatory session of event type " + eventType + " missing.");
      }
    } else {
      var session = sessionSupplier.get();
      requireNonNull(session.getId());
      eventBuilder
              .sessionId(session.getId())
              .consultingType(buildConsultingType(session))
              .agency(buildAgency(session));
    }

    return eventBuilder.build();
  }

  private void validateAttributes() {
    requireNonNull(this.eventType);
    requireNonNull(this.timestamp);
    requireNonNull(this.userId);
    requireNonNull(this.userRole);
  }

  private Agency buildAgency(SessionStatisticsResultDTO session) {
    requireNonNull(session.getAgencyId());
    return Agency
        .builder()
        .id(session.getAgencyId()).build();
  }

  private ConsultingType buildConsultingType(SessionStatisticsResultDTO session) {
    requireNonNull(session.getConsultingType());
    return ConsultingType
        .builder()
        .id(session.getConsultingType())
        .build();
  }

  private User buildUser() {
    return User
        .builder()
        .userRole(userRole)
        .id(this.userId)
        .build();
  }
}
