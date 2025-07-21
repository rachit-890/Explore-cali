package com.example.explorecali.controller;

import com.example.explorecali.dto.RatingDto;
import com.example.explorecali.model.TourRating;
import com.example.explorecali.service.TourRatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping(path = "/tours/{tourId}/ratings")
public class TourRatingController {
    private TourRatingService tourRatingService;

    public TourRatingController(TourRatingService tourRatingService) {
        this.tourRatingService = tourRatingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTourRating(@PathVariable(value= "tourId") int tourId,
                                 @RequestBody @Valid RatingDto ratingDto)
    {
        tourRatingService.createNew(tourId,ratingDto.getCustomerId(),ratingDto.getScore(),ratingDto.getComment());

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RatingDto> getAllRatingsForTour(@PathVariable(value= "tourId") int tourId) {
        List<TourRating> tourRatings = tourRatingService.lookupRatings(tourId);
        return tourRatings
                 .stream().map(RatingDto::new).toList();
    }

    @GetMapping("/average")
    public Map<String, Double> getAverageRatingForTour(@PathVariable(value= "tourId") int tourId) {
        Double average = tourRatingService.getAverageScore(tourId);
        return Map.of("average", average);
    }

    @PutMapping
    public RatingDto updateWithPut(@PathVariable(value = "tourId") int tourId, @RequestBody @Valid RatingDto ratingDto) {
        return new RatingDto(tourRatingService.update(tourId, ratingDto.getCustomerId(),
                ratingDto.getScore(), ratingDto.getComment()));
    }

    @PatchMapping
    public RatingDto updateWithPatch(@PathVariable(value = "tourId") int tourId,
                                     @RequestBody @Valid RatingDto ratingDto) {
        return new RatingDto(tourRatingService.updateSome(tourId,
                ratingDto.getCustomerId(),
                Optional.ofNullable(ratingDto.getScore()),
                Optional.ofNullable(ratingDto.getComment())));
    }

    @DeleteMapping("/{customerId}")
    public void delete(@PathVariable(value = "tourId") int tourId, @PathVariable(value = "customerId") int customerId) {
        tourRatingService.delete(tourId, customerId);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNoSuchElement(NoSuchElementException e) {
        System.out.println("No Tour found for id:" + e.getMessage());
    }


}
