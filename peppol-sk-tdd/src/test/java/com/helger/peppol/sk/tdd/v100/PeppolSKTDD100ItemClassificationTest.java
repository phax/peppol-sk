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

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ItemClassificationCodeType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test that the item classification identifier (BT-158) is taken over with its
 * scheme identifier (BT-158-1) only, because the TDD does not allow the scheme
 * version identifier (BT-158-2).
 *
 * @author Philip Helger
 */
public final class PeppolSKTDD100ItemClassificationTest
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

  private static void _assertSourceHasListVersionID (final ItemType aItem)
  {
    final ItemClassificationCodeType aICC = aItem.getCommodityClassificationAtIndex (0).getItemClassificationCode ();
    assertEquals ("2008", aICC.getListVersionID ());
  }

  private static void _assertItemClassification (final ReportedDocumentType aDoc)
  {
    final ItemType aItem = aDoc.getDocumentLineAtIndex (0).getItem ();
    assertNotNull (aItem);
    assertEquals (1, aItem.getCommodityClassificationCount ());

    final ItemClassificationCodeType aICC = aItem.getCommodityClassificationAtIndex (0).getItemClassificationCode ();
    assertNotNull (aICC);
    assertEquals ("73110000", aICC.getValue ());
    assertEquals ("STI", aICC.getListID ());
    assertNull (aICC.getListVersionID ());
  }

  @Test
  public void testInvoiceWithListVersionID () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/sk-item-classification.xml"));
    assertNotNull (aInvoice);
    _assertSourceHasListVersionID (aInvoice.getInvoiceLineAtIndex (0).getItem ());

    _assertItemClassification (_build (rt -> rt.initFromInvoice (aInvoice)));
  }

  @Test
  public void testCreditNoteWithListVersionID () throws Exception
  {
    final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ()
                                                      .read (new ClassPathResource ("external/creditnote/good/sk-item-classification.xml"));
    assertNotNull (aCreditNote);
    _assertSourceHasListVersionID (aCreditNote.getCreditNoteLineAtIndex (0).getItem ());

    _assertItemClassification (_build (rt -> rt.initFromCreditNote (aCreditNote)));
  }
}
