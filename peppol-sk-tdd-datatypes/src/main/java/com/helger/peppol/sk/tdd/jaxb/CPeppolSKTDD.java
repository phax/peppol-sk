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
package com.helger.peppol.sk.tdd.jaxb;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.io.resource.ClassPathResource;

/**
 * Contains all the constants for Peppol SK TDD handling.
 *
 * @author Philip Helger
 */
@Immutable
public final class CPeppolSKTDD
{
  @NonNull
  private static ClassLoader _getCL ()
  {
    return CPeppolSKTDD.class.getClassLoader ();
  }

  private static final String PATH_1_0_0 = "/external/schemas/2026-02-20/";
  /**
   * XML Schema resources for Peppol SK TDD XSD 1.0.0
   */
  public static final String TDD_XSD_1_0_0_PATH = PATH_1_0_0 + "sk-tdd-1.0.0.xsd";

  /**
   * XML Schema resources for Peppol SK TDD XSD 1.0.0
   */
  public static final ClassPathResource TDD_XSD_1_0_0 = new ClassPathResource (TDD_XSD_1_0_0_PATH, _getCL ());

  /** Namespace URI for Peppol SK TDD XSD 1.0.0 */
  public static final String TDD_XSD_1_0_0_NS = "urn:peppol:schema:sk-taxdata:1.0";

  @PresentForCodeCoverage
  private static final CPeppolSKTDD INSTANCE = new CPeppolSKTDD ();

  private CPeppolSKTDD ()
  {}
}
