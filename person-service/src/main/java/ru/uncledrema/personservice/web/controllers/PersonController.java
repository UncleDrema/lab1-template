package ru.uncledrema.personservice.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.uncledrema.personservice.domain.services.PersonService;
import ru.uncledrema.personservice.web.dto.PersonRequest;
import ru.uncledrema.personservice.web.dto.PersonResponse;
import ru.uncledrema.personservice.web.mapper.WebMapper;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/persons/")
@Validated
@Tag(name = "Пользователи")
class PersonController {
    private final PersonService service;
    private final WebMapper mapper;

    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public ResponseEntity<List<PersonResponse>> getAll() {
        var allPersons = service.findAll();
        var response = allPersons.stream().map(mapper::map).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить пользователя")
    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> get(@PathVariable int id) {
        var person = service.find(id);
        var response = person.map(mapper::map);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @Operation(summary = "Добавить пользователя")
    @PostMapping
    public ResponseEntity<PersonResponse> create(@RequestBody PersonRequest personRequest) {
        var createdPerson = service.create(
                personRequest.name(),
                personRequest.age(),
                personRequest.address(),
                personRequest.work()
        );

        var response = mapper.map(createdPerson);
        return ResponseEntity.created(URI.create("/api/v1/persons/" + createdPerson.getId())).body(response);
    }

    @Operation(summary = "Изменить пользователя")
    @PatchMapping("/{id}")
    public ResponseEntity<PersonResponse> update(@PathVariable int id, @RequestBody PersonRequest personRequest) {
        var updatedPerson = service.update(
                id,
                personRequest.name(),
                personRequest.age(),
                personRequest.address(),
                personRequest.work());
        var response = mapper.map(updatedPerson);
        return ResponseEntity.of(Optional.of(response));
    }

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
