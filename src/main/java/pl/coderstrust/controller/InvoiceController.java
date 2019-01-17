package pl.coderstrust.controller;

import java.net.URI;
import java.util.Optional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.InvoiceService;

@RestController
@Api(value = "Invoices", description = "Aviable operations for invoice application", tags = {"Invoices"})
@RequestMapping("/invoices")
public class InvoiceController {

  private final InvoiceService invoiceService;

  @Autowired
  public InvoiceController(@NonNull InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @GetMapping
  @ApiOperation(
      value = "Get all invoices",
      notes = "Get all invoices from database",
      response = Invoice.class,
      responseContainer = "List")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = Invoice.class),
      @ApiResponse(code = 500, message = "Internal server error.", response = ResponseMessage.class)})
  public ResponseEntity<?> getAll() {
    try {
      return new ResponseEntity<>(invoiceService.getAllInvoices(), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(new ResponseMessage("Internal server error while getting invoices."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{invoiceId}")
  @ApiOperation(
      value = "Get invoice by id.",
      notes = "Get invoice from database using it's id.",
      response = Invoice.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = Invoice.class),
      @ApiResponse(code = 404, message = "Invoice not found for passed id.", response = ResponseMessage.class),
      @ApiResponse(code = 500, message = "Internal server error.", response = ResponseMessage.class)})
  public ResponseEntity<?> getById(@ApiParam(value = "ID of Invoice that need to be found.", required = true) @PathVariable("invoiceId") String invoiceId) {
    try {
      Optional<Invoice> optionalInvoice = invoiceService.getInvoice(invoiceId);
      if (optionalInvoice.isPresent()) {
        return new ResponseEntity<>(optionalInvoice.get(), HttpStatus.OK);
      }
      return new ResponseEntity<>(new ResponseMessage("Invoice not found for passed id."), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>(new ResponseMessage(String.format("Internal server error while getting invoice by id: %s", invoiceId)),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping
  @ApiOperation(
      value = "Add invoice.",
      notes = "Add invoice to database.",
      response = Invoice.class)
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Created", response = Invoice.class),
      @ApiResponse(code = 500, message = "Internal server error.", response = ResponseMessage.class)})
  public ResponseEntity<?> add(@ApiParam(value = "Invoice need to be added to database.", required = true) @RequestBody Invoice invoice) {
    try {
      invoiceService.addInvoice(invoice);
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.setLocation(URI.create(String.format("/invoices/%s", invoice.getId())));
      return new ResponseEntity<>(invoice, responseHeaders, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(new ResponseMessage("Internal server error while saving specified invoice."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/{invoiceId}")
  @ApiOperation(
      value = "Update invoice.",
      notes = "Update existing invoice in database.",
      response = Invoice.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = Invoice.class),
      @ApiResponse(code = 400, message = "Passed data is invalid.", response = ResponseMessage.class),
      @ApiResponse(code = 404, message = "Invoice not found.", response = ResponseMessage.class),
      @ApiResponse(code = 500, message = "Internal server error.", response = ResponseMessage.class)})
  public ResponseEntity<?> update(
      @ApiParam(value = "Id of invoice to be updated.", required = true) @PathVariable String invoiceId,
      @ApiParam(value = "Invoice to be updated.", required = true) @RequestBody Invoice invoice) {
    try {
      if (!invoiceId.equals(invoice.getId())) {
        return new ResponseEntity<>(new ResponseMessage("Passed data is invalid. Please verify invoice id."), HttpStatus.BAD_REQUEST);
      }
      if (!invoiceService.invoiceExists(invoiceId)) {
        return new ResponseEntity<>(new ResponseMessage("Invoice not found."), HttpStatus.NOT_FOUND);
      }
      invoiceService.updateInvoice(invoice);
      return new ResponseEntity<>(invoice, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(new ResponseMessage("Internal server error while updating specified invoice."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/{invoiceId}")
  @ApiOperation(
      value = "Delete invoice.",
      notes = "Delete invoice from database.",
      response = Invoice.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK", response = Invoice.class),
      @ApiResponse(code = 404, message = "Invoice not found.", response = ResponseMessage.class),
      @ApiResponse(code = 500, message = "Internal server error.", response = ResponseMessage.class)})
  public ResponseEntity<?> delete(@ApiParam(value = "Id of invoice to be deleted.", required = true) @PathVariable("invoiceId") String invoiceId) {
    try {
      Optional<Invoice> optionalInvoice = invoiceService.getInvoice(invoiceId);
      if (!optionalInvoice.isPresent()) {
        return new ResponseEntity<>(new ResponseMessage("Invoice not found."), HttpStatus.NOT_FOUND);
      }
      invoiceService.deleteInvoice(invoiceId);
      return new ResponseEntity<>(optionalInvoice.get(), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(new ResponseMessage("Internal server error while deleting specified invoice."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
