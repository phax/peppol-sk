# peppol-sk

[![Maven Central](https://img.shields.io/maven-central/v/com.helger.peppol/peppol-sk-parent-pom)](https://img.shields.io/maven-central/v/com.helger.peppol/peppol-sk-parent-pom)
[![javadoc](https://javadoc.io/badge2/com.helger.peppol/peppol-sk-tdd/javadoc.svg)](https://javadoc.io/doc/com.helger.peppol/peppol-sk-tdd)

A Java library for converting Peppol e-invoices (UBL 2.1 Invoice / CreditNote) into Slovakia Tax Data Documents (TDD) for tax reporting.

**This project is work in progress and contains the version to be send out for member review - there might be future changes for the final data model for SK!**

This contains a set of Java libraries.
They are licensed under the Apache 2.0 license.
The minimum requirement is Java 17.

The backing specifications are:
* SK TDD draft: https://test-docs.peppol.eu/tdd/sk/

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

# Building

This project requires Apache Maven 3.x and Java 17 for building.
Simply run
```
mvn clean install
```
to build the solution.

# News and noteworthy

v0.1.1 - 2026-03-05
* Added the mandatory mapping of Reported Document / buyer name (BT-44)
* Added the optional mapping for price base quantity (BT-149)

v0.1.0 - 2026-03-03
* Initial version targeting Peppol SK member review TDD specs

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.
