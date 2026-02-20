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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TaxExemptionReasonType;

/**
 * Builder for Peppol SK TDD 1.0.0 sub element called "TaxSubtotal".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD100TaxSubtotalBuilder implements IBuilder <TaxSubtotalType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD100TaxSubtotalBuilder.class);

  private final String m_sCurrencyCode;
  private BigDecimal m_aTaxableAmount;
  private BigDecimal m_aTaxAmount;
  private String m_sTaxCategoryID;
  private String m_sTaxCategoryIDScheme;
  private BigDecimal m_aPercentage;
  private String m_sTaxExemptionReasonCode;
  private String m_sTaxExemptionReason;
  private String m_sTaxSchemeID;

  public PeppolSKTDD100TaxSubtotalBuilder (@NonNull final String sCurrencyCode)
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
  public PeppolSKTDD100TaxSubtotalBuilder initFromUBL (@NonNull final TaxSubtotalType aObj)
  {
    ValueEnforcer.notNull (aObj, "TaxSubtotal");

    taxableAmount (aObj.getTaxableAmountValue ());
    taxAmount (aObj.getTaxAmountValue ());

    final TaxCategoryType aTaxCategory = aObj.getTaxCategory ();
    if (aTaxCategory != null)
    {
      final IDType aID = aTaxCategory.getID ();
      if (aID != null)
      {
        taxCategoryID (aID.getValue ());
        taxCategoryIDScheme (aID.getSchemeID ());
      }
      percentage (aTaxCategory.getPercentValue ());
      taxExemptionReasonCode (aTaxCategory.getTaxExemptionReasonCodeValue ());
      if (aTaxCategory.hasTaxExemptionReasonEntries ())
        taxExemptionReason (aTaxCategory.getTaxExemptionReasonAtIndex (0).getValue ());

      final TaxSchemeType aTS = aTaxCategory.getTaxScheme ();
      if (aTS != null)
        taxSchemeID (aTS.getIDValue ());
    }

    return this;
  }

  @Nullable
  public BigDecimal taxableAmount ()
  {
    return m_aTaxableAmount;
  }

  @NonNull
  public PeppolSKTDD100TaxSubtotalBuilder taxableAmount (@Nullable final BigDecimal a)
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
  public PeppolSKTDD100TaxSubtotalBuilder taxAmount (@Nullable final BigDecimal a)
  {
    m_aTaxAmount = a;
    return this;
  }

  @Nullable
  public String taxCategoryID ()
  {
    return m_sTaxCategoryID;
  }

  @NonNull
  public PeppolSKTDD100TaxSubtotalBuilder taxCategoryID (@Nullable final String s)
  {
    m_sTaxCategoryID = s;
    return this;
  }

  @Nullable
  public String taxCategoryIDScheme ()
  {
    return m_sTaxCategoryIDScheme;
  }

  @NonNull
  public PeppolSKTDD100TaxSubtotalBuilder taxCategoryIDScheme (@Nullable final String s)
  {
    m_sTaxCategoryIDScheme = s;
    return this;
  }

  @Nullable
  public BigDecimal percentage ()
  {
    return m_aPercentage;
  }

  @NonNull
  public PeppolSKTDD100TaxSubtotalBuilder percentage (@Nullable final BigDecimal a)
  {
    m_aPercentage = a;
    return this;
  }

  @Nullable
  public String taxExemptionReasonCode ()
  {
    return m_sTaxExemptionReasonCode;
  }

  @NonNull
  public PeppolSKTDD100TaxSubtotalBuilder taxExemptionReasonCode (@Nullable final String s)
  {
    m_sTaxExemptionReasonCode = s;
    return this;
  }

  @Nullable
  public String taxExemptionReason ()
  {
    return m_sTaxExemptionReason;
  }

  @NonNull
  public PeppolSKTDD100TaxSubtotalBuilder taxExemptionReason (@Nullable final String s)
  {
    m_sTaxExemptionReason = s;
    return this;
  }

  @Nullable
  public String taxSchemeID ()
  {
    return m_sTaxSchemeID;
  }

  @NonNull
  public PeppolSKTDD100TaxSubtotalBuilder taxSchemeID (@Nullable final String s)
  {
    m_sTaxSchemeID = s;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol SK TDD 1.0.0 TaxSubtotal builder: ";

    if (m_aTaxableAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxableAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aTaxAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sTaxCategoryID))
    {
      aCondLog.error (sErrorPrefix + "TaxCategoryID is missing");
      aReportedDocsErrs.inc ();
    }
    // m_sTaxCategoryIDScheme is optional
    // m_aPercentage is optional
    // m_sTaxExemptionReasonCode is optional
    // m_sTaxExemptionReason is optional
    if (StringHelper.isEmpty (m_sTaxSchemeID))
    {
      aCondLog.error (sErrorPrefix + "TaxSchemeID is missing");
      aReportedDocsErrs.inc ();
    }

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aErrorCount = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aErrorCount);
  }

  @Nullable
  public TaxSubtotalType build ()
  {
    final MutableInt aErrorCount = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aErrorCount))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD TaxSubtotal cannot be build.");
      return null;
    }

    final TaxSubtotalType ret = new TaxSubtotalType ();
    ret.setTaxableAmount (m_aTaxableAmount).setCurrencyID (m_sCurrencyCode);
    ret.setTaxAmount (m_aTaxAmount).setCurrencyID (m_sCurrencyCode);
    {
      final var aTC = new TaxCategoryType ();
      aTC.setID (m_sTaxCategoryID).setSchemeID (m_sTaxCategoryIDScheme);
      if (m_aPercentage != null)
        aTC.setPercent (m_aPercentage);
      if (StringHelper.isNotEmpty (m_sTaxExemptionReasonCode))
        aTC.setTaxExemptionReasonCode (m_sTaxExemptionReasonCode);
      if (StringHelper.isNotEmpty (m_sTaxExemptionReason))
        aTC.addTaxExemptionReason (new TaxExemptionReasonType (m_sTaxExemptionReason));
      {
        final var aTS = new TaxSchemeType ();
        aTS.setID (m_sTaxSchemeID);
        aTC.setTaxScheme (aTS);
      }
      ret.setTaxCategory (aTC);
    }
    return ret;
  }
}
