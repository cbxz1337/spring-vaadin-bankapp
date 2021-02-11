package org.cbxz.bankapp.models.schedule;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.cbxz.bankapp.models.creditOffer.CreditOffer;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.Date;

@EqualsAndHashCode
@Entity
@Data
@NoArgsConstructor
@Table(name = "schedules")
public class Schedule {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private int Id;

  private int creditId;

  private int clientId;


  private Date dateOfPayment;

  private double sumOfPayment;

  private double principleLoanAmount;

  private double interestLoanAmount;


  public Schedule(Date dateOfPayment, double sumOfPayment, double principleLoanAmount,
      double interestLoanAmount, int clientId, int creditId) {
    this.creditId = creditId;
    this.clientId = clientId;
    this.dateOfPayment = dateOfPayment;
    this.sumOfPayment = sumOfPayment;
    this.principleLoanAmount = principleLoanAmount;
    this.interestLoanAmount = interestLoanAmount;
  }

  @Override
  public String toString() {
    DecimalFormat df = new DecimalFormat("#.##");
    return "Дата платежа: " + this.dateOfPayment + " " + "Сумма платежа руб.," + df
        .format(this.sumOfPayment) + " Сумма гашения тела кредита.,: " + df
        .format(this.principleLoanAmount) + " Сумма гашения процентов " + df
        .format(this.interestLoanAmount);
  }
}
