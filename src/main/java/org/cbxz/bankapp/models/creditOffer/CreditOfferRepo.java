package org.cbxz.bankapp.models.creditOffer;

import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.credit.Credit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditOfferRepo extends CrudRepository<CreditOffer, Integer> {

  CreditOffer findByCreditAndClient(Credit credit, Client client);
}
