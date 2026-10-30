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
ISchematronResource schematron = PeppolSKTDDValidator.getSchematronSK_TDD_100 ();
```

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

v0.9.2 - work in progress
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
