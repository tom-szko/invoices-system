package pl.coderstrust;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.coderstrust.service.InvoiceService;

@SpringBootApplication
public class Application implements CommandLineRunner {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Autowired
  InvoiceService invoiceService;

  @Override
  public void run(String... args) throws Exception {
    System.out.println(invoiceService.getInvoice("1"));
    System.out.println(invoiceService.getAllInvoices());
    System.out.println(invoiceService.invoiceExists("2"));
    System.out.println(invoiceService.invoiceExists("1"));
    System.out.println(invoiceService.invoiceExists("5"));
  }
}
