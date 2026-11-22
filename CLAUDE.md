# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Java library implementing the Peppol Slovakia Tax Data Document (TDD) specification. The current spec is v1.1.0 at https://docs.peppol.eu/tdd/sk/v1.1.0/ (released 2026-11-20, mandatory from 2026-11-25); v1.0.0 is supported alongside.

Minimum Java version: **17**

## Build Commands

```bash
# Build everything (runs tests)
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run tests for a single module
mvn test -pl peppol-sk-tdd

# Run a single test class
mvn test -pl peppol-sk-tdd -Dtest=PeppolSKTDD100BuilderTest

# Run a single test method
mvn test -pl peppol-sk-tdd -Dtest=PeppolSKTDD100BuilderTest#testBasicMinimal
```

## Module Structure

The BOM `peppol-sk-bom` manages the versions of all artifacts below; add every new artifact there as well.

Three code modules with strict build order (each depends on the previous):

1. **`peppol-sk-testfiles`** — Bundles test XML files as classpath resources. `PeppolSKTestFiles` provides programmatic access to valid UBL invoices, credit notes, and TDD documents.

2. **`peppol-sk-tdd-datatypes`** — JAXB-generated data model from `external/schemas/2026-04-14/Peppol-Slovak Republic-TDD.xsd`. Contains `CPeppolSKTDD` (schema constants/resources) and `PeppolSKTDD100Marshaller` / `PeppolSKTDD110Marshaller` (XML serialization; both use the same XSD).

3. **`peppol-sk-tdd`** — Main business logic. Key entry points:
   - `PeppolSKTDD100Builder` (package `v100`, spec 1.0.0) and `PeppolSKTDD110Builder` (package `v110`, spec 1.1.0) — build TDD documents from scratch or convert from UBL 2.1 invoices/credit notes. Each spec version has its own full set of `PeppolSKTDD<ver>*Builder` classes (like peppol-vida); never add version switches to an existing package
   - `PeppolSKTDDValidator` — Schematron validation: `validateSK_TDD_100` (1.0.0, `external/schematron/2026-04-14/`; deprecated, not for removal yet) and `validateSK_TDD_110` (1.1.0, `external/schematron/2026-11-20/`); same XSD and CustomizationID for both
   - `PeppolSKTDD100Marshaller` / `PeppolSKTDD110Marshaller` — read/write TDD XML
   - The XSLTs are pre-generated and committed; the (commented out) Schematron convert execution in `peppol-sk-tdd/pom.xml` points to the latest rule sources (`2026-11-20`)

## Architecture

The library converts Peppol e-invoice documents (UBL 2.1 Invoice / CreditNote) into Slovakia TDD XML for tax reporting.

**Data flow:**
```
UBL Invoice/CreditNote XML
        ↓
PeppolSKTDD100Builder.initFromInvoice() / initFromCreditNote()
        ↓
TaxDataType (JAXB model)  ←→  PeppolSKTDD100Marshaller (XML)
        ↓
PeppolSKTDDValidator (Schematron)
```

**Builder pattern** is used throughout with fluent/lambda-based APIs:
```java
// Direct construction
new PeppolSKTDD100Builder()
    .taxDataTypeCode(ESKTDDTaxDataTypeCode.SUBMIT)
    .reportingParty(rp -> rp.partyID("SK1234567890"))
    .build();

// Sub-builders via lambdas
.reportedTransaction(rt -> rt.customizationID(...).profileID(...))
```

`build()` returns `null` (not an exception) if required fields are missing.

## Code Conventions

This codebase follows the [Helger framework](https://github.com/phax) conventions:

- **Annotations:** `@NonNull`/`@Nullable` (JSpecify), `@Immutable` for thread-safe classes
- **Preconditions:** `ValueEnforcer.notNull()` — not standard Java assertions
- **Validation:** `_isEveryRequiredFieldSet()` (private check), `isEveryRequiredFieldSet(ErrorList)` (public)
- **Enums** implement `IHasID<String>` for serialization: `ESKTDDTaxDataTypeCode`, `ESKTDDDocumentScope`, `ESKTDDReporterRole`
- **Resources:** `ClassPathResource` for schema/schematron files, accessed via static `_getCL()` method
- **Logging:** `ConditionalLogger` for optional debug/error messages

## JAXB Code Generation

The classes in `peppol-sk-tdd-datatypes/src/main/java/.../jaxb/` are **auto-generated** from the XSD schema using the JAXB Maven plugin. Do not edit them manually — edit the XSD instead and regenerate with `mvn generate-sources`.

## Test Files

Test XML resources live in `peppol-sk-testfiles/src/main/resources/external/`:
- `invoice/good/` — Valid UBL 2.1 invoices (VAT categories E, O, S, Z; allowances; corrections)
- `creditnote/good/` — Valid UBL 2.1 credit notes
- `tdd/1.0.0/good/` — Valid TDD documents (spec 1.0.0)
- `tdd/1.1.0/good/` — Valid TDD documents (spec 1.1.0); `tdd/1.1.0/bad/bad-<rule-id>.xml` violate exactly that 1.1.0 rule

`PeppolSKTestFiles` exposes these as `IReadableResource` collections.

## Updating to a new spec version

- Add a new package `v<ver>` with a full copy of the builders and a new `PeppolSKTDD<ver>Marshaller`; leave the existing packages unchanged (no API break).
- The XSLTs are taken verbatim from the `resources.zip` of the spec (no local Schematron conversion; the plugin execution in `peppol-sk-tdd/pom.xml` is commented out). Store the zip in `docs/`, the `.sch` files in `peppol-sk-tdd/src/test/resources/external/rule-source/<date>/`, the `.xslt` files in `peppol-sk-tdd/src/main/resources/external/schematron/<date>/` and the examples in `peppol-sk-testfiles/.../tdd/<version>/good/`. Diff the XSD against the one in `peppol-sk-tdd-datatypes`.
- Diff every Schematron rule against the previous version and map each new or changed rule (including cardinality changes) to a builder check in `isEveryRequiredFieldSet` and a `tdd/<version>/bad/bad-<rule-id>.xml` file.
- Register the new VESID in `PeppolSKTDDValidator` and add a `validateSK_TDD_<ver>` method.
