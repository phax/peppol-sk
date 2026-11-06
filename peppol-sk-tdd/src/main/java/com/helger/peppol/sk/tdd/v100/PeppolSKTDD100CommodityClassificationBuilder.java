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

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ItemClassificationCodeType;

/**
 * Builder for Peppol SK TDD 1.0.0 sub element called "CommodityClassification".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD100CommodityClassificationBuilder implements IBuilder <CommodityClassificationType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD100CommodityClassificationBuilder.class);

  private ILogHandler m_aLogHandler = ILogHandler.createForLogger (LOGGER);
  private String m_sItemClassification;
  private String m_sItemClassificationListID;

  public PeppolSKTDD100CommodityClassificationBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD100CommodityClassificationBuilder initFromUBL (@NonNull final CommodityClassificationType aObj)
  {
    ValueEnforcer.notNull (aObj, "Item");

    final ItemClassificationCodeType aICC = aObj.getItemClassificationCode ();
    if (aICC != null)
    {
      itemClassification (aICC.getValue ());
      itemClassificationListID (aICC.getListID ());
      // The list version ID (BT-158-2) is not allowed in the TDD (ibr-tdd-79)
    }
    return this;
  }

  @Nullable
  public String itemClassification ()
  {
    return m_sItemClassification;
  }

  @NonNull
  public PeppolSKTDD100CommodityClassificationBuilder itemClassification (@Nullable final String s)
  {
    m_sItemClassification = s;
    return this;
  }

  @Nullable
  public String itemClassificationListID ()
  {
    return m_sItemClassificationListID;
  }

  @NonNull
  public PeppolSKTDD100CommodityClassificationBuilder itemClassificationListID (@Nullable final String s)
  {
    m_sItemClassificationListID = s;
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
  public PeppolSKTDD100CommodityClassificationBuilder logHandler (@NonNull final ILogHandler aLogHandler)
  {
    ValueEnforcer.notNull (aLogHandler, "LogHandler");
    m_aLogHandler = aLogHandler;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (@NonNull final ILogHandler aLogHandler, @NonNull final MutableInt aReportedDocsErrs)
  {
    final String sErrorPrefix = "Error in Peppol SK TDD 1.0.0 CommodityClassification builder: ";

    if (StringHelper.isEmpty (m_sItemClassification))
    {
      aLogHandler.error (sErrorPrefix + "ItemClassification is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sItemClassificationListID))
    {
      aLogHandler.error (sErrorPrefix + "ItemClassificationListID is missing");
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
  public CommodityClassificationType build ()
  {
    final MutableInt aErrorCount = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (m_aLogHandler, aErrorCount))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD CommodityClassification cannot be build.");
      return null;
    }

    final CommodityClassificationType ret = new CommodityClassificationType ();
    {
      final ItemClassificationCodeType aIC = new ItemClassificationCodeType ();
      aIC.setValue (m_sItemClassification);
      aIC.setListID (m_sItemClassificationListID);
      ret.setItemClassificationCode (aIC);
    }
    return ret;
  }
}
