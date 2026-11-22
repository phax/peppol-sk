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
package com.helger.peppol.sk.tdd.v110;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.function.Consumer;

import org.junit.Test;

import com.helger.diagnostics.error.IError;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.inmemory.ReadableResourceString;
import com.helger.peppol.sk.tdd.codelist.ESKTDDDocumentScope;
import com.helger.peppol.sk.tdd.codelist.ESKTDDReporterRole;
import com.helger.peppol.sk.tdd.codelist.ESKTDDTaxDataTypeCode;
import com.helger.peppol.sk.tdd.jaxb.PeppolSKTDD110Marshaller;
import com.helger.peppol.sk.tdd.v2026_04_14.ReportedDocumentType;
import com.helger.peppol.sk.tdd.v2026_04_14.TaxDataType;
import com.helger.peppol.sk.tdd.validate.PeppolSKTDDValidator;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.ubl21.UBL21Marshaller;

import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test the mapping of BT-7 (Value added tax point date) to cbc:TaxPointDate.
 *
 * @author Philip Helger
 */
public final class PeppolSKTDD110TaxPointDateTest
{
  private static final LocalDate TAX_POINT_DATE = LocalDate.of (2026, 5, 31);

  private static void _validate (final TaxDataType aTDD)
  {
    final String sXML = new PeppolSKTDD110Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);

    final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_110 (new ReadableResourceString (sXML,
                                                                                                           StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  private static ReportedDocumentType _build (final Consumer <PeppolSKTDD110ReportedTransactionBuilder> aInit)
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final TaxDataType aTDD = new PeppolSKTDD110Builder ().taxDataTypeCode (ESKTDDTaxDataTypeCode.SUBMIT)
                                                         .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                         .reporterRole (ESKTDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0245:2020000001"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         .taxAuthorityID ("XX")
                                                         .reportedTransaction (aInit)
                                                         .build ();
    assertNotNull (aTDD);
    _validate (aTDD);
    return aTDD.getReportedTransactionAtIndex (0).getReportedDocument ();
  }

  @Test
  public void testInvoiceWithTaxPointDate () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/sk-tax-point-date.xml"));
    assertNotNull (aInvoice);

    final ReportedDocumentType aDoc = _build (rt -> rt.initFromInvoice (aInvoice));
    assertNotNull (aDoc.getTaxPointDate ());
    assertEquals (TAX_POINT_DATE, aDoc.getTaxPointDate ().getValueLocal ());
    // The TaxPointDate is not the IssueDate
    assertEquals (LocalDate.of (2026, 6, 3), aDoc.getIssueDate ().getValueLocal ());
  }

  @Test
  public void testCreditNoteWithTaxPointDate () throws Exception
  {
    final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ()
                                                      .read (new ClassPathResource ("external/creditnote/good/sk-tax-point-date.xml"));
    assertNotNull (aCreditNote);

    final ReportedDocumentType aDoc = _build (rt -> rt.initFromCreditNote (aCreditNote));
    assertNotNull (aDoc.getTaxPointDate ());
    assertEquals (TAX_POINT_DATE, aDoc.getTaxPointDate ().getValueLocal ());
    assertEquals (LocalDate.of (2026, 6, 3), aDoc.getIssueDate ().getValueLocal ());
  }

  @Test
  public void testInvoiceWithoutTaxPointDate () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/base-example.xml"));
    assertNotNull (aInvoice);
    assertNull (aInvoice.getTaxPointDate ());

    final ReportedDocumentType aDoc = _build (rt -> rt.initFromInvoice (aInvoice));
    assertNull (aDoc.getTaxPointDate ());
  }

  @Test
  public void testExplicitTaxPointDate ()
  {
    final ReportedDocumentType aDoc = _build (rt -> {
      final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                  .read (new ClassPathResource ("external/invoice/good/sk-tax-point-date.xml"));
      rt.initFromInvoice (aInvoice).taxPointDate (LocalDate.of (2026, 4, 30));
    });
    assertEquals (LocalDate.of (2026, 4, 30), aDoc.getTaxPointDate ().getValueLocal ());
  }
}
