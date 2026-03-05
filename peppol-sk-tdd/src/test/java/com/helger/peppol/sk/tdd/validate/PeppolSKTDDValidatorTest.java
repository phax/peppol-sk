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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.diagnostics.error.IError;
import com.helger.io.file.FilenameHelper;
import com.helger.io.resource.IReadableResource;
import com.helger.peppol.sk.tdd.jaxb.PeppolSKTDD100Marshaller;
import com.helger.peppol.sk.tdd.testfiles.PeppolSKTestFiles;
import com.helger.peppol.sk.tdd.v2026_03_02.TaxDataType;
import com.helger.phive.api.executor.IValidationExecutor;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.xml.source.IValidationSourceXML;

/**
 * Test class for class {@link PeppolSKTDDValidator}.
 *
 * @author Philip Helger
 */
public final class PeppolSKTDDValidatorTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDDValidatorTest.class);

  @Test
  public void testFilesExist ()
  {
    for (final IValidationExecutorSet <IValidationSourceXML> aVES : PeppolSKTDDValidator.VES_REGISTRY.getAll ())
      for (final IValidationExecutor <IValidationSourceXML> aVE : aVES)
      {
        final IReadableResource aRes = aVE.getValidationArtefact ().getRuleResource ();
        assertTrue (aRes.toString (), aRes.exists ());
      }
  }

  @Test
  public void testReadTDD100Good () throws Exception
  {
    final PeppolSKTDD100Marshaller aMarshaller = new PeppolSKTDD100Marshaller ();

    for (final IReadableResource aRes : PeppolSKTestFiles.getAllGoodTDD100Files ())
    {
      LOGGER.info ("Reading " + aRes.getPath ());
      final TaxDataType tdd = aMarshaller.read (aRes);
      assertNotNull (tdd);

      final var aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (aRes);
      assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                  aVRL.getOverallValidity ().isValid ());
    }
  }

  @Test
  public void testReadTDD100Bad () throws Exception
  {
    final PeppolSKTDD100Marshaller aMarshaller = new PeppolSKTDD100Marshaller ();

    for (final IReadableResource aRes : PeppolSKTestFiles.getAllSchematronBadTDD100Files ())
    {
      LOGGER.info ("Reading " + aRes.getPath ());
      final TaxDataType tdd = aMarshaller.read (aRes);
      assertNotNull (tdd);

      final var aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (aRes);
      assertFalse (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                   aVRL.getOverallValidity ().isValid ());

      final ICommonsList <String> aAllErrorIDs = new CommonsArrayList <> ();
      aVRL.forEachFlattened (x -> {
        if (x.isError ())
          aAllErrorIDs.add (x.getErrorID ().toLowerCase (Locale.ROOT));
      });

      LOGGER.info ("Found " + aAllErrorIDs.size () + " errors: " + aAllErrorIDs);
      final String sBaseName = FilenameHelper.getBaseName (aRes.getPath ());

      assertTrue (aAllErrorIDs.toString () + " found for " + aRes.getPath () + " (" + sBaseName + ")",
                  aAllErrorIDs.contains (sBaseName.substring (sBaseName.indexOf ('-') + 1)));
    }
  }
}
