# peppol-sk

[![Maven Central](https://img.shields.io/maven-central/v/com.helger.peppol/peppol-sk-parent-pom)](https://img.shields.io/maven-central/v/com.helger.peppol/peppol-sk-parent-pom)
[![javadoc](https://javadoc.io/badge2/com.helger.peppol/peppol-sk-tdd/javadoc.svg)](https://javadoc.io/doc/com.helger.peppol/peppol-sk-tdd)

Special support for Peppol Slovakia (SK).

**This project is work in progress and does NOT YET contain the final data model for SK!**

This contains a set of Java libraries.
They are licensed under the Apache 2.0 license.
The minimum requirement is Java 17.

The backing specifications are:
* SK TDD draft: https://test-docs.peppol.eu/tdd/sk/

# Submodules

This project consists of the following submodules (in alphabetic order)

* `peppol-sk-tdd` - contains the main logic to create Peppol SK TDD documents based on the Peppol SK pilot documents as well as documentation
    * Main class to build a complete SK from scratch is `PeppolSKTDD10Builder`
    * To run the Schematron validation, use class `PeppolSKTDDValidator`
* `peppol-sk-tdd-datatypes` - contains the JAXB generated Peppol SK TDD data model
    * Main class to read and write TDD XML is `PeppolSLTDD10Marshaller`
* `peppol-sk-testfiles` - contains Peppol SK specific test files as a reusable component
    * Main class is `PeppolSKTestFiles`

# Maven usage

Add the following to your `pom.xml` to use this artifact:, replacing `x.y.z` with the real version number.

```xml
<dependency>
  <groupId>com.helger.peppol</groupId>
  <artifactId>peppol-sk-tdd</artifactId>
  <version>x.y.z</version>
</dependency>
```

# Building

This project requires Apache Maven 3.x and Java 17 for building.
Simply run
```
mvn clean install
```
to build the solution.

# News and noteworthy

v0.1.0 - work in progress
* Initial version targeting Peppol SK pilot draft TDD specs

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.
