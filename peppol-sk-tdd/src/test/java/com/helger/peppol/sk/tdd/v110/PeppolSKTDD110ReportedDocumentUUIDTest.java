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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import com.helger.base.uuid.UUID5Helper;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.io.resource.ClassPathResource;
import com.helger.peppol.sk.tdd.CSKTDD;
import com.helger.peppol.sk.tdd.codelist.ESKTDDTaxDataTypeCode;
import com.helger.peppol.sk.tdd.v2026_04_14.ReportedTransactionType;
import com.helger.ubl21.UBL21Marshaller;

import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test the calculation of the ReportedDocument UUID (rule ID-BDID-01) from
 * BT-34-1, BT-34, BT-3, BT-1 and BT-2.
 *
 * @author Philip Helger
 */
public final class PeppolSKTDD110ReportedDocumentUUIDTest
{
  private static final String EXPECTED_UUID_NAME = "0245 2020000001 380 SK-UUID-1 2026-06-03";

  private static InvoiceType _readInvoice ()
  {
    final InvoiceType ret = UBL21Marshaller.invoice ()
                                           .read (new ClassPathResource ("external/invoice/good/sk-uuid-inputs.xml"));
    assertNotNull (ret);
    return ret;
  }

  private static PeppolSKTDD110ReportedTransactionBuilder _init ()
  {
    return new PeppolSKTDD110ReportedTransactionBuilder (ESKTDDTaxDataTypeCode.SUBMIT).initFromInvoice (_readInvoice ());
  }

  private static String _uuid (final ReportedTransactionType aRT)
  {
    assertNotNull (aRT);
    return aRT.getReportedDocument ().getUUID ().getValue ();
  }

  @Test
  public void testUUIDOfCleanInputs ()
  {
    final String sUUID = _uuid (_init ().build ());
    assertEquals (UUID5Helper.fromUTF8 (CSKTDD.PEPPOL_SK_NAMESPACE, EXPECTED_UUID_NAME).toString (), sUUID);
    assertEquals ("90d197a7-48ce-51e0-a249-3726f7544f70", sUUID);
  }

  @Test
  public void testUUIDInputsAreTrimmed ()
  {
    final String sClean = _uuid (_init ().build ());

    final ReportedTransactionType aRT = _init ().sellerEndpointIDSchemeID ("  0245\t")
                                                .sellerEndpointID (" 2020000001\n")
                                                .documentTypeCode ("\n380 ")
                                                .id ("  SK-UUID-1  ")
                                                .build ();
    assertEquals (sClean, _uuid (aRT));
    // Only the UUID calculation normalizes, the document itself is copied as is
    assertEquals ("  SK-UUID-1  ", aRT.getReportedDocument ().getID ().getValue ());
  }

  @Test
  public void testEmptyUUIDInputFailsBuild ()
  {
    final List <Consumer <PeppolSKTDD110ReportedTransactionBuilder>> aModifiers = List.of (rt -> rt.sellerEndpointIDSchemeID ("  "),
                                                                                           rt -> rt.sellerEndpointID ("\t"),
                                                                                           rt -> rt.documentTypeCode (" "),
                                                                                           rt -> rt.id ("\n"),
                                                                                           rt -> rt.sellerEndpointIDSchemeID (""),
                                                                                           rt -> rt.sellerEndpointID (""),
                                                                                           rt -> rt.documentTypeCode (""),
                                                                                           rt -> rt.id (""),
                                                                                           rt -> rt.sellerEndpointIDSchemeID (null),
                                                                                           rt -> rt.sellerEndpointID (null),
                                                                                           rt -> rt.documentTypeCode (null),
                                                                                           rt -> rt.id (null),
                                                                                           rt -> rt.issueDate ((LocalDate) null));
    for (final Consumer <PeppolSKTDD110ReportedTransactionBuilder> aModifier : aModifiers)
    {
      final PeppolSKTDD110ReportedTransactionBuilder aBuilder = _init ();
      aModifier.accept (aBuilder);
      assertFalse (aBuilder.isEveryRequiredFieldSet (new ErrorList ()));
      assertNull (aBuilder.build ());
    }
  }
}
