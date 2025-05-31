package com.persons.finder.presentation.dto

/**
 * DTO for returning nearby persons
 */
data class NearbyPersonsResponseDto(
    val personIds: List<Long>
)