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
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Credit credit;

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Client client;

    public Bank(@NotNull Credit credit, @NotNull Client client) {
        this.credit = credit;
        this.client = client;
    }
}
