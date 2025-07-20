package com.example.explorecali.repository;

import com.example.explorecali.model.TourPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@Tag(name = "Tour Package", description = "Tour Package API")
@RepositoryRestResource(path = "packages",collectionResourceRel ="packages" )
public interface TourPackageRepository extends JpaRepository<TourPackage, String> {

    @io.swagger.v3.oas.annotations.Operation(summary = "Find tour package by name")
    Optional<TourPackage> findByName(String name);
}