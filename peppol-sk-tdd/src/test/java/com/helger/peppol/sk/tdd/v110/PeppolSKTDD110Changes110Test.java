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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.junit.Test;

import com.helger.diagnostics.error.IError;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.inmemory.ReadableResourceString;
import com.helger.peppol.sk.tdd.codelist.ESKTDDDocumentScope;
import com.helger.peppol.sk.tdd.codelist.ESKTDDReporterRole;
import com.helger.peppol.sk.tdd.codelist.ESKTDDTaxDataTypeCode;
import com.helger.peppol.sk.tdd.jaxb.PeppolSKTDD110Marshaller;
import com.helger.peppol.sk.tdd.v100.PeppolSKTDD100DocumentLineBuilder;
import com.helger.peppol.sk.tdd.v100.PeppolSKTDD100ReportedTransactionBuilder;
import com.helger.peppol.sk.tdd.v2026_04_14.TaxDataType;
import com.helger.peppol.sk.tdd.validate.PeppolSKTDDValidator;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.ubl21.UBL21Marshaller;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyLegalEntityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CompanyIDType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Tests for the changes of the 1.1.0 builders compared to the 1.0.0 builders: the Buyer legal
 * registration identifier (BT-47) and the removed price base quantity (BT-149).
 *
 * @author Philip Helger
 */
public final class PeppolSKTDD110Changes110Test
{
  private static TaxDataType _build (final Consumer <PeppolSKTDD110ReportedTransactionBuilder> aInit)
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final TaxDataType aTDD = new PeppolSKTDD110Builder ().taxDataTypeCode (ESKTDDTaxDataTypeCode.SUBMIT)
                                                         .documentScope (ESKTDDDocumentScope.DOMESTIC)
                                                         .reporterRole (ESKTDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         .taxAuthorityID ("XX")
                                                         .reportedTransaction (aInit)
                                                         .build ();
    assertNotNull (aTDD);
    return aTDD;
  }

  private static String _assertValid110 (final TaxDataType aTDD)
  {
    final String sXML = new PeppolSKTDD110Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_110 (new ReadableResourceString (sXML,
                                                                                                           StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
    return sXML;
  }

  @Test
  public void testBuyerLegalRegistrationIDInvoice () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/base-example.xml"));
    assertNotNull (aInvoice);
    final PartyLegalEntityType aBuyerPLE = aInvoice.getAccountingCustomerParty ()
                                                   .getParty ()
                                                   .getPartyLegalEntityAtIndex (0);
    assertEquals ("39937423947", aBuyerPLE.getCompanyIDValue ());
    assertEquals ("0183", aBuyerPLE.getCompanyID ().getSchemeID ());

    // 1.0.0 builder does not take it over (ibr-tdd-86 of 1.0.0 forbids it)
    assertNull (new PeppolSKTDD100ReportedTransactionBuilder (ESKTDDTaxDataTypeCode.SUBMIT).initFromInvoice (aInvoice)
                                                                                         .build ()
                                                                                         .getReportedDocument ()
                                                                                         .getAccountingCustomerParty ()
                                                                                         .getParty ()
                                                                                         .getPartyLegalEntityAtIndex (0)
                                                                                         .getCompanyID ());

    // 1.1.0 builder takes it over
    final PeppolSKTDD110ReportedTransactionBuilder aRT = new PeppolSKTDD110ReportedTransactionBuilder (ESKTDDTaxDataTypeCode.SUBMIT).initFromInvoice (aInvoice);
    assertEquals ("39937423947", aRT.buyerLegalRegistrationID ());
    assertEquals ("0183", aRT.buyerLegalRegistrationIDSchemeID ());

    final TaxDataType aTDD = _build (rt -> rt.initFromInvoice (aInvoice));
    final String sXML = _assertValid110 (aTDD);
    final PartyLegalEntityType aPLE = aTDD.getReportedTransactionAtIndex (0)
                                          .getReportedDocument ()
                                          .getAccountingCustomerParty ()
                                          .getParty ()
                                          .getPartyLegalEntityAtIndex (0);
    assertEquals ("39937423947", aPLE.getCompanyIDValue ());
    assertEquals ("0183", aPLE.getCompanyID ().getSchemeID ());

    // The 1.0.0 rules reject it
    assertFalse (PeppolSKTDDValidator.validateSK_TDD_100 (new ReadableResourceString (sXML, StandardCharsets.UTF_8))
                                     .getOverallValidity ()
                                     .isValid ());
  }

  @Test
  public void testBuyerLegalRegistrationIDCreditNote () throws Exception
  {
    final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ()
                                                      .read (new ClassPathResource ("external/creditnote/good/base-creditnote-correction.xml"));
    assertNotNull (aCreditNote);

    final PeppolSKTDD110ReportedTransactionBuilder aRT = new PeppolSKTDD110ReportedTransactionBuilder (ESKTDDTaxDataTypeCode.SUBMIT).initFromCreditNote (aCreditNote);
    assertEquals ("39937423947", aRT.buyerLegalRegistrationID ());
    assertEquals ("0183", aRT.buyerLegalRegistrationIDSchemeID ());

    final TaxDataType aTDD = _build (rt -> rt.initFromCreditNote (aCreditNote));
    _assertValid110 (aTDD);
    assertEquals ("39937423947",
                  aTDD.getReportedTransactionAtIndex (0)
                      .getReportedDocument ()
                      .getAccountingCustomerParty ()
                      .getParty ()
                      .getPartyLegalEntityAtIndex (0)
                      .getCompanyIDValue ());
  }

  @Test
  public void testBuyerLegalRegistrationIDSchemeWithoutID () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/base-example.xml"));
    assertNotNull (aInvoice);

    final PeppolSKTDD110ReportedTransactionBuilder aRT = new PeppolSKTDD110ReportedTransactionBuilder (ESKTDDTaxDataTypeCode.SUBMIT).initFromInvoice (aInvoice);
    assertTrue (aRT.isEveryRequiredFieldSet (new ErrorList ()));

    aRT.buyerLegalRegistrationID (null);
    final ErrorList aErrorList = new ErrorList ();
    assertFalse (aRT.isEveryRequiredFieldSet (aErrorList));
    assertEquals (1, aErrorList.size ());
  }

  @Test
  public void testBuyerLegalRegistrationIDWithoutScheme () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/base-example.xml"));
    assertNotNull (aInvoice);
    aInvoice.getAccountingCustomerParty ().getParty ().getPartyLegalEntityAtIndex (0).getCompanyID ().setSchemeID (null);

    final TaxDataType aTDD = _build (rt -> rt.initFromInvoice (aInvoice));
    _assertValid110 (aTDD);
    final PartyLegalEntityType aPLE = aTDD.getReportedTransactionAtIndex (0)
                                          .getReportedDocument ()
                                          .getAccountingCustomerParty ()
                                          .getParty ()
                                          .getPartyLegalEntityAtIndex (0);
    assertEquals ("39937423947", aPLE.getCompanyIDValue ());
    assertNull (aPLE.getCompanyID ().getSchemeID ());
  }

  @Test
  public void testNoBuyerLegalRegistrationID () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/base-example.xml"));
    assertNotNull (aInvoice);
    final PartyLegalEntityType aBuyerPLE = aInvoice.getAccountingCustomerParty ()
                                                   .getParty ()
                                                   .getPartyLegalEntityAtIndex (0);

    // No CompanyID element at all
    aBuyerPLE.setCompanyID ((CompanyIDType) null);
    _assertNoBuyerCompanyID (aInvoice);

    // CompanyID element with a scheme, but without a value - the scheme must not be taken over
    // alone
    final CompanyIDType aEmpty = new CompanyIDType ();
    aEmpty.setSchemeID ("0183");
    aBuyerPLE.setCompanyID (aEmpty);
    _assertNoBuyerCompanyID (aInvoice);
  }

  private static void _assertNoBuyerCompanyID (final InvoiceType aInvoice)
  {
    final PeppolSKTDD110ReportedTransactionBuilder aRT = new PeppolSKTDD110ReportedTransactionBuilder (ESKTDDTaxDataTypeCode.SUBMIT).initFromInvoice (aInvoice);
    assertNull (aRT.buyerLegalRegistrationID ());
    assertNull (aRT.buyerLegalRegistrationIDSchemeID ());

    final TaxDataType aTDD = _build (rt -> rt.initFromInvoice (aInvoice));
    final String sXML = _assertValid110 (aTDD);
    assertNull (aTDD.getReportedTransactionAtIndex (0)
                    .getReportedDocument ()
                    .getAccountingCustomerParty ()
                    .getParty ()
                    .getPartyLegalEntityAtIndex (0)
                    .getCompanyID ());
    assertFalse (sXML.contains ("39937423947"));
  }

  @Test
  public void testTaxRepresentativeWithoutID () throws Exception
  {
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/base-example.xml"));
    assertNotNull (aInvoice);

    final PeppolSKTDD110ReportedTransactionBuilder aRT = new PeppolSKTDD110ReportedTransactionBuilder (ESKTDDTaxDataTypeCode.SUBMIT).initFromInvoice (aInvoice)
                                                                                                                                     .taxRepresentativeCountryCode ("CH")
                                                                                                                                     .taxRepresentativeID (null);
    // cac:TaxRepresentativeParty/cac:PartyTaxScheme is 1..1 since 1.1.0
    final ErrorList aErrorList = new ErrorList ();
    assertFalse (aRT.isEveryRequiredFieldSet (aErrorList));
    assertEquals (1, aErrorList.size ());

    aRT.taxRepresentativeID ("CHE123456789");
    assertTrue (aRT.isEveryRequiredFieldSet (new ErrorList ()));
  }

  @Test
  public void testPriceBaseQuantityCreditNote () throws Exception
  {
    final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ()
                                                      .read (new ClassPathResource ("external/creditnote/good/base-creditnote-correction.xml"));
    assertNotNull (aCreditNote);
    // Set a price of 200 for a base quantity of 2 in the first line
    final var aPrice = aCreditNote.getCreditNoteLineAtIndex (0).getPrice ();
    final BigDecimal aOrigPrice = aPrice.getPriceAmountValue ();
    aPrice.setPriceAmount (aOrigPrice.multiply (BigDecimal.valueOf (2)));
    aPrice.setBaseQuantity (BigDecimal.valueOf (2));

    final PeppolSKTDD110DocumentLineBuilder aDL = new PeppolSKTDD110DocumentLineBuilder ("EUR").initFromCreditNote (aCreditNote.getCreditNoteLineAtIndex (0));
    assertEquals (0, aOrigPrice.compareTo (aDL.priceAmount ()));
    assertNull (aDL.build ().getPrice ().getBaseQuantity ());

    final String sXML = _assertValid110 (_build (rt -> rt.initFromCreditNote (aCreditNote)));
    assertFalse (sXML.contains ("BaseQuantity"));
  }

  @Test
  public void testPriceBaseQuantity () throws Exception
  {
    // Line 2 has a price of 200 for a base quantity of 2
    final InvoiceType aInvoice = UBL21Marshaller.invoice ()
                                                .read (new ClassPathResource ("external/invoice/good/Allowance-example.xml"));
    assertNotNull (aInvoice);
    assertEquals (0, new BigDecimal ("2").compareTo (aInvoice.getInvoiceLineAtIndex (1).getPrice ().getBaseQuantityValue ()));

    // 1.0.0: taken over as is
    final PeppolSKTDD100DocumentLineBuilder aDL100 = new PeppolSKTDD100DocumentLineBuilder ("EUR").initFromInvoice (aInvoice.getInvoiceLineAtIndex (1));
    assertEquals (0, new BigDecimal ("200").compareTo (aDL100.priceAmount ()));
    assertEquals (0, new BigDecimal ("2").compareTo (aDL100.priceBaseQuantity ()));

    // 1.1.0: price for one unit, no base quantity (ibr-tdd-82)
    final PeppolSKTDD110DocumentLineBuilder aDL110 = new PeppolSKTDD110DocumentLineBuilder ("EUR").initFromInvoice (aInvoice.getInvoiceLineAtIndex (1));
    assertEquals (0, new BigDecimal ("100").compareTo (aDL110.priceAmount ()));
    assertNull (aDL110.build ().getPrice ().getBaseQuantity ());

    final TaxDataType aTDD = _build (rt -> rt.initFromInvoice (aInvoice));
    final String sXML = _assertValid110 (aTDD);
    assertFalse (sXML.contains ("BaseQuantity"));
    assertEquals (0,
                  new BigDecimal ("100").compareTo (aTDD.getReportedTransactionAtIndex (0)
                                                        .getReportedDocument ()
                                                        .getDocumentLineAtIndex (1)
                                                        .getPrice ()
                                                        .getPriceAmountValue ()));
  }

  @Test
  public void testGetPriceAmountPerUnit ()
  {
    assertNull (PeppolSKTDD110DocumentLineBuilder.getPriceAmountPerUnit (null, BigDecimal.TEN));
    assertEquals (new BigDecimal ("5.50"), PeppolSKTDD110DocumentLineBuilder.getPriceAmountPerUnit (new BigDecimal ("5.50"), null));
    assertEquals (new BigDecimal ("5.50"),
                  PeppolSKTDD110DocumentLineBuilder.getPriceAmountPerUnit (new BigDecimal ("5.50"), BigDecimal.ZERO));
    assertEquals (new BigDecimal ("5.50"),
                  PeppolSKTDD110DocumentLineBuilder.getPriceAmountPerUnit (new BigDecimal ("5.50"), new BigDecimal ("1.00")));
    assertEquals (new BigDecimal ("100"),
                  PeppolSKTDD110DocumentLineBuilder.getPriceAmountPerUnit (new BigDecimal ("200"), new BigDecimal ("2")));
    assertEquals (new BigDecimal ("0.25"),
                  PeppolSKTDD110DocumentLineBuilder.getPriceAmountPerUnit (new BigDecimal ("25.00"), new BigDecimal ("100")));
    assertEquals (new BigDecimal ("3.333333333333333"),
                  PeppolSKTDD110DocumentLineBuilder.getPriceAmountPerUnit (new BigDecimal ("10"), new BigDecimal ("3")));
  }
}
