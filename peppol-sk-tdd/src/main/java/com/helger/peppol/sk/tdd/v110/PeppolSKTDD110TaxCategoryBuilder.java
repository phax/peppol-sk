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

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TaxExemptionReasonType;

/**
 * Builder for Peppol SK TDD 1.1.0 sub element called "TaxCategory".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD110TaxCategoryBuilder implements IBuilder <TaxCategoryType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD110TaxCategoryBuilder.class);

  private ILogHandler m_aLogHandler = ILogHandler.createForLogger (LOGGER);
  private String m_sID;
  private BigDecimal m_aPerc;
  private String m_sTaxSchemeID;
  private String m_sTaxExemptionReason;
  private String m_sTaxExemptionReasonCode;

  public PeppolSKTDD110TaxCategoryBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD110TaxCategoryBuilder initFromUBL (@NonNull final TaxCategoryType aObj)
  {
    ValueEnforcer.notNull (aObj, "TaxCategory");

    id (aObj.getIDValue ());
    percentage (aObj.getPercentValue ());
    final TaxSchemeType aTaxScheme = aObj.getTaxScheme ();
    if (aTaxScheme != null)
      taxSchemeID (aTaxScheme.getIDValue ());

    for (final var aTER : aObj.getTaxExemptionReason ())
      if (StringHelper.isNotEmpty (aTER.getValue ()))
        taxExemptionReason (aTER.getValue ());

    if (aObj.getTaxExemptionReasonCode () != null)
      taxExemptionReasonCode (aObj.getTaxExemptionReasonCode ().getValue ());

    return this;
  }

  @Nullable
  public String id ()
  {
    return m_sID;
  }

  @NonNull
  public PeppolSKTDD110TaxCategoryBuilder id (@Nullable final String s)
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
  public PeppolSKTDD110TaxCategoryBuilder percentage (final long n)
  {
    return percentage (BigHelper.toBigDecimal (n));
  }

  @NonNull
  public PeppolSKTDD110TaxCategoryBuilder percentage (@Nullable final BigDecimal a)
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
  public PeppolSKTDD110TaxCategoryBuilder taxSchemeID_VAT ()
  {
    return taxSchemeID ("VAT");
  }

  @NonNull
  public PeppolSKTDD110TaxCategoryBuilder taxSchemeID (@Nullable final String s)
  {
    m_sTaxSchemeID = s;
    return this;
  }

  @Nullable
  public String taxExemptionReason ()
  {
    return m_sTaxExemptionReason;
  }

  @NonNull
  public PeppolSKTDD110TaxCategoryBuilder taxExemptionReason (@Nullable final String s)
  {
    m_sTaxExemptionReason = s;
    return this;
  }

  @Nullable
  public String taxExemptionReasonCode ()
  {
    return m_sTaxExemptionReasonCode;
  }

  @NonNull
  public PeppolSKTDD110TaxCategoryBuilder taxExemptionReasonCode (@Nullable final String s)
  {
    m_sTaxExemptionReasonCode = s;
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
  public PeppolSKTDD110TaxCategoryBuilder logHandler (@NonNull final ILogHandler aLogHandler)
  {
    ValueEnforcer.notNull (aLogHandler, "LogHandler");
    m_aLogHandler = aLogHandler;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (@NonNull final ILogHandler aLogHandler, @NonNull final MutableInt aReportedDocsErrs)
  {
    final String sErrorPrefix = "Error in Peppol SK TDD 1.1.0 TaxCategory builder: ";

    if (StringHelper.isEmpty (m_sID))
    {
      aLogHandler.error (sErrorPrefix + "ID is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aPercentage is optional
    if (StringHelper.isEmpty (m_sTaxSchemeID))
    {
      aLogHandler.error (sErrorPrefix + "TaxSchemeID is missing");
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
  public TaxCategoryType build ()
  {
    final MutableInt aErrorCount = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (m_aLogHandler, aErrorCount))
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

    if (StringHelper.isNotEmpty (m_sTaxExemptionReason))
    {
      final TaxExemptionReasonType aET = new TaxExemptionReasonType ();
      aET.setValue (m_sTaxExemptionReason);
      ret.addTaxExemptionReason (aET);
    }

    if (StringHelper.isNotEmpty (m_sTaxExemptionReasonCode))
      ret.setTaxExemptionReasonCode (m_sTaxExemptionReasonCode);

    return ret;
  }
}
