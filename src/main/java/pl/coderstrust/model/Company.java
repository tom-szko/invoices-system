package pl.coderstrust.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  String id;

  String name;

  String taxIdentificationNumber;

  @OneToOne(cascade = CascadeType.ALL)
  AccountNumber accountNumber;

  @OneToOne(cascade = CascadeType.ALL)
  ContactDetails contactDetails;

  public Company(String name, String taxIdentificationNumber,
                 AccountNumber accountNumber, ContactDetails contactDetails) {
    this.name = name;
    this.taxIdentificationNumber = taxIdentificationNumber;
    this.accountNumber = accountNumber;
    this.contactDetails = contactDetails;
  }
}
