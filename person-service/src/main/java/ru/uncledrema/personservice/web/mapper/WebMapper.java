package ru.uncledrema.personservice.web.mapper;

import ru.uncledrema.personservice.domain.entities.PersonEntity;
import ru.uncledrema.personservice.web.dto.PersonResponse;

public interface WebMapper {
    PersonResponse map(PersonEntity entity);
}
