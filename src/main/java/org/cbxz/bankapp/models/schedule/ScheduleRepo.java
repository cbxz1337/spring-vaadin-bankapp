package org.cbxz.bankapp.models.schedule;

import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.creditOffer.CreditOffer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepo extends CrudRepository<Schedule, Integer> {

    Iterable<Schedule> findByClientIdAndCreditId(int clientId, int creditId);
}
