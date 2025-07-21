package com.example.explorecali.repository;

import com.example.explorecali.model.TourRating;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
@Tag(name = "Tour Rating", description = "Tour Rating API")
public interface TourRatingRepository extends JpaRepository<TourRating, Integer>, CrudRepository<TourRating, Integer> {

    /**
     * Look up all the TourRatings for a tour.
     *
     * @param tourId is the tour Identifier
     * @return a List of any found TourRatings
     */
    List<TourRating> findByTourId(Integer tourId);

    /**
     * Look up a TourRating by the TourId and Customer Id
     *
     * @param tourId
     * @param customerId
     * @return TourRating if found, null otherwise.
     */
    Optional<TourRating> findByTourIdAndCustomerId(Integer tourId, Integer customerId);
}
