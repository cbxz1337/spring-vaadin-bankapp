package org.cbxz.bankapp.models.credit;




import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cbxz.bankapp.models.Bank.Bank;
import org.cbxz.bankapp.models.creditOffer.CreditOffer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@EqualsAndHashCode
@Entity
@Data
@Table(name = "CREDITS")
public class Credit implements Comparable<Credit>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "credit_id")
    private Integer id;

    private long limit;

    private double percent;

    @OneToMany(mappedBy = "credit", fetch = FetchType.EAGER)
    private List<CreditOffer> creditOffers;

    @OneToMany(mappedBy = "credit", fetch = FetchType.EAGER)
    private List<Bank> banks;

    public Credit() {
    }

    public Credit(long limit, double percent) {
        this.limit = limit;
        this.percent = percent;
    }

    @Override
    public int compareTo(Credit o) {
        return Long.compare(o.getLimit(), this.getLimit());
    }
}
