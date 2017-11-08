package com.carsaver.banana.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BananaRepository extends CrudRepository<Banana, Long> {
}
