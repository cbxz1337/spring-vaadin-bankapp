package org.cbxz.bankapp.models.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ClientsRepository extends CrudRepository<Client, Integer> {

  Optional<Client> findByPassportNumber(String passportNumber);

  Optional<Client> findByFirstName(String firstName);

}
