package com.example.explorecali.repository;

import com.example.explorecali.model.Difficulty;
import com.example.explorecali.model.Tour;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Tag(name = "Tour", description = "Tour API")
public interface TourRepository extends JpaRepository<Tour, Integer> {
    List<Tour> findByDifficulty(Difficulty diff);
    List<Tour> findByTourPackageCode(String code);
}
