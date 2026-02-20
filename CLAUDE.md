# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Java library implementing the Peppol Slovakia Tax Data Document (TDD) specification. The spec is at https://test-docs.peppol.eu/tdd/sk/ and is currently a **work in progress** — the data model is not yet final.

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

Three Maven modules with strict build order (each depends on the previous):

1. **`peppol-sk-testfiles`** — Bundles test XML files as classpath resources. `PeppolSKTestFiles` provides programmatic access to valid UBL invoices, credit notes, and TDD documents.

2. **`peppol-sk-tdd-datatypes`** — JAXB-generated data model from `external/schemas/2026-02-20/sk-tdd-1.0.0.xsd`. Contains `CPeppolSKTDD` (schema constants/resources) and `PeppolSLTDD100Marshaller` (XML serialization).

3. **`peppol-sk-tdd`** — Main business logic. Key entry points:
   - `PeppolSKTDD100Builder` — builds TDD documents from scratch or converts from UBL 2.1 invoices/credit notes
   - `PeppolSKTDDValidator` — Schematron validation using `external/schematron/2026-02-20/Peppol-Slovak Republic-TDD.sch`
   - `PeppolSLTDD100Marshaller` — read/write TDD XML

## Architecture

The library converts Peppol e-invoice documents (UBL 2.1 Invoice / CreditNote) into Slovakia TDD XML for tax reporting.

**Data flow:**
```
UBL Invoice/CreditNote XML
        ↓
PeppolSKTDD100Builder.initFromInvoice() / initFromCreditNote()
        ↓
TaxDataType (JAXB model)  ←→  PeppolSLTDD100Marshaller (XML)
        ↓
PeppolSKTDDValidator (Schematron)
```

**Builder pattern** is used throughout with fluent/lambda-based APIs:
```java
// Direct construction
new PeppolSKTDD100Builder()
    .documentTypeCode(ESKTDDDocumentTypeCode.SUBMIT)
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
- **Validation:** `_isEveryRequiredFieldSet()` (private check), `isEveryRequiredFieldSet()` (public)
- **Enums** implement `IHasID<String>` for serialization: `ESKTDDDocumentTypeCode`, `ESKTDDDocumentScope`, `ESKTDDReporterRole`
- **Resources:** `ClassPathResource` for schema/schematron files, accessed via static `_getCL()` method
- **Logging:** `ConditionalLogger` for optional debug/error messages

## JAXB Code Generation

The classes in `peppol-sk-tdd-datatypes/src/main/java/.../jaxb/` are **auto-generated** from the XSD schema using the JAXB Maven plugin. Do not edit them manually — edit the XSD instead and regenerate with `mvn generate-sources`.

## Test Files

Test XML resources live in `peppol-sk-testfiles/src/main/resources/external/`:
- `invoice/good/` — Valid UBL 2.1 invoices (VAT categories E, O, S, Z; allowances; corrections)
- `creditnote/good/` — Valid UBL 2.1 credit notes
- `tdd/1.0.0/good/` — Valid TDD documents

`PeppolSKTestFiles` exposes these as `IReadableResource` collections.
