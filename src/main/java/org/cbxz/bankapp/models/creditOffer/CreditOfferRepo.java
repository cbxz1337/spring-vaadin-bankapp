package org.cbxz.bankapp.models.creditOffer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditOfferRepo extends CrudRepository<CreditOffer, Integer> {
}
