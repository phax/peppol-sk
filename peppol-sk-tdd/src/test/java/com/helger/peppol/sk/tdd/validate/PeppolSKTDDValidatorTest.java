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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.diagnostics.error.IError;
import com.helger.diver.api.coord.DVRCoordinate;
import com.helger.io.file.FilenameHelper;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.IReadableResource;
import com.helger.jaxb.GenericJAXBMarshaller;
import com.helger.peppol.sk.tdd.jaxb.PeppolSKTDD100Marshaller;
import com.helger.peppol.sk.tdd.jaxb.PeppolSKTDD110Marshaller;
import com.helger.peppol.sk.tdd.testfiles.PeppolSKTestFiles;
import com.helger.peppol.sk.tdd.v2026_04_14.TaxDataType;
import com.helger.phive.api.executor.IValidationExecutor;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.phive.xml.source.IValidationSourceXML;

/**
 * Test class for class {@link PeppolSKTDDValidator}.
 *
 * @author Philip Helger
 */
public final class PeppolSKTDDValidatorTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolSKTDDValidatorTest.class);

  private static ICommonsList <String> _getAllErrorIDs (final ValidationResultList aVRL)
  {
    final ICommonsList <String> aAllErrorIDs = new CommonsArrayList <> ();
    aVRL.forEachFlattened (x -> {
      if (x.isError ())
        aAllErrorIDs.add (x.getErrorID ().toLowerCase (Locale.ROOT));
    });
    return aAllErrorIDs;
  }

  private static ValidationResultList _validate (final DVRCoordinate aVESID, final IReadableResource aRes)
  {
    if (aVESID.equals (PeppolSKTDDValidator.VID_TDD_SK_100))
      return PeppolSKTDDValidator.validateSK_TDD_100 (aRes);
    return PeppolSKTDDValidator.validateSK_TDD_110 (aRes);
  }

  private static GenericJAXBMarshaller <TaxDataType> _createMarshaller (final DVRCoordinate aVESID)
  {
    if (aVESID.equals (PeppolSKTDDValidator.VID_TDD_SK_100))
      return new PeppolSKTDD100Marshaller ();
    return new PeppolSKTDD110Marshaller ();
  }

  private static void _assertValid (final DVRCoordinate aVESID, final IReadableResource aRes)
  {
    final ValidationResultList aVRL = _validate (aVESID, aRes);
    assertTrue (aVESID.getAsSingleID () +
                " " +
                aRes.getPath () +
                ": " +
                aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  private static void _readGood (final DVRCoordinate aVESID,
                                 final ICommonsList <ClassPathResource> aFiles)
  {
    final GenericJAXBMarshaller <TaxDataType> aMarshaller = _createMarshaller (aVESID);
    for (final IReadableResource aRes : aFiles)
    {
      LOGGER.info ("Reading " + aRes.getPath ());
      final TaxDataType tdd = aMarshaller.read (aRes);
      assertNotNull (tdd);

      _assertValid (aVESID, aRes);
    }
  }

  private static void _readBad (final DVRCoordinate aVESID,
                                final ICommonsList <ClassPathResource> aFiles)
  {
    final GenericJAXBMarshaller <TaxDataType> aMarshaller = _createMarshaller (aVESID);
    for (final IReadableResource aRes : aFiles)
    {
      LOGGER.info ("Reading " + aRes.getPath ());
      final TaxDataType tdd = aMarshaller.read (aRes);
      assertNotNull (tdd);

      final ValidationResultList aVRL = _validate (aVESID, aRes);
      assertFalse (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                   aVRL.getOverallValidity ().isValid ());

      final ICommonsList <String> aAllErrorIDs = _getAllErrorIDs (aVRL);
      LOGGER.info ("Found " + aAllErrorIDs.size () + " errors: " + aAllErrorIDs);
      final String sBaseName = FilenameHelper.getBaseName (aRes.getPath ());

      assertTrue (aAllErrorIDs.toString () + " found for " + aRes.getPath () + " (" + sBaseName + ")",
                  aAllErrorIDs.contains (sBaseName.substring (sBaseName.indexOf ('-') + 1)));
    }
  }

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
  public void testVESIDs ()
  {
    assertEquals (2, PeppolSKTDDValidator.VES_REGISTRY.getAll ().size ());
    // Trailing ".0" version parts are omitted in the string representation
    assertEquals ("org.peppol.taxdata:sk:1", PeppolSKTDDValidator.VID_TDD_SK_100.getAsSingleID ());
    assertEquals ("org.peppol.taxdata:sk:1.1", PeppolSKTDDValidator.VID_TDD_SK_110.getAsSingleID ());
    assertNotNull (PeppolSKTDDValidator.VES_REGISTRY.getOfID (PeppolSKTDDValidator.VID_TDD_SK_100));
    assertNotNull (PeppolSKTDDValidator.VES_REGISTRY.getOfID (PeppolSKTDDValidator.VID_TDD_SK_110));
    // Different rules per version
    assertNotSame (PeppolSKTDDValidator.VES_REGISTRY.getOfID (PeppolSKTDDValidator.VID_TDD_SK_100),
                   PeppolSKTDDValidator.VES_REGISTRY.getOfID (PeppolSKTDDValidator.VID_TDD_SK_110));
  }

  @Test
  public void testReadTDD100Good () throws Exception
  {
    _readGood (PeppolSKTDDValidator.VID_TDD_SK_100, PeppolSKTestFiles.getAllGoodTDD100Files ());
  }

  @Test
  public void testReadTDD100Bad () throws Exception
  {
    _readBad (PeppolSKTDDValidator.VID_TDD_SK_100, PeppolSKTestFiles.getAllSchematronBadTDD100Files ());
  }

  @Test
  public void testReadTDD110Good () throws Exception
  {
    _readGood (PeppolSKTDDValidator.VID_TDD_SK_110, PeppolSKTestFiles.getAllGoodTDD110Files ());
  }

  @Test
  public void testReadTDD110Bad () throws Exception
  {
    _readBad (PeppolSKTDDValidator.VID_TDD_SK_110, PeppolSKTestFiles.getAllSchematronBadTDD110Files ());
  }

  @Test
  public void testRulesOnlyIn110 () throws Exception
  {
    // These files only break rules that were changed in 1.1.0, so they are valid according to the
    // 1.0.0 rules - proves that 1.1.0 really uses the new Schematrons
    for (final String sFilename : new String [] { "bad-ibr-tdd-05.xml", "bad-ibr-tdd-82.xml" })
      _assertValid (PeppolSKTDDValidator.VID_TDD_SK_100,
                    new ClassPathResource ("external/tdd/1.1.0/bad/" + sFilename,
                                           PeppolSKTestFiles.class.getClassLoader ()));
  }

  @Test
  public void testBuyerLegalRegistrationIDOnlyIn110 () throws Exception
  {
    // BT-47 is only allowed since 1.1.0 - ibr-tdd-86 of 1.0.0 forbids it
    final ClassPathResource aRes = new ClassPathResource ("external/tdd/1.1.0/good/vat-category-AE-BT-47.xml",
                                                          PeppolSKTestFiles.class.getClassLoader ());
    _assertValid (PeppolSKTDDValidator.VID_TDD_SK_110, aRes);

    final ValidationResultList aVRL = PeppolSKTDDValidator.validateSK_TDD_100 (aRes);
    assertFalse (aVRL.getOverallValidity ().isValid ());
    assertTrue (_getAllErrorIDs (aVRL).toString (), _getAllErrorIDs (aVRL).contains ("ibr-tdd-86"));
  }
}
