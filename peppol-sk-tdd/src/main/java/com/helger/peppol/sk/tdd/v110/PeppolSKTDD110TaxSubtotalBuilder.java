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
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.diagnostics.log.ErrorListLogHandler;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSubtotalType;

/**
 * Builder for Peppol SK TDD 1.1.0 sub element called "TaxSubtotal".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD110TaxSubtotalBuilder implements IBuilder <TaxSubtotalType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD110TaxSubtotalBuilder.class);

  private ILogHandler m_aLogHandler = ILogHandler.createForLogger (LOGGER);
  private final String m_sCurrencyCode;
  private BigDecimal m_aTaxableAmount;
  private BigDecimal m_aTaxAmount;
  private TaxCategoryType m_aTaxCategory;

  public PeppolSKTDD110TaxSubtotalBuilder (@NonNull final String sCurrencyCode)
  {
    m_sCurrencyCode = sCurrencyCode;
  }

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD110TaxSubtotalBuilder initFromUBL (@NonNull final TaxSubtotalType aObj)
  {
    ValueEnforcer.notNull (aObj, "TaxSubtotal");

    taxableAmount (aObj.getTaxableAmountValue ());
    taxAmount (aObj.getTaxAmountValue ());
    taxCategory (x -> x.initFromUBL (aObj.getTaxCategory ()));
    return this;
  }

  @Nullable
  public BigDecimal taxableAmount ()
  {
    return m_aTaxableAmount;
  }

  @NonNull
  public PeppolSKTDD110TaxSubtotalBuilder taxableAmount (final long n)
  {
    return taxableAmount (BigHelper.toBigDecimal (n));
  }

  @NonNull
  public PeppolSKTDD110TaxSubtotalBuilder taxableAmount (@Nullable final BigDecimal a)
  {
    m_aTaxableAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal taxAmount ()
  {
    return m_aTaxAmount;
  }

  @NonNull
  public PeppolSKTDD110TaxSubtotalBuilder taxAmount (final long n)
  {
    return taxAmount (BigHelper.toBigDecimal (n));
  }

  @NonNull
  public PeppolSKTDD110TaxSubtotalBuilder taxAmount (@Nullable final BigDecimal a)
  {
    m_aTaxAmount = a;
    return this;
  }

  @Nullable
  public TaxCategoryType taxCategory ()
  {
    return m_aTaxCategory;
  }

  @NonNull
  public PeppolSKTDD110TaxSubtotalBuilder taxCategory (@Nullable final TaxCategoryType a)
  {
    m_aTaxCategory = a;
    return this;
  }

  @NonNull
  public PeppolSKTDD110TaxSubtotalBuilder taxCategory (@Nullable final PeppolSKTDD110TaxCategoryBuilder a)
  {
    return taxCategory (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD110TaxSubtotalBuilder taxCategory (@NonNull final Consumer <? super PeppolSKTDD110TaxCategoryBuilder> aBuilderConsumer)
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
  public PeppolSKTDD110TaxSubtotalBuilder logHandler (@NonNull final ILogHandler aLogHandler)
  {
    ValueEnforcer.notNull (aLogHandler, "LogHandler");
    m_aLogHandler = aLogHandler;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (@NonNull final ILogHandler aLogHandler, @NonNull final MutableInt aReportedDocsErrs)
  {
    final String sErrorPrefix = "Error in Peppol SK TDD 1.1.0 TaxSubtotal builder: ";

    if (m_aTaxableAmount == null)
    {
      aLogHandler.error (sErrorPrefix + "TaxableAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aTaxAmount == null)
    {
      aLogHandler.error (sErrorPrefix + "TaxAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aTaxCategory == null)
    {
      aLogHandler.error (sErrorPrefix + "TaxCategory is missing");
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
  public TaxSubtotalType build ()
  {
    final MutableInt aErrorCount = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (m_aLogHandler, aErrorCount))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD TaxSubtotal cannot be build.");
      return null;
    }

    final TaxSubtotalType ret = new TaxSubtotalType ();
    ret.setTaxableAmount (m_aTaxableAmount).setCurrencyID (m_sCurrencyCode);
    ret.setTaxAmount (m_aTaxAmount).setCurrencyID (m_sCurrencyCode);
    ret.setTaxCategory (m_aTaxCategory);
    return ret;
  }
}
