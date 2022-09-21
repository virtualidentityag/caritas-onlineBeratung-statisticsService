package de.caritas.cob.statisticsservice.api.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {
  TECHNICAL("technical"),
  TENANT_ADMIN("tenant-admin");

  private final String value;
}
