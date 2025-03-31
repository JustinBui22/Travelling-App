package com.example.travelingapp.repository;

import com.example.travelingapp.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {

    Optional<Configuration> findByConfigCode(String configCode);
}
