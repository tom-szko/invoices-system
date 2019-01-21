package pl.coderstrust.service.soap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;
import static org.springframework.ws.test.server.ResponseMatchers.validPayload;

import java.io.IOException;
import javax.xml.transform.Source;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;
import pl.coderstrust.service.InvoiceService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InvoiceEndpointTest {

  @Autowired
  private WebApplicationContext applicationContext;

  private MockWebServiceClient mockClient;
  private Resource xsdSchema = new ClassPathResource("invoices.xsd");

  @Autowired
  private InvoiceService invoiceService;

  @Before
  void setUp() {
    mockClient = MockWebServiceClient.createClient(applicationContext);
  }

  @Test
  void contextLoads() {
    assertNotNull(applicationContext);
    assertNotNull(invoiceService);
  }

  @Test
  void shouldAddInvoice() throws IOException {
    //given
    String requestFilePath = "src/test/resources/soaprequests/addInvoiceRequest.xml";
    String responseFilePath = "src/test/resources/soaprequests/addInvoiceResponse.xml";
    XmlFileReader xmlFileReader = new XmlFileReader();
    String invoiceStringRequest = xmlFileReader.readFromFile(requestFilePath);
    String invoiceStringResponse = xmlFileReader.readFromFile(responseFilePath);

    Source requestPayload = new StringSource(invoiceStringRequest);
    Source responsePayload = new StringSource(invoiceStringResponse);

    //when
    mockClient
        .sendRequest(withPayload(requestPayload))

        //then
        .andExpect(noFault())
        .andExpect(validPayload(xsdSchema))
        .andExpect(payload(responsePayload));
  }

  @Test
  void shouldGetInvoice() throws IOException {
    //given
    String invoiceFilePath = "src/test/resources/soaprequests/addInvoiceRequest.xml";
    String requestFilePath = "src/test/resources/soaprequests/getInvoiceRequest.xml";
    String responseFilePath = "src/test/resources/soaprequests/getInvoiceResponse.xml";
    XmlFileReader xmlFileReader = new XmlFileReader();
    String addInvoiceStringRequest = xmlFileReader.readFromFile(invoiceFilePath);
    String invoiceStringRequest = xmlFileReader.readFromFile(requestFilePath);
    String invoiceStringResponse = xmlFileReader.readFromFile(responseFilePath);

    Source invoicePayload = new StringSource(addInvoiceStringRequest);
    Source requestPayload = new StringSource(invoiceStringRequest);
    Source responsePayload = new StringSource(invoiceStringResponse);

    mockClient
        .sendRequest(withPayload(invoicePayload))
        .andExpect(noFault());

    //when
    mockClient
        .sendRequest(withPayload(requestPayload))

        //then
        .andExpect(noFault())
        .andExpect(validPayload(xsdSchema))
        .andExpect(payload(responsePayload));
  }

  @Test
  void shouldUpdateInvoice() throws IOException {
    //given
    String invoiceFilePath = "src/test/resources/soaprequests/addInvoiceRequest.xml";
    String requestFilePath = "src/test/resources/soaprequests/updateInvoiceRequest.xml";
    String responseFilePath = "src/test/resources/soaprequests/updateInvoiceResponse.xml";
    XmlFileReader xmlFileReader = new XmlFileReader();
    String addInvoiceStringRequest = xmlFileReader.readFromFile(invoiceFilePath);
    String invoiceStringRequest = xmlFileReader.readFromFile(requestFilePath);
    String invoiceStringResponse = xmlFileReader.readFromFile(responseFilePath);

    Source invoicePayload = new StringSource(addInvoiceStringRequest);
    Source requestPayload = new StringSource(invoiceStringRequest);
    Source responsePayload = new StringSource(invoiceStringResponse);

    mockClient
        .sendRequest(withPayload(invoicePayload))
        .andExpect(noFault());

    //when
    mockClient
        .sendRequest(withPayload(requestPayload))

        //then
        .andExpect(noFault())
        .andExpect(validPayload(xsdSchema))
        .andExpect(payload(responsePayload));
  }

  @Test
  void shouldRemoveInvoice() throws IOException {
    //given
    String invoiceFilePath = "src/test/resources/soaprequests/addInvoiceRequest.xml";
    String requestFilePath = "src/test/resources/soaprequests/removeInvoiceRequest.xml";
    String responseFilePath = "src/test/resources/soaprequests/removeInvoiceResponse.xml";
    XmlFileReader xmlFileReader = new XmlFileReader();
    String addInvoiceStringRequest = xmlFileReader.readFromFile(invoiceFilePath);
    String invoiceStringRequest = xmlFileReader.readFromFile(requestFilePath);
    String invoiceStringResponse = xmlFileReader.readFromFile(responseFilePath);

    Source invoicePayload = new StringSource(addInvoiceStringRequest);
    Source requestPayload = new StringSource(invoiceStringRequest);
    Source responsePayload = new StringSource(invoiceStringResponse);

    mockClient
        .sendRequest(withPayload(invoicePayload))
        .andExpect(noFault());

    //when
    mockClient
        .sendRequest(withPayload(requestPayload))

        //then
        .andExpect(noFault())
        .andExpect(validPayload(xsdSchema))
        .andExpect(payload(responsePayload));
  }

  @Test
  void shouldGetAllInvoices() throws IOException {
    //given
    String invoiceFilePath = "src/test/resources/soaprequests/addInvoiceRequest.xml";
    String requestFilePath = "src/test/resources/soaprequests/getAllInvoicesRequest.xml";
    String responseFilePath = "src/test/resources/soaprequests/getAllInvoicesResponse.xml";
    XmlFileReader xmlFileReader = new XmlFileReader();
    String addInvoiceStringRequest = xmlFileReader.readFromFile(invoiceFilePath);
    String invoiceStringRequest = xmlFileReader.readFromFile(requestFilePath);
    String invoiceStringResponse = xmlFileReader.readFromFile(responseFilePath);

    Source invoicePayload = new StringSource(addInvoiceStringRequest);
    Source requestPayload = new StringSource(invoiceStringRequest);
    Source responsePayload = new StringSource(invoiceStringResponse);

    mockClient
        .sendRequest(withPayload(invoicePayload))
        .andExpect(noFault());

    //when
    mockClient
        .sendRequest(withPayload(requestPayload))

        //then
        .andExpect(noFault())
        .andExpect(validPayload(xsdSchema))
        .andExpect(payload(responsePayload));
  }
}
