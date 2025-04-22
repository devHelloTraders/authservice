package com.traders.auth.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name="wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    private String createdAt;
    private Double balance;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @JsonBackReference
    private User user;
}
