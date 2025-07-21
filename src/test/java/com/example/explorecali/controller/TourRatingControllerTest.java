package com.example.explorecali.controller;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Map; // Added for getAverage test

import com.example.explorecali.dto.RatingDto;
import com.example.explorecali.model.Tour; // Removed if not directly used in controller for clarity
import com.example.explorecali.model.TourRating; // Removed if not directly used in controller for clarity
import com.example.explorecali.service.TourRatingService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
// import org.mockito.Mock; // REMOVE THIS IMPORT FOR TourRatingService
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // <--- ADD THIS IMPORT
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@SpringBootTest(webEnvironment = RANDOM_PORT)
public class TourRatingControllerTest {

    // These Tour and rating id's do not already exist in the db
    private static final int TOUR_ID = 999;
    private static final int CUSTOMER_ID = 1000;
    private static final int SCORE = 3;
    private static final String COMMENT = "comment";
    private static final String TOUR_RATINGS_URL = "/tours/" + TOUR_ID + "/ratings";

    @Autowired
    private TestRestTemplate restTemplate;

    // CHANGE FROM @Mock to @MockBean
    @MockBean
    private TourRatingService serviceMock;

    // You likely don't need these mocks if they are only used to mock the service's return values
    // and not directly passed to methods you are verifying on the serviceMock.
    // If serviceMock.lookupRatings() returns a List<TourRating>, and TourRating is a simple entity,
    // you might not need a mock for TourRating itself here, but rather instantiate it.
    // However, if your service methods operate on these specific mocks, keep them.
    @Mock // Keep if TourRating is used to return from serviceMock
    private TourRating tourRatingMock;
    @Mock // Keep if Tour is used to return from serviceMock
    private Tour tourMock;


    private RatingDto ratingDto = new RatingDto(SCORE, COMMENT,CUSTOMER_ID);

    @Test
    void testCreateTourRating() {
        // Stub the service method if it returns something that the controller uses
        // For POST, service.createNew usually returns a TourRating, which the controller converts
        when(serviceMock.createNew(anyInt(), anyInt(), anyInt(), any())).thenReturn(tourRatingMock); // Assuming tourRatingMock is set up to provide a valid return

        restTemplate.postForEntity(TOUR_RATINGS_URL, ratingDto, RatingDto.class);

        // Verify the service was called with correct arguments
        verify(this.serviceMock).createNew(TOUR_ID, CUSTOMER_ID, SCORE, COMMENT);
    }

    @Test
    void testDelete() {
        // The service.delete method is void, so no when().thenReturn() is needed usually
        // Unless it throws an exception that the controller catches and handles.
        // Mockito will do nothing by default for void methods.
        restTemplate.delete(TOUR_RATINGS_URL + "/" + CUSTOMER_ID);

        verify(this.serviceMock).delete(TOUR_ID, CUSTOMER_ID);
    }

    @Test
    void testGetAllRatingsForTour() {
        // Stub the service method to return a list of mock TourRating objects
        when(serviceMock.lookupRatings(anyInt())).thenReturn(List.of(tourRatingMock));
        ResponseEntity<List<RatingDto>> res = restTemplate.exchange(TOUR_RATINGS_URL, org.springframework.http.HttpMethod.GET, null, new org.springframework.core.ParameterizedTypeReference<List<RatingDto>>() {});
        // If the controller returns List<RatingDto>, you should use ParameterizedTypeReference
        // Or change the return type of restTemplate.getForEntity to a raw String and then assert on JSON content.

        assertThat(res.getStatusCode(), is(HttpStatus.OK));
        verify(serviceMock).lookupRatings(anyInt());
    }

    @Test
    void testGetAverage() {
        // Stub the service method to return a specific average score
        when(serviceMock.getAverageScore(TOUR_ID)).thenReturn(10.0); // Assuming 10.0 as example average
        ResponseEntity<Map> res = restTemplate.getForEntity(TOUR_RATINGS_URL + "/average", Map.class); // Get a Map to check average

        assertThat(res.getStatusCode(), is(HttpStatus.OK));
        assertThat(res.getBody().get("average"), is(10.0)); // Assert on the average value in the map
        verify(serviceMock).getAverageScore(TOUR_ID);
    }

    /*
     * PATCH testing only works when adding http client dependency to pom.xml
     */
    @Test
    void testUpdateWithPatch() {
        when(serviceMock.updateSome(anyInt(), anyInt(), any(), any())).thenReturn(tourRatingMock);

        // Use patchForEntity to get the ResponseEntity for status code assertion if needed.
        restTemplate.patchForObject(TOUR_RATINGS_URL, ratingDto, String.class);
        verify(this.serviceMock).updateSome(TOUR_ID, CUSTOMER_ID, Optional.of(SCORE), Optional.of(COMMENT)); // Verify with actual values if possible, or use captors
    }

    @Test
    void testUpdateWithPut() {
        when(serviceMock.update(TOUR_ID, CUSTOMER_ID, SCORE, COMMENT))
                .thenReturn(tourRatingMock);

        // put also returns void if there's no return value from the controller.
        restTemplate.put(TOUR_RATINGS_URL, ratingDto);

        verify(serviceMock).update(TOUR_ID, CUSTOMER_ID, SCORE, COMMENT);
    }

    @Test
    void testCreateManyTourRatings() {
        // Mock the service call for void methods
        // No when().thenReturn() needed if service.rateMany is void.
        Integer customers[] = {123}; // Consider List.of(123) for clarity
        restTemplate.postForObject(TOUR_RATINGS_URL + "/batch?score=" + SCORE, customers,
                String.class);

        verify(serviceMock).rateMany(TOUR_ID, SCORE, List.of(customers[0])); // Verify with specific values
    }

    /** Test unhappy Paths too to validate GlobalExceptionHandler */

    @Test
    public void test404() {
        // Stub the service to throw NoSuchElementException
        when(serviceMock.lookupRatings(anyInt())).thenThrow(new NoSuchElementException());
        ResponseEntity<String> res = restTemplate.getForEntity(TOUR_RATINGS_URL, String.class);

        assertThat(res.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void test400() {
        // Stub the service to throw ConstraintViolationException
        when(serviceMock.lookupRatings(anyInt())).thenThrow(new ConstraintViolationException("Validation failed", null)); // Provide a message and a set of violations if needed
        ResponseEntity<String> res = restTemplate.getForEntity(TOUR_RATINGS_URL, String.class);

        assertThat(res.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }
}