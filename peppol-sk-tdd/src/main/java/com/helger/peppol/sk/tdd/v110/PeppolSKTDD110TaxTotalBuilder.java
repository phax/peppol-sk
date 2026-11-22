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

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ILogHandler;
import com.helger.base.numeric.BigHelper;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.diagnostics.log.ErrorListLogHandler;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;

/**
 * Builder for Peppol SK TDD 1.1.0 sub element called "TaxTotal".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD110TaxTotalBuilder implements IBuilder <TaxTotalType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD110TaxTotalBuilder.class);

  private ILogHandler m_aLogHandler = ILogHandler.createForLogger (LOGGER);
  private final String m_sCurrencyCode;
  private BigDecimal m_aTaxAmount;
  private final ICommonsList <TaxSubtotalType> m_aSubtotals = new CommonsArrayList <> ();

  public PeppolSKTDD110TaxTotalBuilder (@NonNull final String sCurrencyCode)
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
  public PeppolSKTDD110TaxTotalBuilder initFromUBL (@NonNull final TaxTotalType aObj)
  {
    ValueEnforcer.notNull (aObj, "TaxTotal");

    taxAmount (aObj.getTaxAmountValue ());
    for (final var aTS : aObj.getTaxSubtotal ())
      addTaxSubtotal (x -> x.initFromUBL (aTS));

    return this;
  }

  @Nullable
  public BigDecimal taxAmount ()
  {
    return m_aTaxAmount;
  }

  @NonNull
  public PeppolSKTDD110TaxTotalBuilder taxAmount (final long n)
  {
    return taxAmount (BigHelper.toBigDecimal (n));
  }

  @NonNull
  public PeppolSKTDD110TaxTotalBuilder taxAmount (@Nullable final BigDecimal a)
  {
    m_aTaxAmount = a;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <TaxSubtotalType> subtotals ()
  {
    return m_aSubtotals;
  }

  @NonNull
  public PeppolSKTDD110TaxTotalBuilder subtotals (@Nullable final ICommonsList <TaxSubtotalType> a)
  {
    m_aSubtotals.setAll (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD110TaxTotalBuilder addTaxSubtotal (@Nullable final TaxSubtotalType a)
  {
    if (a != null)
      m_aSubtotals.add (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD110TaxTotalBuilder addTaxSubtotal (@Nullable final PeppolSKTDD110TaxSubtotalBuilder a)
  {
    return addTaxSubtotal (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD110TaxTotalBuilder addTaxSubtotal (@NonNull final Consumer <? super PeppolSKTDD110TaxSubtotalBuilder> aBuilderConsumer)
  {
    final PeppolSKTDD110TaxSubtotalBuilder aBuilder = new PeppolSKTDD110TaxSubtotalBuilder (m_sCurrencyCode);
    aBuilder.logHandler (m_aLogHandler);
    aBuilderConsumer.accept (aBuilder);
    return addTaxSubtotal (aBuilder);
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
  public PeppolSKTDD110TaxTotalBuilder logHandler (@NonNull final ILogHandler aLogHandler)
  {
    ValueEnforcer.notNull (aLogHandler, "LogHandler");
    m_aLogHandler = aLogHandler;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (@NonNull final ILogHandler aLogHandler, @NonNull final MutableInt aReportedDocsErrs)
  {
    final String sErrorPrefix = "Error in Peppol SK TDD 1.1.0 TaxTotal builder: ";

    if (m_aTaxAmount == null)
    {
      aLogHandler.error (sErrorPrefix + "TaxAmount is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aSubtotals may be empty

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (@NonNull final ErrorList aErrorList)
  {
    ValueEnforcer.notNull (aErrorList, "ErrorList");
    final MutableInt aErrorCount = new MutableInt (0);
    return _isEveryRequiredFieldSet (new ErrorListLogHandler (aErrorList), aErrorCount);
  }

  @Nullable
  public TaxTotalType build ()
  {
    final MutableInt aErrorCount = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (m_aLogHandler, aErrorCount))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD TaxTotal cannot be build.");
      return null;
    }

    final TaxTotalType ret = new TaxTotalType ();
    ret.setTaxAmount (m_aTaxAmount).setCurrencyID (m_sCurrencyCode);
    ret.setTaxSubtotal (m_aSubtotals);
    return ret;
  }
}
