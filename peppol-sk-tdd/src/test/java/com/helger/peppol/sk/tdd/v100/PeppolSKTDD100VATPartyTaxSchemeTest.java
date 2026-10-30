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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.junit.Test;

import com.helger.diagnostics.error.IError;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.inmemory.ReadableResourceString;
import com.helger.peppol.sk.tdd.codelist.ESKTDDDocumentScope;
import com.helger.peppol.sk.tdd.codelist.ESKTDDReporterRole;
import com.helger.peppol.sk.tdd.codelist.ESKTDDTaxDataTypeCode;
import com.helger.peppol.sk.tdd.jaxb.PeppolSKTDD100Marshaller;
import com.helger.peppol.sk.tdd.v2026_04_14.ReportedDocumentType;
import com.helger.peppol.sk.tdd.v2026_04_14.TaxDataType;
import com.helger.peppol.sk.tdd.validate.PeppolSKTDDValidator;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.ubl21.UBL21Marshaller;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyTaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test that the VAT identifiers (BT-31, BT-48, BT-63) are taken from the
 * cac:PartyTaxScheme with TaxScheme/ID "VAT", independent of the order of the
 * cac:PartyTaxScheme elements and, as in the EN 16931 Schematron, of the case
 * and surrounding whitespace of the TaxScheme/ID.
 *
 * @author Philip Helger
 */
public final class PeppolSKTDD100VATPartyTaxSchemeTest
{
  private static ReportedDocumentType _build (final Consumer <PeppolSKTDD100ReportedTransactionBuilder> aInit)
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final TaxDataType aTDD = new PeppolSKTDD100Builder ().taxDataTypeCode (ESKTDDTaxDataTypeCode.SUBMIT)
                                                         .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                         .reporterRole (ESKTDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0245:2020000001"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         .taxAuthorityID ("XX")
                                                         .reportedTransaction (aInit)
                                                         .build ();
    assertNotNull (aTDD);

    final String sXML = new PeppolSKTDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);

    final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (new ReadableResourceString (sXML,
                                                                                                           StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
    return aTDD.getReportedTransactionAtIndex (0).getReportedDocument ();
  }

  private static void _assertVAT (final String sExpected, final PartyType aParty)
  {
    assertNotNull (aParty);
    assertEquals (1, aParty.getPartyTaxSchemeCount ());
    final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
    assertEquals (sExpected, aPTS.getCompanyIDValue ());
    assertEquals ("VAT", aPTS.getTaxScheme ().getIDValue ());
  }

  private static void _assertAllVAT (final ReportedDocumentType aDoc)
  {
    _assertVAT ("SK2020000001", aDoc.getAccountingSupplierParty ().getParty ());
    _assertVAT ("SK2020000002", aDoc.getAccountingCustomerParty ().getParty ());
    _assertVAT ("SK2020000003", aDoc.getTaxRepresentativeParty ());
  }

  private static void _setVATSchemeID (final String sSchemeID, final PartyType... aParties)
  {
    for (final PartyType aParty : aParties)
      for (final PartyTaxSchemeType aPTS : aParty.getPartyTaxScheme ())
        if ("VAT".equals (aPTS.getTaxScheme ().getIDValue ()))
          aPTS.getTaxScheme ().setID (sSchemeID);
  }

  @Test
  public void testInvoiceTaxSchemeBeforeVAT () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/sk-tax-scheme-order.xml"));
    assertNotNull (aInvoice);
    // BT-32 first, BT-31 second
    assertEquals ("TAX",
                  aInvoice.getAccountingSupplierParty ()
                          .getParty ()
                          .getPartyTaxSchemeAtIndex (0)
                          .getTaxScheme ()
                          .getIDValue ());

    _assertAllVAT (_build (rt -> rt.initFromInvoice (aInvoice)));
  }

  @Test
  public void testCreditNoteTaxSchemeBeforeVAT () throws Exception
  {
    final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ()
                                                      .read (new ClassPathResource ("external/creditnote/good/sk-tax-scheme-order.xml"));
    assertNotNull (aCreditNote);

    _assertAllVAT (_build (rt -> rt.initFromCreditNote (aCreditNote)));
  }

  @Test
  public void testNoVATEntry () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/sk-tax-scheme-order.xml"));
    assertNotNull (aInvoice);
    // Only keep the non-VAT entry of the seller
    aInvoice.getAccountingSupplierParty ().getParty ().getPartyTaxScheme ().remove (1);
    // Buyer and tax representative only have a VAT entry - change it to non-VAT
    aInvoice.getAccountingCustomerParty ().getParty ().getPartyTaxSchemeAtIndex (0).getTaxScheme ().setID ("TAX");
    aInvoice.getTaxRepresentativeParty ().getPartyTaxSchemeAtIndex (0).getTaxScheme ().setID ("TAX");

    final PeppolSKTDD100ReportedTransactionBuilder aRT = new PeppolSKTDD100ReportedTransactionBuilder (ESKTDDTaxDataTypeCode.SUBMIT).initFromInvoice (aInvoice);
    // The tax registration number must not be reported as VAT identifier
    assertNull (aRT.sellerTaxID ());
    assertNull (aRT.buyerTaxID ());
    assertNull (aRT.taxRepresentativeID ());
  }

  @Test
  public void testInvoiceLowerCaseVAT () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/sk-tax-scheme-order.xml"));
    assertNotNull (aInvoice);
    _setVATSchemeID ("vat",
                     aInvoice.getAccountingSupplierParty ().getParty (),
                     aInvoice.getAccountingCustomerParty ().getParty (),
                     aInvoice.getTaxRepresentativeParty ());

    _assertAllVAT (_build (rt -> rt.initFromInvoice (aInvoice)));
  }

  @Test
  public void testCreditNoteLowerCaseVAT () throws Exception
  {
    final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ()
                                                      .read (new ClassPathResource ("external/creditnote/good/sk-tax-scheme-order.xml"));
    assertNotNull (aCreditNote);
    _setVATSchemeID ("vat",
                     aCreditNote.getAccountingSupplierParty ().getParty (),
                     aCreditNote.getAccountingCustomerParty ().getParty (),
                     aCreditNote.getTaxRepresentativeParty ());

    _assertAllVAT (_build (rt -> rt.initFromCreditNote (aCreditNote)));
  }

  @Test
  public void testInvoicePaddedMixedCaseVAT () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/sk-tax-scheme-order.xml"));
    assertNotNull (aInvoice);
    // UBL-SR-13 does not normalize the space, so next to BT-32 the padded
    // entry would count as a second seller tax registration
    aInvoice.getAccountingSupplierParty ().getParty ().getPartyTaxScheme ().remove (0);
    _setVATSchemeID (" Vat ",
                     aInvoice.getAccountingSupplierParty ().getParty (),
                     aInvoice.getAccountingCustomerParty ().getParty (),
                     aInvoice.getTaxRepresentativeParty ());

    _assertAllVAT (_build (rt -> rt.initFromInvoice (aInvoice)));
  }

  @Test
  public void testCreditNotePaddedMixedCaseVAT () throws Exception
  {
    final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ()
                                                      .read (new ClassPathResource ("external/creditnote/good/sk-tax-scheme-order.xml"));
    assertNotNull (aCreditNote);
    // UBL-SR-13 does not normalize the space, so next to BT-32 the padded
    // entry would count as a second seller tax registration
    aCreditNote.getAccountingSupplierParty ().getParty ().getPartyTaxScheme ().remove (0);
    _setVATSchemeID (" Vat ",
                     aCreditNote.getAccountingSupplierParty ().getParty (),
                     aCreditNote.getAccountingCustomerParty ().getParty (),
                     aCreditNote.getTaxRepresentativeParty ());

    _assertAllVAT (_build (rt -> rt.initFromCreditNote (aCreditNote)));
  }
}
