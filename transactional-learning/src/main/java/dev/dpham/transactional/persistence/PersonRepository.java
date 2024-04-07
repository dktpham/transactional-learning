package dev.dpham.transactional.persistence;

import dev.dpham.transactional.persistence.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
