package org.cbxz.bankapp.models.creditOffer;

import java.io.Serializable;
import java.util.Objects;

public class CreditOfferPk implements Serializable {


  private int client;

  private int credit;


  public CreditOfferPk() {
  }

  public CreditOfferPk(int client, int credit) {
    this.client = client;
    this.credit = credit;
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (o == null || getClass() != o.getClass()) {
          return false;
      }
    CreditOfferPk that = (CreditOfferPk) o;
    return Objects.equals(client, that.client) &&
        Objects.equals(credit, that.credit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(client, credit);
  }
}
