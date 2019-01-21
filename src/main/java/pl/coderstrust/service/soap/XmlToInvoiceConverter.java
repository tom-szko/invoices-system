package pl.coderstrust.service.soap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import pl.coderstrust.model.AccountNumber;
import pl.coderstrust.model.Address;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.ContactDetails;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.InvoiceType;
import pl.coderstrust.model.UnitType;
import pl.coderstrust.model.Vat;

public class XmlToInvoiceConverter {

  public static InvoiceType convertXmlInvoiceTypeToInvoiceType(pl.coderstrust.soap.domainclasses.InvoiceType xmlInvoiceType) {
    return InvoiceType.valueOf(xmlInvoiceType.value());
  }

  public static UnitType convertXmlUnitTypeToUnitType(pl.coderstrust.soap.domainclasses.UnitType xmlUnitType) {
    return UnitType.valueOf(xmlUnitType.value());

  }

  public static Vat convertXmlVatToVat(pl.coderstrust.soap.domainclasses.Vat xmlVat) {
    return Vat.valueOf(xmlVat.value());
  }

  public static ContactDetails convertXmlContactDetailsToContactDetails(pl.coderstrust.soap.domainclasses.ContactDetails xmlContactDetails) {
    return new ContactDetails(
        xmlContactDetails.getEmail(),
        xmlContactDetails.getPhoneNumber(),
        xmlContactDetails.getWebsite(),
        convertXmlAddressToAddress(xmlContactDetails.getAddress())
    );
  }

  public static AccountNumber convertXmlAccountNumberToAccountNumber(pl.coderstrust.soap.domainclasses.AccountNumber xmlAccountNumber) {
    return new AccountNumber(xmlAccountNumber.getIbanNumber());
  }

  public static Address convertXmlAddressToAddress(pl.coderstrust.soap.domainclasses.Address xmlAddress) {
    return new Address(
        xmlAddress.getStreet(),
        xmlAddress.getNumber(),
        xmlAddress.getPostalCode(),
        xmlAddress.getCity(),
        xmlAddress.getCountry()
    );
  }

  public static Company convertXmlCompanyToCompany(pl.coderstrust.soap.domainclasses.Company xmlCompany) {
    return new Company(
        xmlCompany.getName(),
        xmlCompany.getTaxIdentificationNumber(),
        convertXmlAccountNumberToAccountNumber(xmlCompany.getAccountNumber()),
        convertXmlContactDetailsToContactDetails(xmlCompany.getContactDetails())
    );
  }

  public static LocalDate convertStringToDate(String dateAsString) {
    return LocalDate.parse(dateAsString);
  }

  public static List<InvoiceEntry> convertXmlInvoiceEntriesToInvoiceEntryList(pl.coderstrust.soap.domainclasses.InvoiceEntries xmlInvoiceEntries) {
    List<InvoiceEntry> invoiceEntries = new ArrayList<>();
    for (pl.coderstrust.soap.domainclasses.InvoiceEntry xmlInvoiceEntry : xmlInvoiceEntries.getInvoiceEntry()) {
      invoiceEntries.add(convertXmlInvoiceEntryToInvoiceEntry(xmlInvoiceEntry));
    }
    return invoiceEntries;
  }

  public static InvoiceEntry convertXmlInvoiceEntryToInvoiceEntry(pl.coderstrust.soap.domainclasses.InvoiceEntry xmlInvoiceEntry) {
    return new InvoiceEntry(
        xmlInvoiceEntry.getItem(),
        xmlInvoiceEntry.getQuantity(),
        convertXmlUnitTypeToUnitType(xmlInvoiceEntry.getUnit()),
        xmlInvoiceEntry.getPrice(),
        convertXmlVatToVat(xmlInvoiceEntry.getVatRate()),
        xmlInvoiceEntry.getNetValue(),
        xmlInvoiceEntry.getGrossValue()
    );
  }

  public static Invoice convertXmlInvoiceToInvoice(pl.coderstrust.soap.domainclasses.Invoice xmlInvoice) {
    return new Invoice(
        xmlInvoice.getId(),
        convertXmlInvoiceTypeToInvoiceType(xmlInvoice.getType()),
        convertStringToDate(xmlInvoice.getIssueDate()),
        convertStringToDate(xmlInvoice.getDueDate()),
        convertXmlCompanyToCompany(xmlInvoice.getSeller()),
        convertXmlCompanyToCompany(xmlInvoice.getBuyer()),
        convertXmlInvoiceEntriesToInvoiceEntryList(xmlInvoice.getInvoiceEntries()),
        xmlInvoice.getTotalNetValue(),
        xmlInvoice.getTotalGrossValue(),
        xmlInvoice.getComments()
    );
  }
}
