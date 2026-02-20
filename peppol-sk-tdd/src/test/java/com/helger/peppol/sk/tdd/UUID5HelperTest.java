/*
 * Copyright (C) 2026 Philip Helger
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol.sk.tdd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

/**
 * Test class for class {@link UUID5Helper}
 *
 * @author Philip Helger
 */
public final class UUID5HelperTest
{
  @Test
  public void testSpecificCase1 ()
  {
    final UUID aNSUUID = UUID.fromString ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    assertNotNull (aNSUUID);
    final UUID aUUID5 = UUID5Helper.fromUTF8 (aNSUUID, "380 33445566 2026-01-13 5060012349998 0088");
    assertNotNull (aUUID5);
    assertEquals ("8aeba72d-2253-57fe-86f0-7c35648eb808", aUUID5.toString ());
  }

  @Test
  public void testSpecificCase2 ()
  {
    final UUID aNSUUID = UUID.fromString ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    assertNotNull (aNSUUID);
    final UUID aUUID5 = UUID5Helper.fromUTF8 (aNSUUID, "0088 5060012349998 380 33445566 2026-01-13");
    assertNotNull (aUUID5);
    assertEquals ("3fd00464-9f15-52ce-8cdc-6dd374d5ad42", aUUID5.toString ());
  }

  @Test
  public void testSpecificCase2a ()
  {
    final UUID aUUID5 = UUID5Helper.fromUTF8 (UUID5Helper.PEPPOL_SK_NAMESPACE,
                                              "0088 5060012349998 380 33445566 2026-01-13");
    assertNotNull (aUUID5);
    assertEquals ("1780de4f-a87c-50cc-9d8a-f982abe36912", aUUID5.toString ());
  }
}
