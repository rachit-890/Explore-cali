package com.example.explorecali.service;

import com.example.explorecali.model.Tour;
import com.example.explorecali.model.TourRating;
import com.example.explorecali.repository.TourRatingRepository;
import com.example.explorecali.repository.TourRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;

@Service
@Slf4j
public class TourRatingService {
    private TourRatingRepository tourRatingRepository;
    private TourRepository tourRepository;

    public TourRatingService(TourRatingRepository tourRatingRepository, TourRepository tourRepository) {
        this.tourRatingRepository = tourRatingRepository;
        this.tourRepository = tourRepository;
    }

    public TourRating createNew(int tourId, Integer customerId, Integer score, String comment) throws NoSuchElementException {
        log.info("Create a tour rating for tour {} and customer {}", tourId, String.valueOf(customerId));
        return tourRatingRepository.save(new TourRating(verifyTour(tourId), customerId,
                score, comment));
    }

    public Optional<TourRating> lookupRatingById(int id) {
        log.info("lookup rating by id {}", id);
        return tourRatingRepository.findById(id);
    }

    public List<TourRating> lookupAll() {
        log.info("Lookup all tour ratings");
        return tourRatingRepository.findAll();
    }

    public List<TourRating> lookupRatings(int tourId) throws NoSuchElementException {
        log.info("Lookup ratings for tour {}", tourId);
        return tourRatingRepository.findByTourId(verifyTour(tourId).getId());
    }

    public TourRating update(int tourId, Integer customerId, Integer score, String comment)
            throws NoSuchElementException {
        log.info("Update tour {} customer {}", tourId, customerId);
        TourRating rating = verifyTourRating(tourId, customerId);
        rating.setScore(score);
        rating.setComment(comment);
        return tourRatingRepository.save(rating);
    }

    public TourRating updateSome(int tourId, Integer customerId, Optional<Integer> score, Optional<String> comment)
            throws NoSuchElementException {
        log.info("Update some of tour {} customer {}", tourId, customerId);
        TourRating rating = verifyTourRating(tourId, customerId);
        score.ifPresent(s ->rating.setScore(s));
        comment.ifPresent(c -> rating.setComment(c));
        return tourRatingRepository.save(rating);
    }

    public void delete(int tourId, Integer customerId) throws NoSuchElementException {
        log.info("Delete rating for tour {} customer {}", tourId, customerId);
        TourRating rating = verifyTourRating(tourId, customerId);
        tourRatingRepository.delete(rating);
    }

    public Double getAverageScore(int tourId) throws NoSuchElementException {
        List<TourRating> ratings = tourRatingRepository.findByTourId(verifyTour(tourId).getId());
        OptionalDouble average = ratings.stream().mapToInt((rating) -> rating.getScore()).average();
        return average.isPresent() ? average.getAsDouble() : null;
    }

    public void rateMany(int tourId,  int score, List<Integer> customers) {
        Tour tour = verifyTour(tourId);
        for (Integer c : customers) {
            if (tourRatingRepository.findByTourIdAndCustomerId(tourId, c).isPresent()) {
                throw new ConstraintViolationException("Unable to create duplicate ratings", null);
            }
            tourRatingRepository.save(new TourRating(tour, c, score));
        }
    }

    private Tour verifyTour(int tourId) throws NoSuchElementException {
        return tourRepository.findById(tourId)
                .orElseThrow(() -> new NoSuchElementException("Tour does not exist " + tourId));
    }

    public TourRating verifyTourRating(int tourId, int customerId) throws NoSuchElementException {
        return tourRatingRepository.findByTourIdAndCustomerId(tourId, customerId)
                .orElseThrow(() -> new NoSuchElementException("Tour-Rating pair for request: " 
                    + tourId + " for customer " + customerId));
    }

}