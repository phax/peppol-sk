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
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.datetime.xml.XMLOffsetDate;
import com.helger.peppol.sk.tdd.v2026_02_20.DocumentLineType;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CreditNoteLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PriceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CreditedQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.InvoicedQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.LineExtensionAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;

/**
 * Builder for Peppol SK TDD 1.0.0 sub element called "DocumentLine".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD100DocumentLineBuilder implements IBuilder <DocumentLineType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD100DocumentLineBuilder.class);

  private final String m_sDocumentCurrencyCode;
  private String m_sID;
  private String m_sNote;
  private BigDecimal m_aQuantity;
  private String m_sQuantityUnit;
  private BigDecimal m_aLineExtensionAmount;
  private LocalDate m_aInvoicePeriodStart;
  private LocalDate m_aInvoicePeriodEnd;
  private String m_sInvoicePeriodDescriptionCode;
  private final ICommonsList <AllowanceChargeType> m_aAllowanceCharges = new CommonsArrayList <> ();
  private ItemType m_aItem;
  private BigDecimal m_aPriceAmount;

  public PeppolSKTDD100DocumentLineBuilder (@Nullable final String sDocumentCurrencyCode)
  {
    m_sDocumentCurrencyCode = sDocumentCurrencyCode;
  }

  /**
   * Set all fields from the provided UBL 2.1 Invoice line
   *
   * @param aLine
   *        The Invoice line to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD100DocumentLineBuilder initFromInvoice (@NonNull final InvoiceLineType aLine)
  {
    ValueEnforcer.notNull (aLine, "InvoiceLine");

    id (aLine.getIDValue ());
    if (aLine.hasNoteEntries ())
      note (aLine.getNoteAtIndex (0).getValue ());

    final InvoicedQuantityType aIQ = aLine.getInvoicedQuantity ();
    if (aIQ != null)
    {
      quantity (aIQ.getValue ());
      quantityUnit (aIQ.getUnitCode ());
    }

    lineExtensionAmount (aLine.getLineExtensionAmountValue ());

    if (aLine.hasInvoicePeriodEntries ())
    {
      final PeriodType aIP = aLine.getInvoicePeriodAtIndex (0);
      invoicePeriodStart (aIP.getStartDateValueLocal ());
      invoicePeriodEnd (aIP.getEndDateValueLocal ());
      if (aIP.hasDescriptionCodeEntries ())
        invoicePeriodDescriptionCode (aIP.getDescriptionAtIndex (0).getValue ());
    }

    for (final var aAC : aLine.getAllowanceCharge ())
      addAllowanceCharge (x -> x.initFromUBL (aAC));

    if (aLine.getItem () != null)
      item (x -> x.initFromUBL (aLine.getItem ()));

    if (aLine.getPrice () != null)
      priceAmount (aLine.getPrice ().getPriceAmountValue ());

    return this;
  }

  /**
   * Set all fields from the provided UBL 2.1 CreditNote line
   *
   * @param aLine
   *        The CreditNote line to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD100DocumentLineBuilder initFromCreditNote (@NonNull final CreditNoteLineType aLine)
  {
    ValueEnforcer.notNull (aLine, "CreditNoteLine");

    id (aLine.getIDValue ());
    if (aLine.hasNoteEntries ())
      note (aLine.getNoteAtIndex (0).getValue ());

    final CreditedQuantityType aIQ = aLine.getCreditedQuantity ();
    if (aIQ != null)
    {
      quantity (aIQ.getValue ());
      quantityUnit (aIQ.getUnitCode ());
    }

    lineExtensionAmount (aLine.getLineExtensionAmountValue ());

    if (aLine.hasInvoicePeriodEntries ())
    {
      final PeriodType aIP = aLine.getInvoicePeriodAtIndex (0);
      invoicePeriodStart (aIP.getStartDateValueLocal ());
      invoicePeriodEnd (aIP.getEndDateValueLocal ());
      if (aIP.hasDescriptionCodeEntries ())
        invoicePeriodDescriptionCode (aIP.getDescriptionAtIndex (0).getValue ());
    }

    for (final var aAC : aLine.getAllowanceCharge ())
      addAllowanceCharge (x -> x.initFromUBL (aAC));

    if (aLine.getItem () != null)
      item (x -> x.initFromUBL (aLine.getItem ()));

    if (aLine.getPrice () != null)
      priceAmount (aLine.getPrice ().getPriceAmountValue ());

    return this;
  }

  @Nullable
  public String id ()
  {
    return m_sID;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder id (@Nullable final String s)
  {
    m_sID = s;
    return this;
  }

  @Nullable
  public String note ()
  {
    return m_sNote;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder note (@Nullable final String s)
  {
    m_sNote = s;
    return this;
  }

  @Nullable
  public BigDecimal quantity ()
  {
    return m_aQuantity;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder quantity (@Nullable final BigDecimal a)
  {
    m_aQuantity = a;
    return this;
  }

  @Nullable
  public String quantityUnit ()
  {
    return m_sQuantityUnit;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder quantityUnit (@Nullable final String s)
  {
    m_sQuantityUnit = s;
    return this;
  }

  @Nullable
  public BigDecimal lineExtensionAmount ()
  {
    return m_aLineExtensionAmount;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder lineExtensionAmount (@Nullable final BigDecimal a)
  {
    m_aLineExtensionAmount = a;
    return this;
  }

  @Nullable
  public LocalDate invoicePeriodStart ()
  {
    return m_aInvoicePeriodStart;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder invoicePeriodStart (@Nullable final LocalDate a)
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
  public PeppolSKTDD100DocumentLineBuilder invoicePeriodEnd (@Nullable final LocalDate a)
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
  public PeppolSKTDD100DocumentLineBuilder invoicePeriodDescriptionCode (@Nullable final String s)
  {
    m_sInvoicePeriodDescriptionCode = s;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <AllowanceChargeType> allowanceCharges ()
  {
    return m_aAllowanceCharges;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder allowanceCharges (@Nullable final ICommonsList <AllowanceChargeType> a)
  {
    m_aAllowanceCharges.setAll (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder addAllowanceCharge (@Nullable final AllowanceChargeType a)
  {
    if (a != null)
      m_aAllowanceCharges.add (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder addAllowanceCharge (@Nullable final PeppolSKTDD100AllowanceChargeBuilder a)
  {
    return addAllowanceCharge (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder addAllowanceCharge (@NonNull final Consumer <PeppolSKTDD100AllowanceChargeBuilder> aBuilderConsumer)
  {
    final PeppolSKTDD100AllowanceChargeBuilder aBuilder = new PeppolSKTDD100AllowanceChargeBuilder (m_sDocumentCurrencyCode);
    aBuilderConsumer.accept (aBuilder);
    return addAllowanceCharge (aBuilder);
  }

  @Nullable
  public ItemType item ()
  {
    return m_aItem;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder item (@Nullable final ItemType a)
  {
    m_aItem = a;
    return this;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder item (@Nullable final PeppolSKTDD100ItemBuilder a)
  {
    return item (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder item (@NonNull final Consumer <PeppolSKTDD100ItemBuilder> aBuilderConsumer)
  {
    final PeppolSKTDD100ItemBuilder aBuilder = new PeppolSKTDD100ItemBuilder ();
    aBuilderConsumer.accept (aBuilder);
    return item (aBuilder);
  }

  @Nullable
  public BigDecimal priceAmount ()
  {
    return m_aPriceAmount;
  }

  @NonNull
  public PeppolSKTDD100DocumentLineBuilder priceAmount (@Nullable final BigDecimal a)
  {
    m_aPriceAmount = a;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol SK TDD 1.0.0 DocumentLine builder: ";

    if (StringHelper.isEmpty (m_sID))
    {
      aCondLog.error (sErrorPrefix + "ID is missing");
      aReportedDocsErrs.inc ();
    }
    // m_sNote is optional
    if (m_aQuantity == null)
    {
      aCondLog.error (sErrorPrefix + "Quantity is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sQuantityUnit))
    {
      aCondLog.error (sErrorPrefix + "QuantityUnit is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aLineExtensionAmount == null)
    {
      aCondLog.error (sErrorPrefix + "LineExtensionAmount is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aInvoicePeriodStart is optional
    // m_aInvoicePeriodEnd is optional
    // m_sInvoicePeriodDescriptionCode is optional
    // m_aAllowanceCharges may be empty
    if (m_aItem == null)
    {
      aCondLog.error (sErrorPrefix + "Item is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aPriceAmount == null)
    {
      aCondLog.error (sErrorPrefix + "PriceAmount is missing");
      aReportedDocsErrs.inc ();
    }

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aReportedDocErrs);
  }

  @Nullable
  public DocumentLineType build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD DocumentLine cannot be build.");
      return null;
    }

    final DocumentLineType ret = new DocumentLineType ();
    ret.setID (new IDType (m_sID));
    if (StringHelper.isNotEmpty (m_sNote))
      ret.setNote (new NoteType (m_sNote));
    {
      final InvoicedQuantityType a = new InvoicedQuantityType ();
      a.setValue (m_aQuantity);
      a.setUnitCode (m_sQuantityUnit);
      ret.setInvoicedQuantity (a);
    }
    {
      final LineExtensionAmountType aLEA = new LineExtensionAmountType (m_aLineExtensionAmount);
      aLEA.setCurrencyID (m_sDocumentCurrencyCode);
      ret.setLineExtensionAmount (aLEA);
    }

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
      ret.setInvoicePeriod (aIP);
    }

    ret.setAllowanceCharge (m_aAllowanceCharges);
    ret.setItem (m_aItem);
    {
      final PriceType a = new PriceType ();
      a.setPriceAmount (m_aPriceAmount).setCurrencyID (m_sDocumentCurrencyCode);
      ret.setPrice (a);
    }

    return ret;
  }
}
