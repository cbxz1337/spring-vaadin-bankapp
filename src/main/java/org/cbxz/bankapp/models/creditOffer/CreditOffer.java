package org.cbxz.bankapp.models.creditOffer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.credit.Credit;

import javax.persistence.*;
import java.io.Serializable;


@EqualsAndHashCode
@Entity
@Data
@NoArgsConstructor
@IdClass(CreditOfferPk.class)
@Table(name = "credit_offer")
public class CreditOffer implements Serializable {

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


  public CreditOffer(Client client, Credit credit, double sum) {
    this.client = client;
    this.credit = credit;
    this.sum = sum;
  }

}
