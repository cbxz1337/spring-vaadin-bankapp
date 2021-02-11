package org.cbxz.bankapp.models.credit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditRepository extends CrudRepository<Credit, Integer> {

  Optional<Credit> findByLimitAndPercent(long limit, double percent);

  Iterable<Credit> findByLimit(long limit);
}
