package com.example.explorecali.controller;

import com.example.explorecali.dto.RatingDto;
import com.example.explorecali.model.TourRating;
import com.example.explorecali.service.TourRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@Tag(name = "Tour Ratings", description = "The rating for a tour API")
@RequestMapping(path = "/tours/{tourId}/ratings")
public class TourRatingController {
    private TourRatingService tourRatingService;

    public TourRatingController(TourRatingService tourRatingService) {
        this.tourRatingService = tourRatingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Tour Rating")
    public RatingDto createTourRating(@PathVariable(value = "tourId") int tourId,
                                      @RequestBody @Valid RatingDto ratingDto) {
        log.info("POST /tours/{}/ratings ", tourId);
        TourRating rating = tourRatingService.createNew(tourId, ratingDto.getCustomerId(),
                ratingDto.getScore(), ratingDto.getComment());
        return new RatingDto(rating);
    }

    @GetMapping
    @Operation(summary = "Get all Tour Ratings for a Tour")
    public List<RatingDto> getAllRatingsForTour(@PathVariable(value = "tourId") int tourId) {
        log.info("GET /tours/{}/ratings", tourId);
        List<TourRating> tourRatings = tourRatingService.lookupRatings(tourId);
        return tourRatings.stream().map(RatingDto::new).toList();
    }

    @GetMapping("/average")
    @Operation(summary = "Get the average score for a Tour")
    public Map<String, Double> getAverage(@PathVariable(value = "tourId") int tourId) {
        log.info("GET /tours/{}/ratings/average", tourId);
        return Map.of("average", tourRatingService.getAverageScore(tourId));
    }


    @PutMapping
    @Operation(summary = "Update a Tour Rating")
    public RatingDto updateWithPut(@PathVariable(value = "tourId") int tourId, @RequestBody @Valid RatingDto ratingDto) {
        log.info("PUT /tours/{}/ratings", tourId);
        return new RatingDto(tourRatingService.update(tourId, ratingDto.getCustomerId(),
                ratingDto.getScore(), ratingDto.getComment()));
    }

    @Operation(summary = "Update a Tour Rating")
    @PatchMapping
    public RatingDto updateWithPatch(@PathVariable(value = "tourId") int tourId,
                                     @RequestBody @Valid RatingDto ratingDto) {
        log.info("PATCH /tours/{}/ratings", tourId);
        return new RatingDto(tourRatingService.updateSome(tourId,
                ratingDto.getCustomerId(),
                Optional.ofNullable(ratingDto.getScore()),
                Optional.ofNullable(ratingDto.getComment())));
    }

    @Operation(summary = "Delete a Tour Rating")
    @DeleteMapping("/{customerId}")
    public void delete(@PathVariable(value = "tourId") int tourId, @PathVariable(value = "customerId") int customerId) {
        log.info("DELETE /tours/{}/ratings/{}", tourId, customerId);
        tourRatingService.delete(tourId, customerId);
    }

  @Operation(summary = "Get a Tour Rating")
    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void createManyTourRatings(@PathVariable(value = "tourId") int tourId,
                                      @RequestParam(value = "score") int score,
                                      @RequestBody List<Integer> customers) {
        log.info("POST /tours/{}/ratings/batch", tourId);
        tourRatingService.rateMany(tourId, score, customers);
    }
}