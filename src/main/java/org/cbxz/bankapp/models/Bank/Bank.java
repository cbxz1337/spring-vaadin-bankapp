package org.cbxz.bankapp.models.Bank;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.credit.Credit;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


//@EqualsAndHashCode
//@Data
//@Entity
//@Table(name = "bank")
public class Bank {

    @ManyToOne(fetch = FetchType.EAGER)
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    private Credit credit;

    public Bank(Client client, Credit credit) {
        this.client = client;
        this.credit = credit;
    }

    public Bank() {
    }

}
