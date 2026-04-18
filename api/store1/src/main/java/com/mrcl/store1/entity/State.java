package com.mrcl.store1.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="state")
@Data
public class State {

    /*
    @Data is a Lombok annotation that automatically generates
    boilerplate code for Java classes, significantly reducing verbosity.
    It combines the functionality of @Getter, @Setter, @ToString, @EqualsAndHashCode, and @RequiredArgsConstructor into a single annotation.
    Generates:
    Getters for all fields.
    Setters for all non-final fields.
    A toString() method that includes all non-transient fields.
    equals() and hashCode() methods that compare all non-transient fields.
    A constructor that initializes all final fields and non-final fields
    without initializers (if marked with @NonNull).
     */

    @Id
    @Column(name = "id")
    private Short id; // ✅ matches SMALLINT UNSIGNED in DB

    @Column(name="name")
    private String name;

    // many states belong to one country
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;


}
