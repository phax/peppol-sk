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

import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ILogHandler;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.diagnostics.log.ErrorListLogHandler;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionType;

/**
 * Builder for Peppol SK TDD 1.1.0 sub element called "Item".
 *
 * @author Philip Helger
 */
public class PeppolSKTDD110ItemBuilder implements IBuilder <ItemType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDD110ItemBuilder.class);

  private ILogHandler m_aLogHandler = ILogHandler.createForLogger (LOGGER);
  private String m_sDescription;
  private String m_sName;
  private final ICommonsList <CommodityClassificationType> m_aCommodityClassifications = new CommonsArrayList <> ();
  private TaxCategoryType m_aClassifiedTaxCategory;

  public PeppolSKTDD110ItemBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolSKTDD110ItemBuilder initFromUBL (@NonNull final ItemType aObj)
  {
    ValueEnforcer.notNull (aObj, "Item");

    if (aObj.hasDescriptionEntries ())
      description (aObj.getDescriptionAtIndex (0).getValue ());
    name (aObj.getNameValue ());

    for (final var aCC : aObj.getCommodityClassification ())
      addCommodityClassification (x -> x.initFromUBL (aCC));

    if (aObj.hasClassifiedTaxCategoryEntries ())
      classifiedTaxCategory (x -> x.initFromUBL (aObj.getClassifiedTaxCategoryAtIndex (0))
                                   // BT-120 and BT-121 are not allowed here (ibr-tdd-80)
                                   .taxExemptionReason (null)
                                   .taxExemptionReasonCode (null));

    return this;
  }

  @Nullable
  public String description ()
  {
    return m_sDescription;
  }

  @NonNull
  public PeppolSKTDD110ItemBuilder description (@Nullable final String s)
  {
    m_sDescription = s;
    return this;
  }

  @Nullable
  public String name ()
  {
    return m_sName;
  }

  @NonNull
  public PeppolSKTDD110ItemBuilder name (@Nullable final String s)
  {
    m_sName = s;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <CommodityClassificationType> commodityClassifications ()
  {
    return m_aCommodityClassifications;
  }

  @NonNull
  public PeppolSKTDD110ItemBuilder commodityClassifications (@Nullable final ICommonsList <CommodityClassificationType> a)
  {
    m_aCommodityClassifications.setAll (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD110ItemBuilder addCommodityClassification (@Nullable final CommodityClassificationType a)
  {
    if (a != null)
      m_aCommodityClassifications.add (a);
    return this;
  }

  @NonNull
  public PeppolSKTDD110ItemBuilder addCommodityClassification (@Nullable final PeppolSKTDD110CommodityClassificationBuilder a)
  {
    return addCommodityClassification (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD110ItemBuilder addCommodityClassification (@NonNull final Consumer <? super PeppolSKTDD110CommodityClassificationBuilder> aBuilderConsumer)
  {
    final PeppolSKTDD110CommodityClassificationBuilder aBuilder = new PeppolSKTDD110CommodityClassificationBuilder ();
    aBuilder.logHandler (m_aLogHandler);
    aBuilderConsumer.accept (aBuilder);
    return addCommodityClassification (aBuilder);
  }

  @Nullable
  public TaxCategoryType classifiedTaxCategory ()
  {
    return m_aClassifiedTaxCategory;
  }

  @NonNull
  public PeppolSKTDD110ItemBuilder classifiedTaxCategory (@Nullable final TaxCategoryType a)
  {
    m_aClassifiedTaxCategory = a;
    return this;
  }

  @NonNull
  public PeppolSKTDD110ItemBuilder classifiedTaxCategory (@Nullable final PeppolSKTDD110TaxCategoryBuilder a)
  {
    return classifiedTaxCategory (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolSKTDD110ItemBuilder classifiedTaxCategory (@NonNull final Consumer <? super PeppolSKTDD110TaxCategoryBuilder> aBuilderConsumer)
  {
    final PeppolSKTDD110TaxCategoryBuilder aBuilder = new PeppolSKTDD110TaxCategoryBuilder ();
    aBuilder.logHandler (m_aLogHandler);
    aBuilderConsumer.accept (aBuilder);
    return classifiedTaxCategory (aBuilder);
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
  public PeppolSKTDD110ItemBuilder logHandler (@NonNull final ILogHandler aLogHandler)
  {
    ValueEnforcer.notNull (aLogHandler, "LogHandler");
    m_aLogHandler = aLogHandler;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (@NonNull final ILogHandler aLogHandler, @NonNull final MutableInt aReportedDocsErrs)
  {
    final String sErrorPrefix = "Error in Peppol SK TDD 1.1.0 Item builder: ";

    // m_sDescription is optional
    if (StringHelper.isEmpty (m_sName))
    {
      aLogHandler.error (sErrorPrefix + "Name is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aCommodityClassifications may be empty
    if (m_aClassifiedTaxCategory == null)
    {
      aLogHandler.error (sErrorPrefix + "ClassifiedTaxCategory is missing");
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
  public ItemType build ()
  {
    final MutableInt aErrorCount = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (m_aLogHandler, aErrorCount))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD Item cannot be build.");
      return null;
    }

    final ItemType ret = new ItemType ();
    if (StringHelper.isNotEmpty (m_sDescription))
      ret.addDescription (new DescriptionType (m_sDescription));
    ret.setName (m_sName);
    ret.setCommodityClassification (m_aCommodityClassifications);
    ret.addClassifiedTaxCategory (m_aClassifiedTaxCategory);
    return ret;
  }
}
