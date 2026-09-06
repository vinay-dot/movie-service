package com.vinsguru.netflux.movie;

import com.vinsguru.netflux.movie.dto.MovieSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
class PopularMoviesApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return movies sorted by voteCount descending, honoring an explicit limit")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnMoviesSortedByVoteCountDescending() {
        var results = restTestClient.get()
                .uri("/api/movies/popular?limit=3")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(results).extracting(MovieSummary::id)
                .containsExactly(5L, 3L, 4L);
    }

    @Test
    @DisplayName("Should default to a limit of 20 when no limit is provided")
    @Sql(scripts = "/sql/insert-bulk-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldApplyDefaultLimitOf20() {
        var results = restTestClient.get()
                .uri("/api/movies/popular")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(results).hasSize(20);
    }

    @Test
    @DisplayName("Should cap the limit at 100 when the requested limit exceeds the max")
    @Sql(scripts = "/sql/insert-bulk-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldCapLimitAt100() {
        var results = restTestClient.get()
                .uri("/api/movies/popular?limit=500")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(results).hasSize(100);
    }

    @Test
    @DisplayName("Should fall back to the default limit when the requested limit is below 1")
    @Sql(scripts = "/sql/insert-bulk-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldFallBackToDefaultLimitWhenBelowOne() {
        var zeroResults = restTestClient.get()
                .uri("/api/movies/popular?limit=0")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();

        var negativeResults = restTestClient.get()
                .uri("/api/movies/popular?limit=-5")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(zeroResults).hasSize(20);
        assertThat(negativeResults).hasSize(20);
    }

}
