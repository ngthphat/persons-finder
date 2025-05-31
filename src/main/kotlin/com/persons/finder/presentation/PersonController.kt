package com.persons.finder.presentation

import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.presentation.dto.*
import com.persons.finder.presentation.exception.ResourceNotFoundException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("api/v1/persons")
@Tag(name = "Persons", description = "Persons API")
class PersonController @Autowired constructor(
    private val personsService: PersonsService,
    private val locationsService: LocationsService
) {

    @Operation(summary = "Create a new person", description = "Creates a new person with the provided name")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Person created successfully"),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        )
    )
    @PostMapping
    fun createPerson(@Valid @RequestBody personRequest: PersonRequestDto): ResponseEntity<PersonResponseDto> {
        val person = Person(
            id = 0, // ID will be assigned by the service
            name = personRequest.name
        )
        personsService.save(person)

        val responseDto = PersonResponseDto(
            id = person.id,
            name = person.name
        )

        return ResponseEntity(responseDto, HttpStatus.CREATED)
    }

    @Operation(
        summary = "Update a person's location",
        description = "Updates or creates a person's location using latitude and longitude"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Location updated successfully"),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Person not found",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        )
    )
    @PutMapping("/{id}/location")
    fun updateLocation(
        @Parameter(description = "Person ID", required = true) @PathVariable id: Long,
        @Valid @RequestBody locationRequest: LocationRequestDto
    ): ResponseEntity<Unit> {
        try {
            // Check if a person exists
            personsService.getById(id)

            val location = Location(
                id = 0, // ID will be assigned by the database
                referenceId = id,
                latitude = locationRequest.latitude,
                longitude = locationRequest.longitude
            )

            locationsService.addLocation(location)
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            throw ResourceNotFoundException("Person with ID $id not found")
        }
    }

    @Operation(
        summary = "Find nearby persons",
        description = "Finds persons around a query location within a specified radius"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Successfully retrieved list of nearby persons"),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input parameters",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        )
    )
    @GetMapping("/nearby")
    fun findNearbyPersons(
        @Parameter(description = "Latitude", required = true) @RequestParam @NotNull lat: Double,
        @Parameter(description = "Longitude", required = true) @RequestParam @NotNull lon: Double,
        @Parameter(
            description = "Radius in kilometers",
            required = true
        ) @RequestParam @NotNull @Min(0) radiusKm: Double
    ): ResponseEntity<NearbyPersonsResponseDto> {
        val nearbyLocations = locationsService.findAround(lat, lon, radiusKm)
        val personIds = nearbyLocations.map { it.referenceId }

        return ResponseEntity.ok(NearbyPersonsResponseDto(personIds))
    }

    @Operation(summary = "Get persons by IDs", description = "Retrieves one or more persons by their IDs")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Successfully retrieved persons"),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input parameters",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "One or more persons not found",
            content = [Content(schema = Schema(implementation = ErrorResponseDto::class))]
        )
    )
    @GetMapping
    fun getPersonsByIds(
        @Parameter(description = "Person IDs", required = true) @RequestParam id: List<Long>
    ): ResponseEntity<List<PersonResponseDto>> {
        if (id.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val persons = id.map {
            try {
                val person = personsService.getById(it)
                PersonResponseDto(
                    id = person.id,
                    name = person.name
                )
            } catch (e: Exception) {
                throw ResourceNotFoundException("Person with ID $it not found")
            }
        }

        return ResponseEntity.ok(persons)
    }
}
