package com.helger.peppol.sk.tdd;

import java.util.UUID;

import com.helger.annotation.concurrent.Immutable;

/**
 * Peppol ViDA pilot TDD constants.
 *
 * @author Philip Helger
 * @since 0.1.4
 */
@Immutable
public final class CSKTDD
{
  /**
   * The following namespace is a type-4 UUID from the Peppol ViDA Pilot solution architecture
   */
  // TODO get correct UUID (same as ViDA pilot)
  public static final UUID PEPPOL_SK_NAMESPACE = UUID.fromString ("e0bc4ac8-b025-46e5-a76d-0c893fc3027e");

  private CSKTDD ()
  {}
}
