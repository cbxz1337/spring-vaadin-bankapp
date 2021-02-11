package org.cbxz.bankapp.models.client;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cbxz.bankapp.models.Bank.Bank;
import org.cbxz.bankapp.models.creditOffer.CreditOffer;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode()
@Entity
@Data
@Table(name = "CLIENTS")
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "client_id")
  private Integer id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "patronymic")
  private String patronymic;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "email")
  private String email;

  @Column(name = "passport_number")
  private String passportNumber;

  @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
  private List<CreditOffer> creditOfferSet;

  @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
  private List<Bank> banks;

  public Client() {
  }

  public Client(String firstName, String lastName, String patronymic, String phoneNumber,
      String email, String passportNumber) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.patronymic = patronymic;
    this.phoneNumber = phoneNumber;
    this.passportNumber = passportNumber;
  }

  @Override
  public String toString() {
    return "Имя: " + this.getFirstName() + " " + this.getLastName() + ", паспорт: " + this
        .getPassportNumber();
  }
}
