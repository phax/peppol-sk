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
package com.helger.peppol.sk.tdd.validate;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.exception.InitializationException;
import com.helger.peppol.sk.tdd.jaxb.CPeppolSKTDD;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.sch.SchematronResourceSCH;

/**
 * This class contains the Schematron resources for validating Peppol SK TDD documents.
 *
 * @author Philip Helger
 */
@Immutable
public final class PeppolSKTDDValidator
{
  @NonNull
  private static ClassLoader _getCL ()
  {
    return CPeppolSKTDD.class.getClassLoader ();
  }

  public static final String SCH_SK_TDD_100_PATH = "external/schematron/2026-02-20/Peppol-Slovak Republic-TDD.sch";

  private static final ISchematronResource SK_TDD_100 = SchematronResourceSCH.fromClassPath (SCH_SK_TDD_100_PATH,
                                                                                               _getCL ());

  static
  {
    for (final ISchematronResource aSch : new ISchematronResource [] { SK_TDD_100 })
      if (!aSch.isValidSchematron ())
        throw new InitializationException ("Schematron in " + aSch.getResource ().getPath () + " is invalid");
  }

  private PeppolSKTDDValidator ()
  {}

  /**
   * @return Schematron SK TDD v1.0.0
   */
  @NonNull
  public static ISchematronResource getSchematronSK_TDD_100 ()
  {
    return SK_TDD_100;
  }
}
