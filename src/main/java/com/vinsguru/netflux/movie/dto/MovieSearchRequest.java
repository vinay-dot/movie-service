package com.vinsguru.netflux.movie.dto;

import jakarta.validation.constraints.AssertTrue;

import java.util.Objects;

public record MovieSearchRequest(String title,
                                  String genre,
                                  String director,
                                  Integer limit) {

    @AssertTrue(message = "At least one of title, genre, or director is required")
    public boolean isAtLeastOneFilterPresent() {
        return Objects.nonNull(title) || Objects.nonNull(genre) || Objects.nonNull(director);
    }

}
