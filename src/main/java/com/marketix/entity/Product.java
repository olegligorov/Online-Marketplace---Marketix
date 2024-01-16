package com.marketix.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @NonNull
    @NotBlank
    private String name;
    private String description;
    @NonNull
    @Min(0)
    private double price;
    @Min(0)
    private double rating;

    @NonNull
    private String seller;

    private String imageUrl;

    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();

    public Product(@NonNull String name, String description, @NonNull double price, double rating) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.rating = rating;
    }

    public Product(@NonNull String name, String description, String seller, @NonNull double price, double rating) {
        this.name = name;
        this.description = description;
        this.seller = seller;
        this.price = price;
        this.rating = rating;
    }
}
