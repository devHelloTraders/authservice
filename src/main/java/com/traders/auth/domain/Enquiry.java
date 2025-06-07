package com.traders.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="enquiry")
@Getter
@Setter
public class Enquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String name;
    private String contactNo;
    private String message;
    private String createdDatetime;
    private String ipAddress;
}
