package ru.uncledrema.personservice.domain.services;

import org.jspecify.annotations.Nullable;
import ru.uncledrema.personservice.domain.entities.PersonEntity;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    List<PersonEntity> findAll();
    Optional<PersonEntity> find(int id);
    PersonEntity create(String name, @Nullable Integer age, @Nullable String address, @Nullable String work);
    PersonEntity update(int id, String name, @Nullable Integer age, @Nullable String address, @Nullable String work);
    void delete(int id);
}
