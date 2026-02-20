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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.base.uuid.UUID5Helper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.datetime.helper.PDTFactory;
import com.helger.datetime.web.PDTWebDateHelper;
import com.helger.datetime.xml.XMLOffsetDate;
import com.helger.datetime.xml.XMLOffsetTime;
import com.helger.peppol.sk.tdd.CSKTDD;
import com.helger.peppol.sk.tdd.codelist.ESKTDDDocumentTypeCode;
import com.helger.peppol.sk.tdd.v2026_02_20.DocumentLineType;
import com.helger.peppol.sk.tdd.v2026_02_20.MonetaryTotalType;
import com.helger.peppol.sk.tdd.v2026_02_20.ReportedDocumentType;
import com.helger.peppol.sk.tdd.v2026_02_20.ReportedTransactionType;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.BillingReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DeliveryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyTaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.AllowanceTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ChargeTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CustomizationIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DocumentCurrencyCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueTimeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.LineExtensionAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PayableAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PayableRoundingAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PrepaidAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ProfileIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TaxCurrencyCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TaxExclusiveAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TaxInclusiveAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.UUIDType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Builder for Peppol SK TDD 1.0.0 sub element called "ReportedTransaction".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD100ReportedTransactionBuilder implements IBuilder <ReportedTransactionType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD100ReportedTransactionBuilder.class);

  private final ESKTDDDocumentTypeCode m_eDocumentTypeCode;
  private String m_sCustomizationID;
  private String m_sProfileID;
  private String m_sID;
  private LocalDate m_aIssueDate;
  private OffsetTime m_aIssueTime;
  private String m_sDocumentTypeCode;
  private String m_sNote;
  private String m_sDocumentCurrencyCode;
  private String m_sTaxCurrencyCode;
  private LocalDate m_aInvoicePeriodStart;
  private LocalDate m_aInvoicePeriodEnd;
  private String m_sInvoicePeriodDescriptionCode;
  private final ICommonsList <BillingReferenceType> m_aBillingReferences = new CommonsArrayList <> ();
  private String m_sSellerTaxID;
  private String m_sSellerCountryCode;
  private String m_sBuyerTaxID;
  private String m_sBuyerCountryCode;
  private String m_sTaxRepresentativeID;
  private String m_sTaxRepresentativeCountryCode;
  private LocalDate m_aDeliveryDate;
  private final ICommonsList <PaymentMeansType> m_aPaymentMeans = new CommonsArrayList <> ();
  private final ICommonsList <AllowanceChargeType> m_aAllowanceCharges = new CommonsArrayList <> ();
  private TaxTotalType m_aTaxTotalDocumentCurrency;
  private TaxTotalType m_aTaxTotalTaxCurrency;

  private BigDecimal m_aLineExtensionAmount;
  private BigDecimal m_aTaxExclusiveTotalAmount;
  private BigDecimal m_aTaxInclusiveTotalAmount;
  private BigDecimal m_aAllowanceTotalAmount;
  private BigDecimal m_aChargeTotalAmount;
  private BigDecimal m_aPrepaidAmount;
  private BigDecimal m_aPayableRoundingAmount;
  private BigDecimal m_aPayableAmount;

  private final ICommonsList <DocumentLineType> m_aDocumentLines = new CommonsArrayList <> ();

  public PeppolSKTDD100ReportedTransactionBuilder (@NonNull final ESKTDDDocumentTypeCode eDocumentTypeCode)
  {
    ValueEnforcer.notNull (eDocumentTypeCode, "DocumentTypeCode");
    m_eDocumentTypeCode = eDocumentTypeCode;
  }

  /**
   * Set all fields from the provided UBL 2.1 Invoice
   *
   * @param aInv
   *        The Invoice to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder initFromInvoice (@NonNull final InvoiceType aInv)
  {
    ValueEnforcer.notNull (aInv, "Invoice");

    customizationID (aInv.getCustomizationIDValue ());
    profileID (aInv.getProfileIDValue ());
    id (aInv.getIDValue ());
    issueDate (aInv.getIssueDateValueLocal ());
    issueTime (aInv.getIssueTimeValue ());
    documentTypeCode (aInv.getInvoiceTypeCodeValue ());
    if (aInv.hasNoteEntries ())
      note (aInv.getNoteAtIndex (0).getValue ());
    documentCurrencyCode (aInv.getDocumentCurrencyCodeValue ());
    taxCurrencyCode (aInv.getTaxCurrencyCodeValue ());

    if (aInv.hasInvoicePeriodEntries ())
    {
      final PeriodType aIP = aInv.getInvoicePeriodAtIndex (0);
      invoicePeriodStart (aIP.getStartDateValueLocal ());
      invoicePeriodEnd (aIP.getEndDateValueLocal ());
      if (aIP.hasDescriptionCodeEntries ())
        invoicePeriodDescriptionCode (aIP.getDescriptionAtIndex (0).getValue ());
    }

    for (final var aBR : aInv.getBillingReference ())
    {
      final DocumentReferenceType aDocRef = aBR.getInvoiceDocumentReference ();
      final IDType aID = aDocRef.getID ();
      addBillingReference (x -> x.id (aID == null ? null : aID.getValue ())
                                 .idScheme (aID == null ? null : aID.getSchemeID ())
                                 .issueDate (aDocRef.getIssueDateValueLocal ()));
    }

    final SupplierPartyType aSupplier = aInv.getAccountingSupplierParty ();
    if (aSupplier != null)
    {
      final PartyType aParty = aSupplier.getParty ();
      if (aParty != null)
      {
        if (aParty.hasPartyTaxSchemeEntries ())
        {
          final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
          sellerTaxID (aPTS.getCompanyIDValue ());
        }

        final AddressType aPA = aParty.getPostalAddress ();
        if (aPA != null && aPA.getCountry () != null)
          sellerCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
      }
    }

    final CustomerPartyType aCustomer = aInv.getAccountingCustomerParty ();
    if (aCustomer != null)
    {
      final PartyType aParty = aCustomer.getParty ();
      if (aParty != null)
      {
        if (aParty.hasPartyTaxSchemeEntries ())
        {
          final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
          buyerTaxID (aPTS.getCompanyIDValue ());
        }

        final AddressType aPA = aParty.getPostalAddress ();
        if (aPA != null && aPA.getCountry () != null)
          buyerCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
      }
    }

    final PartyType aTaxRep = aInv.getTaxRepresentativeParty ();
    if (aTaxRep != null)
    {
      if (aTaxRep.hasPartyTaxSchemeEntries ())
      {
        final PartyTaxSchemeType aPTS = aTaxRep.getPartyTaxSchemeAtIndex (0);
        taxRepresentativeID (aPTS.getCompanyIDValue ());
      }

      final AddressType aPA = aTaxRep.getPostalAddress ();
      if (aPA != null && aPA.getCountry () != null)
        taxRepresentativeCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
    }

    if (aInv.hasDeliveryEntries ())
    {
      final DeliveryType aDelivery = aInv.getDeliveryAtIndex (0);
      deliveryDate (aDelivery.getActualDeliveryDateValueLocal ());
    }

    for (final var aPM : aInv.getPaymentMeans ())
      addPaymentMeans (x -> x.initFromUBL (aPM));

    for (final var aAC : aInv.getAllowanceCharge ())
      addAllowanceCharge (x -> x.initFromUBL (aAC));

    if (m_sDocumentCurrencyCode != null)
    {
      final var aTT = aInv.getTaxTotal ()
                          .stream ()
                          .filter (x -> m_sDocumentCurrencyCode.equals (x.getTaxAmount ().getCurrencyID ()))
                          .findFirst ()
                          .orElse (null);
      if (aTT != null)
        taxTotalDocumentCurrency (x -> x.initFromUBL (aTT));
    }

    if (m_sTaxCurrencyCode != null)
    {
      final var aTT = aInv.getTaxTotal ()
                          .stream ()
                          .filter (x -> m_sTaxCurrencyCode.equals (x.getTaxAmount ().getCurrencyID ()))
                          .findFirst ()
                          .orElse (null);
      if (aTT != null)
        taxTotalTaxCurrency (x -> x.initFromUBL (aTT));
    }

    final oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType aLegalMonetaryTotal = aInv.getLegalMonetaryTotal ();
    if (aLegalMonetaryTotal != null)
    {
      lineExtensionAmount (aLegalMonetaryTotal.getLineExtensionAmountValue ());
      taxExclusiveTotalAmount (aLegalMonetaryTotal.getTaxExclusiveAmountValue ());
      taxInclusiveTotalAmount (aLegalMonetaryTotal.getTaxInclusiveAmountValue ());
      allowanceTotalAmount (aLegalMonetaryTotal.getAllowanceTotalAmountValue ());
      chargeTotalAmount (aLegalMonetaryTotal.getChargeTotalAmountValue ());
      prepaidAmount (aLegalMonetaryTotal.getPrepaidAmountValue ());
      payableRoundingAmount (aLegalMonetaryTotal.getPayableRoundingAmountValue ());
      payableAmount (aLegalMonetaryTotal.getPayableAmountValue ());
    }

    for (final var aLine : aInv.getInvoiceLine ())
      addDocumentLine (x -> x.initFromInvoice (aLine));

    return this;
  }

  /**
   * Set all fields from the provided UBL 2.1 CreditNote
   *
   * @param aCN
   *        The CreditNote to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder initFromCreditNote (@NonNull final CreditNoteType aCN)
  {
    ValueEnforcer.notNull (aCN, "CreditNote");

    customizationID (aCN.getCustomizationIDValue ());
    profileID (aCN.getProfileIDValue ());
    id (aCN.getIDValue ());
    issueDate (aCN.getIssueDateValueLocal ());
    issueTime (aCN.getIssueTimeValue ());
    documentTypeCode (aCN.getCreditNoteTypeCodeValue ());
    if (aCN.hasNoteEntries ())
      note (aCN.getNoteAtIndex (0).getValue ());
    documentCurrencyCode (aCN.getDocumentCurrencyCodeValue ());
    taxCurrencyCode (aCN.getTaxCurrencyCodeValue ());

    if (aCN.hasInvoicePeriodEntries ())
    {
      final PeriodType aIP = aCN.getInvoicePeriodAtIndex (0);
      invoicePeriodStart (aIP.getStartDateValueLocal ());
      invoicePeriodEnd (aIP.getEndDateValueLocal ());
      if (aIP.hasDescriptionCodeEntries ())
        invoicePeriodDescriptionCode (aIP.getDescriptionAtIndex (0).getValue ());
    }

    for (final var aBR : aCN.getBillingReference ())
    {
      final DocumentReferenceType aDocRef = aBR.getInvoiceDocumentReference ();
      final IDType aID = aDocRef.getID ();
      addBillingReference (x -> x.id (aID == null ? null : aID.getValue ())
                                 .idScheme (aID == null ? null : aID.getSchemeID ())
                                 .issueDate (aDocRef.getIssueDateValueLocal ()));
    }

    final SupplierPartyType aSupplier = aCN.getAccountingSupplierParty ();
    if (aSupplier != null)
    {
      final PartyType aParty = aSupplier.getParty ();
      if (aParty != null)
      {
        if (aParty.hasPartyTaxSchemeEntries ())
        {
          final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
          sellerTaxID (aPTS.getCompanyIDValue ());
        }

        final AddressType aPA = aParty.getPostalAddress ();
        if (aPA != null && aPA.getCountry () != null)
          sellerCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
      }
    }

    final CustomerPartyType aCustomer = aCN.getAccountingCustomerParty ();
    if (aCustomer != null)
    {
      final PartyType aParty = aCustomer.getParty ();
      if (aParty != null)
      {
        if (aParty.hasPartyTaxSchemeEntries ())
        {
          final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
          buyerTaxID (aPTS.getCompanyIDValue ());
        }

        final AddressType aPA = aParty.getPostalAddress ();
        if (aPA != null && aPA.getCountry () != null)
          buyerCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
      }
    }

    final PartyType aTaxRep = aCN.getTaxRepresentativeParty ();
    if (aTaxRep != null)
    {
      if (aTaxRep.hasPartyTaxSchemeEntries ())
      {
        final PartyTaxSchemeType aPTS = aTaxRep.getPartyTaxSchemeAtIndex (0);
        taxRepresentativeID (aPTS.getCompanyIDValue ());
      }

      final AddressType aPA = aTaxRep.getPostalAddress ();
      if (aPA != null && aPA.getCountry () != null)
        taxRepresentativeCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
    }

    if (aCN.hasDeliveryEntries ())
    {
      final DeliveryType aDelivery = aCN.getDeliveryAtIndex (0);
      deliveryDate (aDelivery.getActualDeliveryDateValueLocal ());
    }

    for (final var aPM : aCN.getPaymentMeans ())
      addPaymentMeans (x -> x.initFromUBL (aPM));

    for (final var aAC : aCN.getAllowanceCharge ())
      addAllowanceCharge (x -> x.initFromUBL (aAC));

    if (m_sDocumentCurrencyCode != null)
    {
      final var aTT = aCN.getTaxTotal ()
                         .stream ()
                         .filter (x -> m_sDocumentCurrencyCode.equals (x.getTaxAmount ().getCurrencyID ()))
                         .findFirst ()
                         .orElse (null);
      if (aTT != null)
        taxTotalDocumentCurrency (x -> x.initFromUBL (aTT));
    }

    if (m_sTaxCurrencyCode != null)
    {
      final var aTT = aCN.getTaxTotal ()
                         .stream ()
                         .filter (x -> m_sTaxCurrencyCode.equals (x.getTaxAmount ().getCurrencyID ()))
                         .findFirst ()
                         .orElse (null);
      if (aTT != null)
        taxTotalTaxCurrency (x -> x.initFromUBL (aTT));
    }

    final oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType aLegalMonetaryTotal = aCN.getLegalMonetaryTotal ();
    if (aLegalMonetaryTotal != null)
    {
      lineExtensionAmount (aLegalMonetaryTotal.getLineExtensionAmountValue ());
      taxExclusiveTotalAmount (aLegalMonetaryTotal.getTaxExclusiveAmountValue ());
      taxInclusiveTotalAmount (aLegalMonetaryTotal.getTaxInclusiveAmountValue ());
      allowanceTotalAmount (aLegalMonetaryTotal.getAllowanceTotalAmountValue ());
      chargeTotalAmount (aLegalMonetaryTotal.getChargeTotalAmountValue ());
      prepaidAmount (aLegalMonetaryTotal.getPrepaidAmountValue ());
      payableRoundingAmount (aLegalMonetaryTotal.getPayableRoundingAmountValue ());
      payableAmount (aLegalMonetaryTotal.getPayableAmountValue ());
    }

    for (final var aLine : aCN.getCreditNoteLine ())
      addDocumentLine (x -> x.initFromCreditNote (aLine));

    return this;
  }

  @Nullable
  public String customizationID ()
  {
    return m_sCustomizationID;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder customizationID (@Nullable final String s)
  {
    m_sCustomizationID = s;
    return this;
  }

  @Nullable
  public String profileID ()
  {
    return m_sProfileID;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder profileID (@Nullable final String s)
  {
    m_sProfileID = s;
    return this;
  }

  @Nullable
  public String id ()
  {
    return m_sID;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder id (@Nullable final String s)
  {
    m_sID = s;
    return this;
  }

  @Nullable
  public LocalDate issueDate ()
  {
    return m_aIssueDate;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder issueDate (@Nullable final LocalDate a)
  {
    m_aIssueDate = a;
    return this;
  }

  @Nullable
  public OffsetTime issueTime ()
  {
    return m_aIssueTime;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder issueTime (@Nullable final XMLOffsetTime a)
  {
    return issueTime (a == null ? null : a.toOffsetTime ());
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder issueTime (@Nullable final OffsetTime a)
  {
    // XSD can only handle milliseconds
    m_aIssueTime = PDTFactory.getWithMillisOnly (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder issueDateTime (@Nullable final OffsetDateTime a)
  {
    if (a == null)
      return issueDate (null).issueTime ((OffsetTime) null);
    return issueDate (a.toLocalDate ()).issueTime (a.toOffsetTime ());
  }

  @Nullable
  public String documentTypeCode ()
  {
    return m_sDocumentTypeCode;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder documentTypeCode (@Nullable final String s)
  {
    m_sDocumentTypeCode = s;
    return this;
  }

  @Nullable
  public String note ()
  {
    return m_sNote;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder note (@Nullable final String s)
  {
    m_sNote = s;
    return this;
  }

  @Nullable
  public String documentCurrencyCode ()
  {
    return m_sDocumentCurrencyCode;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder documentCurrencyCode (@Nullable final String s)
  {
    m_sDocumentCurrencyCode = s;
    return this;
  }

  @Nullable
  public String taxCurrencyCode ()
  {
    return m_sTaxCurrencyCode;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder taxCurrencyCode (@Nullable final String s)
  {
    m_sTaxCurrencyCode = s;
    return this;
  }

  @Nullable
  public LocalDate invoicePeriodStart ()
  {
    return m_aInvoicePeriodStart;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder invoicePeriodStart (@Nullable final LocalDate a)
  {
    m_aInvoicePeriodStart = a;
    return this;
  }

  @Nullable
  public LocalDate invoicePeriodEnd ()
  {
    return m_aInvoicePeriodEnd;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder invoicePeriodEnd (@Nullable final LocalDate a)
  {
    m_aInvoicePeriodEnd = a;
    return this;
  }

  @Nullable
  public String invoicePeriodDescriptionCode ()
  {
    return m_sInvoicePeriodDescriptionCode;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder invoicePeriodDescriptionCode (@Nullable final String s)
  {
    m_sInvoicePeriodDescriptionCode = s;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <BillingReferenceType> billingReferences ()
  {
    return m_aBillingReferences;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder billingReferences (@Nullable final ICommonsList <BillingReferenceType> a)
  {
    m_aBillingReferences.setAll (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addBillingReference (@Nullable final BillingReferenceType a)
  {
    if (a != null)
      m_aBillingReferences.add (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addBillingReference (@Nullable final PeppolSKTDD100BillingReferenceBuilder a)
  {
    return addBillingReference (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addBillingReference (@NonNull final Consumer <PeppolSKTDD100BillingReferenceBuilder> a)
  {
    final PeppolSKTDD100BillingReferenceBuilder aBuilder = new PeppolSKTDD100BillingReferenceBuilder ();
    a.accept (aBuilder);
    return addBillingReference (aBuilder);
  }

  @Nullable
  public String sellerTaxID ()
  {
    return m_sSellerTaxID;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder sellerTaxID (@Nullable final String s)
  {
    m_sSellerTaxID = s;
    return this;
  }

  @Nullable
  public String sellerCountryCode ()
  {
    return m_sSellerCountryCode;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder sellerCountryCode (@Nullable final String s)
  {
    m_sSellerCountryCode = s;
    return this;
  }

  @Nullable
  public String buyerTaxID ()
  {
    return m_sBuyerTaxID;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder buyerTaxID (@Nullable final String s)
  {
    m_sBuyerTaxID = s;
    return this;
  }

  @Nullable
  public String buyerCountryCode ()
  {
    return m_sBuyerCountryCode;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder buyerCountryCode (@Nullable final String s)
  {
    m_sBuyerCountryCode = s;
    return this;
  }

  @Nullable
  public String taxRepresentativeID ()
  {
    return m_sTaxRepresentativeID;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder taxRepresentativeID (@Nullable final String s)
  {
    m_sTaxRepresentativeID = s;
    return this;
  }

  @Nullable
  public String taxRepresentativeCountryCode ()
  {
    return m_sTaxRepresentativeCountryCode;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder taxRepresentativeCountryCode (@Nullable final String s)
  {
    m_sTaxRepresentativeCountryCode = s;
    return this;
  }

  @Nullable
  public LocalDate deliveryDate ()
  {
    return m_aDeliveryDate;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder deliveryDate (@Nullable final LocalDate a)
  {
    m_aDeliveryDate = a;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PaymentMeansType> paymentMeans ()
  {
    return m_aPaymentMeans;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder paymentMeans (@Nullable final ICommonsList <PaymentMeansType> a)
  {
    m_aPaymentMeans.setAll (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addPaymentMeans (@Nullable final PaymentMeansType a)
  {
    if (a != null)
      m_aPaymentMeans.add (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addPaymentMeans (@Nullable final PeppolSKTDD100PaymentMeansBuilder a)
  {
    return addPaymentMeans (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addPaymentMeans (@NonNull final Consumer <PeppolSKTDD100PaymentMeansBuilder> a)
  {
    final PeppolSKTDD100PaymentMeansBuilder aBuilder = new PeppolSKTDD100PaymentMeansBuilder ();
    a.accept (aBuilder);
    return addPaymentMeans (aBuilder);
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <AllowanceChargeType> allowanceCharges ()
  {
    return m_aAllowanceCharges;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder allowanceCharges (@Nullable final ICommonsList <AllowanceChargeType> a)
  {
    m_aAllowanceCharges.setAll (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addAllowanceCharge (@Nullable final AllowanceChargeType a)
  {
    if (a != null)
      m_aAllowanceCharges.add (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addAllowanceCharge (@Nullable final PeppolSKTDD100AllowanceChargeBuilder a)
  {
    return addAllowanceCharge (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addAllowanceCharge (@NonNull final Consumer <PeppolSKTDD100AllowanceChargeBuilder> a)
  {
    final PeppolSKTDD100AllowanceChargeBuilder aBuilder = new PeppolSKTDD100AllowanceChargeBuilder (m_sDocumentCurrencyCode);
    a.accept (aBuilder);
    return addAllowanceCharge (aBuilder);
  }

  @Nullable
  public TaxTotalType taxTotalDocumentCurrency ()
  {
    return m_aTaxTotalDocumentCurrency;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder taxTotalDocumentCurrency (@Nullable final TaxTotalType a)
  {
    m_aTaxTotalDocumentCurrency = a;
    return this;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder taxTotalDocumentCurrency (@Nullable final PeppolSKTDD100TaxTotalBuilder a)
  {
    return taxTotalDocumentCurrency (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder taxTotalDocumentCurrency (@NonNull final Consumer <PeppolSKTDD100TaxTotalBuilder> a)
  {
    if (StringHelper.isEmpty (m_sDocumentCurrencyCode))
      throw new IllegalStateException ("The TaxTotal can only be built, after the DocumentCurrencyCode is set!");
    final PeppolSKTDD100TaxTotalBuilder aBuilder = new PeppolSKTDD100TaxTotalBuilder (m_sDocumentCurrencyCode);
    a.accept (aBuilder);
    return taxTotalDocumentCurrency (aBuilder);
  }

  @Nullable
  public TaxTotalType taxTotalTaxCurrency ()
  {
    return m_aTaxTotalTaxCurrency;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder taxTotalTaxCurrency (@Nullable final TaxTotalType a)
  {
    m_aTaxTotalTaxCurrency = a;
    return this;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder taxTotalTaxCurrency (@Nullable final PeppolSKTDD100TaxTotalBuilder a)
  {
    return taxTotalTaxCurrency (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder taxTotalTaxCurrency (@Nullable final Consumer <PeppolSKTDD100TaxTotalBuilder> a)
  {
    if (StringHelper.isEmpty (m_sTaxCurrencyCode))
      throw new IllegalStateException ("The TaxTotal can only be built, after the TaxCurrencyCode is set!");
    final PeppolSKTDD100TaxTotalBuilder aBuilder = new PeppolSKTDD100TaxTotalBuilder (m_sTaxCurrencyCode);
    a.accept (aBuilder);
    return taxTotalTaxCurrency (aBuilder);
  }

  @Nullable
  public BigDecimal lineExtensionAmount ()
  {
    return m_aLineExtensionAmount;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder lineExtensionAmount (@Nullable final BigDecimal a)
  {
    m_aLineExtensionAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal taxExclusiveTotalAmount ()
  {
    return m_aTaxExclusiveTotalAmount;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder taxExclusiveTotalAmount (@Nullable final BigDecimal a)
  {
    m_aTaxExclusiveTotalAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal taxInclusiveTotalAmount ()
  {
    return m_aTaxInclusiveTotalAmount;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder taxInclusiveTotalAmount (@Nullable final BigDecimal a)
  {
    m_aTaxInclusiveTotalAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal allowanceTotalAmount ()
  {
    return m_aAllowanceTotalAmount;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder allowanceTotalAmount (@Nullable final BigDecimal a)
  {
    m_aAllowanceTotalAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal chargeTotalAmount ()
  {
    return m_aChargeTotalAmount;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder chargeTotalAmount (@Nullable final BigDecimal a)
  {
    m_aChargeTotalAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal prepaidAmount ()
  {
    return m_aPrepaidAmount;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder prepaidAmount (@Nullable final BigDecimal a)
  {
    m_aPrepaidAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal payableRoundingAmount ()
  {
    return m_aPayableRoundingAmount;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder payableRoundingAmount (@Nullable final BigDecimal a)
  {
    m_aPayableRoundingAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal payableAmount ()
  {
    return m_aPayableAmount;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder payableAmount (@Nullable final BigDecimal a)
  {
    m_aPayableAmount = a;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <DocumentLineType> documentLines ()
  {
    return m_aDocumentLines;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder documentLines (@Nullable final ICommonsList <DocumentLineType> a)
  {
    m_aDocumentLines.setAll (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addDocumentLine (@Nullable final DocumentLineType a)
  {
    if (a != null)
      m_aDocumentLines.add (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addDocumentLine (@Nullable final PeppolSKTDD100DocumentLineBuilder a)
  {
    return addDocumentLine (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD100ReportedTransactionBuilder addDocumentLine (@NonNull final Consumer <PeppolSKTDD100DocumentLineBuilder> a)
  {
    if (StringHelper.isEmpty (m_sDocumentCurrencyCode))
      throw new IllegalStateException ("The DocumentLine can only be built, after the DocumentCurrencyCode is set!");
    final PeppolSKTDD100DocumentLineBuilder aBuilder = new PeppolSKTDD100DocumentLineBuilder (m_sDocumentCurrencyCode);
    a.accept (aBuilder);
    return addDocumentLine (aBuilder);
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    int nErrs = 0;
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol SK TDD 1.0.0 ReportedTransaction builder: ";

    // TransportHeaderID is optional

    // Check all ReportedDocument fields
    if (StringHelper.isEmpty (m_sCustomizationID))
    {
      aCondLog.error (sErrorPrefix + "CustomizationID is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sProfileID))
    {
      aCondLog.error (sErrorPrefix + "ProfileID is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sID))
    {
      aCondLog.error (sErrorPrefix + "ID is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aIssueDate == null)
    {
      aCondLog.error (sErrorPrefix + "IssueDate is missing");
      aReportedDocsErrs.inc ();
    }
    // IssueTime is optional
    if (StringHelper.isEmpty (m_sDocumentTypeCode))
    {
      aCondLog.error (sErrorPrefix + "DocumentTypeCode is missing");
      aReportedDocsErrs.inc ();
    }
    // Note is optional
    // TaxPointDate is optional
    if (StringHelper.isEmpty (m_sDocumentCurrencyCode))
    {
      aCondLog.error (sErrorPrefix + "DocumentCurrencyCode is missing");
      aReportedDocsErrs.inc ();
    }
    // InvoicePeriod is optional

    // m_aBillingReferences may be empty

    // m_sSellerTaxID is optional
    // m_sSellerCountryCode is optional

    // m_sBuyerTaxID is optional
    // m_sBuyerCountryCode is optional

    // m_sTaxRepresentativeID is optional
    // m_sTaxRepresentativeCountryCode is optional

    // m_aDeliveryDate is optional

    // m_aAllowanceCharges may be empty

    if (m_aTaxTotalDocumentCurrency == null)
    {
      aCondLog.error (sErrorPrefix + "TaxTotalDocumentCurrency is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aTaxTotalTaxCurrency != null)
    {
      if (StringHelper.isEmpty (m_sTaxCurrencyCode))
      {
        aCondLog.error (sErrorPrefix +
                        "If TaxTotalAmountTaxCurrency is provided, TaxCurrencyCode must also be provided");
        aReportedDocsErrs.inc ();
      }
    }
    else
    {
      if (StringHelper.isNotEmpty (m_sTaxCurrencyCode))
      {
        aCondLog.error (sErrorPrefix +
                        "If TaxCurrencyCode is provided, TaxTotalAmountTaxCurrency must also be provided");
        aReportedDocsErrs.inc ();
      }
    }
    if (m_aLineExtensionAmount == null)
    {
      aCondLog.error (sErrorPrefix + "LineExtensionAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aTaxExclusiveTotalAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxExclusiveTotalAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aTaxInclusiveTotalAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxInclusiveTotalAmount is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aAllowanceTotalAmount is optional
    // m_aChargeTotalAmount is optional
    // m_aPrepaidAmount is optional
    // m_aPayableRoundingAmount is optional
    if (m_aPayableAmount == null)
    {
      aCondLog.error (sErrorPrefix + "PayableAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aDocumentLines.isEmpty ())
    {
      aCondLog.error (sErrorPrefix + "At least one DocumentLine is needed");
      aReportedDocsErrs.inc ();
    }

    // Failed TDDs don't need this
    // TODO missing in 1.0.0
    // if (m_eDocumentTypeCode != ESKTDDDocumentTypeCode.DISREGARD)
    nErrs += aReportedDocsErrs.intValue ();

    return nErrs == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aErrorCount = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aErrorCount);
  }

  @Nullable
  public ReportedTransactionType build ()
  {
    final MutableInt aErrorCount = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aErrorCount))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD ReportedTransaction cannot be build.");
      return null;
    }

    final ReportedTransactionType ret = new ReportedTransactionType ();

    // ReportedDocument - optional for FAILED state
    if (m_eDocumentTypeCode != ESKTDDDocumentTypeCode.DISREGARD || aErrorCount.is0 ())
    {
      // The UUID is calculated based on rule ID-BDID-01
      // TODO check if the concatenation is correct
      final UUID aUUID = UUID5Helper.fromUTF8 (CSKTDD.PEPPOL_SK_NAMESPACE,
                                               StringHelper.getNotNull (m_sDocumentTypeCode, "") +
                                                                           StringHelper.getNotNull (m_sID, "") +
                                                                           StringHelper.getNotNull (PDTWebDateHelper.getAsStringXSD (m_aIssueDate),
                                                                                                    "") +
                                                                           StringHelper.getNotNull (m_sSellerTaxID,
                                                                                                    ""));

      final ReportedDocumentType a = new ReportedDocumentType ();
      if (StringHelper.isNotEmpty (m_sCustomizationID))
        a.setCustomizationID (new CustomizationIDType (m_sCustomizationID));
      if (StringHelper.isNotEmpty (m_sProfileID))
        a.setProfileID (new ProfileIDType (m_sProfileID));
      if (StringHelper.isNotEmpty (m_sID))
        a.setID (new IDType (m_sID));
      a.setUUID (new UUIDType (aUUID.toString ()));
      if (m_aIssueDate != null)
        a.setIssueDate (new IssueDateType (XMLOffsetDate.of (m_aIssueDate)));
      if (m_aIssueTime != null)
        a.setIssueTime (new IssueTimeType (XMLOffsetTime.of (m_aIssueTime)));
      if (StringHelper.isNotEmpty (m_sDocumentTypeCode))
        a.setDocumentTypeCode (m_sDocumentTypeCode);
      if (StringHelper.isNotEmpty (m_sNote))
        a.setNote (new NoteType (m_sNote));
      if (StringHelper.isNotEmpty (m_sDocumentCurrencyCode))
        a.setDocumentCurrencyCode (new DocumentCurrencyCodeType (m_sDocumentCurrencyCode));
      if (StringHelper.isNotEmpty (m_sTaxCurrencyCode))
        a.setTaxCurrencyCode (new TaxCurrencyCodeType (m_sTaxCurrencyCode));

      if (m_aInvoicePeriodStart != null ||
          m_aInvoicePeriodEnd != null ||
          StringHelper.isNotEmpty (m_sInvoicePeriodDescriptionCode))
      {
        final PeriodType aIP = new PeriodType ();
        if (m_aInvoicePeriodStart != null)
          aIP.setStartDate (XMLOffsetDate.of (m_aInvoicePeriodStart));
        if (m_aInvoicePeriodEnd != null)
          aIP.setEndDate (XMLOffsetDate.of (m_aInvoicePeriodEnd));
        if (StringHelper.isNotEmpty (m_sInvoicePeriodDescriptionCode))
          aIP.addDescriptionCode (new DescriptionCodeType (m_sInvoicePeriodDescriptionCode));
        a.setInvoicePeriod (aIP);
      }

      a.setBillingReference (m_aBillingReferences);

      {
        final SupplierPartyType a2 = new SupplierPartyType ();
        {
          final PartyType aParty = new PartyType ();
          {
            if (StringHelper.isNotEmpty (m_sSellerCountryCode))
            {
              final AddressType aPA = new AddressType ();
              final CountryType aC = new CountryType ();
              aC.setIdentificationCode (m_sSellerCountryCode);
              aPA.setCountry (aC);
              aParty.setPostalAddress (aPA);
            }

            if (StringHelper.isNotEmpty (m_sSellerTaxID))
            {
              final PartyTaxSchemeType aPTS = new PartyTaxSchemeType ();
              aPTS.setCompanyID (m_sSellerTaxID);
              final TaxSchemeType aTS = new TaxSchemeType ();
              aTS.setID ("VAT");
              aPTS.setTaxScheme (aTS);
              aParty.addPartyTaxScheme (aPTS);
            }
          }
          a2.setParty (aParty);
        }
        a.setAccountingSupplierParty (a2);
      }

      {
        final CustomerPartyType aAccountingCustomer = new CustomerPartyType ();
        {
          final PartyType aParty = new PartyType ();
          if (StringHelper.isNotEmpty (m_sBuyerCountryCode))
          {
            final AddressType aPA = new AddressType ();
            final CountryType aC = new CountryType ();
            aC.setIdentificationCode (m_sBuyerCountryCode);
            aPA.setCountry (aC);
            aParty.setPostalAddress (aPA);
          }

          if (StringHelper.isNotEmpty (m_sBuyerTaxID))
          {
            final PartyTaxSchemeType aPTS = new PartyTaxSchemeType ();
            aPTS.setCompanyID (m_sBuyerTaxID);
            final TaxSchemeType aTS = new TaxSchemeType ();
            aTS.setID ("VAT");
            aPTS.setTaxScheme (aTS);
            aParty.addPartyTaxScheme (aPTS);
          }
          aAccountingCustomer.setParty (aParty);
        }
        a.setAccountingCustomerParty (aAccountingCustomer);
      }

      if (StringHelper.isNotEmpty (m_sTaxRepresentativeID) || StringHelper.isNotEmpty (m_sTaxRepresentativeCountryCode))
      {
        final PartyType aTaxRep = new PartyType ();
        if (StringHelper.isNotEmpty (m_sTaxRepresentativeCountryCode))
        {
          final AddressType aPA = new AddressType ();
          final CountryType aC = new CountryType ();
          aC.setIdentificationCode (m_sTaxRepresentativeCountryCode);
          aPA.setCountry (aC);
          aTaxRep.setPostalAddress (aPA);
        }

        if (StringHelper.isNotEmpty (m_sTaxRepresentativeID))
        {
          final PartyTaxSchemeType aPTS = new PartyTaxSchemeType ();
          aPTS.setCompanyID (m_sTaxRepresentativeID);
          final TaxSchemeType aTS = new TaxSchemeType ();
          aTS.setID ("VAT");
          aPTS.setTaxScheme (aTS);
          aTaxRep.addPartyTaxScheme (aPTS);
        }
        a.setTaxRepresentativeParty (aTaxRep);
      }

      if (m_aDeliveryDate != null)
      {
        final DeliveryType aDel = new DeliveryType ();
        aDel.setActualDeliveryDate (XMLOffsetDate.of (m_aDeliveryDate));
        a.setDelivery (aDel);
      }

      a.setAllowanceCharge (m_aAllowanceCharges);

      a.addTaxTotal (m_aTaxTotalDocumentCurrency);
      if (m_aTaxTotalTaxCurrency != null)
        a.addTaxTotal (m_aTaxTotalTaxCurrency);

      {
        final MonetaryTotalType aMonetaryTotal = new MonetaryTotalType ();
        {
          final LineExtensionAmountType aAmount = new LineExtensionAmountType (m_aLineExtensionAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setLineExtensionAmount (aAmount);
        }
        {
          final TaxExclusiveAmountType aAmount = new TaxExclusiveAmountType (m_aTaxExclusiveTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setTaxExclusiveAmount (aAmount);
        }
        {
          final TaxInclusiveAmountType aAmount = new TaxInclusiveAmountType (m_aTaxInclusiveTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setTaxInclusiveAmount (aAmount);
        }
        if (m_aAllowanceTotalAmount != null)
        {
          final AllowanceTotalAmountType aAmount = new AllowanceTotalAmountType (m_aAllowanceTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setAllowanceTotalAmount (aAmount);
        }
        if (m_aChargeTotalAmount != null)
        {
          final ChargeTotalAmountType aAmount = new ChargeTotalAmountType (m_aChargeTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setChargeTotalAmount (aAmount);
        }
        if (m_aPrepaidAmount != null)
        {
          final PrepaidAmountType aAmount = new PrepaidAmountType (m_aPrepaidAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setPrepaidAmount (aAmount);
        }
        if (m_aPayableRoundingAmount != null)
        {
          final PayableRoundingAmountType aAmount = new PayableRoundingAmountType (m_aPayableRoundingAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setPayableRoundingAmount (aAmount);
        }
        {
          final PayableAmountType aAmount = new PayableAmountType (m_aPayableAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setPayableAmount (aAmount);
        }
        a.setMonetaryTotal (aMonetaryTotal);
      }
      // Set all lines
      a.setDocumentLine (m_aDocumentLines);
      ret.setReportedDocument (a);
    }

    return ret;
  }
}
