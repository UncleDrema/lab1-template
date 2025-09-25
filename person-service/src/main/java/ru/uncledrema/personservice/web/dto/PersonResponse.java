package ru.uncledrema.personservice.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Пользователь")
public record PersonResponse(
        @Schema(description = "Идентификатор", requiredMode = Schema.RequiredMode.REQUIRED)
        int id,
        @Schema(description = "Имя", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Возраст", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer age,
        @Schema(description = "Адрес", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String address,
        @Schema(description = "Место работы", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String work
) { }
