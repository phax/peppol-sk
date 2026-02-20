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
package com.helger.peppol.sk.tdd.validate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.file.FilenameHelper;
import com.helger.io.resource.IReadableResource;
import com.helger.peppol.sk.tdd.jaxb.PeppolSLTDD100Marshaller;
import com.helger.peppol.sk.tdd.testfiles.PeppolSKTestFiles;
import com.helger.peppol.sk.tdd.v2026_02_20.TaxDataType;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.SVRLMarshaller;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;

/**
 * Test class for class {@link PeppolSKTDDValidator}.
 *
 * @author Philip Helger
 */
public final class PeppolSKTDDValidatorTest
{

  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDDValidatorTest.class);

  @Test
  public void testReadTDD100Good () throws Exception
  {
    final ISchematronResource aSCHRes = PeppolSKTDDValidator.getSchematronSK_TDD_100 ();
    assertNotNull (aSCHRes);
    assertTrue (aSCHRes.getResource ().exists ());

    final PeppolSLTDD100Marshaller aMarshaller = new PeppolSLTDD100Marshaller ();

    for (final IReadableResource aRes : PeppolSKTestFiles.getAllGoodTDD100Files ())
    {
      LOGGER.info ("Reading " + aRes.getPath ());
      final TaxDataType tdd = aMarshaller.read (aRes);
      assertNotNull (tdd);

      final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (aRes);
      assertNotNull (aSVRL);

      if (false)
        LOGGER.info (new SVRLMarshaller ().setFormattedOutput (true).getAsString (aSVRL));

      assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
    }
  }

  @Test
  public void testReadTDD100Bad () throws Exception
  {
    final ISchematronResource aSCHRes = PeppolSKTDDValidator.getSchematronSK_TDD_100 ();
    assertNotNull (aSCHRes);

    final PeppolSLTDD100Marshaller aMarshaller = new PeppolSLTDD100Marshaller ();

    for (final IReadableResource aRes : PeppolSKTestFiles.getAllSchematronBadTDD100Files ())
    {
      LOGGER.info ("Reading " + aRes.getPath ());
      final TaxDataType tdd = aMarshaller.read (aRes);
      assertNotNull (tdd);

      final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (aRes);
      assertNotNull (aSVRL);

      if (false)
        LOGGER.info (new SVRLMarshaller ().setFormattedOutput (true).getAsString (aSVRL));

      final ICommonsList <String> aAllErrorIDs = SVRLHelper.getAllFailedAssertions (aSVRL)
                                                           .getAllMapped (x -> x.getFlag ().isError (),
                                                                          x -> x.getID ().toLowerCase (Locale.ROOT));
      LOGGER.info ("Found " + aAllErrorIDs.size () + " errors: " + aAllErrorIDs);
      final String sBaseName = FilenameHelper.getBaseName (aRes.getPath ());

      assertTrue (aAllErrorIDs.toString () + " found for " + aRes.getPath () + " (" + sBaseName + ")",
                  aAllErrorIDs.contains (sBaseName.substring (sBaseName.indexOf ('-') + 1)));
    }
  }
}
