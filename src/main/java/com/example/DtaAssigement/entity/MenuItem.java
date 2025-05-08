package com.example.DtaAssigement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Tên món không được để trống")
    @Size(max = 50, message = "Tên món không được vượt quá 50 ký tự")
    private String name;

    @Positive(message = "Giá món phải lớn hơn 0")
    private double price;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Danh mục món ăn không được để trống")
    private Category category;
}
