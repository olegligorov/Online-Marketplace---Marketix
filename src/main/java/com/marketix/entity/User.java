package com.marketix.entity;

import com.marketix.util.Gender;
import com.marketix.util.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;


    @Size(min = 0, max = 60)
    @NonNull
    @Valid
    private String name;

    @Column(name = "email")
    private String email;
    @NonNull
    @Size(min = 8)
    @Valid
//    private @Pattern(regexp = "(?=.*[a-zA-Z])(?=.*\\W)(?=.*[0-9])") String password;
    private String password;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Role role;

    //#TODO will fix it later
    private String imageUrl;

    @Size(min = 0, max = 520)
    private String caption;

    //@TODO add Cart object
    @OneToOne(mappedBy = "user", orphanRemoval = true)
    @JoinColumn(name = "cart_id", referencedColumnName = "cart_id")
    private Cart shoppingCart;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="user")
    private Set<Invoice> invoices;


    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();

    public User(@NonNull String name, String email, @NonNull String password, @NonNull Gender gender, Role role, String imageUrl, String caption) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.role = role;
        this.imageUrl = imageUrl;
        this.caption = caption;
    }
}
