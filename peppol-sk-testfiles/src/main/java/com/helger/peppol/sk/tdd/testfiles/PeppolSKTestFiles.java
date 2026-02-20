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
package com.helger.peppol.sk.tdd.testfiles;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.resource.ClassPathResource;

/**
 * Sanity methods to get all Peppol SK test files
 *
 * @author Philip Helger
 */
public final class PeppolSKTestFiles
{
  private PeppolSKTestFiles ()
  {}

  @NonNull
  private static ClassLoader _getCL ()
  {
    return PeppolSKTestFiles.class.getClassLoader ();
  }

  @NonNull
  @ReturnsMutableCopy
  private static ICommonsList <@NonNull ClassPathResource> _getAll (@NonNull final String sPrefix,
                                                                    @NonNull final String @NonNull... aFilenames)
  {
    final ICommonsList <ClassPathResource> ret = new CommonsArrayList <> (aFilenames.length);
    for (final String s : aFilenames)
      ret.add (new ClassPathResource ("external/" + sPrefix + s, _getCL ()));
    return ret;
  }

  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public static ICommonsList <@NonNull ClassPathResource> getAllGoodBillingInvoiceFiles ()
  {
    return _getAll ("invoice/good/",
                    "Allowance-example.xml",
                    "base-example.xml",
                    "base-negative-inv-correction.xml",
                    "sales-order-example.xml",
                    "vat-category-E.xml",
                    "vat-category-O.xml",
                    "Vat-category-S.xml",
                    "vat-category-Z.xml");
  }

  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public static ICommonsList <@NonNull ClassPathResource> getAllGoodBillingCreditNoteFiles ()
  {
    return _getAll ("creditnote/good/", "base-creditnote-correction.xml");
  }

  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public static ICommonsList <@NonNull ClassPathResource> getAllGoodTDD100Files ()
  {
    return _getAll ("tdd/1.0.0/good/",
                    "Allowance-example.xml",
                    "base-creditnote-correction.xml",
                    "base-example.xml",
                    "base-negative-inv-correction.xml",
                    "vat-category-E.xml",
                    "vat-category-O.xml",
                    "Vat-category-S.xml",
                    "vat-category-Z.xml");
  }

  @NonNull
  @ReturnsMutableCopy
  public static ICommonsList <@NonNull ClassPathResource> getAllSchematronBadTDD100Files ()
  {
    return new CommonsArrayList <> ();
  }

  @NonNull
  @ReturnsMutableCopy
  public static ICommonsList <@NonNull ClassPathResource> getAllPayloadBadTDD100Files ()
  {
    return new CommonsArrayList <> ();
  }
}
