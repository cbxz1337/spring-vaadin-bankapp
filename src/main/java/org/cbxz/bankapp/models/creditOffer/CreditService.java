package org.cbxz.bankapp.models.creditOffer;


import org.cbxz.bankapp.models.credit.Credit;
import org.cbxz.bankapp.models.credit.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreditService {


  private CreditRepository creditRepository;

  @Autowired
  public CreditService(CreditRepository creditRepository) {
    this.creditRepository = creditRepository;
  }

  public Optional<Credit> findById(int id) {
    return creditRepository.findById(id);
  }

  public Iterable<Credit> findByLimit(long limit) {
    return creditRepository.findByLimit(limit);
  }

  public Iterable<Credit> findAll() {
    return creditRepository.findAll();
  }

}
