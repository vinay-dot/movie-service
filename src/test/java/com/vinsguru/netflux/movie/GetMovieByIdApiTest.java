package com.vinsguru.netflux.movie;

import com.vinsguru.netflux.movie.dto.MovieDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
class GetMovieByIdApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return full movie details when the movie exists")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnMovieDetailsWhenFound() {
        var movie = restTestClient.get()
                .uri("/api/movies/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieDetails.class)
                .returnResult()
                .getResponseBody();

        assertThat(movie).isNotNull();
        assertThat(movie.id()).isEqualTo(1L);
        assertThat(movie.title()).isEqualTo("The Matrix");
        assertThat(movie.director()).isEqualTo("Lana Wachowski");
        assertThat(movie.releaseDate()).isEqualTo(LocalDate.of(1999, 3, 31));
        assertThat(movie.genres()).containsExactlyInAnyOrder("Action", "Sci-Fi");
        assertThat(movie.themes()).containsExactly("Dystopia");
    }

    @Test
    @DisplayName("Should return 404 ProblemDetail when the movie does not exist")
    void shouldReturn404WhenMovieNotFound() {
        var problem = restTestClient.get()
                .uri("/api/movies/{id}", 999)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem).isNotNull();
        assertThat(problem.getStatus()).isEqualTo(404);
    }

}
