package com.pailsom.batchprocessing.repository;

import com.pailsom.batchprocessing.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person,Long> {
}
