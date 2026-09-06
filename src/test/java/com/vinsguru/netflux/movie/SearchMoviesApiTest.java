package com.vinsguru.netflux.movie;

import com.vinsguru.netflux.movie.dto.MovieSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
class SearchMoviesApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return 400 ProblemDetail when no filter is provided")
    void shouldReturn400WhenNoFilterProvided() {
        var problem = restTestClient.get()
                .uri("/api/movies/search")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem).isNotNull();
        assertThat(problem.getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should match title with a partial case-insensitive search")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldFilterByTitlePartialCaseInsensitive() {
        var results = search("title=matrix");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("Should match director with a partial case-insensitive search")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldFilterByDirectorPartialCaseInsensitive() {
        var results = search("director=nolan");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactlyInAnyOrder(3L, 4L, 5L);
    }

    @Test
    @DisplayName("Should match genre exactly, case-insensitively")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldFilterByGenreExactCaseInsensitive() {
        var results = search("genre=sci-fi");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 4L);
    }

    @Test
    @DisplayName("Should combine title, director and genre filters using AND logic")
    @Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldCombineFiltersWithAndLogic() {
        var results = search("director=nolan&genre=sci-fi");

        assertThat(results).extracting(MovieSummary::id)
                .containsExactlyInAnyOrder(3L, 4L);
    }

    private List<MovieSummary> search(String queryString) {
        return restTestClient.get()
                .uri("/api/movies/search?" + queryString)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();
    }

}
