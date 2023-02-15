package de.caritas.cob.statisticsservice.api.controller;

import de.caritas.cob.statisticsservice.api.authorization.StatisticsFeatureAuthorisationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticsFeatureAssertionAspect {

  private final @NonNull StatisticsFeatureAuthorisationService statisticsFeatureAuthorisationService;

  @Before(
      "execution(* de.caritas.cob.statisticsservice.api.controller.StatisticsController.*(..)))")
  public void beforeQueryAspect() {
    log.debug("Asserting statistics feature is enabled");
    statisticsFeatureAuthorisationService.assertStatisticsFeatureIsEnabled();
  }
}
