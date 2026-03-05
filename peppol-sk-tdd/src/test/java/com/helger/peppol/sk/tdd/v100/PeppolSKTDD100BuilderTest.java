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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.time.ZoneOffset;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.datetime.helper.PDTFactory;
import com.helger.diagnostics.error.IError;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.inmemory.ReadableResourceString;
import com.helger.jaxb.GenericJAXBMarshaller;
import com.helger.peppol.sk.tdd.codelist.ESKTDDDocumentScope;
import com.helger.peppol.sk.tdd.codelist.ESKTDDReporterRole;
import com.helger.peppol.sk.tdd.codelist.ESKTDDTaxDataTypeCode;
import com.helger.peppol.sk.tdd.jaxb.PeppolSKTDD100Marshaller;
import com.helger.peppol.sk.tdd.testfiles.PeppolSKTestFiles;
import com.helger.peppol.sk.tdd.v2026_03_02.TaxDataType;
import com.helger.peppol.sk.tdd.validate.PeppolSKTDDValidator;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.phive.api.result.ValidationResultList;
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
  public void testBasicMinimal_TaxCat_E () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final TaxDataType aTDD = new PeppolSKTDD100Builder ().taxDataTypeCode (ESKTDDTaxDataTypeCode.SUBMIT)
                                                         .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                         .reporterRole (ESKTDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         .taxAuthorityID ("XX")
                                                         // Provide all fields manually
                                                         .reportedTransaction (rt -> rt.customizationID ("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")
                                                                                       .profileID ("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")
                                                                                       .id ("invoice-1")
                                                                                       .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                               Month.SEPTEMBER,
                                                                                                                               20))
                                                                                       .documentTypeCode ("380")
                                                                                       .documentCurrencyCode ("EUR")
                                                                                       .sellerEndpointIDSchemeID ("9915")
                                                                                       .sellerEndpointID ("peppol-sk-1")
                                                                                       .sellerTaxID ("DE11223344")
                                                                                       .buyerTaxID ("AT987654321")
                                                                                       .buyerName ("buyerComp")
                                                                                       .taxTotalDocumentCurrency (x -> x.taxAmount (0)
                                                                                                                        .addTaxSubtotal (y -> y.taxableAmount (1200)
                                                                                                                                               .taxAmount (0)
                                                                                                                                               .taxCategory (z -> z.id ("E")
                                                                                                                                                                   .percentage (0)
                                                                                                                                                                   .taxSchemeID ("VAT"))))
                                                                                       .lineExtensionAmount (1200)
                                                                                       .taxExclusiveTotalAmount (1200)
                                                                                       .taxInclusiveTotalAmount (1200)
                                                                                       .payableAmount (1200)
                                                                                       .addDocumentLine (x -> x.id ("1")
                                                                                                               .quantity (10)
                                                                                                               .quantityUnit ("STK")
                                                                                                               .lineExtensionAmount (1200)
                                                                                                               .item (y -> y.name ("What")
                                                                                                                            .classifiedTaxCategory (z -> z.id ("E")
                                                                                                                                                          .taxSchemeID ("VAT")
                                                                                                                                                          .percentage (0)))
                                                                                                               .priceAmount (120)))
                                                         .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolSKTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (new ReadableResourceString (sXML,
                                                                                                           StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  @Test
  public void testBasicMinimal_TaxCat_O () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final TaxDataType aTDD = new PeppolSKTDD100Builder ().taxDataTypeCode (ESKTDDTaxDataTypeCode.SUBMIT)
                                                         .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                         .reporterRole (ESKTDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         .taxAuthorityID ("XX")
                                                         // Provide all fields manually
                                                         .reportedTransaction (rt -> rt.customizationID ("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")
                                                                                       .profileID ("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")
                                                                                       .id ("invoice-1")
                                                                                       .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                               Month.SEPTEMBER,
                                                                                                                               20))
                                                                                       .documentTypeCode ("380")
                                                                                       .documentCurrencyCode ("EUR")
                                                                                       .sellerEndpointIDSchemeID ("9915")
                                                                                       .sellerEndpointID ("peppol-sk-1")
                                                                                       /*
                                                                                        * Seller and
                                                                                        * Buyer Tax
                                                                                        * IDs not
                                                                                        * needed for
                                                                                        * TaxCat "O"
                                                                                        */
                                                                                       // .sellerTaxID
                                                                                       // ("DE11223344")
                                                                                       .sellerCountryCode ("SK")
                                                                                       // .buyerTaxID
                                                                                       // ("AT987654321")
                                                                                       .buyerName ("buyerComp")
                                                                                       .taxTotalDocumentCurrency (x -> x.taxAmount (0)
                                                                                                                        .addTaxSubtotal (y -> y.taxableAmount (1200)
                                                                                                                                               .taxAmount (0)
                                                                                                                                               .taxCategory (z -> z.id ("O")
                                                                                                                                                                   .taxSchemeID_VAT ())))
                                                                                       .lineExtensionAmount (1200)
                                                                                       .taxExclusiveTotalAmount (1200)
                                                                                       .taxInclusiveTotalAmount (1200)
                                                                                       .payableAmount (1200)
                                                                                       .addDocumentLine (x -> x.id ("1")
                                                                                                               .quantity (10)
                                                                                                               .quantityUnit ("STK")
                                                                                                               .lineExtensionAmount (1200)
                                                                                                               .item (y -> y.name ("What")
                                                                                                                            .classifiedTaxCategory (z -> z.id ("O")
                                                                                                                                                          .taxSchemeID_VAT ()))
                                                                                                               .priceAmount (120)))
                                                         .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolSKTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (new ReadableResourceString (sXML,
                                                                                                           StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  @Test
  public void testBasicMaximal () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final TaxDataType aTDD = new PeppolSKTDD100Builder ().taxDataTypeCode (ESKTDDTaxDataTypeCode.SUBMIT)
                                                         .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                         .reporterRole (ESKTDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         .taxAuthorityID ("XX")
                                                         // Provide all fields manually
                                                         .reportedTransaction (rt -> rt.customizationID ("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")
                                                                                       .profileID ("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")
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
                                                                                       .sellerEndpointIDSchemeID ("9915")
                                                                                       .sellerEndpointID ("peppol-sk-1")
                                                                                       .sellerTaxID ("DE11223344")
                                                                                       .sellerCountryCode ("SK")
                                                                                       .buyerTaxID ("SK987654321")
                                                                                       .buyerCountryCode ("SK")
                                                                                       .buyerName ("buyerComp")
                                                                                       .taxRepresentativeID ("CH000111222")
                                                                                       .taxRepresentativeCountryCode ("CH")
                                                                                       .taxTotalDocumentCurrency (x -> x.taxAmount (120)
                                                                                                                        .addTaxSubtotal (y -> y.taxableAmount (1200)
                                                                                                                                               .taxAmount (120)
                                                                                                                                               .taxCategory (z -> z.id ("S")
                                                                                                                                                                   .percentage (10)
                                                                                                                                                                   .taxSchemeID ("VAT"))))
                                                                                       .taxTotalTaxCurrency (x -> x.taxAmount (500))
                                                                                       .lineExtensionAmount (1200)
                                                                                       .taxExclusiveTotalAmount (1200)
                                                                                       .taxInclusiveTotalAmount (1320)
                                                                                       .allowanceTotalAmount (0)
                                                                                       .chargeTotalAmount (0)
                                                                                       .payableRoundingAmount (0)
                                                                                       .payableAmount (1320)
                                                                                       .addDocumentLine (x -> x.id ("1")
                                                                                                               .quantity (10)
                                                                                                               .quantityUnit ("STK")
                                                                                                               .lineExtensionAmount (1200)
                                                                                                               .item (y -> y.name ("What")
                                                                                                                            .classifiedTaxCategory (z -> z.id ("S")
                                                                                                                                                          .percentage (10)
                                                                                                                                                          .taxSchemeID ("VAT")))
                                                                                                               .priceAmount (120)))
                                                         .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolSKTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (new ReadableResourceString (sXML,
                                                                                                           StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  @Test
  public void testCreateFromAllInvoices () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    for (final ClassPathResource aRes : PeppolSKTestFiles.getAllGoodBillingInvoiceFiles ())
    {
      LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

      final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
      assertNotNull (aInvoice);

      final TaxDataType aTDD = new PeppolSKTDD100Builder ().taxDataTypeCode (ESKTDDTaxDataTypeCode.SUBMIT)
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
      final GenericJAXBMarshaller <TaxDataType> m = new PeppolSKTDD100Marshaller ().setFormattedOutput (true);
      final String sXML = m.getAsString (aTDD);
      assertNotNull (sXML);

      if (false)
        LOGGER.info (sXML);

      // Schematron validation
      final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (new ReadableResourceString (sXML,
                                                                                                             StandardCharsets.UTF_8));
      assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                  aVRL.getOverallValidity ().isValid ());
    }
  }

  @Test
  public void testCreateFromAllCreditNotes () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    for (final ClassPathResource aRes : PeppolSKTestFiles.getAllGoodBillingCreditNoteFiles ())
    {
      LOGGER.info ("Converting CreditNote '" + aRes.getPath () + "' to a TDD");

      final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ().read (aRes);
      assertNotNull (aCreditNote);

      final TaxDataType aTDD = new PeppolSKTDD100Builder ().taxDataTypeCode (ESKTDDTaxDataTypeCode.SUBMIT)
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
      final GenericJAXBMarshaller <TaxDataType> m = new PeppolSKTDD100Marshaller ().setFormattedOutput (true);
      final String sXML = m.getAsString (aTDD);
      assertNotNull (sXML);

      if (false)
        LOGGER.info (sXML);

      // Schematron validation
      final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (new ReadableResourceString (sXML,
                                                                                                             StandardCharsets.UTF_8));
      assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                  aVRL.getOverallValidity ().isValid ());
    }
  }

  @Test
  public void testCreateFailedInvoiceWithReportedDocument () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final ClassPathResource aRes = PeppolSKTestFiles.getAllGoodBillingInvoiceFiles ().getFirstOrNull ();
    LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

    final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
    assertNotNull (aInvoice);

    final TaxDataType aTDD = new PeppolSKTDD100Builder ().taxDataTypeCode (ESKTDDTaxDataTypeCode.DISREGARD)
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
    final String sXML = new PeppolSKTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    assertTrue (sXML.contains ("<pxs:ReportedDocument>"));

    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (new ReadableResourceString (sXML,
                                                                                                           StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  @Test
  @Ignore ("Not supported by v1.0.0")
  public void testCreateFailedInvoiceWithoutReportedDocument () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final ClassPathResource aRes = PeppolSKTestFiles.getAllGoodBillingInvoiceFiles ().getFirstOrNull ();
    LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

    final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
    assertNotNull (aInvoice);

    // Explicitly sets a "null" CustomizationID to indicate an invalid source message
    aInvoice.setCustomizationID ((CustomizationIDType) null);
    // This one is special, because it is an XSD mandatory fields
    aInvoice.setID ((IDType) null);

    final TaxDataType aTDD = new PeppolSKTDD100Builder ().taxDataTypeCode (ESKTDDTaxDataTypeCode.DISREGARD)
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
    final String sXML = new PeppolSKTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    assertFalse (sXML.contains ("<pxs:ReportedDocument>"));

    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (new ReadableResourceString (sXML,
                                                                                                           StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  @Test
  public void testReadBadPayloads () throws Exception
  {
    for (final ClassPathResource aRes : PeppolSKTestFiles.getAllPayloadBadTDD100Files ())
    {
      LOGGER.info ("Reading Bad Payload TDD '" + aRes.getPath () + "'");

      final TaxDataType aTDD = new PeppolSKTDD100Marshaller ().read (aRes);
      assertNotNull (aTDD);

      // Serialize
      final String sXML = new PeppolSKTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
      assertNotNull (sXML);
      assertFalse (sXML.contains ("<pxs:ReportedDocument>"));

      if (false)
        LOGGER.info (sXML);

      // Schematron validation
      final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (new ReadableResourceString (sXML,
                                                                                                             StandardCharsets.UTF_8));
      assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                  aVRL.getOverallValidity ().isValid ());
    }
  }
}
