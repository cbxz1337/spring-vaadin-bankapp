package org.cbxz.bankapp.models.Bank;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BankRepository extends CrudRepository<Bank, Integer> {

}
