package ru.uncledrema.personservice.domain.services.impl;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.uncledrema.personservice.domain.entities.PersonEntity;
import ru.uncledrema.personservice.domain.repositories.PersonRepository;
import ru.uncledrema.personservice.domain.services.PersonService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository repository;

    @Override
    public List<PersonEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<PersonEntity> find(int id) {
        return repository.findById(id);
    }

    @Override
    public PersonEntity create(String name, @Nullable Integer age, @Nullable String address, @Nullable String work) {
        var entity = new PersonEntity();
        entity.setName(name);
        entity.setAge(age);
        entity.setAddress(address);
        entity.setWork(work);
        return repository.save(entity);
    }

    @Override
    public PersonEntity update(int id, String name, @Nullable Integer age, @Nullable String address, @Nullable String work) {
        var entity = repository.findById(id).orElseThrow(
                () -> new NoSuchElementException("No such person found with id: " + id)
        );
        boolean changed = false;
        if (!entity.getName().equals(name)) {
            entity.setName(name);
            changed = true;
        }
        if (age != null && (entity.getAge() == null || !age.equals(entity.getAge()))) {
            entity.setAge(age);
            changed = true;
        }
        if (address != null && (entity.getAddress() == null || !address.equals(entity.getAddress()))) {
            entity.setAddress(address);
            changed = true;
        }
        if (work != null && (entity.getWork() == null || !work.equals(entity.getWork()))) {
            entity.setWork(work);
            changed = true;
        }

        if (changed) {
            repository.save(entity);
        }
        return entity;
    }

    @Override
    public void delete(int id) {
        var entity = repository.findById(id).orElseThrow(
                () -> new NoSuchElementException("No such person found with id: " + id)
        );
        repository.delete(entity);
    }
}
