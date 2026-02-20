<?xml version="1.0" encoding="UTF-8"?><schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pxc="urn:peppol:xslt:custom-function" queryBinding="xslt2">
	<title>OpenPeppol Slovak Republic TDD Schematron</title>
	<p id="about">These are the Schematron rules for the OpenPeppol Slovak Republic TDD.

		Author:
      		Susheel Kumar
	</p>
	

	<xsl:function name="pxc:genPath" as="xs:string">
		<xsl:param name="node" as="node()"/>
		
		<xsl:sequence select="         string-join(for $ancestor in $node/ancestor-or-self::node()                     return                       if ($ancestor instance of element())                       then concat('/',                                   name($ancestor),                                   if (   count($ancestor/preceding-sibling::*[name() = name($ancestor)]) &gt;    0                                       or count($ancestor/following-sibling::*[name() = name($ancestor)]) &gt; 0)                                   then concat('[', count($ancestor/preceding-sibling::*[name() = name($ancestor)]) + 1, ']')                                   else ''                                   )                       else                         if ($ancestor instance of attribute())                         then concat('/@', name($ancestor))                         else ''                     , '')     "/>
	</xsl:function>
	<ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
	<ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
	<ns prefix="pxs" uri="urn:peppol:schema:sk-taxdata:1.0"/>
	<ns prefix="pxc" uri="urn:peppol:xslt:custom-function"/>
	<pattern id="default">
		<let name="cl_dtc" value="' S R D '"/>
		<let name="cl_ds" value="' D IC INTL '"/>
		<let name="cl_rr" value="' C2 C3 '"/>
		<let name="cl_currency" value="' AED AFN ALL AMD ANG AOA ARS AUD AWG AZN BAM BBD BDT BGN BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYN BZD CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUP CVE CZK DJF DKK DOP DZD EGP ERN ETB EUR FJD FKP GBP GEL GHS GIP GMD GNF GTQ GYD HKD HNL HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LYD MAD MDL MGA MKD MMK MNT MOP MRU MUR MVR MWK MXN MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD SCR SDG SEK SGD SHP SLE SOS SRD SSP STD SVC SYP SZL THB TJS TMT TND TOP TRY TTD TWD TZS UAH UGX USD USN UYI UYU UYW UZS VED VES VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XDR XOF XPD XPF XPT XSU XTS XUA YER ZAR ZMW ZWG XXX CNH '"/>
		<let name="regex_pidscheme" value="'^[0-9]{4}$'"/>
		<rule context="/pxs:TaxData">
			<let name="dtc" value="normalize-space(pxs:DocumentTypeCode)"/>
			<let name="ds" value="normalize-space(pxs:DocumentScope)"/>
			<let name="rr" value="normalize-space(pxs:ReporterRole)"/>
			<let name="rtCount" value="count(pxs:ReportedTransaction)"/>
			<assert id="ibr-tdd-00" flag="fatal" test="count(*[not(self::cbc:CustomizationID or self::cbc:ProfileID or self::cbc:UUID or self::cbc:IssueDate or self::cbc:IssueTime or self::pxs:DocumentTypeCode or self::pxs:DocumentScope or self::pxs:TaxAuthority or self::pxs:ReporterRole or self::pxs:ReportingParty or self::pxs:ReceivingParty or self::pxs:ReportersRepresentative or self::pxs:ReportedTransaction or self::pxs:ReportedDocument)]) = 0">[ibr-tdd-00] – The pxs:TaxData element MUST NOT contain elements other than cbc:CustomizationID, cbc:ProfileID, cbc:UUID, cbc:IssueDate, cbc:IssueTime, pxs:DocumentTypeCode, pxs:DocumentScope, pxs:TaxAuthority, pxs:ReporterRole, pxs:ReportingParty, pxs:ReceivingParty, pxs:ReportersRepresentative, pxs:ReportedTransaction, and pxs:ReportedDocument. </assert>

			<assert id="ibr-tdd-01" flag="fatal" test="normalize-space(cbc:CustomizationID) = 'urn:peppol:taxdata:sk-1'">[ibr-tdd-01] – The cbc:CustomizationID element MUST have the value 'urn:peppol:taxdata:sk-1'.</assert>

			<assert id="ibr-tdd-02" flag="fatal" test="normalize-space(cbc:ProfileID) = 'urn:peppol:taxreporting'">[ibr-tdd-02] – The cbc:ProfileID element MUST have the value 'urn:peppol:taxreporting'.</assert>

			<assert id="ibr-tdd-03" flag="fatal" test="exists(cbc:UUID)">[ibr-tdd-03] – The cbc:UUID element MUST be present.</assert>

			<assert id="ibr-tdd-04" flag="fatal" test="string-length(normalize-space(cbc:IssueDate)) = 10">[ibr-tdd-04] – The cbc:IssueDate element MUST NOT contain timezone information.</assert>

			<assert id="ibr-tdd-05" flag="fatal" test="matches(normalize-space(cbc:IssueTime), '^(?:([01]\d|2[0-3]):[0-5]\d:[0-5]\d|24:00:00)(\.\d+)?(?:Z|[+-]\d{2}:\d{2})?$')">[ibr-tdd-05] – The cbc:IssueTime element MUST contain timezone information.</assert>

			<assert id="ibr-tdd-06" flag="fatal" test="not(contains($dtc, ' ')) and contains($cl_dtc, concat(' ', $dtc, ' '))">[ibr-tdd-06] – The pxs:DocumentTypeCode element MUST be coded according to the applicable code list.</assert>

			<assert id="ibr-tdd-08" flag="fatal" test="not(contains($ds, ' ')) and contains($cl_ds, concat(' ', $ds, ' '))">[ibr-tdd-08] – The pxs:DocumentScope element MUST be coded according to the applicable code list.</assert>

			<assert id="ibr-tdd-09" flag="fatal" test="not(contains($rr, ' ')) and contains($cl_rr, concat(' ', $rr, ' '))">[ibr-tdd-09] – The pxs:ReporterRole element MUST be coded according to the applicable code list.</assert>

			<assert id="ibr-tdd-10" flag="fatal" test="exists(pxs:TaxAuthority)">[ibr-tdd-10] – The pxs:TaxData element MUST contain a pxs:TaxAuthority element.</assert>

			<assert id="ibr-tdd-11" flag="fatal" test="count(pxs:ReportedTransaction) = 1">[ibr-tdd-11] – Exactly one pxs:ReportedTransaction element MUST be present.</assert>

   		 </rule>

    	<rule context="pxs:TaxData/pxs:TaxAuthority">

      		<assert id="ibr-tdd-12" test="count(*[not(self::cbc:ID or self::cbc:Name)]) = 0">[ibr-tdd-12] – The pxs:TaxAuthority element MUST NOT contain elements other than cbc:ID and cbc:Name.</assert>

			<assert id="ibr-tdd-13" flag="fatal" test="exists(cbc:ID)">[ibr-tdd-13] – The pxs:TaxAuthority element MUST contain the cbc:ID element.</assert>

    	</rule>

    	<rule context="/pxs:TaxData/pxs:ReportingParty">

      		<assert id="ibr-tdd-14" flag="fatal" test="count(*[not(self::cbc:EndpointID)]) = 0">[ibr-tdd-14] – The pxs:ReportingParty element MUST NOT contain elements other than cbc:EndpointID.</assert>

			<assert id="ibr-tdd-15" flag="fatal" test="exists(cbc:EndpointID)">[ibr-tdd-15] – The cbc:EndpointID element of pxs:ReportingParty MUST be present.</assert>

			<assert id="ibr-tdd-16" flag="fatal" test="exists(cbc:EndpointID/@schemeID)">[ibr-tdd-16] – The scheme identifier attribute of cbc:EndpointID MUST be present.</assert>

			<assert id="ibr-tdd-17" flag="fatal" test="not(exists(cbc:EndpointID/@schemeID)) or matches(cbc:EndpointID/@schemeID, $regex_pidscheme)">[ibr-tdd-17] – The scheme identifier attribute of cbc:EndpointID MUST be a Peppol Participant Identifier Scheme.</assert>

    	</rule>

    	<rule context="/pxs:TaxData/pxs:ReceivingParty">

      		<assert id="ibr-tdd-18" flag="fatal" test="count(*[not(self::cbc:EndpointID)]) = 0">[ibr-tdd-18] – The pxs:ReceivingParty element MUST NOT contain elements other than cbc:EndpointID.</assert>

			<assert id="ibr-tdd-19" flag="fatal" test="exists(cbc:EndpointID)">[ibr-tdd-19] – The cbc:EndpointID element of pxs:ReceivingParty MUST be present.</assert>

			<assert id="ibr-tdd-20" flag="fatal" test="exists(cbc:EndpointID/@schemeID) and cbc:EndpointID/@schemeID = '0242'">[ibr-tdd-20] The scheme identifier attribute of Receiving party endpoint ID MUST be present and MUST refer to an SPID ('0242').</assert>

    	</rule>
		<rule context="/pxs:TaxData/pxs:ReportersRepresentative">
			<let name="pidCount" value="count(cac:PartyIdentification/cbc:ID)"/>
			
			<assert id="ibr-tdd-21" flag="fatal" test="count(*[not(self::cac:PartyIdentification)]) = 0">[ibr-tdd-21] – The cac:ReportersRepresentative element MUST NOT contain elements other than cac:PartyIdentification.</assert>
			
			<assert id="ibr-tdd-22" flag="fatal" test="$pidCount = 1">[ibr-tdd-22] – Exactly one cbc:ID element MUST be present within cac:PartyIdentification <value-of select="$pidCount"/> instead</assert>
			
			<assert id="ibr-tdd-23" flag="fatal" test="exists(cac:PartyIdentification/cbc:ID/@schemeID) and cac:PartyIdentification/cbc:ID/@schemeID = '0242'">[ibr-tdd-23] – The scheme identifier attribute of Reporter's Representative party ID MUST be present and MUST refer to an SPID ('0242').</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction">
			<assert id="ibr-tdd-24" flag="fatal" test="exists(pxs:ReportedDocument)">[ibr-tdd-24] – The cac:ReportedDocument element MUST be present.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument">
			<assert id="ibr-tdd-25" flag="fatal" test="count(*[not(self::cbc:CustomizationID or self::cbc:ProfileID or self::cbc:ID or self::cbc:UUID or self::cbc:IssueDate or self::cbc:IssueTime or self::pxs:DocumentTypeCode or self::cbc:Note or self::cbc:TaxPointDate or self::cbc:DocumentCurrencyCode or self::cbc:TaxCurrencyCode or self::cac:InvoicePeriod or self::cac:BillingReference or self::cac:AccountingSupplierParty or self::cac:AccountingCustomerParty or self::cac:TaxRepresentativeParty or self::cac:Delivery or self::cac:PaymentMeans or self::cac:AllowanceCharge or self::cac:TaxTotal or self::pxs:MonetaryTotal or self::pxs:DocumentLine)]) = 0">[ibr-tdd-25] – The cac:ReportedDocument element MUST NOT contain elements other than cbc:CustomizationID, cbc:ProfileID, cbc:ID, cbc:UUID, cbc:IssueDate, cbc:IssueTime, pxs:DocumentTypeCode, cbc:Note, cbc:TaxPointDate, cbc:DocumentCurrencyCode, cbc:TaxCurrencyCode, cac:InvoicePeriod, cac:BillingReference, cac:AccountingSupplierParty, cac:AccountingCustomerParty, cac:TaxRepresentativeParty, cac:Delivery, cac:PaymentMeans, cac:AllowanceCharge, cac:TaxTotal, pxs:MonetaryTotal, and pxs:DocumentLine.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:InvoicePeriod">
			<assert id="ibr-tdd-26" flag="fatal" test="count(*[not(self::cbc:StartDate or self::cbc:EndDate or self::cbc:DescriptionCode)]) = 0">[ibr-tdd-26] – The cac:InvoicePeriod element MUST NOT contain elements other than cbc:StartDate, cbc:EndDate, and cbc:DescriptionCode.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/pxs:DocumentLine/cac:InvoicePeriod">
			<assert id="ibr-tdd-27" flag="fatal" test="count(*[not(self::cbc:StartDate or self::cbc:EndDate)]) = 0">[ibr-tdd-27] – The cac:InvoicePeriod element at line level, MUST NOT contain elements other than cbc:StartDate and cbc:EndDate.</assert>
		</rule>
		
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:BillingReference/cac:InvoiceDocumentReference">
			<assert id="ibr-tdd-28" flag="fatal" test="count(*[not(self::cbc:ID or self::cbc:IssueDate)]) = 0">[ibr-tdd-28] – The cac:InvoiceDocumentReference element MUST NOT contain elements other than cbc:ID and cbc:IssueDate.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingSupplierParty">
			<assert id="ibr-tdd-29" flag="fatal" test="exists(cac:Party)">[ibr-tdd-29] – The SELLER (ibg-04) MUST be present.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingSupplierParty/cac:Party">
			<assert id="ibr-tdd-30" flag="fatal" test="count(*[not(self::cac:PostalAddress or self::cac:PartyTaxScheme)]) = 0">[ibr-tdd-30] – The cac:Party element MUST NOT contain elements other than cac:PostalAddress and optionally cac:PartyTaxScheme.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress">
			<assert id="ibr-tdd-31" flag="fatal" test="count(*[not(self::cac:Country)]) = 0">[ibr-tdd-31] – The cac:PostalAddress element MUST NOT contain elements other than cac:Country.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country">
			<assert id="ibr-tdd-32" flag="fatal" test="count(*[not(self::cbc:IdentificationCode)]) = 0">[ibr-tdd-32] – The cac:Country element MUST NOT contain elements other than cbc:IdentificationCode.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme">
			<assert id="ibr-tdd-33" flag="fatal" test="cac:TaxScheme/cbc:ID = 'VAT'">[ibr-tdd-33] – The value of cac:PartyTaxScheme/cac:TaxScheme/cbc:ID MUST be VAT.</assert>
			
			<assert id="ibr-tdd-34" flag="fatal" test="count(*[not(self::cbc:CompanyID or self::cac:TaxScheme)]) = 0">[ibr-tdd-34] – The cac:PartyTaxScheme element MUST NOT contain elements other than cbc:CompanyID and cac:TaxScheme.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme">
			<assert id="ibr-tdd-35" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-35] – The cac:TaxScheme element MUST NOT contain elements other than cbc:ID.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingCustomerParty">
			<assert id="ibr-tdd-36" flag="fatal" test="exists(cac:Party)">[ibr-tdd-36] – The BUYER (ibg-07) MUST be present.</assert>
			
			<assert id="ibr-tdd-37" flag="fatal" test="count(*[not(self::cac:Party)]) = 0">[ibr-tdd-37] – The cac:AccountingCustomerParty element MUST NOT contain elements other than cac:Party.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingCustomerParty/cac:Party">
			<assert id="ibr-tdd-38" flag="fatal" test="count(*[not(self::cac:PostalAddress or self::cac:PartyTaxScheme or self::cac:PartyLegalEntity)]) = 0">[ibr-tdd-38] – The cac:Party element MUST NOT contain elements other than cac:PostalAddress, cac:PartyLegalEntity, and optionally cac:PartyTaxScheme.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress">
			<assert id="ibr-tdd-39" flag="fatal" test="count(*[not(self::cac:Country)]) = 0">[ibr-tdd-39] – The cac:PostalAddress element MUST NOT contain elements other than cac:Country.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country">
			<assert id="ibr-tdd-40" flag="fatal" test="count(*[not(self::cbc:IdentificationCode)]) = 0">[ibr-tdd-40] – The cac:Country element MUST NOT contain elements other than cbc:IdentificationCode.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme">
			<assert id="ibr-tdd-41" flag="fatal" test="cac:TaxScheme/cbc:ID = 'VAT'">[ibr-tdd-41] – The value of cac:PartyTaxScheme/cac:TaxScheme/cbc:ID MUST be 'VAT'.</assert>
			
			<assert id="ibr-tdd-42" flag="fatal" test="count(*[not(self::cbc:CompanyID or self::cac:TaxScheme)]) = 0">[ibr-tdd-42] – The cac:PartyTaxScheme element MUST NOT contain elements other than cbc:CompanyID and cac:TaxScheme.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme">
			<assert id="ibr-tdd-43" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-43] – The cac:TaxScheme element MUST NOT contain elements other than cbc:ID.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity">
			<assert id="ibr-tdd-86" flag="fatal" test="count(*[not(self::cbc:RegistrationName)]) = 0">[ibr-tdd-86] – The cac:PartyLegalEntity element MUST NOT contain elements other than cbc:RegistrationName.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingCustomerParty/cac:TaxRepresentativeParty">
			<assert id="ibr-tdd-44" flag="fatal" test="count(*[not(self::cac:PostalAddress or self::cac:PartyTaxScheme)]) = 0">[ibr-tdd-44] – The cac:TaxRepresentativeParty element MUST NOT contain elements other than cac:PostalAddress and optionally cac:PartyTaxScheme.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:TaxRepresentativeParty/cac:PostalAddress">
			<assert id="ibr-tdd-45" flag="fatal" test="count(*[not(self::cac:Country)]) = 0">[ibr-tdd-45] – The cac:PostalAddress element MUST NOT contain elements other than cac:Country.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:TaxRepresentativeParty/cac:PostalAddress/cac:Country">
			<assert id="ibr-tdd-46" flag="fatal" test="count(*[not(self::cbc:IdentificationCode)]) = 0">[ibr-tdd-46] – The cac:Country element MUST NOT contain elements other than cbc:IdentificationCode.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:Delivery">
			<assert id="ibr-tdd-85" flag="fatal" test="count(*[not(self::cbc:ActualDeliveryDate)]) = 0">[ibr-tdd-85] – The cac:Delivery element MUST NOT contain elements other than cbc:ActualDeliveryDate.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:TaxRepresentativeParty/cac:PartyTaxScheme">
			<assert id="ibr-tdd-47" flag="fatal" test="cac:TaxScheme/cbc:ID = 'VAT'">[ibr-tdd-47] – The value of cac:PartyTaxScheme/cac:TaxScheme/cbc:ID MUST be 'VAT'.</assert>
			
			<assert id="ibr-tdd-48" flag="fatal" test="count(*[not(self::cbc:CompanyID or self::cac:TaxScheme)]) = 0">[ibr-tdd-48] – The cac:PartyTaxScheme element MUST NOT contain elements other than cbc:CompanyID and cac:TaxScheme.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:TaxScheme">
			<assert id="ibr-tdd-49" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-49] – The cac:TaxScheme element MUST NOT contain elements other than cbc:ID.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:PaymentMeans">
			<assert id="ibr-tdd-50" flag="fatal" test="count(*[not(self::cbc:PaymentMeansCode or self::cbc:PaymentID or self::cac:CardAccount or self::cac:PayeeFinancialAccount or self::cac:PaymentMandate)]) = 0">[ibr-tdd-50] – The cac:PaymentMeans element MUST NOT contain elements other than cbc:PaymentMeansCode, cbc:PaymentID, cac:CardAccount, cac:PayeeFinancialAccount, and cac:PaymentMandate.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:PaymentMeans/cbc:PaymentMeansCode">
			<assert id="ibr-tdd-51" flag="fatal" test="count(@*[not(local-name() = 'name')]) = 0">[ibr-tdd-51] – The cbc:PaymentMeansCode element MUST NOT have attributes other than 'name'.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:PaymentMeans/cac:CardAccount">
			<assert id="ibr-tdd-52" flag="fatal" test="count(*[not(self::cbc:PrimaryAccountNumberID or self::cbc:NetworkID or self::cbc:HolderName)]) = 0">[ibr-tdd-52] – The cac:CardAccount element MUST NOT contain elements other than cbc:PrimaryAccountNumberID, cbc:NetworkID, and cbc:HolderName.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:PaymentMeans/cac:PayeeFinancialAccount">
			<assert id="ibr-tdd-53" flag="fatal" test="count(*[not(self::cbc:ID or self::cac:FinancialInstitutionBranch)]) = 0">[ibr-tdd-53] – The cac:PayeeFinancialAccount element MUST NOT contain elements other than cbc:ID and cac:FinancialInstitutionBranch.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch">
			<assert id="ibr-tdd-54" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-54] – The cac:FinancialInstitutionBranch element MUST NOT contain elements other than cbc:ID.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:PaymentMeans/cac:PaymentMandate">
			<assert id="ibr-tdd-55" flag="fatal" test="count(*[not(self::cbc:ID or self::cac:PayerFinancialAccount)]) = 0">[ibr-tdd-55] – The cac:PaymentMandate element MUST NOT contain elements other than cbc:ID and cac:PayerFinancialAccount.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount">
			<assert id="ibr-tdd-56" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-56] – The cac:PayerFinancialAccount element MUST NOT contain elements other than cbc:ID.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AllowanceCharge">
			<assert id="ibr-tdd-57" flag="fatal" test="count(*[not(self::cbc:ChargeIndicator or self::cbc:AllowanceChargeReasonCode or self::cbc:AllowanceChargeReason or self::cbc:MultiplierFactorNumeric or self::cbc:Amount or self::cbc:BaseAmount or self::cac:TaxCategory)]) = 0">[ibr-tdd-57] – The cac:AllowanceCharge element MUST NOT contain elements other than cbc:ChargeIndicator, cbc:AllowanceChargeReasonCode, cbc:AllowanceChargeReason, cbc:MultiplierFactorNumeric, cbc:Amount, cbc:BaseAmount, and cac:TaxCategory.</assert>

			<assert id="ibr-tdd-58" flag="fatal" test="count(cbc:Amount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-58] – The cbc:Amount element MUST have the attribute 'currencyID'.</assert>

			<assert id="ibr-tdd-59" flag="fatal" test="count(cbc:BaseAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-59] – The cbc:BaseAmount element MUST have the attribute 'currencyID'.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/pxs:DocumentLine/cac:AllowanceCharge">
			<assert id="ibr-tdd-60" flag="fatal" test="count(*[not(self::cbc:ChargeIndicator or self::cbc:AllowanceChargeReasonCode or self::cbc:AllowanceChargeReason or self::cbc:MultiplierFactorNumeric or self::cbc:Amount or self::cbc:BaseAmount)]) = 0">[ibr-tdd-60] – The cac:AllowanceCharge element MUST NOT contain elements other than cbc:ChargeIndicator, cbc:AllowanceChargeReasonCode, cbc:AllowanceChargeReason, cbc:MultiplierFactorNumeric, cbc:Amount, and cbc:BaseAmount.</assert>

			<assert id="ibr-tdd-61" flag="fatal" test="count(cbc:Amount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-61] – The cbc:Amount element MUST have the attribute 'currencyID'.</assert>

			<assert id="ibr-tdd-62" flag="fatal" test="count(cbc:BaseAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-62] – The cbc:BaseAmount element MUST have the attribute 'currencyID'.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AllowanceCharge/cac:TaxCategory">
			<assert id="ibr-tdd-63" flag="fatal" test="count(*[not(self::cbc:ID or self::cbc:Percent or self::cac:TaxScheme)]) = 0">[ibr-tdd-63] – The cac:TaxCategory element MUST NOT contain elements other than cbc:ID, cbc:Percent, and cac:TaxScheme.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme">
			<assert id="ibr-tdd-64" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-64] – The cac:TaxScheme element MUST NOT contain elements other than cbc:ID.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:TaxTotal">
			<assert id="ibr-tdd-65" flag="fatal" test="count(*[not(self::cbc:TaxAmount or self::cac:TaxSubtotal)]) = 0">[ibr-tdd-65] – The cac:TaxTotal element MUST NOT contain elements other than cbc:TaxAmount and cac:TaxSubtotal.</assert>

			<assert id="ibr-tdd-66" flag="fatal" test="count(cbc:TaxAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-66] – The cbc:TaxAmount element MUST have the attribute 'currencyID'.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:TaxTotal/cac:TaxSubtotal">
			<assert id="ibr-tdd-67" flag="fatal" test="count(*[not(self::cbc:TaxableAmount or self::cbc:TaxAmount or self::cac:TaxCategory)]) = 0">[ibr-tdd-67] – The cac:TaxSubtotal element MUST NOT contain elements other than cbc:TaxableAmount, cbc:TaxAmount, and cac:TaxCategory.</assert>

			<assert id="ibr-tdd-68" flag="fatal" test="count(cbc:TaxableAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-68] – The cbc:TaxableAmount element MUST have the attribute 'currencyID'.</assert>

			<assert id="ibr-tdd-69" flag="fatal" test="count(cbc:TaxAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-69] – The cbc:TaxAmount element MUST have the attribute 'currencyID'.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory">
			<assert id="ibr-tdd-70" flag="fatal" test="count(*[not(self::cbc:ID or self::cbc:Percent or self::cbc:TaxExemptionReasonCode or self::cbc:TaxExemptionReason or self::cac:TaxScheme)]) = 0">[ibr-tdd-70] – The cac:TaxCategory element MUST NOT contain elements other than cbc:ID, cbc:Percent, cbc:TaxExemptionReasonCode, cbc:TaxExemptionReason, and cac:TaxScheme.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme">
			<assert id="ibr-tdd-71" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-71] – The cac:TaxScheme element MUST NOT contain elements other than cbc:ID.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/pxs:MonetaryTotal">
			<assert id="ibr-tdd-72" flag="fatal" test="count(*[not(self::cbc:LineExtensionAmount or self::cbc:TaxExclusiveAmount or self::cbc:TaxInclusiveAmount or self::cbc:AllowanceTotalAmount or self::cbc:ChargeTotalAmount or self::cbc:PrepaidAmount or self::cbc:PayableRoundingAmount or self::cbc:PayableAmount)]) = 0">[ibr-tdd-72] – The pxs:MonetaryTotal element MUST NOT contain elements other than cbc:LineExtensionAmount, cbc:TaxExclusiveAmount, cbc:TaxInclusiveAmount, cbc:AllowanceTotalAmount, cbc:ChargeTotalAmount, cbc:PrepaidAmount, cbc:PayableRoundingAmount, and cbc:PayableAmount.</assert>

			<assert id="ibr-tdd-73" flag="fatal" test="count(*[self::cbc:LineExtensionAmount or self::cbc:TaxExclusiveAmount or self::cbc:TaxInclusiveAmount or self::cbc:AllowanceTotalAmount or self::cbc:ChargeTotalAmount or self::cbc:PrepaidAmount or self::cbc:PayableAmount]/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-73] – All amount elements within pxs:MonetaryTotal MUST have the attribute 'currencyID'.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/pxs:DocumentLine">
			<assert id="ibr-tdd-74" flag="fatal" test="count(*[not(self::cbc:ID or self::cbc:Note or self::cbc:InvoicedQuantity or self::cbc:LineExtensionAmount or self::cac:InvoicePeriod or self::cac:AllowanceCharge or self::cac:Item or self::cac:Price)]) = 0">[ibr-tdd-74] – The pxs:DocumentLine element MUST NOT contain elements other than cbc:ID, cbc:Note, cbc:InvoicedQuantity, cbc:LineExtensionAmount, cac:InvoicePeriod, cac:AllowanceCharge, cac:Item, and cac:Price.</assert>

			<assert id="ibr-tdd-75" flag="fatal" test="count(cbc:InvoicedQuantity/@*[not(local-name() = 'unitCode')]) = 0">[ibr-tdd-75] – The cbc:InvoicedQuantity element MUST have the attribute 'unitCode'.</assert>

			<assert id="ibr-tdd-76" flag="fatal" test="count(cbc:LineExtensionAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-76] – The cbc:LineExtensionAmount element MUST have the attribute 'currencyID'.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/pxs:DocumentLine/cac:Item">
			<assert id="ibr-tdd-77" flag="fatal" test="count(*[not(self::cbc:Description or self::cbc:Name or self::cac:CommodityClassification or self::cac:ClassifiedTaxCategory)]) = 0">[ibr-tdd-77] – The cac:Item element MUST NOT contain elements other than cbc:Description, cbc:Name, cac:CommodityClassification, and cac:ClassifiedTaxCategory.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/pxs:DocumentLine/cac:Item/cac:CommodityClassification">
			<assert id="ibr-tdd-78" flag="fatal" test="count(*[not(self::cbc:ItemClassificationCode)]) = 0">[ibr-tdd-78] – The cac:CommodityClassification element MUST NOT contain elements other than cbc:ItemClassificationCode.</assert>

			<assert id="ibr-tdd-79" flag="fatal" test="count(cbc:ItemClassificationCode/@*[not(local-name() = 'listID')]) = 0">[ibr-tdd-79] – The cbc:ItemClassificationCode element MUST have the attribute 'listID'.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/pxs:DocumentLine/cac:Item/cac:ClassifiedTaxCategory">
			<assert id="ibr-tdd-80" flag="fatal" test="count(*[not(self::cbc:ID or self::cbc:Percent or self::cac:TaxScheme)]) = 0">[ibr-tdd-80] – The cac:ClassifiedTaxCategory element MUST NOT contain elements other than cbc:ID, cbc:Percent, and cac:TaxScheme.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/pxs:DocumentLine/cac:Item/cac:ClassifiedTaxCategory/cac:TaxScheme">
			<assert id="ibr-tdd-81" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-81] – The cac:TaxScheme element MUST NOT contain elements other than cbc:ID.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/pxs:DocumentLine/cac:Price">
			<assert id="ibr-tdd-82" flag="fatal" test="count(*[not(self::cbc:PriceAmount or self::cac:AllowanceCharge)]) = 0">[ibr-tdd-82] – The cac:Price element MUST NOT contain elements other than cbc:PriceAmount and cac:AllowanceCharge.</assert>

			<assert id="ibr-tdd-83" flag="fatal" test="count(cbc:PriceAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-83] – The cbc:PriceAmount element MUST have the attribute 'currencyID'.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:BillingReference">
			<assert id="ibr-tdd-84" flag="fatal" test="count(*[not(self::cac:InvoiceDocumentReference)]) = 0">[ibr-tdd-84] – The cac:BillingReference element MUST NOT contain elements other than cac:InvoiceDocumentReference.</assert>
		</rule>

		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/pxs:DocumentLine/cac:Price/cac:AllowanceCharge">
			<assert id="ibr-tdd-87" flag="fatal" test="count(*[not(self::cbc:ChargeIndicator or self::cbc:Amount or self::cbc:BaseAmount)]) = 0">[ibr-tdd-87] – The cac:AllowanceCharge element MUST NOT contain elements other than cbc:ChargeIndicator, cbc:Amount, and cbc:BaseAmount.</assert>

			<assert id="ibr-tdd-88" flag="fatal" test="count(cbc:Amount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-88] – The cbc:Amount element MUST have the attribute 'currencyID'.</assert>

			<assert id="ibr-tdd-89" flag="fatal" test="count(cbc:BaseAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-89] – The cbc:BaseAmount element MUST have the attribute 'currencyID'.</assert>
		</rule>

	</pattern>
</schema>