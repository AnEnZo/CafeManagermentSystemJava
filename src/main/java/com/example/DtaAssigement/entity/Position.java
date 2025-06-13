package com.example.DtaAssigement.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden=true)
    private Long id;

    @NotBlank @Size(max = 50)
    private String name; // e.g. "Barista", "Shift Manager"
}
