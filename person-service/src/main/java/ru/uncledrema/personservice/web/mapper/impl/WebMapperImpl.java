package ru.uncledrema.personservice.web.mapper.impl;

import org.springframework.stereotype.Service;
import ru.uncledrema.personservice.domain.entities.PersonEntity;
import ru.uncledrema.personservice.web.dto.PersonResponse;
import ru.uncledrema.personservice.web.mapper.WebMapper;

@Service
public class WebMapperImpl implements WebMapper {
    @Override
    public PersonResponse map(PersonEntity entity) {
        return new PersonResponse(
                entity.getId(),
                entity.getName(),
                entity.getAge(),
                entity.getAddress(),
                entity.getWork()
        );
    }
}
