package pl.coderstrust.database.invoice;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.AccountNumber;
import pl.coderstrust.model.Address;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.ContactDetails;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

@Slf4j
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "mysql")
@Repository
public class MySQLInvoiceDatabase implements InvoiceDatabase {

  static final String INVOICES_VIEW = "SELECT * FROM invoices_db.invoices_view";
  static final String GET_INVOICE_BY_ID_QUERY = INVOICES_VIEW + " WHERE invoice_id = ";
  static final String GET_INVOICES_BY_SELLER = INVOICES_VIEW + " WHERE seller_company_name = ";
  static final String GET_INVOICES_BY_BUYER = INVOICES_VIEW + " WHERE buyer_company_name = ";
  static final String GET_INVOICES_QUERY = "SELECT * FROM invoices_db.invoices";
  static final String INVOICE_ENTRY_TABLE = "invoice_entry";
  static final String ADDRESSES_TABLE = "addresses";
  static final String ACCOUNT_NUMBERS_TABLE = "account_numbers";
  static final String CONTACT_DETAILS_TABLE = "contact_details";
  static final String COMPANIES_TABLE = "companies";
  static final String INVOICES_TABLE = "invoices";

  JdbcTemplate jdbcTemplate;

  @Autowired
  public MySQLInvoiceDatabase(JdbcTemplate jdbcTemplate) throws DatabaseOperationException {
    try {
      this.jdbcTemplate = jdbcTemplate;
      log.info("Creating database and tables...");
      Resource resource = new ClassPathResource("CreateInvoicesDatabase.sql");
      ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
      databasePopulator.execute(Objects.requireNonNull(jdbcTemplate.getDataSource()));
      log.info("Database and tables successfully created.");
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Encountered error while initializing database", e);
    }
  }

  @Override
  public Iterable<Invoice> findAllBySellerName(String sellerName) throws DatabaseOperationException {
    try {
      return jdbcTemplate.query(GET_INVOICES_BY_SELLER + sellerName, new InvoiceExtractor());
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error retrieving invoices.", e);
    }
  }

  @Override
  public Iterable<Invoice> findAllByBuyerName(String buyerName) throws DatabaseOperationException {
    try {
      return jdbcTemplate.query(GET_INVOICES_BY_BUYER + buyerName, new InvoiceExtractor());
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error retrieving invoices.", e);
    }
  }

  @Override
  public Invoice save(Invoice invoice) throws DatabaseOperationException {
    return null;
  }

  @Override
  public Optional<Invoice> findById(String id) throws DatabaseOperationException {
    try {
      List<Invoice> invoices = jdbcTemplate.query(GET_INVOICE_BY_ID_QUERY + id, new InvoiceExtractor());
      //TODO ->>> replace getting from list with something else.///////
      return Optional.of(invoices.get(0));
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error retrieving invoice with id: " + id, e);
    }
  }

  @Override
  public boolean existsById(String id) throws DatabaseOperationException {
    try {
      String sql = "SELECT EXISTS(SELECT 1 FROM invoices_db.invoices WHERE id = ?)";
      int count = jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class);
      return count > 0;
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error while checking if invoice exists.", e);
    }
  }

  @Override
  public Iterable<Invoice> findAll() throws DatabaseOperationException {
    try {
      return jdbcTemplate.query(INVOICES_VIEW, new InvoiceExtractor());
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error retrieving invoices.", e);
    }
  }

  @Override
  public long count() throws DatabaseOperationException {
    try {
      SqlRowSet rs = jdbcTemplate.queryForRowSet(GET_INVOICES_QUERY);
      return (long) rs.getInt(1);
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error retrieving number of invoices.", e);
    }
  }

  @Override
  public void deleteById(String var1) throws DatabaseOperationException {

  }

  @Override
  public void deleteAll() throws DatabaseOperationException {

  }

  public long addInvoiceEntry(InvoiceEntry invoiceEntry) throws DatabaseOperationException {
    try {
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
          .withTableName(INVOICE_ENTRY_TABLE).usingGeneratedKeyColumns("id");
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("item", invoiceEntry.getItem());
      parameters.put("quantity", invoiceEntry.getQuantity());
      parameters.put("unit_type", invoiceEntry.getUnit());
      parameters.put("price", invoiceEntry.getPrice());
      parameters.put("vat_rate", invoiceEntry.getVatRate());
      parameters.put("net_value", invoiceEntry.getNetValue());
      parameters.put("gross_value", invoiceEntry.getGrossValue());
      return (long) simpleJdbcInsert.executeAndReturnKey(parameters);
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error saving invoice to database.", e);
    }
  }

  public long addAddress(Address address) throws DatabaseOperationException {
    try {
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
          .withTableName(ADDRESSES_TABLE).usingGeneratedKeyColumns("id");
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("street", address.getStreet());
      parameters.put("number", address.getNumber());
      parameters.put("postalCode", address.getPostalCode());
      parameters.put("city", address.getCity());
      parameters.put("country", address.getCountry());
      return (long) simpleJdbcInsert.executeAndReturnKey(parameters);
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error saving invoice to database.", e);
    }
  }

  public long addAccountNumber(AccountNumber accountNumber) throws DatabaseOperationException {
    try {
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
          .withTableName(ACCOUNT_NUMBERS_TABLE).usingGeneratedKeyColumns("id");
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("iban_number", accountNumber.getIbanNumber());
      parameters.put("local_number", accountNumber.getLocalNumber());
      return (long) simpleJdbcInsert.executeAndReturnKey(parameters);
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error saving invoice to database.", e);
    }
  }

  public long addContactDetails(ContactDetails contactDetails, long addressKey) throws DatabaseOperationException {
    try {
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
          .withTableName(CONTACT_DETAILS_TABLE).usingGeneratedKeyColumns("id");
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("email", contactDetails.getEmail());
      parameters.put("phone_number", contactDetails.getPhoneNumber());
      parameters.put("website", contactDetails.getWebsite());
      parameters.put("address", addressKey);
      return (long) simpleJdbcInsert.executeAndReturnKey(parameters);
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error saving invoice to database.", e);
    }
  }

  public long addCompany(Company company, long accountNumber, long contactDetails) throws DatabaseOperationException {
    try {
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
          .withTableName(COMPANIES_TABLE).usingGeneratedKeyColumns("id");
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("name", company.getName());
      parameters.put("tax_id", company.getTaxIdentificationNumber());
      parameters.put("account_number", accountNumber);
      parameters.put("contact_details", contactDetails);
      return (long) simpleJdbcInsert.executeAndReturnKey(parameters);
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error saving invoice to database.", e);
    }
  }

  public long addInvoice(Invoice invoice, long seller, long buyer) throws DatabaseOperationException {
    try {
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
          .withTableName(INVOICES_TABLE).usingGeneratedKeyColumns("id");
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("invoice_type", invoice.getType());
      parameters.put("issue_date", invoice.getIssueDate());
      parameters.put("due_date", invoice.getDueDate());
      parameters.put("seller", seller);
      parameters.put("buyer", buyer);
      parameters.put("total_net_value", invoice.getTotalNetValue());
      parameters.put("total_gross_value", invoice.getTotalGrossValue());
      parameters.put("comments", invoice.getComments());
      return (long) simpleJdbcInsert.executeAndReturnKey(parameters);
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error saving invoice to database.", e);
    }
  }

  public void addInvoiceEntries(long invoiceId, List<InvoiceEntry> entries) throws DatabaseOperationException {
    try {
      jdbcTemplate.batchUpdate("INSERT INTO invoice_entries SET id = ?, entry = ?",
          new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
              ps.setString(1, String.valueOf(invoiceId));
              ps.setString(2, entries.get(i).getId());
            }

            public int getBatchSize() {
              return entries.size();
            }
          });
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error saving invoice to database", e);
    }
  }
}
