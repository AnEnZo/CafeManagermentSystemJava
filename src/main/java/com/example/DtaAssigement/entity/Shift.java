package com.example.DtaAssigement.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden=true)
    private Long id;

    @NotBlank @Size(max = 20)
    private String name; // "Ca sáng", "Ca chiều", ...

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @Transient
    @Schema(hidden=true)
    public Duration getDuration() {
        // nếu endTime trước startTime thì có thể coi là qua nửa đêm
        if (endTime.isBefore(startTime)) {
            return Duration.between(startTime, endTime.plusHours(24));
        }
        return Duration.between(startTime, endTime);
    }
}
