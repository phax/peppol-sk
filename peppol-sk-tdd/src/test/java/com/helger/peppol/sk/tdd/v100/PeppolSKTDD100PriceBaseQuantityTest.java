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

import java.math.BigDecimal;
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
import com.helger.peppol.sk.tdd.v2026_04_14.DocumentLineType;
import com.helger.peppol.sk.tdd.v2026_04_14.ReportedDocumentType;
import com.helger.peppol.sk.tdd.v2026_04_14.TaxDataType;
import com.helger.peppol.sk.tdd.validate.PeppolSKTDDValidator;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.ubl21.UBL21Marshaller;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.BaseQuantityType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test the mapping of BT-149 (Item price base quantity) and BT-150 (Item price base quantity unit
 * of measure code) to cac:Price/cbc:BaseQuantity.
 *
 * @author Philip Helger
 */
public final class PeppolSKTDD100PriceBaseQuantityTest
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

  private static void _assertBaseQuantity (final DocumentLineType aLine,
                                           final String sExpectedValue,
                                           final String sExpectedUnitCode)
  {
    final BaseQuantityType aBQ = aLine.getPrice ().getBaseQuantity ();
    assertNotNull (aBQ);
    assertEquals (new BigDecimal (sExpectedValue), aBQ.getValue ());
    assertEquals (sExpectedUnitCode, aBQ.getUnitCode ());
  }

  @Test
  public void testInvoice () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/sk-price-base-quantity.xml"));
    assertNotNull (aInvoice);

    final ReportedDocumentType aDoc = _build (rt -> rt.initFromInvoice (aInvoice));
    assertEquals (2, aDoc.getDocumentLineCount ());
    _assertBaseQuantity (aDoc.getDocumentLineAtIndex (0), "100", "H87");
    // No unit code in the source
    _assertBaseQuantity (aDoc.getDocumentLineAtIndex (1), "2", null);
  }

  @Test
  public void testCreditNote () throws Exception
  {
    final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ()
                                                      .read (new ClassPathResource ("external/creditnote/good/sk-price-base-quantity.xml"));
    assertNotNull (aCreditNote);

    final ReportedDocumentType aDoc = _build (rt -> rt.initFromCreditNote (aCreditNote));
    assertEquals (2, aDoc.getDocumentLineCount ());
    _assertBaseQuantity (aDoc.getDocumentLineAtIndex (0), "100", "H87");
    // No unit code in the source
    _assertBaseQuantity (aDoc.getDocumentLineAtIndex (1), "2", null);
  }

  @Test
  public void testExplicitPriceBaseQuantityUnitCode () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/sk-price-base-quantity.xml"));
    assertNotNull (aInvoice);

    final ReportedDocumentType aDoc = _build (rt -> {
      rt.initFromInvoice (aInvoice);
      // Rebuild the second line with an explicit unit code
      rt.documentLines ().remove (1);
      rt.addDocumentLine (x -> {
        x.initFromInvoice (aInvoice.getInvoiceLineAtIndex (1));
        assertNull (x.priceBaseQuantityUnitCode ());
        x.priceBaseQuantityUnitCode ("C62");
      });
    });
    _assertBaseQuantity (aDoc.getDocumentLineAtIndex (1), "2", "C62");
  }
}
