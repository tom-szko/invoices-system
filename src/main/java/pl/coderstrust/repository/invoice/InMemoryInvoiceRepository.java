package pl.coderstrust.repository.invoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.repository.RepositoryOperationException;

@NoArgsConstructor
@ConditionalOnProperty(name = "pl.coderstrust.repository", havingValue = "in-memory")
@Repository
public class InMemoryInvoiceRepository implements InvoiceRepository {

  private List<Invoice> invoices = Collections.synchronizedList(new ArrayList<>());

  @Override
  public boolean existsById(@NonNull String id) {
    return invoices
        .stream()
        .anyMatch(i -> i.getId().equals(id));
  }

  @Override
  public Invoice save(@NonNull Invoice invoice) throws RepositoryOperationException {
    deleteById(invoice.getId());
    invoices.add(invoice);
    return invoice;
  }

  @Override
  public void deleteById(@NonNull String id) throws RepositoryOperationException{
    if (invoices.removeIf(i -> i.getId().equals(id))) {
      return;
    } else {
      throw new RepositoryOperationException(String.format("There was no invoice in database with id %s", id));
    }
  }

  @Override
  public void deleteAll() {
    invoices.clear();
  }

  @Override
  public long count() {
    return (long) invoices.size();
  }

  @Override
  public Optional<Invoice> findById(@NonNull String id) {
    return invoices
        .stream()
        .filter(invoice -> invoice.getId().equals(id))
        .findFirst();
  }

  @Override
  public Iterable<Invoice> findAll() {
    return invoices;
  }

  @Override
  public Iterable<Invoice> findAllBySellerName(@NonNull String sellerName) {
    return invoices
        .stream()
        .filter(invoice -> invoice.getSeller().getName().equals(sellerName))
        .collect(Collectors.toList());
  }

  @Override
  public Iterable<Invoice> findAllByBuyerName(@NonNull String buyerName) {
    return invoices
        .stream()
        .filter(invoice -> invoice.getBuyer().getName().equals(buyerName))
        .collect(Collectors.toList());
  }
}
