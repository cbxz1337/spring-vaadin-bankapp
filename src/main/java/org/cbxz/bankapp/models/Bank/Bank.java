package org.cbxz.bankapp.models.Bank;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.credit.Credit;
import org.cbxz.bankapp.models.creditOffer.CreditOfferPk;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@EqualsAndHashCode
@NoArgsConstructor
@Data
@Entity
@IdClass(BankPK.class)
@Table(name = "BANK")
public class Bank {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "credit_id", insertable = false, updatable = false)
    private Credit credit;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;

    public Bank(Credit credit, Client client) {
        this.credit = credit;
        this.client = client;
    }
}
