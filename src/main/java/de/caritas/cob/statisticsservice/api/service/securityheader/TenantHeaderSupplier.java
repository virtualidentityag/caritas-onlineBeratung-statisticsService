package de.caritas.cob.statisticsservice.api.service.securityheader;

import static java.lang.Boolean.TRUE;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantHeaderSupplier {

  private static final String TECHNICAL_TENANT_CONTEXT = "0";

  @Value("${multitenancy.enabled}")
  private Boolean multitenancy;

  public void addTechnicalTenantHeaderIfMultitenancyEnabled(HttpHeaders headers) {
    if (TRUE.equals(multitenancy)) {
      headers.add("tenantId", TECHNICAL_TENANT_CONTEXT);
    }
  }

  public Optional<Long> getTenantFromHeader() {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
            .getRequest();
    try {
      return Optional.of(Long.parseLong(request.getHeader("tenantId")));
    } catch (NumberFormatException exception) {
      log.debug("No tenantId provided via headers.");
      return Optional.empty();
    }
  }
}
