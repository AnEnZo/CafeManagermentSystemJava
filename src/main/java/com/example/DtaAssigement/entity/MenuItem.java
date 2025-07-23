package com.example.DtaAssigement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;


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
    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @NotBlank(message = "Ảnh món ăn không được để trống")
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Danh mục món ăn không được để trống")
    private Category category;

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<OrderItem> orderItems;

}
