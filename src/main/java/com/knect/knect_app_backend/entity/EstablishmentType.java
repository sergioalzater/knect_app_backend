package com.knect.knect_app_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "establishment_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstablishmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "establishment_types_id")
    private Integer establishmentTypeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(length = 50)
    private String icon;

    @Column(columnDefinition = "TEXT")
    private String description;
}
