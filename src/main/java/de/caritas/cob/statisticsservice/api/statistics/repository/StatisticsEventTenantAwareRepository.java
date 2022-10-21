package de.caritas.cob.statisticsservice.api.statistics.repository;

import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface StatisticsEventTenantAwareRepository extends MongoRepository<StatisticsEvent, String> {

  @Query(value = "{'eventType': 'REGISTRATION', 'metaData.tenantId': ?0}")
  List<StatisticsEvent> getAllRegistrationStatistics(Long tenantId);

  @Query(value = "{'eventType': 'ARCHIVE_SESSION', 'metaData.tenantId': ?0}")
  List<StatisticsEvent> getAllArchiveSessionEvents(Long tenantId);
}
