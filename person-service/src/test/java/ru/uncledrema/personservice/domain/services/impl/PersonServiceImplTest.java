package ru.uncledrema.personservice.domain.services.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import ru.uncledrema.personservice.domain.entities.PersonEntity;
import ru.uncledrema.personservice.domain.repositories.PersonRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonServiceImplTest {
    private final PersonRepository repository = mock(PersonRepository.class);
    private final PersonServiceImpl service = new PersonServiceImpl(repository);

    @ParameterizedTest
    @MethodSource("peopleLists")
    void findAll(List<PersonEntity> people) {
        // Arrange
        when(repository.findAll()).thenReturn(people);

        // Act
        var result = service.findAll();

        // Assert
        assertEquals(people, result);
    }

    static Stream<List<PersonEntity>> peopleLists() {
        PersonEntity p1 = new PersonEntity();
        p1.setId(1);
        p1.setName("John Doe");

        PersonEntity p2 = new PersonEntity();
        p2.setId(2);
        p2.setName("Jane Roe");

        return Stream.of(
                List.of(),         // empty case
                List.of(p1, p2)    // non-empty case
        );
    }

    @Test
    void find_found() {
        // Arrange
        var person = new PersonEntity();
        person.setId(1);
        person.setName("John Doe");

        when(repository.findById(1)).thenReturn(Optional.of(person));

        // Act
        var res = service.find(1);

        // Assert
        assertTrue(res.isPresent());
        var resPerson = res.get();
        assertEquals(1, resPerson.getId());
        assertEquals("John Doe", resPerson.getName());

        verify(repository, times(1)).findById(1);
    }

    @Test
    void find_notFound() {
        // Arrange
        when(repository.findById(1)).thenReturn(Optional.empty());

        // Act
        var res = service.find(1);

        // Assert
        assertTrue(res.isEmpty());
        verify(repository, times(1)).findById(1);
    }

    @Test
    void create() {
        // Arrange
        // входные параметры метода
        String name = "New Person";
        Integer age = 30;
        String address = "Some street";
        String work = "Engineer";

        // ожидаемый результат от репозитория (с id присвоенным)
        var saved = new PersonEntity();
        saved.setId(10);
        saved.setName(name);
        saved.setAge(age);
        saved.setAddress(address);
        saved.setWork(work);

        // ловим любой объект, который будет передан в save, и возвращаем saved
        when(repository.save(any(PersonEntity.class))).thenReturn(saved);

        // Act
        var res = service.create(name, age, address, work);

        // Assert
        assertNotNull(res);
        assertEquals(10, res.getId());
        assertEquals(name, res.getName());
        assertEquals(age, res.getAge());
        assertEquals(address, res.getAddress());
        assertEquals(work, res.getWork());

        // проверим, что в репозиторий передали объект с корректными полями
        ArgumentCaptor<PersonEntity> captor = ArgumentCaptor.forClass(PersonEntity.class);
        verify(repository, times(1)).save(captor.capture());
        PersonEntity savedArg = captor.getValue();
        assertEquals(name, savedArg.getName());
        assertEquals(age, savedArg.getAge());
        assertEquals(address, savedArg.getAddress());
        assertEquals(work, savedArg.getWork());
    }

    @Test
    void update_found() {
        // Arrange
        // существующая сущность в репозитории
        var existing = new PersonEntity();
        existing.setId(1);
        existing.setName("Old Name");
        existing.setAge(25);
        existing.setAddress("Old Addr");
        existing.setWork("Old Work");

        // входные параметры обновления
        String newName = "Updated Name";
        Integer newAge = 26;
        String newAddress = "New Addr";
        String newWork = "New Work";

        var saved = new PersonEntity();
        saved.setId(1);
        saved.setName(newName);
        saved.setAge(newAge);
        saved.setAddress(newAddress);
        saved.setWork(newWork);

        when(repository.findById(1)).thenReturn(Optional.of(existing));
        when(repository.save(any(PersonEntity.class))).thenReturn(saved);

        // Act
        var res = service.update(1, newName, newAge, newAddress, newWork);

        // Assert
        assertNotNull(res);
        assertEquals(1, res.getId());
        assertEquals(newName, res.getName());
        assertEquals(newAge, res.getAge());
        assertEquals(newAddress, res.getAddress());
        assertEquals(newWork, res.getWork());

        verify(repository, times(1)).findById(1);

        ArgumentCaptor<PersonEntity> captor = ArgumentCaptor.forClass(PersonEntity.class);
        verify(repository, times(1)).save(captor.capture());
        PersonEntity savedArg = captor.getValue();

        // убедимся, что id сохранился и поля обновлены
        assertEquals(1, savedArg.getId());
        assertEquals(newName, savedArg.getName());
        assertEquals(newAge, savedArg.getAge());
        assertEquals(newAddress, savedArg.getAddress());
        assertEquals(newWork, savedArg.getWork());
    }

    @Test
    void update_notFound() {
        // Arrange
        String newName = "Updated Name";
        Integer newAge = 26;
        String newAddress = "New Addr";
        String newWork = "New Work";

        when(repository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(java.util.NoSuchElementException.class, () -> service.update(1, newName, newAge, newAddress, newWork));

        verify(repository, times(1)).findById(1);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_found() {
        // Arrange
        var person = new PersonEntity();
        person.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(person));
        doNothing().when(repository).delete(argThat((PersonEntity arg) -> arg.getId() == person.getId()));

        // Act
        service.delete(1);

        // Assert
        verify(repository, times(1)).delete(argThat((PersonEntity arg) -> arg.getId() == person.getId()));
    }

    @Test
    void delete_notFound() {
        // Arrange
        when(repository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(java.util.NoSuchElementException.class, () -> service.delete(1));

        verify(repository, never()).delete((PersonEntity) any());
    }
}