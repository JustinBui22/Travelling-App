package com.example.travelingapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Configuration")
@Getter
@Setter
public class ConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private int configId;

    @Column(name = "config_code", nullable = false, unique = true)
    private String configCode;

    @Column(name = "config_value", nullable = false)
    private String configValue;

    @Column(name = "config_message", nullable = false)
    private String configMessage;

    @Column(name = "created_date", unique = true)
    private LocalDate createdDate;

    @Column(name = "modified_date")
    private LocalDate modifiedDate;

    @Column(name = "config_type")
    private String configType;


    public ConfigurationEntity(String configCode, String configValue) {
        this.configCode = configCode;
        this.configValue = configValue;
    }

    public ConfigurationEntity() {

    }
}
