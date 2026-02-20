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
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;

/**
 * Builder for Peppol SK TDD 1.0.0 sub element called "TaxTotal".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD100TaxTotalBuilder implements IBuilder <TaxTotalType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD100TaxTotalBuilder.class);

  private final String m_sCurrencyCode;
  private BigDecimal m_aTaxAmount;
  private final ICommonsList <TaxSubtotalType> m_aSubtotals = new CommonsArrayList <> ();

  public PeppolSKTDD100TaxTotalBuilder (@NonNull final String sCurrencyCode)
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
  public PeppolSKTDD100TaxTotalBuilder initFromUBL (@NonNull final TaxTotalType aObj)
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
  public PeppolSKTDD100TaxTotalBuilder taxAmount (@Nullable final BigDecimal a)
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
  public PeppolSKTDD100TaxTotalBuilder subtotals (@Nullable final ICommonsList <TaxSubtotalType> a)
  {
    m_aSubtotals.setAll (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100TaxTotalBuilder addTaxSubtotal (@Nullable final TaxSubtotalType a)
  {
    if (a != null)
      m_aSubtotals.add (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD100TaxTotalBuilder addTaxSubtotal (@Nullable final PeppolSKTDD100TaxSubtotalBuilder a)
  {
    return addTaxSubtotal (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD100TaxTotalBuilder addTaxSubtotal (@NonNull final Consumer <PeppolSKTDD100TaxSubtotalBuilder> aBuilderConsumer)
  {
    final PeppolSKTDD100TaxSubtotalBuilder aBuilder = new PeppolSKTDD100TaxSubtotalBuilder (m_sCurrencyCode);
    aBuilderConsumer.accept (aBuilder);
    return addTaxSubtotal (aBuilder);
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol SK TDD 1.0.0 TaxTotal builder: ";

    if (m_aTaxAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxAmount is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aSubtotals may be empty

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aReportedDocErrs);
  }

  @Nullable
  public TaxTotalType build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
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
