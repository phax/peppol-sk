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

import java.util.Locale;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.version.Version;
import com.helger.diver.api.coord.DVRCoordinate;
import com.helger.diver.api.version.DVRVersion;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.IReadableResource;
import com.helger.peppol.sk.tdd.jaxb.CPeppolSKTDD;
import com.helger.peppol.sk.tdd.jaxb.PeppolSKTDD100Marshaller;
import com.helger.phive.api.execute.ValidationExecutionManager;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.api.executorset.ValidationExecutorSet;
import com.helger.phive.api.executorset.ValidationExecutorSetRegistry;
import com.helger.phive.api.executorset.status.ValidationExecutorSetStatus;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.phive.api.validity.IValidityDeterminator;
import com.helger.phive.xml.schematron.ValidationExecutorSchematron;
import com.helger.phive.xml.source.IValidationSourceXML;
import com.helger.phive.xml.source.ValidationSourceXML;
import com.helger.phive.xml.xsd.ValidationExecutorXSD;
import com.helger.ubl21.UBL21NamespaceContext;

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

  public static final DVRCoordinate VID_TDD_SK_100 = new DVRCoordinate ("org.peppol.sk",
                                                                        "tdd",
                                                                        DVRVersion.of (new Version (1, 0, 0)));
  private static final String PREFIX_100 = "external/schematron/2026-03-02/";
  public static final IReadableResource XSLT_CEN_TDD_100 = new ClassPathResource (PREFIX_100 + "CEN-EN16931-UBL.xslt",
                                                                                  _getCL ());
  public static final IReadableResource XSLT_BILLING_TDD_100 = new ClassPathResource (PREFIX_100 +
                                                                                      "PEPPOL-EN16931-UBL.xslt",
                                                                                      _getCL ());
  public static final IReadableResource XSLT_SK_TDD_100 = new ClassPathResource (PREFIX_100 +
                                                                                 "Peppol-Slovak Republic-TDD-PH.xslt",
                                                                                 _getCL ());

  public static final ValidationExecutorSetRegistry <IValidationSourceXML> VES_REGISTRY = new ValidationExecutorSetRegistry <> ();

  static
  {
    VES_REGISTRY.registerValidationExecutorSet (ValidationExecutorSet.create (VID_TDD_SK_100,
                                                                              "Peppol SK TDD 1.0.0",
                                                                              ValidationExecutorSetStatus.createDeprecatedNow (false),
                                                                              ValidationExecutorXSD.create (PeppolSKTDD100Marshaller.getAllXSDs ()),
                                                                              ValidationExecutorSchematron.createXSLT (XSLT_CEN_TDD_100,
                                                                                                                       UBL21NamespaceContext.getInstance ()),
                                                                              ValidationExecutorSchematron.createXSLT (XSLT_BILLING_TDD_100,
                                                                                                                       UBL21NamespaceContext.getInstance ()),
                                                                              ValidationExecutorSchematron.createXSLT (XSLT_SK_TDD_100,
                                                                                                                       UBL21NamespaceContext.getInstance ())));
  }

  private PeppolSKTDDValidator ()
  {}

  /**
   * Validate against Schematron SK TDD v1.0.0 rules
   *
   * @param aXmlRes
   *        The XML resource to use. May not be <code>null</code>.
   * @return The Validation result list. Never <code>null</code>.
   */
  @NonNull
  public static ValidationResultList validateSK_TDD_100 (@NonNull final IReadableResource aXmlRes)
  {
    final IValidationExecutorSet <IValidationSourceXML> aExecutors = VES_REGISTRY.getOfID (VID_TDD_SK_100);
    final IValidationSourceXML aSource = ValidationSourceXML.create (aXmlRes);
    return ValidationExecutionManager.executeValidation (IValidityDeterminator.createDefault (),
                                                         aExecutors,
                                                         aSource,
                                                         Locale.US);
  }
}
