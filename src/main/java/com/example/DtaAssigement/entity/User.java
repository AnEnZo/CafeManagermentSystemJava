package com.example.DtaAssigement.entity;


import com.example.DtaAssigement.ennum.AuthProvider;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String displayName;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private Integer rewardPoints;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    @Builder.Default
    @JsonManagedReference
    private Set<Roles> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<UserVoucher> userVouchers = new HashSet<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Message> messages;

    @ManyToOne
    @JoinColumn(name = "branch_id",nullable = true)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;   // LOCAL, GOOGLE, FACEBOOK…

    private String providerId;       // sẽ lưu “sub” hoặc “id” của user bên provider

}
