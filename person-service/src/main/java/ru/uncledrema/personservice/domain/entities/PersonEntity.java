package ru.uncledrema.personservice.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@Entity
@Table(name="person")
public class PersonEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    String name;

    @Column
    @Nullable
    Integer age;

    @Column
    @Nullable
    String address;

    @Column
    @Nullable
    String work;
}
