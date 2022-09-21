package de.caritas.cob.statisticsservice.api.authorization;

import java.util.stream.Stream;

/**
 *
 * Definition of all authorities and of the role-authority-mapping.
 *
 */
public enum Authority {

  CONSULTANT("consultant", AuthorityValue.CONSULTANT_DEFAULT),
  SINGLE_TENANT_ADMIN("single-tenant-admin", AuthorityValue.SINGLE_TENANT_ADMIN),
  TENANT_ADMIN("tenant-admin", AuthorityValue.TENANT_ADMIN);
  private final String roleName;
  private final String authorityName;

  Authority(final String roleName, final String authorityName) {
    this.roleName = roleName;
    this.authorityName = authorityName;
  }

  /**
   * Finds a {@link Authority} instance by given roleName.
   *
   * @param roleName the role name to search for
   * @return the {@link Authority} instance
   */
  public static Authority fromRoleName(String roleName) {
    return Stream.of(values())
        .filter(authority -> authority.roleName.equals(roleName))
        .findFirst()
        .orElse(null);
  }

  /**
   * Get all authorities for a specific role.
   *
   * @return the authorities for current role
   **/
  public String getAuthority() {
    return this.authorityName;
  }

  public static class AuthorityValue {

    private AuthorityValue() {
    }

    public static final String PREFIX = "AUTHORIZATION_";
    public static final String CONSULTANT_DEFAULT = PREFIX + "CONSULTANT_DEFAULT";
    public static final String SINGLE_TENANT_ADMIN = PREFIX + "SINGLE_TENANT_ADMIN";
    public static final String TENANT_ADMIN = PREFIX + "TENANT_ADMIN";
  }

}
