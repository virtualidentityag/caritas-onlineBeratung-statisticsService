package de.caritas.cob.statisticsservice.api.exception.httpresponses;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "statistics disabled")
public class StatisticsDisabledException extends AccessDeniedException {

  public StatisticsDisabledException(String msg) {
    super(msg);
  }
}
