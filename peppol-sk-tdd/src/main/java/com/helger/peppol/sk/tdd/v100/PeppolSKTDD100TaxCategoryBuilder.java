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

/**
 * Builder for Peppol SK TDD 1.0.0 sub element called "TaxCategory".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD100TaxCategoryBuilder implements IBuilder <TaxCategoryType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD100TaxCategoryBuilder.class);

  private String m_sID;
  private BigDecimal m_aPerc;
  private String m_sTaxSchemeID;

  public PeppolSKTDD100TaxCategoryBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD100TaxCategoryBuilder initFromUBL (@NonNull final TaxCategoryType aObj)
  {
    ValueEnforcer.notNull (aObj, "TaxCategory");

    id (aObj.getIDValue ());
    percentage (aObj.getPercentValue ());
    final TaxSchemeType aTaxScheme = aObj.getTaxScheme ();
    if (aTaxScheme != null)
      taxSchemeID (aTaxScheme.getIDValue ());

    return this;
  }

  @Nullable
  public String id ()
  {
    return m_sID;
  }

  @NonNull
  public PeppolSKTDD100TaxCategoryBuilder id (@Nullable final String s)
  {
    m_sID = s;
    return this;
  }

  @Nullable
  public BigDecimal percentage ()
  {
    return m_aPerc;
  }

  @NonNull
  public PeppolSKTDD100TaxCategoryBuilder percentage (@Nullable final BigDecimal a)
  {
    m_aPerc = a;
    return this;
  }

  @Nullable
  public String taxSchemeID ()
  {
    return m_sTaxSchemeID;
  }

  @NonNull
  public PeppolSKTDD100TaxCategoryBuilder taxSchemeID (@Nullable final String s)
  {
    m_sTaxSchemeID = s;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol SK TDD 1.0.0 TaxCategory builder: ";

    if (StringHelper.isEmpty (m_sID))
    {
      aCondLog.error (sErrorPrefix + "ID is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aPercentage is optional
    if (StringHelper.isEmpty (m_sTaxSchemeID))
    {
      aCondLog.error (sErrorPrefix + "TaxSchemeID is missing");
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
  public TaxCategoryType build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD TaxCategory cannot be build.");
      return null;
    }

    final TaxCategoryType ret = new TaxCategoryType ();
    ret.setID (m_sID);
    if (m_aPerc != null)
      ret.setPercent (m_aPerc);
    {
      final TaxSchemeType aTS = new TaxSchemeType ();
      aTS.setID (m_sTaxSchemeID);
      ret.setTaxScheme (aTS);
    }
    return ret;
  }
}
