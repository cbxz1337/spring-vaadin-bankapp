package org.cbxz.bankapp.models.creditOffer;

import lombok.Data;
import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.credit.Credit;
import org.cbxz.bankapp.models.schedule.Schedule;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@IdClass(CreditOfferPk.class)
@Table(name = "credit_offer")
public class CreditOffer implements Serializable{

    @Id
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;

    @Id
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "credit_id")
    private Credit credit;

    @Column(name = "credit_sum")
    private double sum;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    private Schedule schedule;

}
