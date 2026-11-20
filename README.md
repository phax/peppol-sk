# peppol-sk

<!-- ph-badge-start -->
[![Sonatype Central](https://maven-badges.sml.io/sonatype-central/com.helger.peppol/peppol-sk-parent-pom/badge.svg)](https://maven-badges.sml.io/sonatype-central/com.helger.peppol/peppol-sk-parent-pom/)
[![javadoc](https://javadoc.io/badge2/com.helger.peppol/peppol-sk-testfiles/javadoc.svg)](https://javadoc.io/doc/com.helger.peppol/peppol-sk-testfiles)

> If this project saved you some time or made your day a little easier, a star would mean a lot — it helps others find it too.
<!-- ph-badge-end -->

A Java library for converting Peppol e-invoices (UBL 2.1 Invoice / CreditNote) into Slovakia Tax Data Documents (TDD) for tax reporting.

peppol-sk is part of my Peppol solution stack. See https://github.com/phax/peppol for other components and libraries in that area.

**This project is work in progress and contains the version to be send out for member review - there might be future changes for the final data model for SK!**

This contains a set of Java libraries.
They are licensed under the Apache 2.0 license.
The minimum requirement is Java 17.

The backing specifications are:
* SK TDD: https://docs.peppol.eu/tdd/sk/

# Submodules

This project consists of the following submodules (in alphabetic order)

* `peppol-sk-bom` - the Bill of Materials (BOM) with the managed versions of all artifacts of this project (since v0.9.2)
* `peppol-sk-tdd` - contains the main logic to create Peppol SK TDD documents based on the Peppol SK pilot documents as well as documentation
    * Main class to build a complete SK from scratch is `PeppolSKTDD100Builder`
    * To run the Schematron validation, use class `PeppolSKTDDValidator`
* `peppol-sk-tdd-datatypes` - contains the JAXB generated Peppol SK TDD data model
    * Main class to read and write TDD XML is `PeppolSKTDD100Marshaller`
* `peppol-sk-testfiles` - contains Peppol SK specific test files as a reusable component
    * Main class is `PeppolSKTestFiles`

# Maven usage

Add the following to your `pom.xml` to use this artifact, replacing `x.y.z` with the real version number.

```xml
<dependency>
  <groupId>com.helger.peppol</groupId>
  <artifactId>peppol-sk-tdd</artifactId>
  <version>x.y.z</version>
</dependency>
```

Alternatively use the following code in your `dependencyManagement` section to use it as a BOM:

```xml
<dependency>
  <groupId>com.helger.peppol</groupId>
  <artifactId>peppol-sk-bom</artifactId>
  <version>x.y.z</version>
  <type>pom</type>
  <scope>import</scope>
</dependency>
```

Note: prior to v0.9.2 there was no dedicated BOM and `peppol-sk-parent-pom` had to be imported instead, which also carries the build configuration of this project.

# Usage example

```java
// Convert a UBL Invoice to a TDD document
TaxDataType tdd = new PeppolSKTDD100Builder ()
    .taxDataTypeCode (ESKTDDTaxDataTypeCode.SUBMIT)
    .reportingParty (participantID)
    .receivingParty (receiverID)
    .taxAuthorityID ("SK")
    .reportedTransaction (rt -> rt.initFromInvoice (invoice))
    .build ();

// Serialize to XML
String xml = new PeppolSKTDD100Marshaller ().setFormattedOutput (true).getAsString (tdd);

// Validate with Schematron
ValidationResultList vrl = PeppolSKTDDValidator.validateSK_TDD_100 (new ReadableResourceString (xml, StandardCharsets.UTF_8));
```

## Specification versions

The TDD specification versions 1.0.0 and 1.1.0 are supported side by side, each with its own set of classes (package `v100` with `PeppolSKTDD100*` and package `v110` with `PeppolSKTDD110*`).
Both use the same XML Schema and the same CustomizationID `urn:peppol:taxdata:sk-1`, but different Schematron rules:

| Version | Builders | Marshaller | VESID | Validation | Schematrons | Test files |
|---|---|---|---|---|---|---|
| 1.0.0 | `v100.PeppolSKTDD100*Builder` | `PeppolSKTDD100Marshaller` | `org.peppol.taxdata:sk:1.0.0` | `validateSK_TDD_100` | `external/schematron/2026-04-14/` | `tdd/1.0.0/` |
| 1.1.0 | `v110.PeppolSKTDD110*Builder` | `PeppolSKTDD110Marshaller` | `org.peppol.taxdata:sk:1.1.0` | `validateSK_TDD_110` | `external/schematron/2026-11-20/` | `tdd/1.1.0/` |

```java
TaxDataType tdd = new PeppolSKTDD110Builder ()
    .taxDataTypeCode (ESKTDDTaxDataTypeCode.SUBMIT)
    // ...
    .reportedTransaction (rt -> rt.initFromInvoice (invoice))
    .build ();
String xml = new PeppolSKTDD110Marshaller ().setFormattedOutput (true).getAsString (tdd);
ValidationResultList vrl = PeppolSKTDDValidator.validateSK_TDD_110 (new ReadableResourceString (xml, StandardCharsets.UTF_8));
```

Differences of the 1.1.0 builders compared to the 1.0.0 builders:
* `PeppolSKTDD110ReportedTransactionBuilder` has the Buyer legal registration identifier (BT-47) with its scheme identifier, and `initFromInvoice`/`initFromCreditNote` take it over.
* `PeppolSKTDD110ReportedTransactionBuilder` requires the TAX identifier (BT-63) if a tax representative is provided, as `cac:TaxRepresentativeParty/cac:PartyTaxScheme` is 1..1 since 1.1.0.
* `PeppolSKTDD110DocumentLineBuilder` has no price base quantity (BT-149), because rule `ibr-tdd-82` of 1.1.0 forbids it. `initFromInvoice`/`initFromCreditNote` report the item net price (BT-146) for one unit instead (price divided by base quantity), as the samples of the specification do.
* The 1.1.0 warning rule `ibr-tdd-88` (Slovak domestic corrected invoice 384 SHOULD reference the preceding invoice) is not checked by the builder - only by the validation.
* The VESIDs are DVR coordinates with version 1.0.0 and 1.1.0; their string representation omits trailing zero parts (`org.peppol.taxdata:sk:1` and `org.peppol.taxdata:sk:1.1`).
* Note: in 1.1.0 several inherited EN 16931 rules are evaluated that never matched a 1.0.0 TDD, e.g. BR-E-10 / BR-O-10 (VAT exemption reason for categories E and O). Documents valid under 1.0.0 may therefore be invalid under 1.1.0.

## Notes

The `ReportedDocument` is created for every `TaxDataTypeCode`, including `D` (DISREGARD), so all of its mandatory fields must be set.
The `ReportedDocument/UUID` is calculated according to rule `ID-BDID-01` from BT-34-1, BT-34, BT-3, BT-1 and BT-2.
Leading and trailing whitespace is removed from these values for the calculation, and `build()` returns `null` if one of them is missing or empty.
All other values are copied as is.

# Building

This project requires Apache Maven 3.x and Java 17 for building.
Simply run
```
mvn clean install
```
to build the solution.

# News and noteworthy

v0.9.3 - work in progress
* Added support for the Peppol SK TDD specification version 1.1.0 (released 2026-11-20) next to version 1.0.0, which is unchanged.
  See https://test-docs.peppol.eu/tdd/sk/2026-Q4-FIN/tdd-sk/trn-tdd/
* Added the new package `com.helger.peppol.sk.tdd.v110` with the builders `PeppolSKTDD110*Builder` for 1.1.0, and the new `PeppolSKTDD110Marshaller`
* `PeppolSKTDD110ReportedTransactionBuilder` supports the Buyer legal registration identifier (BT-47), and reports an error if a tax representative has no TAX identifier (BT-63), as its `cac:PartyTaxScheme` is mandatory since 1.1.0
* `PeppolSKTDD110DocumentLineBuilder` has no price base quantity (BT-149) any more; the item net price is divided by it instead (`getPriceAmountPerUnit`)
* Added the VESID `PeppolSKTDDValidator.VID_TDD_SK_110` (`org.peppol.taxdata:sk:1.1.0`) with the 1.1.0 Schematrons and `PeppolSKTDDValidator.validateSK_TDD_110`
* Added the 1.1.0 test files to `PeppolSKTestFiles` (`getAllGoodTDD110Files`, `getAllSchematronBadTDD110Files`, `getAllPayloadBadTDD110Files`)

v0.9.2 - 2026-11-15
* Requires ph-commons 12.7.1
* Added the new module `peppol-sk-bom` as a Maven BOM (Bill of Materials) for all peppol-sk artifacts.
  Use it instead of `peppol-sk-parent-pom` when importing the managed versions
* `PeppolSKTDD100ReportedTransactionBuilder.initFromInvoice` and `initFromCreditNote` now find the `cac:PartyTaxScheme` for the VAT identifiers (BT-31, BT-48, BT-63) independent of the case and surrounding whitespace of the `TaxScheme/ID`, as the EN 16931 Schematron does.
  Previously a BIS3-valid `TaxScheme/ID` like `vat` or ` Vat ` lost the VAT identifiers in the TDD.
  See [#10](https://github.com/phax/peppol-sk/pull/10) - thx @FilipKovac

v0.9.1 - 2026-10-28
* Requires ph-commons 12.5.3
* Added `taxPointDate (LocalDate)` to `PeppolSKTDD100ReportedTransactionBuilder`.
  The value added tax point date (BT-7) of an Invoice or CreditNote is now taken over into `ReportedDocument/cbc:TaxPointDate`.
  See [#5](https://github.com/phax/peppol-sk/pull/5) - thx @FilipKovac
* Fixed an `IndexOutOfBoundsException` in `PeppolSKTDD100ReportedTransactionBuilder.initFromInvoice` and `initFromCreditNote` if the invoice period contains a tax point date code (BT-8).
  The value is now correctly taken over into `ReportedDocument/cac:InvoicePeriod/cbc:DescriptionCode`.
  See [#6](https://github.com/phax/peppol-sk/pull/6) - thx @FilipKovac
* `PeppolSKTDD100ReportedTransactionBuilder.initFromInvoice` and `initFromCreditNote` now take the VAT identifiers (BT-31, BT-48, BT-63) from the `cac:PartyTaxScheme` with `TaxScheme/ID` `VAT`, instead of always using the first one.
  Previously a Seller tax registration identifier (BT-32) listed first was reported as VAT identifier. A party without a `VAT` tax scheme now has no VAT identifier.
  See [#7](https://github.com/phax/peppol-sk/pull/7) - thx @FilipKovac
* Fixed `PeppolSKTDD100ReportedTransactionBuilder.build ()` to write the payment instructions (BG-16) to the TDD.
  Previously the `cac:PaymentMeans` elements were collected but never emitted, so no TDD contained them.
  See [#8](https://github.com/phax/peppol-sk/pull/8) - thx @FilipKovac
* Added `paymentMandateID (String)` and `payerFinancialAccountID (String)` to `PeppolSKTDD100PaymentMeansBuilder`.
  The direct debit information (BG-19 with BT-89 and BT-91) of an Invoice or CreditNote is now taken over into `cac:PaymentMeans/cac:PaymentMandate`.
  See [#8](https://github.com/phax/peppol-sk/pull/8) - thx @FilipKovac
* `PeppolSKTDD100ReportedTransactionBuilder` now removes leading and trailing whitespace from the five values used for the `ReportedDocument/cbc:UUID` calculation (rule `ID-BDID-01`).
  A value that is empty after trimming is now treated as missing, so `build ()` returns `null` instead of creating a deviating UUID.
  See [#9](https://github.com/phax/peppol-sk/pull/9) - thx @FilipKovac

v0.9.0 - 2026-10-22
* Requires ph-commons 12.5.2
* Added `logHandler (ILogHandler)` to `PeppolSKTDD100Builder` and all sub-builders.
  It receives the message of each missing or invalid required field (as error) when calling `build ()`; the default handler logs to the logger of the builder class.
  Use `ILogHandler.createForConsumer`, `ILogHandler.createForBiConsumer` or `ErrorListLogHandler` to redirect the messages, and `ILogHandler.createNoOp ()` to discard them.
  Call it first, because it is passed on to sub-builders created via lambda afterwards
* Added `isEveryRequiredFieldSet (ErrorList)` which collects the messages in an `ErrorList`, independent of the log handler
* Removed the public `isEveryRequiredFieldSet ()` and `isEveryRequiredFieldSet (boolean)` methods; use `isEveryRequiredFieldSet (ErrorList)` or `build ()` instead

v0.2.2 - 2026-09-23
* The tax exemption reason (BT-120) and tax exemption reason code (BT-121) are no longer copied into the invoice line VAT information (BG-30) and into the document level allowance/charge (BG-20, BG-21), because the SK TDD rules `ibr-tdd-80` and `ibr-tdd-63` forbid them there.
  See [#4](https://github.com/phax/peppol-sk/issues/4) - thx @mariansbr
* The tax category is no longer copied into the invoice line level allowance/charge (BG-27, BG-28), because the SK TDD rule `ibr-tdd-60` forbids it there
* Added the new test invoice `invoice/good/vat-category-AE.xml` covering all of the above

v0.2.1 - 2026-08-12
* Added the mapping of tax exemption reason (BT-120) and tax exemption reason code (BT-121) in class `PeppolSKTDD100TaxCategoryBuilder`.
  See [#3](https://github.com/phax/peppol-sk/pull/3) - thx @vrbyjimmy

v0.2.0 - 2026-07-17
* Updated to ph-schematron v10.x
* Requires at least phive 12.0.2

v0.1.3 - 2026-03-11
* Updated to latest published Schematrons.

v0.1.2 - 2026-03-10
* Added support for participant identifier scheme `0245`. See [#1](https://github.com/phax/peppol-sk/issues/1) - thx @vrbyjimmy

v0.1.1 - 2026-03-05
* Validating all output against all 3 layers of Schematron instead of just one
* Added the mandatory mapping of Reported Document / buyer name (BT-44)
* Added the optional mapping for price base quantity (BT-149)
* The builder class `PeppolSKTDD100TaxSubtotalBuilder` not also correctly uses the nested `PeppolSKTDD100TaxCategoryBuilder` builder class

v0.1.0 - 2026-03-03
* Initial version targeting Peppol SK member review TDD specs

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.
