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

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PaymentMandateType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test the mapping of BG-19 (Direct debit; BT-89 and BT-91) to
 * cac:PaymentMeans/cac:PaymentMandate and of the PaymentMeans in general.
 *
 * @author Philip Helger
 */
public final class PeppolSKTDD110PaymentMandateTest
{
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

    final String sXML = new PeppolSKTDD110Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);

    final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_110 (new ReadableResourceString (sXML,
                                                                                                           StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
    return aTDD.getReportedTransactionAtIndex (0).getReportedDocument ();
  }

  private static void _assertDirectDebit (final ReportedDocumentType aDoc)
  {
    assertEquals (1, aDoc.getPaymentMeansCount ());
    final PaymentMeansType aPM = aDoc.getPaymentMeansAtIndex (0);
    assertEquals ("59", aPM.getPaymentMeansCode ().getValue ());
    assertEquals ("Direct debit", aPM.getPaymentMeansCode ().getName ());
    assertEquals ("SK-DD-1", aPM.getPaymentIDAtIndex (0).getValue ());

    final PaymentMandateType aMandate = aPM.getPaymentMandate ();
    assertNotNull (aMandate);
    // BT-89
    assertEquals ("MANDATE-2026-0001", aMandate.getIDValue ());
    // BT-91
    assertNotNull (aMandate.getPayerFinancialAccount ());
    assertEquals ("SK3112000000198742637541", aMandate.getPayerFinancialAccount ().getIDValue ());
  }

  @Test
  public void testInvoiceWithPaymentMandate () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/sk-direct-debit.xml"));
    assertNotNull (aInvoice);

    _assertDirectDebit (_build (rt -> rt.initFromInvoice (aInvoice)));
  }

  @Test
  public void testCreditNoteWithPaymentMandate () throws Exception
  {
    final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ()
                                                      .read (new ClassPathResource ("external/creditnote/good/sk-direct-debit.xml"));
    assertNotNull (aCreditNote);

    _assertDirectDebit (_build (rt -> rt.initFromCreditNote (aCreditNote)));
  }

  @Test
  public void testCreditTransferHasNoPaymentMandate () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/base-example.xml"));
    assertNotNull (aInvoice);

    final ReportedDocumentType aDoc = _build (rt -> rt.initFromInvoice (aInvoice));
    assertEquals (1, aDoc.getPaymentMeansCount ());
    final PaymentMeansType aPM = aDoc.getPaymentMeansAtIndex (0);
    assertEquals ("30", aPM.getPaymentMeansCode ().getValue ());
    assertEquals ("IBAN32423940", aPM.getPayeeFinancialAccount ().getIDValue ());
    assertNull (aPM.getPaymentMandate ());
  }
}
