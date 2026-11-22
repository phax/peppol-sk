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


import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ILogHandler;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.diagnostics.log.ErrorListLogHandler;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.BranchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CardAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.FinancialAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PaymentMandateType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PaymentIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PaymentMeansCodeType;

/**
 * Builder for Peppol SK TDD 1.1.0 sub element called "PaymentMeans".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD110PaymentMeansBuilder implements IBuilder <PaymentMeansType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD110PaymentMeansBuilder.class);

  private ILogHandler m_aLogHandler = ILogHandler.createForLogger (LOGGER);
  private String m_sPaymentMeansCode;
  private String m_sPaymentMeansCodeName;
  private String m_sPaymentID;
  private String m_sCardPrimaryAccountNumberID;
  private String m_sCardNetworkID;
  private String m_sCardHolderName;
  private String m_sPayeeFinancialAccountID;
  private String m_sPayeeFinancialAccountIDScheme;
  private String m_sPayeeFinancialInstitutionBranchID;
  private String m_sPayeeFinancialInstitutionBranchIDScheme;
  private String m_sPaymentMandateID;
  private String m_sPayerFinancialAccountID;

  public PeppolSKTDD110PaymentMeansBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder initFromUBL (@NonNull final PaymentMeansType aObj)
  {
    ValueEnforcer.notNull (aObj, "PaymentMeans");

    final PaymentMeansCodeType aPMC = aObj.getPaymentMeansCode ();
    if (aPMC != null)
    {
      paymentMeansCode (aPMC.getValue ());
      paymentMeansCodeName (aPMC.getName ());
    }

    if (aObj.hasPaymentIDEntries ())
      paymentID (aObj.getPaymentIDAtIndex (0).getValue ());

    final CardAccountType aCA = aObj.getCardAccount ();
    if (aCA != null)
    {
      cardPrimaryAccountNumberID (aCA.getPrimaryAccountNumberIDValue ());
      cardNetworkID (aCA.getNetworkIDValue ());
      cardHolderName (aCA.getHolderNameValue ());
    }

    final FinancialAccountType aPFA = aObj.getPayeeFinancialAccount ();
    if (aPFA != null)
    {
      IDType aID = aPFA.getID ();
      if (aID != null)
      {
        payeeFinancialAccountID (aID.getValue ());
        payeeFinancialAccountIDScheme (aID.getSchemeID ());
      }

      final BranchType aFIB = aPFA.getFinancialInstitutionBranch ();
      if (aFIB != null)
      {
        aID = aFIB.getID ();
        if (aID != null)
        {
          payeeFinancialInstitutionBranchID (aID.getValue ());
          payeeFinancialInstitutionBranchIDScheme (aID.getSchemeID ());
        }
      }
    }

    final PaymentMandateType aPM = aObj.getPaymentMandate ();
    if (aPM != null)
    {
      // BT-89
      paymentMandateID (aPM.getIDValue ());
      final FinancialAccountType aPayerFA = aPM.getPayerFinancialAccount ();
      if (aPayerFA != null)
      {
        // BT-91
        payerFinancialAccountID (aPayerFA.getIDValue ());
      }
    }

    return this;
  }

  @Nullable
  public String paymentMeansCode ()
  {
    return m_sPaymentMeansCode;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder paymentMeansCode (@Nullable final String s)
  {
    m_sPaymentMeansCode = s;
    return this;
  }

  @Nullable
  public String paymentMeansCodeName ()
  {
    return m_sPaymentMeansCodeName;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder paymentMeansCodeName (@Nullable final String s)
  {
    m_sPaymentMeansCodeName = s;
    return this;
  }

  @Nullable
  public String paymentID ()
  {
    return m_sPaymentID;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder paymentID (@Nullable final String s)
  {
    m_sPaymentID = s;
    return this;
  }

  @Nullable
  public String cardPrimaryAccountNumberID ()
  {
    return m_sCardPrimaryAccountNumberID;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder cardPrimaryAccountNumberID (@Nullable final String s)
  {
    m_sCardPrimaryAccountNumberID = s;
    return this;
  }

  @Nullable
  public String cardNetworkID ()
  {
    return m_sCardNetworkID;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder cardNetworkID (@Nullable final String s)
  {
    m_sCardNetworkID = s;
    return this;
  }

  @Nullable
  public String cardHolderName ()
  {
    return m_sCardHolderName;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder cardHolderName (@Nullable final String s)
  {
    m_sCardHolderName = s;
    return this;
  }

  @Nullable
  public String payeeFinancialAccountID ()
  {
    return m_sPayeeFinancialAccountID;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder payeeFinancialAccountID (@Nullable final String s)
  {
    m_sPayeeFinancialAccountID = s;
    return this;
  }

  @Nullable
  public String payeeFinancialAccountIDScheme ()
  {
    return m_sPayeeFinancialAccountIDScheme;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder payeeFinancialAccountIDScheme (@Nullable final String s)
  {
    m_sPayeeFinancialAccountIDScheme = s;
    return this;
  }

  @Nullable
  public String payeeFinancialInstitutionBranchID ()
  {
    return m_sPayeeFinancialInstitutionBranchID;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder payeeFinancialInstitutionBranchID (@Nullable final String s)
  {
    m_sPayeeFinancialInstitutionBranchID = s;
    return this;
  }

  @Nullable
  public String payeeFinancialInstitutionBranchIDScheme ()
  {
    return m_sPayeeFinancialInstitutionBranchIDScheme;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder payeeFinancialInstitutionBranchIDScheme (@Nullable final String s)
  {
    m_sPayeeFinancialInstitutionBranchIDScheme = s;
    return this;
  }

  @Nullable
  public String paymentMandateID ()
  {
    return m_sPaymentMandateID;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder paymentMandateID (@Nullable final String s)
  {
    m_sPaymentMandateID = s;
    return this;
  }

  @Nullable
  public String payerFinancialAccountID ()
  {
    return m_sPayerFinancialAccountID;
  }

  @NonNull
  public PeppolSKTDD110PaymentMeansBuilder payerFinancialAccountID (@Nullable final String s)
  {
    m_sPayerFinancialAccountID = s;
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
  public PeppolSKTDD110PaymentMeansBuilder logHandler (@NonNull final ILogHandler aLogHandler)
  {
    ValueEnforcer.notNull (aLogHandler, "LogHandler");
    m_aLogHandler = aLogHandler;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (@NonNull final ILogHandler aLogHandler, @NonNull final MutableInt aReportedDocsErrs)
  {
    final String sErrorPrefix = "Error in Peppol SK TDD 1.1.0 PaymentMeans builder: ";

    if (StringHelper.isEmpty (m_sPaymentMeansCode))
    {
      aLogHandler.error (sErrorPrefix + "PaymentMeansCode is missing");
      aReportedDocsErrs.inc ();
    }
    // m_sPaymentMeansCodeName is optional
    // m_sPaymentID is optional
    // m_sCardPrimaryAccountNumberID is optional
    // m_sCardNetworkID is optional
    // m_sCardHolderName is optional
    // m_sPayeeFinancialAccountID is optional
    // m_sPayeeFinancialAccountIDScheme is optional
    // m_sPayeeFinancialInstitutionBranchID is optional
    // m_sPayeeFinancialInstitutionBranchIDScheme is optional
    // m_sPaymentMandateID is optional
    // m_sPayerFinancialAccountID is optional

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (@NonNull final ErrorList aErrorList)
  {
    ValueEnforcer.notNull (aErrorList, "ErrorList");
    final MutableInt aErrorCount = new MutableInt (0);
    return _isEveryRequiredFieldSet (new ErrorListLogHandler (aErrorList), aErrorCount);
  }

  @Nullable
  public PaymentMeansType build ()
  {
    final MutableInt aErrorCount = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (m_aLogHandler, aErrorCount))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD PaymentMeans cannot be build.");
      return null;
    }

    final PaymentMeansType ret = new PaymentMeansType ();
    {
      final PaymentMeansCodeType aPMC = new PaymentMeansCodeType ();
      aPMC.setValue (m_sPaymentMeansCode);
      aPMC.setName (m_sPaymentMeansCodeName);
      ret.setPaymentMeansCode (aPMC);
    }
    if (StringHelper.isNotEmpty (m_sPaymentID))
      ret.addPaymentID (new PaymentIDType (m_sPaymentID));
    if (StringHelper.isNotEmpty (m_sCardPrimaryAccountNumberID))
    {
      final CardAccountType aCA = new CardAccountType ();
      aCA.setPrimaryAccountNumberID (m_sCardPrimaryAccountNumberID);
      aCA.setNetworkID (m_sCardNetworkID);
      aCA.setHolderName (m_sCardHolderName);
      ret.setCardAccount (aCA);
    }
    if (StringHelper.isNotEmpty (m_sPayeeFinancialAccountID))
    {
      final FinancialAccountType aPFA = new FinancialAccountType ();
      aPFA.setID (m_sPayeeFinancialAccountID).setSchemeID (m_sPayeeFinancialAccountIDScheme);
      if (StringHelper.isNotEmpty (m_sPayeeFinancialInstitutionBranchID))
      {
        final BranchType aFIB = new BranchType ();
        aFIB.setID (m_sPayeeFinancialInstitutionBranchID).setSchemeID (m_sPayeeFinancialInstitutionBranchIDScheme);
        aPFA.setFinancialInstitutionBranch (aFIB);
      }
      ret.setPayeeFinancialAccount (aPFA);
    }
    if (StringHelper.isNotEmpty (m_sPaymentMandateID) || StringHelper.isNotEmpty (m_sPayerFinancialAccountID))
    {
      final PaymentMandateType aPM = new PaymentMandateType ();
      if (StringHelper.isNotEmpty (m_sPaymentMandateID))
        aPM.setID (m_sPaymentMandateID);
      if (StringHelper.isNotEmpty (m_sPayerFinancialAccountID))
      {
        final FinancialAccountType aPayerFA = new FinancialAccountType ();
        aPayerFA.setID (m_sPayerFinancialAccountID);
        aPM.setPayerFinancialAccount (aPayerFA);
      }
      ret.setPaymentMandate (aPM);
    }
    return ret;
  }
}
