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
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ILogHandler;
import com.helger.base.numeric.BigHelper;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.diagnostics.log.ErrorListLogHandler;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.AllowanceChargeReasonType;

/**
 * Builder for Peppol SK TDD 1.1.0 sub element called "AllowanceCharge".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD110AllowanceChargeBuilder implements IBuilder <AllowanceChargeType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD110AllowanceChargeBuilder.class);

  private ILogHandler m_aLogHandler = ILogHandler.createForLogger (LOGGER);
  private final String m_sDocumentCurrencyCode;
  private boolean m_bCharge;
  private String m_sReasonCode;
  private String m_sReason;
  private BigDecimal m_aMultFactor;
  private BigDecimal m_aAmount;
  private BigDecimal m_aBaseAmount;
  private TaxCategoryType m_aTaxCategory;

  public PeppolSKTDD110AllowanceChargeBuilder (@Nullable final String sDocumentCurrencyCode)
  {
    m_sDocumentCurrencyCode = sDocumentCurrencyCode;
  }

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder initFromUBL (@NonNull final AllowanceChargeType aObj)
  {
    ValueEnforcer.notNull (aObj, "AllowanceCharge");

    if (aObj.getChargeIndicator () != null)
      charge (aObj.getChargeIndicator ().isValue ());
    reasonCode (aObj.getAllowanceChargeReasonCodeValue ());
    if (aObj.hasAllowanceChargeReasonEntries ())
      reason (aObj.getAllowanceChargeReasonAtIndex (0).getValue ());
    multiplicationFactor (aObj.getMultiplierFactorNumericValue ());
    amount (aObj.getAmountValue ());
    baseAmount (aObj.getBaseAmountValue ());
    if (aObj.hasTaxCategoryEntries ())
      taxCategory (x -> x.initFromUBL (aObj.getTaxCategoryAtIndex (0))
                         // BT-120 and BT-121 are not allowed here (ibr-tdd-63)
                         .taxExemptionReason (null)
                         .taxExemptionReasonCode (null));

    return this;
  }

  public boolean charge ()
  {
    return m_bCharge;
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder charge (final boolean b)
  {
    m_bCharge = b;
    return this;
  }

  @Nullable
  public String reasonCode ()
  {
    return m_sReasonCode;
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder reasonCode (@Nullable final String s)
  {
    m_sReasonCode = s;
    return this;
  }

  @Nullable
  public String reason ()
  {
    return m_sReason;
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder reason (@Nullable final String s)
  {
    m_sReason = s;
    return this;
  }

  @Nullable
  public BigDecimal multiplicationFactor ()
  {
    return m_aMultFactor;
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder multiplicationFactor (final long n)
  {
    return multiplicationFactor (BigHelper.toBigDecimal (n));
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder multiplicationFactor (@Nullable final BigDecimal a)
  {
    m_aMultFactor = a;
    return this;
  }

  @Nullable
  public BigDecimal amount ()
  {
    return m_aAmount;
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder amount (final long n)
  {
    return amount (BigHelper.toBigDecimal (n));
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder amount (@Nullable final BigDecimal a)
  {
    m_aAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal baseAmount ()
  {
    return m_aBaseAmount;
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder baseAmount (final long n)
  {
    return baseAmount (BigHelper.toBigDecimal (n));
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder baseAmount (@Nullable final BigDecimal a)
  {
    m_aBaseAmount = a;
    return this;
  }

  @Nullable
  public TaxCategoryType taxCategory ()
  {
    return m_aTaxCategory;
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder taxCategory (@Nullable final TaxCategoryType a)
  {
    m_aTaxCategory = a;
    return this;
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder taxCategory (@Nullable final PeppolSKTDD110TaxCategoryBuilder a)
  {
    return taxCategory (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD110AllowanceChargeBuilder taxCategory (@NonNull final Consumer <? super PeppolSKTDD110TaxCategoryBuilder> aBuilderConsumer)
  {
    final PeppolSKTDD110TaxCategoryBuilder aBuilder = new PeppolSKTDD110TaxCategoryBuilder ();
    aBuilder.logHandler (m_aLogHandler);
    aBuilderConsumer.accept (aBuilder);
    return taxCategory (aBuilder);
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
  public PeppolSKTDD110AllowanceChargeBuilder logHandler (@NonNull final ILogHandler aLogHandler)
  {
    ValueEnforcer.notNull (aLogHandler, "LogHandler");
    m_aLogHandler = aLogHandler;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (@NonNull final ILogHandler aLogHandler, @NonNull final MutableInt aErrorCount)
  {
    final String sErrorPrefix = "Error in Peppol SK TDD 1.1.0 AllowanceCharge builder: ";

    // m_sReasonCode is optional
    // m_sReason is optional
    // m_aMultFactor is optional
    if (m_aAmount == null)
    {
      aLogHandler.error (sErrorPrefix + "Amount is missing");
      aErrorCount.inc ();
    }
    // m_aBaseAmount is optional
    // m_aTaxCategory is optional

    return aErrorCount.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (@NonNull final ErrorList aErrorList)
  {
    ValueEnforcer.notNull (aErrorList, "ErrorList");
    final MutableInt aErrorCount = new MutableInt (0);
    return _isEveryRequiredFieldSet (new ErrorListLogHandler (aErrorList), aErrorCount);
  }

  @Nullable
  public AllowanceChargeType build ()
  {
    final MutableInt aErrorCount = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (m_aLogHandler, aErrorCount))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD AllowanceCharge cannot be build.");
      return null;
    }

    final AllowanceChargeType ret = new AllowanceChargeType ();
    ret.setChargeIndicator (m_bCharge);
    if (StringHelper.isNotEmpty (m_sReasonCode))
      ret.setAllowanceChargeReasonCode (m_sReasonCode);
    if (StringHelper.isNotEmpty (m_sReason))
      ret.addAllowanceChargeReason (new AllowanceChargeReasonType (m_sReason));
    if (m_aMultFactor != null)
      ret.setMultiplierFactorNumeric (m_aMultFactor);
    ret.setAmount (m_aAmount).setCurrencyID (m_sDocumentCurrencyCode);
    if (m_aBaseAmount != null)
      ret.setBaseAmount (m_aBaseAmount).setCurrencyID (m_sDocumentCurrencyCode);
    ret.addTaxCategory (m_aTaxCategory);

    return ret;
  }
}
