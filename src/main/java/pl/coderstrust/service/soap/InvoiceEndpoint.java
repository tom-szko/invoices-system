package pl.coderstrust.service.soap;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.InvoiceService;
import pl.coderstrust.service.InvoiceServiceOperationException;
import pl.coderstrust.soap.domainclasses.AddInvoiceRequest;
import pl.coderstrust.soap.domainclasses.AddInvoiceResponse;
import pl.coderstrust.soap.domainclasses.GetInvoiceRequest;
import pl.coderstrust.soap.domainclasses.GetInvoiceResponse;

@Endpoint
public class InvoiceEndpoint {

  private static final String NAMESPACE_URI = "http://www.coderstrust.pl/project-7-arek-jagoda-slawek-tomek";

  private InvoiceService invoiceService;

  @Autowired
  public InvoiceEndpoint(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoiceRequest")
  @ResponsePayload
  public GetInvoiceResponse getInvoice(@RequestPayload GetInvoiceRequest request) throws InvoiceServiceOperationException {
    GetInvoiceResponse response = new GetInvoiceResponse();
    Optional<Invoice> invoice = invoiceService.getInvoice(request.getId());
    if (!invoice.isPresent()) {
      response.setInvoice(null);
      response.setStatusMessage("Could not find invoice: " + request.getId());
    } else {
      response.setInvoice(InvoiceToXmlConverter.convertInvoiceToXmlInvoice(invoice.get()));
      response.setStatusMessage("OK");
    }
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addInvoiceRequest")
  @ResponsePayload
  public AddInvoiceResponse addInvoice(@RequestPayload AddInvoiceRequest request) throws InvoiceServiceOperationException {
    AddInvoiceResponse response = new AddInvoiceResponse();
    Invoice invoice = invoiceService.addInvoice(XmlToInvoiceConverter.convertXmlInvoiceToInvoice(request.getInvoice()));
    response.setInvoice(InvoiceToXmlConverter.convertInvoiceToXmlInvoice(invoice));
    response.setStatusMessage("Added invoice: " + invoice);
    return response;
  }
}
