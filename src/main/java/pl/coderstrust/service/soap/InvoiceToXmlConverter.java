package pl.coderstrust.service.soap;

import java.time.LocalDate;
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

public class InvoiceToXmlConverter {

  public static pl.coderstrust.soap.domainclasses.Invoice convertInvoiceToXmlInvoice(Invoice invoice) {
    pl.coderstrust.soap.domainclasses.Invoice xmlInvoice = new pl.coderstrust.soap.domainclasses.Invoice();
    xmlInvoice.setId(invoice.getId());
    xmlInvoice.setType(convertInvoiceTypeToXmlInvoiceType(invoice.getType()));
    xmlInvoice.setIssueDate(convertDateToString(invoice.getIssueDate()));
    xmlInvoice.setDueDate(convertDateToString(invoice.getDueDate()));
    xmlInvoice.setSeller(convertCompanyToXmlCompany(invoice.getSeller()));
    xmlInvoice.setBuyer(convertCompanyToXmlCompany(invoice.getBuyer()));
    xmlInvoice.setInvoiceEntries(convertInvoiceEntriesToXmlInvoiceEntries(invoice.getEntries()));
    xmlInvoice.setTotalNetValue(invoice.getTotalNetValue());
    xmlInvoice.setTotalGrossValue(invoice.getTotalGrossValue());
    xmlInvoice.setComments(invoice.getComments());
    return xmlInvoice;
  }

  public static pl.coderstrust.soap.domainclasses.InvoiceType convertInvoiceTypeToXmlInvoiceType(InvoiceType invoiceType) {
    return pl.coderstrust.soap.domainclasses.InvoiceType.fromValue(invoiceType.toString());
  }

  public static String convertDateToString(LocalDate date) {
    return date.toString();
  }

  public static pl.coderstrust.soap.domainclasses.Company convertCompanyToXmlCompany(Company company) {
    pl.coderstrust.soap.domainclasses.Company xmlCompany = new pl.coderstrust.soap.domainclasses.Company();
    xmlCompany.setName(company.getName());
    xmlCompany.setContactDetails(convertContactDetailsToXmlContactDetails(company.getContactDetails()));
    xmlCompany.setTaxIdentificationNumber(company.getTaxIdentificationNumber());
    xmlCompany.setAccountNumber(convertAccountNumberToXmlAccountNumber(company.getAccountNumber()));
    return xmlCompany;
  }

  public static pl.coderstrust.soap.domainclasses.AccountNumber convertAccountNumberToXmlAccountNumber(AccountNumber accountNumber) {
    pl.coderstrust.soap.domainclasses.AccountNumber xmlAccountNumber = new pl.coderstrust.soap.domainclasses.AccountNumber();
    xmlAccountNumber.setIbanNumber(accountNumber.getIbanNumber());
    xmlAccountNumber.setLocalNumber(accountNumber.getLocalNumber());
    return xmlAccountNumber;
  }

  public static pl.coderstrust.soap.domainclasses.ContactDetails convertContactDetailsToXmlContactDetails(ContactDetails contactDetails) {
    pl.coderstrust.soap.domainclasses.ContactDetails xmlContactDetails = new pl.coderstrust.soap.domainclasses.ContactDetails();
    xmlContactDetails.setAddress(convertAddressToXmlAddress(contactDetails.getAddress()));
    xmlContactDetails.setEmail(contactDetails.getEmail());
    xmlContactDetails.setPhoneNumber(contactDetails.getPhoneNumber());
    xmlContactDetails.setWebsite(contactDetails.getWebsite());
    return xmlContactDetails;
  }

  public static pl.coderstrust.soap.domainclasses.Address convertAddressToXmlAddress(Address address) {
    pl.coderstrust.soap.domainclasses.Address xmlAddress = new pl.coderstrust.soap.domainclasses.Address();
    xmlAddress.setCity(address.getCity());
    xmlAddress.setCountry(address.getCountry());
    xmlAddress.setStreet(address.getStreet());
    xmlAddress.setNumber(address.getNumber());
    xmlAddress.setPostalCode(address.getPostalCode());
    return xmlAddress;
  }

  public static pl.coderstrust.soap.domainclasses.InvoiceEntry convertInvoiceEntryToXmlInvoiceEntry(InvoiceEntry invoiceEntry) {
    pl.coderstrust.soap.domainclasses.InvoiceEntry xmlInvoiceEntry = new pl.coderstrust.soap.domainclasses.InvoiceEntry();
    xmlInvoiceEntry.setItem(invoiceEntry.getItem());
    xmlInvoiceEntry.setQuantity(invoiceEntry.getQuantity());
    xmlInvoiceEntry.setUnit(convertUnitTypeToXmlUnitType(invoiceEntry.getUnit()));
    xmlInvoiceEntry.setPrice(invoiceEntry.getPrice());
    xmlInvoiceEntry.setVatRate(convertVatToXmlVat(invoiceEntry.getVatRate()));
    return xmlInvoiceEntry;
  }

  public static pl.coderstrust.soap.domainclasses.UnitType convertUnitTypeToXmlUnitType(UnitType unitType) {
    return pl.coderstrust.soap.domainclasses.UnitType.fromValue(unitType.toString());
  }

  public static pl.coderstrust.soap.domainclasses.Vat convertVatToXmlVat(Vat vatRate) {
    return pl.coderstrust.soap.domainclasses.Vat.fromValue(vatRate.toString());
  }

  public static pl.coderstrust.soap.domainclasses.InvoiceEntries convertInvoiceEntriesToXmlInvoiceEntries(List<InvoiceEntry> invoiceEntries) {
    pl.coderstrust.soap.domainclasses.InvoiceEntries xmlInvoiceEntries = new pl.coderstrust.soap.domainclasses.InvoiceEntries();
    for (InvoiceEntry invoiceEntry : invoiceEntries) {
      xmlInvoiceEntries.getInvoiceEntries().add(convertInvoiceEntryToXmlInvoiceEntry(invoiceEntry));
    }
    return xmlInvoiceEntries;
  }
}
