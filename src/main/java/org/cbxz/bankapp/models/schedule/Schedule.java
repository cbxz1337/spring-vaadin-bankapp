package org.cbxz.bankapp.models.schedule;



import lombok.Data;
import org.cbxz.bankapp.models.creditOffer.CreditOffer;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int Id;

    @OneToOne(mappedBy = "schedule")
    private CreditOffer creditOffer;

    private Date dateOfPayment;

    private double sumOfPayment;

    private double principleLoanAmount;

    private double interestLoanAmount;

    public Schedule(CreditOffer creditOffer, Date dateOfPayment, double sumOfPayment, double principleLoanAmount, double interestLoanAmount) {
        this.creditOffer = creditOffer;
        this.dateOfPayment = dateOfPayment;
        this.sumOfPayment = sumOfPayment;
        this.principleLoanAmount = principleLoanAmount;
        this.interestLoanAmount = interestLoanAmount;
    }

}
