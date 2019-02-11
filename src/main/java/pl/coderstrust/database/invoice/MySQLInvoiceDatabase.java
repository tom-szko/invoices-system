package pl.coderstrust.database.invoice;

import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Invoice;

@Slf4j
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "mysql")
@Repository
public class MySQLInvoiceDatabase implements InvoiceDatabase {

  static final String INVOICES_VIEW = "SELECT * FROM invoices_db.invoices_view";
  static final String GET_INVOICE_BY_ID_QUERY = INVOICES_VIEW + " WHERE invoice_id = ";
  static final String GET_INVOICES_BY_SELLER = INVOICES_VIEW + " WHERE seller_company_name = ";
  static final String GET_INVOICES_BY_BUYER = INVOICES_VIEW + " WHERE buyer_company_name = ";
  static final String GET_INVOICES_QUERY = "SELECT * FROM invoices_db.invoices";

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
      return Optional.of(jdbcTemplate.query(GET_INVOICE_BY_ID_QUERY + id, new InvoiceExtractor()));
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("Error retrieving invoice with id: " + id, e);
    }
  }

  @Override
  public boolean existsById(String var1) throws DatabaseOperationException {
    return false;
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
}
