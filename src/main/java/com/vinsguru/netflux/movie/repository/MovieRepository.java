package com.vinsguru.netflux.movie.repository;

import com.vinsguru.netflux.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Genre containment needs a case-insensitive match against a text[] column
    // (not an @ElementCollection), which Hibernate's JPA Criteria API cannot join
    // or unnest. This native query stays fully parameterized (no string
    // concatenation of input values) and combines all three optional filters with
    // AND, matching the search endpoint's semantics.
    @Query(value = """
            SELECT * FROM movie m
            WHERE (:title IS NULL OR lower(m.title) LIKE lower(concat('%', :title, '%')))
              AND (:director IS NULL OR lower(m.director) LIKE lower(concat('%', :director, '%')))
              AND (:genre IS NULL OR EXISTS (SELECT 1 FROM unnest(m.genres) g WHERE lower(g) = lower(:genre)))
            LIMIT :limit
            """, nativeQuery = true)
    List<Movie> search(@Param("title") String title,
                        @Param("director") String director,
                        @Param("genre") String genre,
                        @Param("limit") int limit);

}
