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
package com.helger.peppol.sk.tdd.jaxb;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.resource.ClassPathResource;
import com.helger.jaxb.GenericJAXBMarshaller;
import com.helger.peppol.sk.tdd.v2026_02_20.ObjectFactory;
import com.helger.peppol.sk.tdd.v2026_02_20.TaxDataType;
import com.helger.ubl21.UBL21Marshaller;
import com.helger.ubl21.UBL21NamespaceContext;
import com.helger.xml.namespace.MapBasedNamespaceContext;

/**
 * This is the reader and writer for Peppol SK TDD 1.0.0 documents. This class may be derived to
 * override protected methods from {@link GenericJAXBMarshaller}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PeppolSLTDD100Marshaller extends GenericJAXBMarshaller <TaxDataType>
{
  @NonNull
  @ReturnsMutableCopy
  private static ICommonsList <ClassPathResource> _getAllXSDs ()
  {
    final ICommonsList <ClassPathResource> ret = UBL21Marshaller.getAllBaseXSDs ();
    ret.add (CPeppolSKTDD.TDD_XSD_1_0_0);
    return ret;
  }

  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public static MapBasedNamespaceContext createNamespaceContext ()
  {
    final MapBasedNamespaceContext ret = UBL21NamespaceContext.getInstance ().getClone ();
    ret.addMapping ("pxs", CPeppolSKTDD.TDD_XSD_1_0_0_NS);
    return ret;
  }

  /**
   * Constructor
   */
  public PeppolSLTDD100Marshaller ()
  {
    super (TaxDataType.class, _getAllXSDs (), new ObjectFactory ()::createTaxData);
    setNamespaceContext (createNamespaceContext ());
  }
}
