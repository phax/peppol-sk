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

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ILogHandler;
import com.helger.base.numeric.BigHelper;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.datetime.xml.XMLOffsetDate;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.diagnostics.log.ErrorListLogHandler;
import com.helger.peppol.sk.tdd.v2026_04_14.DocumentLineType;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CreditNoteLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PriceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CreditedQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.InvoicedQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.LineExtensionAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;

/**
 * Builder for Peppol SK TDD 1.1.0 sub element called "DocumentLine".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD110DocumentLineBuilder implements IBuilder <DocumentLineType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD110DocumentLineBuilder.class);

  private ILogHandler m_aLogHandler = ILogHandler.createForLogger (LOGGER);
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

  public PeppolSKTDD110DocumentLineBuilder (@Nullable final String sDocumentCurrencyCode)
  {
    m_sDocumentCurrencyCode = sDocumentCurrencyCode;
  }

  /**
   * Get the item net price for one unit of the invoiced quantity: the item net price (BT-146)
   * divided by the item price base quantity (BT-149). Version 1.1.0 does not allow the price base
   * quantity (ibr-tdd-82), and the samples of the specification report such a price this way. It
   * also keeps PEPPOL-EN16931-R120 valid.
   *
   * @param aPriceAmount
   *        Item net price. May be <code>null</code>.
   * @param aBaseQuantity
   *        Item price base quantity. May be <code>null</code>.
   * @return The price amount as is, if no base quantity or a base quantity of 0 or 1 is provided.
   *         May be <code>null</code> if no price amount is provided.
   */
  @Nullable
  public static BigDecimal getPriceAmountPerUnit (@Nullable final BigDecimal aPriceAmount,
                                                  @Nullable final BigDecimal aBaseQuantity)
  {
    if (aPriceAmount == null ||
        aBaseQuantity == null ||
        aBaseQuantity.signum () == 0 ||
        aBaseQuantity.compareTo (BigDecimal.ONE) == 0)
      return aPriceAmount;
    BigDecimal ret = aPriceAmount.divide (aBaseQuantity, MathContext.DECIMAL64);
    // Avoid needless decimal places like in "100.0000000000000"
    ret = ret.stripTrailingZeros ();
    if (ret.scale () < 0)
      ret = ret.setScale (0);
    return ret;
  }

  /**
   * Set all fields from the provided UBL 2.1 Invoice line
   *
   * @param aLine
   *        The Invoice line to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD110DocumentLineBuilder initFromInvoice (@NonNull final InvoiceLineType aLine)
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
        invoicePeriodDescriptionCode (aIP.getDescriptionCodeAtIndex (0).getValue ());
    }

    for (final var aAC : aLine.getAllowanceCharge ())
      addAllowanceCharge (x -> x.initFromUBL (aAC)
                                // cac:TaxCategory is not allowed here (ibr-tdd-60)
                                .taxCategory ((TaxCategoryType) null));

    if (aLine.getItem () != null)
      item (x -> x.initFromUBL (aLine.getItem ()));

    if (aLine.getPrice () != null)
    {
      // The price base quantity (BT-149) is not allowed (ibr-tdd-82) - use the price for one unit
      priceAmount (getPriceAmountPerUnit (aLine.getPrice ().getPriceAmountValue (),
                                          aLine.getPrice ().getBaseQuantityValue ()));
    }

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
  public PeppolSKTDD110DocumentLineBuilder initFromCreditNote (@NonNull final CreditNoteLineType aLine)
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
        invoicePeriodDescriptionCode (aIP.getDescriptionCodeAtIndex (0).getValue ());
    }

    for (final var aAC : aLine.getAllowanceCharge ())
      addAllowanceCharge (x -> x.initFromUBL (aAC)
                                // cac:TaxCategory is not allowed here (ibr-tdd-60)
                                .taxCategory ((TaxCategoryType) null));

    if (aLine.getItem () != null)
      item (x -> x.initFromUBL (aLine.getItem ()));

    if (aLine.getPrice () != null)
    {
      // The price base quantity (BT-149) is not allowed (ibr-tdd-82) - use the price for one unit
      priceAmount (getPriceAmountPerUnit (aLine.getPrice ().getPriceAmountValue (),
                                          aLine.getPrice ().getBaseQuantityValue ()));
    }

    return this;
  }

  @Nullable
  public String id ()
  {
    return m_sID;
  }

  @NonNull
  public PeppolSKTDD110DocumentLineBuilder id (@Nullable final String s)
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
  public PeppolSKTDD110DocumentLineBuilder note (@Nullable final String s)
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
  public PeppolSKTDD110DocumentLineBuilder quantity (final long n)
  {
    return quantity (BigHelper.toBigDecimal (n));
  }

  @NonNull
  public PeppolSKTDD110DocumentLineBuilder quantity (@Nullable final BigDecimal a)
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
  public PeppolSKTDD110DocumentLineBuilder quantityUnit (@Nullable final String s)
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
  public PeppolSKTDD110DocumentLineBuilder lineExtensionAmount (final long n)
  {
    return lineExtensionAmount (BigHelper.toBigDecimal (n));
  }

  @NonNull
  public PeppolSKTDD110DocumentLineBuilder lineExtensionAmount (@Nullable final BigDecimal a)
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
  public PeppolSKTDD110DocumentLineBuilder invoicePeriodStart (@Nullable final LocalDate a)
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
  public PeppolSKTDD110DocumentLineBuilder invoicePeriodEnd (@Nullable final LocalDate a)
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
  public PeppolSKTDD110DocumentLineBuilder invoicePeriodDescriptionCode (@Nullable final String s)
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
  public PeppolSKTDD110DocumentLineBuilder allowanceCharges (@Nullable final ICommonsList <AllowanceChargeType> a)
  {
    m_aAllowanceCharges.setAll (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD110DocumentLineBuilder addAllowanceCharge (@Nullable final AllowanceChargeType a)
  {
    if (a != null)
      m_aAllowanceCharges.add (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD110DocumentLineBuilder addAllowanceCharge (@Nullable final PeppolSKTDD110AllowanceChargeBuilder a)
  {
    return addAllowanceCharge (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD110DocumentLineBuilder addAllowanceCharge (@NonNull final Consumer <? super PeppolSKTDD110AllowanceChargeBuilder> aBuilderConsumer)
  {
    final PeppolSKTDD110AllowanceChargeBuilder aBuilder = new PeppolSKTDD110AllowanceChargeBuilder (m_sDocumentCurrencyCode);
    aBuilder.logHandler (m_aLogHandler);
    aBuilderConsumer.accept (aBuilder);
    return addAllowanceCharge (aBuilder);
  }

  @Nullable
  public ItemType item ()
  {
    return m_aItem;
  }

  @NonNull
  public PeppolSKTDD110DocumentLineBuilder item (@Nullable final ItemType a)
  {
    m_aItem = a;
    return this;
  }

  @NonNull
  public PeppolSKTDD110DocumentLineBuilder item (@Nullable final PeppolSKTDD110ItemBuilder a)
  {
    return item (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD110DocumentLineBuilder item (@NonNull final Consumer <? super PeppolSKTDD110ItemBuilder> aBuilderConsumer)
  {
    final PeppolSKTDD110ItemBuilder aBuilder = new PeppolSKTDD110ItemBuilder ();
    aBuilder.logHandler (m_aLogHandler);
    aBuilderConsumer.accept (aBuilder);
    return item (aBuilder);
  }

  @Nullable
  public BigDecimal priceAmount ()
  {
    return m_aPriceAmount;
  }

  @NonNull
  public PeppolSKTDD110DocumentLineBuilder priceAmount (final long n)
  {
    return priceAmount (BigHelper.toBigDecimal (n));
  }

  @NonNull
  public PeppolSKTDD110DocumentLineBuilder priceAmount (@Nullable final BigDecimal a)
  {
    m_aPriceAmount = a;
    return this;
  }

  /**
   * @return The log handler that receives the messages of the required field checks - errors for
   *         missing or invalid fields. Never <code>null</code>.
   */
  @NonNull
  public ILogHandler logHandler ()
  {
    return m_aLogHandler;
  }

  /**
   * Set the log handler that receives the messages of the required field checks, when calling
   * {@link #build()}. It is not used by
   * {@link #isEveryRequiredFieldSet(ErrorList)}. Missing or invalid required fields are
   * reported as errors. The default handler logs to the logger of this class, use
   * {@link ILogHandler#createForConsumer(java.util.function.Consumer)} or
   * {@link ILogHandler#createNoOp()} to redirect or discard them. The handler is passed on to all
   * sub-builders that are created via a lambda <b>after</b> this call, so call this method before
   * adding any sub-elements. Sub-builders that are created externally and passed in are not
   * changed.
   *
   * @param aLogHandler
   *        The log handler to use. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD110DocumentLineBuilder logHandler (@NonNull final ILogHandler aLogHandler)
  {
    ValueEnforcer.notNull (aLogHandler, "LogHandler");
    m_aLogHandler = aLogHandler;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (@NonNull final ILogHandler aLogHandler, @NonNull final MutableInt aReportedDocsErrs)
  {
    final String sErrorPrefix = "Error in Peppol SK TDD 1.1.0 DocumentLine builder: ";

    if (StringHelper.isEmpty (m_sID))
    {
      aLogHandler.error (sErrorPrefix + "ID is missing");
      aReportedDocsErrs.inc ();
    }
    // m_sNote is optional
    if (m_aQuantity == null)
    {
      aLogHandler.error (sErrorPrefix + "Quantity is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sQuantityUnit))
    {
      aLogHandler.error (sErrorPrefix + "QuantityUnit is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aLineExtensionAmount == null)
    {
      aLogHandler.error (sErrorPrefix + "LineExtensionAmount is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aInvoicePeriodStart is optional
    // m_aInvoicePeriodEnd is optional
    // m_sInvoicePeriodDescriptionCode is optional
    // m_aAllowanceCharges may be empty
    if (m_aItem == null)
    {
      aLogHandler.error (sErrorPrefix + "Item is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aPriceAmount == null)
    {
      aLogHandler.error (sErrorPrefix + "PriceAmount is missing");
      aReportedDocsErrs.inc ();
    }

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (@NonNull final ErrorList aErrorList)
  {
    ValueEnforcer.notNull (aErrorList, "ErrorList");
    final MutableInt aErrorCount = new MutableInt (0);
    return _isEveryRequiredFieldSet (new ErrorListLogHandler (aErrorList), aErrorCount);
  }

  @Nullable
  public DocumentLineType build ()
  {
    final MutableInt aErrorCount = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (m_aLogHandler, aErrorCount))
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
