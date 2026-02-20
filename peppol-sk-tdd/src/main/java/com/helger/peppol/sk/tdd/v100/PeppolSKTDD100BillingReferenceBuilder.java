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

import java.time.LocalDate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.datetime.xml.XMLOffsetDate;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.BillingReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;

/**
 * Builder for Peppol SK TDD 1.0.0 sub element called "BillingReference".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD100BillingReferenceBuilder implements IBuilder <BillingReferenceType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD100BillingReferenceBuilder.class);

  private String m_sID;
  private String m_sIDScheme;
  private LocalDate m_aIssueDate;

  public PeppolSKTDD100BillingReferenceBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD100BillingReferenceBuilder initFromUBL (@NonNull final BillingReferenceType aObj)
  {
    ValueEnforcer.notNull (aObj, "BillingReference");

    final DocumentReferenceType aIDR = aObj.getInvoiceDocumentReference ();
    if (aIDR != null)
    {
      final IDType aID = aIDR.getID ();
      if (aID != null)
      {
        id (aID.getValue ());
        idScheme (aID.getSchemeID ());
      }
      issueDate (aIDR.getIssueDateValueLocal ());
    }
    return this;
  }

  @Nullable
  public String id ()
  {
    return m_sID;
  }

  @NonNull
  public PeppolSKTDD100BillingReferenceBuilder id (@Nullable final String s)
  {
    m_sID = s;
    return this;
  }

  @Nullable
  public String idScheme ()
  {
    return m_sIDScheme;
  }

  @NonNull
  public PeppolSKTDD100BillingReferenceBuilder idScheme (@Nullable final String s)
  {
    m_sIDScheme = s;
    return this;
  }

  @Nullable
  public LocalDate issueDate ()
  {
    return m_aIssueDate;
  }

  @NonNull
  public PeppolSKTDD100BillingReferenceBuilder issueDate (@Nullable final LocalDate a)
  {
    m_aIssueDate = a;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol SK TDD 1.0.0 BillingReference builder: ";

    if (StringHelper.isEmpty (m_sID))
    {
      aCondLog.error (sErrorPrefix + "ID is missing");
      aReportedDocsErrs.inc ();
    }
    // m_sIDScheme is optional
    // m_aIssueDate is optional

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aErrorCount = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aErrorCount);
  }

  @Nullable
  public BillingReferenceType build ()
  {
    final MutableInt aErrorCount = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aErrorCount))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD BillingReference cannot be build.");
      return null;
    }

    final BillingReferenceType ret = new BillingReferenceType ();
    {
      final DocumentReferenceType aIDR = new DocumentReferenceType ();
      aIDR.setID (m_sID).setSchemeID (m_sIDScheme);
      if (m_aIssueDate != null)
        aIDR.setIssueDate (XMLOffsetDate.of (m_aIssueDate));
      ret.setInvoiceDocumentReference (aIDR);
    }
    return ret;
  }
}
