package com.persons.finder.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

/**
 * DTO for updating a person's location
 */
data class LocationRequestDto(
    @field:NotNull(message = "Latitude is required")
    @Schema(description = "Latitude coordinate", example = "40.7128", required = true)
    val latitude: Double,

    @field:NotNull(message = "Longitude is required")
    @Schema(description = "Longitude coordinate", example = "-74.0060", required = true)
    val longitude: Double
)