package pl.coderstrust.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class InvoiceBookTest {
  private Invoice testInvoice = InvoiceGenerator.getRandomInvoice();
  private String invoiceId = testInvoice.getId();

  @Mock
  private Database testDatabase;

  @InjectMocks
  private InvoiceBook invoiceBook;

  @Test
  void shouldTestGettingAllInvoices() throws DatabaseOperationException, InvoiceBookOperationException {
    //given
    List<Invoice> invoices = InvoiceGenerator.getRandomInvoiceList();
    when(testDatabase.findAllInvoices()).thenReturn(invoices);

    //when
    List<Invoice> actual = invoiceBook.getAllInvoices();

    //then
    assertEquals(invoices, actual);
    verify(testDatabase, times(1)).findAllInvoices();
  }

  @Test
  void ShouldTestGettingAllInvoicesInGivenDateRange() throws
      DatabaseOperationException, InvoiceBookOperationException {

    //given
    LocalDate startDate = LocalDate.of(2018, 10, 24);
    LocalDate endDate = LocalDate.of(2019, 10, 24);
    List<Invoice> invoices = InvoiceGenerator.getInvoiceListWithSpecificDateRange(startDate,
        endDate);
    when(testDatabase.findAllInvoices()).thenReturn(invoices);

    //when
    List<Invoice> actual = invoiceBook.getAllInvoicesInGivenDateRange(startDate, endDate);

    //then
    assertEquals(invoices, actual);
  }

  @Test
  void shouldTestAddingNewInvoice() throws DatabaseOperationException, InvoiceBookOperationException {
    //given

    Invoice expected = testDatabase.findOneInvoice(invoiceId);
    when(testDatabase.saveInvoice(testInvoice)).thenReturn(expected);

    //when
    Invoice actual = invoiceBook.addNewInvoice(testInvoice);

    //then
    assertEquals(expected, actual);
  }

  @Test
  void shouldTestInvoiceDeletion() throws DatabaseOperationException,
      InvoiceBookOperationException {
    //given
    when(testDatabase.invoiceExists(invoiceId)).thenReturn(true);
    doNothing().when(testDatabase).deleteInvoice(invoiceId);

    //when
    invoiceBook.deleteInvoice(invoiceId);

    //then
    verify(testDatabase, times(1)).deleteInvoice(invoiceId);
  }

  @Test
  void shouldTestGettingSingleInvoiceById() throws InvoiceBookOperationException,
      DatabaseOperationException {
    //given
    when(testDatabase.findOneInvoice(invoiceId)).thenReturn(testInvoice);

    //when
    Invoice actual = invoiceBook.getSingleInvoiceById(invoiceId);

    //then
    assertEquals(testInvoice, actual);
  }

  @Test
  void shouldTestInvoiceUpdating() throws DatabaseOperationException, InvoiceBookOperationException {
    //given
    when(testDatabase.saveInvoice(testInvoice)).thenReturn(testInvoice);

    //when
    invoiceBook.updateInvoice(testInvoice);

    //then
    verify(testDatabase, times(1)).saveInvoice(testInvoice);
  }

  @Test
  public void shouldThrowExceptionWhenGettingAllInvoicesWentWrong() throws
      DatabaseOperationException {
    //given
    when(testDatabase.findAllInvoices()).thenThrow(new DatabaseOperationException("Exception while getting all invoices"));

    //then
    assertThrows(InvoiceBookOperationException.class, () -> invoiceBook.getAllInvoices());
    verify(testDatabase, times(1)).findAllInvoices();
  }

  @Test
  public void shouldThrowExceptionWhenMethodFindAllInvoicesInvokedWithNull() {
    assertThrows(IllegalArgumentException.class, () -> invoiceBook.getSingleInvoiceById(null));
  }

  @Test
  public void shouldThrowExceptionWhenGettingSingleInvoiceByIdWentWrong() throws
      DatabaseOperationException {
    //given
    when(testDatabase.findOneInvoice(invoiceId)).thenThrow(new DatabaseOperationException("Exception while getting single invoice by id"));

    //then
    assertThrows(InvoiceBookOperationException.class, () -> invoiceBook.getSingleInvoiceById(invoiceId));
    verify(testDatabase, times(1)).findOneInvoice(invoiceId);
  }

  @Test
  public void shouldThrowExceptionWhenMethodAddNewInvoiceInvokedWithNull() {
    assertThrows(IllegalArgumentException.class, () -> invoiceBook.addNewInvoice(null));
  }

  @Test
  public void shouldThrowExceptionWhenAddingNewInvoiceWentWrong() throws
      DatabaseOperationException {
    //given
    when(testDatabase.saveInvoice(testInvoice)).thenThrow(new DatabaseOperationException
        ("Exception while adding new invoice"));

    //then
    assertThrows(InvoiceBookOperationException.class, () -> invoiceBook.addNewInvoice(testInvoice));
    verify(testDatabase, times(1)).saveInvoice(testInvoice);
  }

  @Test
  public void shouldThrowExceptionWhenMethodUpdateInvoiceInvokedWithNull() {
    assertThrows(IllegalArgumentException.class, () -> invoiceBook.updateInvoice(null));
  }

  @Test
  public void shouldThrowExceptionWhenUpdatingInvoiceWentWrong() throws
      DatabaseOperationException {
    //given
    when(testDatabase.saveInvoice(testInvoice)).thenThrow(new DatabaseOperationException
        ("Exception while adding new invoice"));

    //then
    assertThrows(InvoiceBookOperationException.class, () -> invoiceBook.updateInvoice(testInvoice));
    verify(testDatabase, times(1)).saveInvoice(testInvoice);
  }

  @Test
  public void shouldThrowExceptionWhenMethodDeleteInvoiceInvokedWithNull() {
    assertThrows(IllegalArgumentException.class, () -> invoiceBook.deleteInvoice(null));
  }

  @Test
  public void shouldThrowExceptionWhenDataBaseIsNull() {
    //given
    testDatabase = null;

    //then
    assertThrows(InvoiceBookOperationException.class, () ->
        invoiceBook.deleteInvoice(invoiceId));
  }

  @Test
  public void shouldThrowExceptionWhenInvoiceWithGivenIdDoesNotExist() {
    String dupa = "DUPA";
  }

  @Test
  public void shouldThrowExceptionWhenMethodGetAllInvoicesInGivenDateRangeInvokedWithNull() {
    //given
    LocalDate startDate = testInvoice.getIssueDate();
    LocalDate endDate = testInvoice.getIssueDate();
    assertThrows(IllegalArgumentException.class, () -> invoiceBook.getAllInvoicesInGivenDateRange
        (null, endDate));
    assertThrows(IllegalArgumentException.class, () -> invoiceBook.getAllInvoicesInGivenDateRange
        (startDate, null));
  }

  @Test
  public void shouldThrowExceptionWhenGettingAllInvoicesInGivenDateRangeWentWrong() throws
      DatabaseOperationException {
    //given
    LocalDate startDate = testInvoice.getIssueDate();
    LocalDate endDate = testInvoice.getIssueDate();
    when(testDatabase.findAllInvoices()).thenThrow(new DatabaseOperationException
        ("Exception while getting all invoices"));

    //then
    assertThrows(InvoiceBookOperationException.class, () -> invoiceBook
        .getAllInvoicesInGivenDateRange(startDate, endDate));
    verify(testDatabase, times(1)).findAllInvoices();
  }
}
