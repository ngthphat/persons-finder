package com.persons.finder.domain.services

import com.persons.finder.data.Location
import com.persons.finder.data.repository.LocationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationsServiceImpl @Autowired constructor(
    private val locationRepository: LocationRepository,
) : LocationsService {

    @Transactional
    override fun addLocation(location: Location) {

        // Check if a location already exists for this reference ID
        val existingLocation = locationRepository.findByReferenceId(location.referenceId)

        if (existingLocation != null) {
            // If it exists, delete it first (to avoid duplicates)
            locationRepository.delete(existingLocation)
        }

        // Save the new location
        locationRepository.save(location)
    }

    @Transactional
    override fun removeLocation(locationReferenceId: Long) {
        val location = locationRepository.findByReferenceId(locationReferenceId)
        location?.let {
            locationRepository.delete(it)
        }
    }

    override fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location> {

        // Validate radius
        if (radiusInKm < 0) {
            throw IllegalArgumentException("Radius must be non-negative")
        }

        return locationRepository.findLocationsWithinRadius(latitude, longitude, radiusInKm)
    }
}
