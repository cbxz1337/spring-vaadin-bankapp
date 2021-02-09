package org.cbxz.bankapp.models.credit;




import lombok.Data;
import org.cbxz.bankapp.models.creditOffer.CreditOffer;

import javax.persistence.*;
import java.util.List;


@Entity
@Data
@Table(name = "CREDITS")
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "credit_id")
    private Integer id;

    private long limit;

    private double percent;

    @OneToMany(mappedBy = "credit", fetch = FetchType.EAGER)
    private List<CreditOffer> creditOffers;

//    @OneToMany(mappedBy = "credit", fetch = FetchType.EAGER)
//    private List<Bank> bank;

    public Credit() {
    }

    public Credit(long limit, double percent) {
        this.limit = limit;
        this.percent = percent;
    }
}
