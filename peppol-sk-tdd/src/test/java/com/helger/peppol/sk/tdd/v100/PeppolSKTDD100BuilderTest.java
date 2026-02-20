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
package com.helger.peppol.sk.tdd.v100;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.time.ZoneOffset;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.numeric.BigHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.datetime.helper.PDTFactory;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.inmemory.ReadableResourceString;
import com.helger.jaxb.GenericJAXBMarshaller;
import com.helger.peppol.sk.tdd.codelist.ESKTDDDocumentScope;
import com.helger.peppol.sk.tdd.codelist.ESKTDDDocumentTypeCode;
import com.helger.peppol.sk.tdd.codelist.ESKTDDReporterRole;
import com.helger.peppol.sk.tdd.jaxb.PeppolSLTDD100Marshaller;
import com.helger.peppol.sk.tdd.testfiles.PeppolSKTestFiles;
import com.helger.peppol.sk.tdd.v2026_02_20.TaxDataType;
import com.helger.peppol.sk.tdd.validate.PeppolSKTDDValidator;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.SVRLMarshaller;
import com.helger.schematron.svrl.jaxb.FiredRule;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import com.helger.ubl21.UBL21Marshaller;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CustomizationIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test class for class {@link PeppolSKTDD100Builder}.
 *
 * @author Philip Helger
 */
public final class PeppolSKTDD100BuilderTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD100BuilderTest.class);

  @Test
  public void testBasicMinimal () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolSKTDDValidator.getSchematronSK_TDD_100 ();

    final TaxDataType aTDD = new PeppolSKTDD100Builder ().documentTypeCode (ESKTDDDocumentTypeCode.SUBMIT)
                                                         .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                         .reporterRole (ESKTDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         .taxAuthorityID ("XX")
                                                         // Provide all fields manually
                                                         .reportedTransaction (rt -> rt.customizationID ("urn:peppol:pint:billing-1@ae-1")
                                                                                       .profileID ("urn:peppol:bis:billing")
                                                                                       .id ("invoice-1")
                                                                                       .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                               Month.SEPTEMBER,
                                                                                                                               20))
                                                                                       .documentTypeCode ("380")
                                                                                       .documentCurrencyCode ("AED")
                                                                                       .sellerTaxID ("123456789")
                                                                                       .buyerTaxID ("987654321")
                                                                                       .taxTotalDocumentCurrency (x -> x.taxAmount (BigHelper.toBigDecimal (240)))
                                                                                       .lineExtensionAmount (BigHelper.toBigDecimal (1200))
                                                                                       .taxExclusiveTotalAmount (BigHelper.toBigDecimal (1200))
                                                                                       .taxInclusiveTotalAmount (BigHelper.toBigDecimal (1440))
                                                                                       .payableAmount (BigHelper.toBigDecimal (1440))
                                                                                       .addDocumentLine (x -> x.id ("1")
                                                                                                               .quantity (BigDecimal.TEN)
                                                                                                               .quantityUnit ("STK")
                                                                                                               .lineExtensionAmount (BigHelper.toBigDecimal (1200))
                                                                                                               .item (y -> y.name ("What")
                                                                                                                            .classifiedTaxCategory (z -> z.id ("X")
                                                                                                                                                          .taxSchemeID ("VAT")))
                                                                                                               .priceAmount (BigHelper.toBigDecimal (120))))
                                                         .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolSLTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (new ReadableResourceString (sXML,
                                                                                                            StandardCharsets.UTF_8));
    assertNotNull (aSVRL);
    assertTrue (aSVRL.getActivePatternAndFiredRuleAndFailedAssert ()
                     .stream ()
                     .filter (FiredRule.class::isInstance)
                     .map (FiredRule.class::cast)
                     .count () > 0);
    assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
  }

  @Test
  public void testBasicMaximal () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolSKTDDValidator.getSchematronSK_TDD_100 ();

    final TaxDataType aTDD = new PeppolSKTDD100Builder ().documentTypeCode (ESKTDDDocumentTypeCode.SUBMIT)
                                                         .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                         .reporterRole (ESKTDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         .taxAuthorityID ("XX")
                                                         // Provide all fields manually
                                                         .reportedTransaction (rt -> rt.customizationID ("urn:peppol:pint:billing-1@eu-1")
                                                                                       .profileID ("urn:peppol:bis:billing")
                                                                                       .id ("invoice-1")
                                                                                       .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                               Month.SEPTEMBER,
                                                                                                                               20))
                                                                                       .issueTime (PDTFactory.createOffsetTime (20,
                                                                                                                                8,
                                                                                                                                0,
                                                                                                                                ZoneOffset.UTC))
                                                                                       .documentTypeCode ("380")
                                                                                       .documentCurrencyCode ("AED")
                                                                                       .taxCurrencyCode ("EUR")
                                                                                       .sellerTaxID ("123456789")
                                                                                       .sellerCountryCode ("DE")
                                                                                       .buyerTaxID ("987654321")
                                                                                       .buyerCountryCode ("AT")
                                                                                       .taxRepresentativeID ("any123")
                                                                                       .taxRepresentativeCountryCode ("CH")
                                                                                       .taxTotalDocumentCurrency (x -> x.taxAmount (BigHelper.toBigDecimal (200)))
                                                                                       .taxTotalTaxCurrency (x -> x.taxAmount (BigHelper.toBigDecimal (500)))
                                                                                       .lineExtensionAmount (BigHelper.toBigDecimal (1200))
                                                                                       .taxExclusiveTotalAmount (BigHelper.toBigDecimal (1200))
                                                                                       .taxInclusiveTotalAmount (BigHelper.toBigDecimal (1700))
                                                                                       .allowanceTotalAmount (BigDecimal.ZERO)
                                                                                       .chargeTotalAmount (BigDecimal.ZERO)
                                                                                       .payableRoundingAmount (BigDecimal.ZERO)
                                                                                       .payableAmount (BigHelper.toBigDecimal (1700))
                                                                                       .addDocumentLine (x -> x.id ("1")
                                                                                                               .quantity (BigDecimal.TEN)
                                                                                                               .quantityUnit ("STK")
                                                                                                               .lineExtensionAmount (BigHelper.toBigDecimal (1200))
                                                                                                               .item (y -> y.name ("What")
                                                                                                                            .classifiedTaxCategory (z -> z.id ("X")
                                                                                                                                                          .taxSchemeID ("VAT")))
                                                                                                               .priceAmount (BigHelper.toBigDecimal (120))))
                                                         .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolSLTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (new ReadableResourceString (sXML,
                                                                                                            StandardCharsets.UTF_8));
    assertNotNull (aSVRL);
    assertTrue (aSVRL.getActivePatternAndFiredRuleAndFailedAssert ()
                     .stream ()
                     .filter (FiredRule.class::isInstance)
                     .map (FiredRule.class::cast)
                     .count () > 0);

    if (false)
      LOGGER.info (new SVRLMarshaller ().setFormattedOutput (true).getAsString (aSVRL));

    assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
  }

  @Test
  public void testCreateFromAllInvoices () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolSKTDDValidator.getSchematronSK_TDD_100 ();

    for (final ClassPathResource aRes : PeppolSKTestFiles.getAllGoodBillingInvoiceFiles ())
    {
      LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

      final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
      assertNotNull (aInvoice);

      final TaxDataType aTDD = new PeppolSKTDD100Builder ().documentTypeCode (ESKTDDDocumentTypeCode.SUBMIT)
                                                           .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                           .reporterRole (ESKTDDReporterRole.SENDER)
                                                           .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                           .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                           .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                           .taxAuthorityID ("XX")
                                                           // Read from pre-parsed UBL Invoice
                                                           .reportedTransaction (rt -> rt.initFromInvoice (aInvoice))
                                                           .build ();
      assertNotNull (aTDD);

      // Serialize
      final GenericJAXBMarshaller <TaxDataType> m = new PeppolSLTDD100Marshaller ().setFormattedOutput (true);
      final String sXML = m.getAsString (aTDD);
      assertNotNull (sXML);

      if (false)
        LOGGER.info (sXML);

      // Schematron validation
      final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (new ReadableResourceString (sXML,
                                                                                                              StandardCharsets.UTF_8));
      assertNotNull (aSVRL);
      assertTrue (aSVRL.getActivePatternAndFiredRuleAndFailedAssert ()
                       .stream ()
                       .filter (FiredRule.class::isInstance)
                       .map (FiredRule.class::cast)
                       .count () > 0);
      if (false)
        LOGGER.info (new SVRLMarshaller ().setFormattedOutput (true).getAsString (aSVRL));
      assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
    }
  }

  @Test
  public void testCreateFromAllCreditNotes () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolSKTDDValidator.getSchematronSK_TDD_100 ();

    for (final ClassPathResource aRes : PeppolSKTestFiles.getAllGoodBillingCreditNoteFiles ())
    {
      LOGGER.info ("Converting CreditNote '" + aRes.getPath () + "' to a TDD");

      final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ().read (aRes);
      assertNotNull (aCreditNote);

      final TaxDataType aTDD = new PeppolSKTDD100Builder ().documentTypeCode (ESKTDDDocumentTypeCode.SUBMIT)
                                                           .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                           .reporterRole (ESKTDDReporterRole.SENDER)
                                                           .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                           .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                           .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                           .taxAuthorityID ("XX")
                                                           // Read from pre-parsed UBL CreditNote
                                                           .reportedTransaction (rt -> rt.initFromCreditNote (aCreditNote))
                                                           .build ();
      assertNotNull (aTDD);

      // Serialize and XSD validate
      final GenericJAXBMarshaller <TaxDataType> m = new PeppolSLTDD100Marshaller ().setFormattedOutput (true);
      final String sXML = m.getAsString (aTDD);
      assertNotNull (sXML);

      if (false)
        LOGGER.info (sXML);

      // Schematron validation
      final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (new ReadableResourceString (sXML,
                                                                                                              StandardCharsets.UTF_8));
      assertNotNull (aSVRL);
      assertTrue (aSVRL.getActivePatternAndFiredRuleAndFailedAssert ()
                       .stream ()
                       .filter (FiredRule.class::isInstance)
                       .map (FiredRule.class::cast)
                       .count () > 0);
      assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
    }
  }

  @Test
  public void testCreateFailedInvoiceWithReportedDocument () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolSKTDDValidator.getSchematronSK_TDD_100 ();

    final ClassPathResource aRes = PeppolSKTestFiles.getAllGoodBillingInvoiceFiles ().getFirstOrNull ();
    LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

    final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
    assertNotNull (aInvoice);

    final TaxDataType aTDD = new PeppolSKTDD100Builder ().documentTypeCode (ESKTDDDocumentTypeCode.DISREGARD)
                                                         .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                         .reporterRole (ESKTDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         .taxAuthorityID ("XX")
                                                         // It's not really an invalid invoice
                                                         .reportedTransaction (rt -> rt.initFromInvoice (aInvoice))
                                                         .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolSLTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    assertTrue (sXML.contains ("<pxs:ReportedDocument>"));

    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (new ReadableResourceString (sXML,
                                                                                                            StandardCharsets.UTF_8));
    assertNotNull (aSVRL);
    assertTrue (aSVRL.getActivePatternAndFiredRuleAndFailedAssert ()
                     .stream ()
                     .filter (FiredRule.class::isInstance)
                     .map (FiredRule.class::cast)
                     .count () > 0);
    assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
  }

  @Test
  @Ignore ("Not supported by v1.0.0")
  public void testCreateFailedInvoiceWithoutReportedDocument () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolSKTDDValidator.getSchematronSK_TDD_100 ();

    final ClassPathResource aRes = PeppolSKTestFiles.getAllGoodBillingInvoiceFiles ().getFirstOrNull ();
    LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

    final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
    assertNotNull (aInvoice);

    // Explicitly sets a "null" CustomizationID to indicate an invalid source message
    aInvoice.setCustomizationID ((CustomizationIDType) null);
    // This one is special, because it is an XSD mandatory fields
    aInvoice.setID ((IDType) null);

    final TaxDataType aTDD = new PeppolSKTDD100Builder ().documentTypeCode (ESKTDDDocumentTypeCode.DISREGARD)
                                                         .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                         .reporterRole (ESKTDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         .taxAuthorityID ("XX")
                                                         // This Invoice is really broken
                                                         .reportedTransaction (rt -> rt.initFromInvoice (aInvoice))
                                                         .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolSLTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    assertFalse (sXML.contains ("<pxs:ReportedDocument>"));

    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (new ReadableResourceString (sXML,
                                                                                                            StandardCharsets.UTF_8));
    assertNotNull (aSVRL);
    assertTrue (aSVRL.getActivePatternAndFiredRuleAndFailedAssert ()
                     .stream ()
                     .filter (FiredRule.class::isInstance)
                     .map (FiredRule.class::cast)
                     .count () > 0);
    assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
  }

  @Test
  public void testReadBadPayloads () throws Exception
  {
    final ISchematronResource aSCHRes = PeppolSKTDDValidator.getSchematronSK_TDD_100 ();

    for (final ClassPathResource aRes : PeppolSKTestFiles.getAllPayloadBadTDD100Files ())
    {
      LOGGER.info ("Reading Bad Payload TDD '" + aRes.getPath () + "'");

      final TaxDataType aTDD = new PeppolSLTDD100Marshaller ().read (aRes);
      assertNotNull (aTDD);

      // Serialize
      final String sXML = new PeppolSLTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
      assertNotNull (sXML);
      assertFalse (sXML.contains ("<pxs:ReportedDocument>"));

      if (false)
        LOGGER.info (sXML);

      // Schematron validation
      final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (new ReadableResourceString (sXML,
                                                                                                              StandardCharsets.UTF_8));
      assertNotNull (aSVRL);
      assertTrue (aSVRL.getActivePatternAndFiredRuleAndFailedAssert ()
                       .stream ()
                       .filter (FiredRule.class::isInstance)
                       .map (FiredRule.class::cast)
                       .count () > 0);
      assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
    }
  }
}
