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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ILogHandler;
import com.helger.base.string.StringHelper;
import com.helger.datetime.helper.PDTFactory;
import com.helger.datetime.xml.XMLOffsetDate;
import com.helger.datetime.xml.XMLOffsetTime;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.diagnostics.log.ErrorListLogHandler;
import com.helger.peppol.sk.tdd.codelist.ESKTDDDocumentScope;
import com.helger.peppol.sk.tdd.codelist.ESKTDDReporterRole;
import com.helger.peppol.sk.tdd.codelist.ESKTDDTaxDataTypeCode;
import com.helger.peppol.sk.tdd.v2026_04_14.ReferencedDocumentTypeCodeType;
import com.helger.peppol.sk.tdd.v2026_04_14.ReportedTransactionType;
import com.helger.peppol.sk.tdd.v2026_04_14.TaxAuthorityType;
import com.helger.peppol.sk.tdd.v2026_04_14.TaxDataDocumentReporterRoleType;
import com.helger.peppol.sk.tdd.v2026_04_14.TaxDataDocumentScopeType;
import com.helger.peppol.sk.tdd.v2026_04_14.TaxDataType;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CustomizationIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueTimeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ProfileIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.UUIDType;

/**
 * Builder for Peppol SK TDD 1.1.0 document.
 *
 * @author Philip Helger
 */
public class PeppolSKTDD110Builder implements IBuilder <TaxDataType>
{
  public static final String DEFAULT_CUSTOMIZATION_ID = "urn:peppol:taxdata:sk-1";
  public static final String DEFAULT_PROFILE_ID = "urn:peppol:taxreporting";

  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD110Builder.class);

  private ILogHandler m_aLogHandler = ILogHandler.createForLogger (LOGGER);
  private String m_sCustomizationID;
  private String m_sProfileID;
  private String m_sUUID;
  private LocalDate m_aIssueDate;
  private OffsetTime m_aIssueTime;
  private ESKTDDTaxDataTypeCode m_eTaxDataTypeCode;
  private ESKTDDDocumentScope m_eDocumentScope;
  private ESKTDDReporterRole m_eReporterRole;
  private String m_sTaxAuthorityID;
  private String m_sTaxAuthorityName;
  private IParticipantIdentifier m_aReportingParty;
  private IParticipantIdentifier m_aReceivingParty;
  private IParticipantIdentifier m_aReportersRepresentative;
  private ReportedTransactionType m_aReportedTransaction;

  public PeppolSKTDD110Builder ()
  {
    customizationID (DEFAULT_CUSTOMIZATION_ID);
    profileID (DEFAULT_PROFILE_ID);
    randomUUID ();
    issueDateTimeNow ();
  }

  @Nullable
  public String customizationID ()
  {
    return m_sCustomizationID;
  }

  @NonNull
  public PeppolSKTDD110Builder customizationID (@Nullable final String s)
  {
    m_sCustomizationID = s;
    return this;
  }

  @Nullable
  public String profileID ()
  {
    return m_sProfileID;
  }

  @NonNull
  public PeppolSKTDD110Builder profileID (@Nullable final String s)
  {
    m_sProfileID = s;
    return this;
  }

  @Nullable
  public String uuid ()
  {
    return m_sUUID;
  }

  @NonNull
  public PeppolSKTDD110Builder uuid (@Nullable final String s)
  {
    m_sUUID = s;
    return this;
  }

  @NonNull
  public PeppolSKTDD110Builder randomUUID ()
  {
    // UUID v4 - as in ID-BDID-02
    return uuid (UUID.randomUUID ().toString ());
  }

  @Nullable
  public LocalDate issueDate ()
  {
    return m_aIssueDate;
  }

  @NonNull
  public PeppolSKTDD110Builder issueDateNow ()
  {
    return issueDate (PDTFactory.getCurrentLocalDate ());
  }

  @NonNull
  public PeppolSKTDD110Builder issueDate (@Nullable final LocalDate a)
  {
    m_aIssueDate = a;
    return this;
  }

  @Nullable
  public OffsetTime issueTime ()
  {
    return m_aIssueTime;
  }

  @NonNull
  public PeppolSKTDD110Builder issueTimeNow ()
  {
    return issueTime (PDTFactory.getCurrentOffsetTime ());
  }

  @NonNull
  public PeppolSKTDD110Builder issueTime (@Nullable final OffsetTime a)
  {
    // XSD can only handle milliseconds
    m_aIssueTime = PDTFactory.getWithMillisOnly (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD110Builder issueDateTime (@Nullable final OffsetDateTime a)
  {
    if (a == null)
      return issueDate (null).issueTime (null);
    return issueDate (a.toLocalDate ()).issueTime (a.toOffsetTime ());
  }

  @NonNull
  public PeppolSKTDD110Builder issueDateTimeNow ()
  {
    return issueDateTime (PDTFactory.getCurrentOffsetDateTime ());
  }

  @Nullable
  public ESKTDDTaxDataTypeCode taxDataTypeCode ()
  {
    return m_eTaxDataTypeCode;
  }

  @NonNull
  public PeppolSKTDD110Builder taxDataTypeCode (@Nullable final ESKTDDTaxDataTypeCode e)
  {
    m_eTaxDataTypeCode = e;
    return this;
  }

  @Nullable
  public ESKTDDDocumentScope documentScope ()
  {
    return m_eDocumentScope;
  }

  @NonNull
  public PeppolSKTDD110Builder documentScope (@Nullable final ESKTDDDocumentScope e)
  {
    m_eDocumentScope = e;
    return this;
  }

  @Nullable
  public ESKTDDReporterRole reporterRole ()
  {
    return m_eReporterRole;
  }

  @NonNull
  public PeppolSKTDD110Builder reporterRole (@Nullable final ESKTDDReporterRole e)
  {
    m_eReporterRole = e;
    return this;
  }

  @Nullable
  public String taxAuthorityID ()
  {
    return m_sTaxAuthorityID;
  }

  @NonNull
  public PeppolSKTDD110Builder taxAuthorityID (@Nullable final String s)
  {
    m_sTaxAuthorityID = s;
    return this;
  }

  @Nullable
  public String taxAuthorityName ()
  {
    return m_sTaxAuthorityName;
  }

  @NonNull
  public PeppolSKTDD110Builder taxAuthorityName (@Nullable final String s)
  {
    m_sTaxAuthorityName = s;
    return this;
  }

  @Nullable
  public IParticipantIdentifier reportingParty ()
  {
    return m_aReportingParty;
  }

  /**
   * @param a
   *        Peppol Participant ID of C1/C4 of the business document.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD110Builder reportingParty (@Nullable final IParticipantIdentifier a)
  {
    m_aReportingParty = a;
    return this;
  }

  @Nullable
  public IParticipantIdentifier receivingParty ()
  {
    return m_aReceivingParty;
  }

  /**
   * @param a
   *        Peppol Participant ID of C5 of the TDD.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD110Builder receivingParty (@Nullable final IParticipantIdentifier a)
  {
    m_aReceivingParty = a;
    return this;
  }

  @Nullable
  public IParticipantIdentifier reportersRepresentative ()
  {
    return m_aReportersRepresentative;
  }

  /**
   * @param a
   *        Peppol Participant ID of C2/C3 of the business document. Must use the SPIS scheme.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD110Builder reportersRepresentative (@Nullable final IParticipantIdentifier a)
  {
    m_aReportersRepresentative = a;
    return this;
  }

  @Nullable
  public ReportedTransactionType reportedTransaction ()
  {
    return m_aReportedTransaction;
  }

  @NonNull
  public PeppolSKTDD110Builder reportedTransaction (@Nullable final ReportedTransactionType a)
  {
    m_aReportedTransaction = a;
    return this;
  }

  @NonNull
  public PeppolSKTDD110Builder reportedTransaction (@Nullable final PeppolSKTDD110ReportedTransactionBuilder a)
  {
    return reportedTransaction (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD110Builder reportedTransaction (@NonNull final Consumer <? super PeppolSKTDD110ReportedTransactionBuilder> aBuilderConsumer)
  {
    if (m_eTaxDataTypeCode == null)
      throw new IllegalStateException ("The ReportedTransaction can only be built, after the TaxDataTypeCode is set!");
    final PeppolSKTDD110ReportedTransactionBuilder aBuilder = new PeppolSKTDD110ReportedTransactionBuilder (m_eTaxDataTypeCode);
    aBuilder.logHandler (m_aLogHandler);
    aBuilderConsumer.accept (aBuilder);
    return reportedTransaction (aBuilder);
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
  public PeppolSKTDD110Builder logHandler (@NonNull final ILogHandler aLogHandler)
  {
    ValueEnforcer.notNull (aLogHandler, "LogHandler");
    m_aLogHandler = aLogHandler;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (@NonNull final ILogHandler aLogHandler)
  {
    int nErrs = 0;
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final String sErrorPrefix = "Error in Peppol SK TDD 1.1.0 builder: ";

    if (StringHelper.isEmpty (m_sCustomizationID))
    {
      aLogHandler.error (sErrorPrefix + "CustomizationID is missing");
      nErrs++;
    }
    if (StringHelper.isEmpty (m_sProfileID))
    {
      aLogHandler.error (sErrorPrefix + "ProfileID is missing");
      nErrs++;
    }
    if (StringHelper.isEmpty (m_sUUID))
    {
      aLogHandler.error (sErrorPrefix + "UUID is missing");
      nErrs++;
    }
    if (m_aIssueDate == null)
    {
      aLogHandler.error (sErrorPrefix + "IssueDate is missing");
      nErrs++;
    }
    if (m_aIssueTime == null)
    {
      aLogHandler.error (sErrorPrefix + "IssueTime is missing");
      nErrs++;
    }
    if (m_eTaxDataTypeCode == null)
    {
      aLogHandler.error (sErrorPrefix + "DocumentTypeCode is missing");
      nErrs++;
    }
    if (m_eDocumentScope == null)
    {
      aLogHandler.error (sErrorPrefix + "DocumentScope is missing");
      nErrs++;
    }
    if (m_eReporterRole == null)
    {
      aLogHandler.error (sErrorPrefix + "ReporterRole is missing");
      nErrs++;
    }

    if (StringHelper.isEmpty (m_sTaxAuthorityID))
    {
      aLogHandler.error (sErrorPrefix + "TaxAuthority ID is missing");
      nErrs++;
    }
    // m_sTaxAuthorityName is optional

    if (m_aReportingParty == null)
    {
      aLogHandler.error (sErrorPrefix + "ReportingParty is missing");
      nErrs++;
    }
    else
      if (!aIF.isParticipantIdentifierSchemeValid (m_aReportingParty.getScheme ()))
      {
        aLogHandler.error (sErrorPrefix +
                           "ReportingParty identifier scheme '" +
                           m_aReportingParty.getScheme () +
                           "' is invalid");
        nErrs++;
      }
      else
        if (!aIF.isParticipantIdentifierValueValid (m_aReportingParty.getScheme (), m_aReportingParty.getValue ()))
        {
          aLogHandler.error (sErrorPrefix +
                             "ReportingParty identifier value '" +
                             m_aReportingParty.getValue () +
                             "' is invalid for scheme '" +
                             m_aReportingParty.getScheme () +
                             "'");
          nErrs++;
        }

    if (m_aReceivingParty == null)
    {
      aLogHandler.error (sErrorPrefix + "ReceivingParty is missing");
      nErrs++;
    }
    else
      if (!aIF.isParticipantIdentifierSchemeValid (m_aReceivingParty.getScheme ()))
      {
        aLogHandler.error (sErrorPrefix +
                           "ReceivingParty identifier scheme '" +
                           m_aReceivingParty.getScheme () +
                           "' is invalid");
        nErrs++;
      }
      else
        if (!aIF.isParticipantIdentifierValueValid (m_aReceivingParty.getScheme (), m_aReceivingParty.getValue ()))
        {
          aLogHandler.error (sErrorPrefix +
                             "ReceivingParty identifier value '" +
                             m_aReceivingParty.getValue () +
                             "' is invalid for scheme '" +
                             m_aReceivingParty.getScheme () +
                             "'");
          nErrs++;
        }
        else
        {
          final String [] aParts = StringHelper.getExplodedArray (':', m_aReceivingParty.getValue (), 2);
          if (!"0242".equals (aParts[0]))
          {
            aLogHandler.error (sErrorPrefix +
                               "ReceivingParty identifier value '" +
                               m_aReceivingParty.getValue () +
                               "' must use the 0242 identifier scheme");
            nErrs++;
          }
        }

    if (m_aReportersRepresentative == null)
    {
      aLogHandler.error (sErrorPrefix + "ReportersRepresentative is missing");
      nErrs++;
    }
    else
      if (!aIF.isParticipantIdentifierSchemeValid (m_aReportersRepresentative.getScheme ()))
      {
        aLogHandler.error (sErrorPrefix +
                           "ReportersRepresentative identifier meta scheme '" +
                           m_aReportersRepresentative.getScheme () +
                           "' is invalid");
        nErrs++;
      }
      else
        if (!aIF.isParticipantIdentifierValueValid (m_aReportersRepresentative.getScheme (),
                                                    m_aReportersRepresentative.getValue ()))
        {
          aLogHandler.error (sErrorPrefix +
                             "ReportersRepresentative identifier value '" +
                             m_aReportersRepresentative.getValue () +
                             "' is invalid for meta scheme '" +
                             m_aReportersRepresentative.getScheme () +
                             "'");
          nErrs++;
        }
        else
        {
          final String [] aParts = StringHelper.getExplodedArray (':', m_aReportersRepresentative.getValue (), 2);
          if (!"0242".equals (aParts[0]))
          {
            aLogHandler.error (sErrorPrefix +
                               "ReportersRepresentative identifier value '" +
                               m_aReportersRepresentative.getValue () +
                               "' must use the 0242 identifier scheme");
            nErrs++;
          }
        }

    // SK must have exactly one reported transaction (according to the Schematron)
    if (m_aReportedTransaction == null)
    {
      aLogHandler.error (sErrorPrefix + "ReportedTransaction is missing");
      nErrs++;
    }

    return nErrs == 0;
  }

  public boolean isEveryRequiredFieldSet (@NonNull final ErrorList aErrorList)
  {
    ValueEnforcer.notNull (aErrorList, "ErrorList");
    return _isEveryRequiredFieldSet (new ErrorListLogHandler (aErrorList));
  }

  @Nullable
  public TaxDataType build ()
  {
    if (!_isEveryRequiredFieldSet (m_aLogHandler))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD cannot be build.");
      return null;
    }

    final TaxDataType ret = new TaxDataType ();
    ret.setCustomizationID (new CustomizationIDType (m_sCustomizationID));
    ret.setProfileID (new ProfileIDType (m_sProfileID));
    ret.setUUID (new UUIDType (m_sUUID));
    ret.setIssueDate (new IssueDateType (XMLOffsetDate.of (m_aIssueDate)));
    ret.setIssueTime (new IssueTimeType (XMLOffsetTime.of (m_aIssueTime)));
    {
      final var aDocType = new ReferencedDocumentTypeCodeType ();
      aDocType.setValue (m_eTaxDataTypeCode.getID ());
      ret.setTaxDataTypeCode (aDocType);
    }
    {
      final var aTDS = new TaxDataDocumentScopeType ();
      aTDS.setValue (m_eDocumentScope.getID ());
      ret.setDocumentScope (aTDS);
    }
    {
      final var aRR = new TaxDataDocumentReporterRoleType ();
      aRR.setValue (m_eReporterRole.getID ());
      ret.setReporterRole (aRR);
    }
    {
      final TaxAuthorityType a = new TaxAuthorityType ();
      a.setID (new IDType (m_sTaxAuthorityID));
      if (StringHelper.isNotEmpty (m_sTaxAuthorityName))
        a.setName (new NameType (m_sTaxAuthorityName));
      ret.setTaxAuthority (a);
    }
    {
      final String [] aParts = StringHelper.getExplodedArray (':', m_aReportingParty.getValue (), 2);
      final PartyType aParty = new PartyType ();
      aParty.setEndpointID (aParts[1]).setSchemeID (aParts[0]);
      ret.setReportingParty (aParty);
    }
    {
      final String [] aParts = StringHelper.getExplodedArray (':', m_aReceivingParty.getValue (), 2);
      final PartyType aParty = new PartyType ();
      aParty.setEndpointID (aParts[1]).setSchemeID (aParts[0]);
      ret.setReceivingParty (aParty);
    }
    {
      final String [] aParts = StringHelper.getExplodedArray (':', m_aReportersRepresentative.getValue (), 2);
      final PartyType aParty = new PartyType ();
      final PartyIdentificationType aPID = new PartyIdentificationType ();
      aPID.setID (aParts[1]).setSchemeID (aParts[0]);
      aParty.addPartyIdentification (aPID);
      ret.setReportersRepresentative (aParty);
    }
    ret.addReportedTransaction (m_aReportedTransaction);
    return ret;
  }
}
