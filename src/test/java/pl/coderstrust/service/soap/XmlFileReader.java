package pl.coderstrust.service.soap;

import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class XmlFileReader {

  public String readFromFile(String filePath) throws IOException {
    return FileUtils.readFileToString(new java.io.File(filePath));
  }
}
