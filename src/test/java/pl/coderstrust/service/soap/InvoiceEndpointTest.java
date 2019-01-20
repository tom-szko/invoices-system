package pl.coderstrust.service.soap;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;
import static org.springframework.ws.test.server.ResponseMatchers.validPayload;

import java.io.IOException;
import javax.xml.transform.Source;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
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
  public void init() {
    mockClient = MockWebServiceClient.createClient(applicationContext);
  }

  @Test
  public void shouldAddInvoice() throws IOException {
    //given
    String requestFilePath = "src/test/resources/soaprequests/addInvoiceRequest.xml";
    String responseFilePath = "src/test/resources/soaprequests/addInvoiceRequest.xml";
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
        .andExpect(payload(responsePayload))
        .andExpect(validPayload(xsdSchema));
  }
}
