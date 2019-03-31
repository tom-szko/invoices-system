package pl.coderstrust.database.invoice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import pl.coderstrust.model.AccountNumber;
import pl.coderstrust.model.Address;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.ContactDetails;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.InvoiceType;
import pl.coderstrust.model.UnitType;
import pl.coderstrust.model.Vat;

public class InvoiceExtractor implements ResultSetExtractor<List<Invoice>> {

  @Override
  public List<Invoice> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
    List<Invoice> invoices = new ArrayList<>();
    List<InvoiceEntry> entries = null;
    Invoice invoice = null;
    String id = null;
    String currentId = null;
    while (resultSet.next()) {
      currentId = String.valueOf(resultSet.getInt("invoice_id"));
      if (!currentId.equals(id)) {
        id = currentId;
        invoice = new Invoice();
        invoice.setId(id);
        invoice.setType(InvoiceType.valueOf(resultSet.getString("invoice_type")));
        invoice.setIssueDate(resultSet.getDate("issue_date").toLocalDate());
        invoice.setDueDate(resultSet.getDate("due_date").toLocalDate());
        invoice.setSeller(extractCompany(
            resultSet,
            "seller_company_name",
            "seller_company_tax_id",
            "seller_company_street",
            "seller_company_street_number",
            "seller_company_postal_code",
            "seller_company_city",
            "seller_company_country",
            "seller_company_email",
            "seller_company_phone_number",
            "seller_company_website",
            "seller_company_iban_number",
            "seller_company_local_number"
        ));
        invoice.setSeller(extractCompany(
            resultSet,
            "buyer_company_name",
            "buyer_company_tax_id",
            "buyer_company_street",
            "buyer_company_street_number",
            "buyer_company_postal_code",
            "buyer_company_city",
            "buyer_company_country",
            "buyer_company_email",
            "buyer_company_phone_number",
            "buyer_company_website",
            "buyer_company_iban_number",
            "buyer_company_local_number"
        ));
        invoice.setTotalNetValue(resultSet.getBigDecimal("total_net_value"));
        invoice.setTotalGrossValue(resultSet.getBigDecimal("total_gross_value"));
        invoice.setComments(resultSet.getString("comments"));
        invoices.add(invoice);
      }
      if (entries == null) {
        entries = new ArrayList<>();
      }
      InvoiceEntry entry = new InvoiceEntry();
      entry.setItem(resultSet.getString("item"));
      entry.setQuantity(resultSet.getLong("quantity"));
      entry.setUnit(UnitType.valueOf(resultSet.getString("unit_type")));
      entry.setPrice(resultSet.getBigDecimal("price"));
      entry.setVatRate(Vat.valueOf(resultSet.getString("vat_rate")));
      entry.setNetValue(resultSet.getBigDecimal("net_value"));
      entry.setGrossValue(resultSet.getBigDecimal("gross_value"));
      entries.add(entry);
    }
    assert invoice != null;
    invoice.setEntries(entries);
    return invoices;
  }

  private Address extractAddress(ResultSet resultSet, String street, String streetNumber, String postalCode, String city, String country)
      throws SQLException {
    Address address = new Address();
    address.setStreet(resultSet.getString(street));
    address.setNumber(resultSet.getString(streetNumber));
    address.setPostalCode(resultSet.getString(postalCode));
    address.setCity(resultSet.getString(city));
    address.setCountry(resultSet.getString(country));
    return address;
  }

  private ContactDetails extractContactDetails(ResultSet resultSet, String email, String phoneNumber, String website, Address address)
      throws SQLException {
    ContactDetails contactDetails = new ContactDetails();
    contactDetails.setEmail(resultSet.getString(email));
    contactDetails.setPhoneNumber(resultSet.getString(phoneNumber));
    contactDetails.setWebsite(resultSet.getString(website));
    contactDetails.setAddress(address);
    return contactDetails;
  }

  private AccountNumber extractAccountNumber(ResultSet resultSet, String ibanNumber, String localNumber) throws SQLException {
    AccountNumber accountNumber = new AccountNumber();
    accountNumber.setIbanNumber(resultSet.getString(ibanNumber));
    accountNumber.setLocalNumber(resultSet.getString(localNumber));
    return accountNumber;
  }

  private Company extractCompany(
      ResultSet resultSet,
      String companyName,
      String taxId,
      String street,
      String streetNumber,
      String postalCode,
      String city,
      String country,
      String email,
      String phoneNumber,
      String website,
      String ibanNumber,
      String localNumber) throws SQLException {
    Company company = new Company();
    company.setName(resultSet.getString(companyName));
    company.setTaxIdentificationNumber(resultSet.getString(taxId));
    company.setAccountNumber(extractAccountNumber(resultSet, ibanNumber, localNumber));
    Address address = extractAddress(resultSet, street, streetNumber, postalCode, city, country);
    company.setContactDetails(extractContactDetails(resultSet, email, phoneNumber, website, address));
    return company;
  }
}
