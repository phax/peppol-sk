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
import com.helger.collection.commons.ICommonsList;
import com.helger.diver.api.coord.DVRCoordinate;
import com.helger.diver.api.version.DVRVersion;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.IReadableResource;
import com.helger.peppol.sk.tdd.jaxb.CPeppolSKTDD;
import com.helger.peppol.sk.tdd.jaxb.PeppolSKTDD100Marshaller;
import com.helger.peppol.sk.tdd.jaxb.PeppolSKTDD110Marshaller;
import com.helger.phive.api.execute.ValidationExecutionManager;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.api.executorset.ValidationExecutorSetRegistry;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.phive.api.validity.IValidityDeterminator;
import com.helger.phive.xml.executorset.VesXmlBuilder;
import com.helger.phive.xml.schematron.ValidationExecutorSchematronBuilder;
import com.helger.phive.xml.source.IValidationSourceXML;
import com.helger.phive.xml.source.ValidationSourceXML;
import com.helger.xml.namespace.MapBasedNamespaceContext;

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

  // Aligned with phive-rules
  public static final DVRCoordinate VID_TDD_SK_100 = new DVRCoordinate ("org.peppol.taxdata",
                                                                        "sk",
                                                                        DVRVersion.of (new Version (1, 0, 0)));
  private static final String PREFIX_100 = "external/schematron/2026-04-14/";
  public static final IReadableResource XSLT_CEN_TDD_100 = new ClassPathResource (PREFIX_100 + "CEN-EN16931-UBL.xslt",
                                                                                  _getCL ());
  public static final IReadableResource XSLT_BILLING_TDD_100 = new ClassPathResource (PREFIX_100 +
                                                                                      "PEPPOL-EN16931-UBL.xslt",
                                                                                      _getCL ());
  public static final IReadableResource XSLT_SK_TDD_100 = new ClassPathResource (PREFIX_100 +
                                                                                 "Peppol-Slovak Republic-TDD.xslt",
                                                                                 _getCL ());

  /**
   * VESID of Peppol SK TDD 1.1.0. Same XML Schema and CustomizationID as 1.0.0, but other
   * Schematron rules.
   *
   * @since 0.9.3
   */
  public static final DVRCoordinate VID_TDD_SK_110 = new DVRCoordinate ("org.peppol.taxdata",
                                                                        "sk",
                                                                        DVRVersion.of (new Version (1, 1, 0)));
  private static final String PREFIX_110 = "external/schematron/2026-11-20/";
  /** @since 0.9.3 */
  public static final IReadableResource XSLT_CEN_TDD_110 = new ClassPathResource (PREFIX_110 + "CEN-EN16931-UBL.xslt",
                                                                                  _getCL ());
  /** @since 0.9.3 */
  public static final IReadableResource XSLT_BILLING_TDD_110 = new ClassPathResource (PREFIX_110 +
                                                                                      "PEPPOL-EN16931-UBL.xslt",
                                                                                      _getCL ());
  /** @since 0.9.3 */
  public static final IReadableResource XSLT_SK_TDD_110 = new ClassPathResource (PREFIX_110 +
                                                                                 "Peppol-Slovak Republic-TDD.xslt",
                                                                                 _getCL ());

  public static final ValidationExecutorSetRegistry <IValidationSourceXML> VES_REGISTRY = new ValidationExecutorSetRegistry <> ();

  private static void _register (@NonNull final DVRCoordinate aVESID,
                                 @NonNull final String sDisplayName,
                                 @NonNull final ICommonsList <ClassPathResource> aXSDs,
                                 @NonNull final MapBasedNamespaceContext aNsCtx,
                                 @NonNull final IReadableResource aXsltCEN,
                                 @NonNull final IReadableResource aXsltBilling,
                                 @NonNull final IReadableResource aXsltSK)
  {
    VesXmlBuilder.builder ()
                 .vesID (aVESID)
                 .displayName (sDisplayName)
                 .notDeprecated ()
                 .addXSD (aXSDs)
                 .addSchematron (ValidationExecutorSchematronBuilder.xslt2 (aXsltCEN).namespaceContext (aNsCtx).build ())
                 .addSchematron (ValidationExecutorSchematronBuilder.xslt2 (aXsltBilling)
                                                                    .namespaceContext (aNsCtx)
                                                                    .build ())
                 .addSchematron (ValidationExecutorSchematronBuilder.xslt2 (aXsltSK).namespaceContext (aNsCtx).build ())
                 .registerInto (VES_REGISTRY);
  }

  static
  {
    _register (VID_TDD_SK_100,
               "Peppol SK TDD 1.0.0",
               PeppolSKTDD100Marshaller.getAllXSDs (),
               PeppolSKTDD100Marshaller.createNamespaceContext (),
               XSLT_CEN_TDD_100,
               XSLT_BILLING_TDD_100,
               XSLT_SK_TDD_100);
    _register (VID_TDD_SK_110,
               "Peppol SK TDD 1.1.0",
               PeppolSKTDD110Marshaller.getAllXSDs (),
               PeppolSKTDD110Marshaller.createNamespaceContext (),
               XSLT_CEN_TDD_110,
               XSLT_BILLING_TDD_110,
               XSLT_SK_TDD_110);
  }

  private PeppolSKTDDValidator ()
  {}

  @NonNull
  private static ValidationResultList _validate (@NonNull final DVRCoordinate aVESID,
                                                 @NonNull final IReadableResource aXmlRes)
  {
    final IValidationExecutorSet <IValidationSourceXML> aExecutors = VES_REGISTRY.getOfID (aVESID);
    final IValidationSourceXML aSource = ValidationSourceXML.create (aXmlRes);
    return ValidationExecutionManager.executeValidation (IValidityDeterminator.createDefault (),
                                                         aExecutors,
                                                         aSource,
                                                         Locale.US);
  }

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
    return _validate (VID_TDD_SK_100, aXmlRes);
  }

  /**
   * Validate against Schematron SK TDD v1.1.0 rules
   *
   * @param aXmlRes
   *        The XML resource to use. May not be <code>null</code>.
   * @return The Validation result list. Never <code>null</code>.
   * @since 0.9.3
   */
  @NonNull
  public static ValidationResultList validateSK_TDD_110 (@NonNull final IReadableResource aXmlRes)
  {
    return _validate (VID_TDD_SK_110, aXmlRes);
  }
}
