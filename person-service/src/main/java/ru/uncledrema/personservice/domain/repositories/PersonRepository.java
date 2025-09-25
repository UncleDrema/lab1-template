package ru.uncledrema.personservice.domain.repositories;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;
import ru.uncledrema.personservice.domain.entities.PersonEntity;

@Repository
public interface PersonRepository extends JpaRepositoryImplementation<PersonEntity, Integer> {
}
