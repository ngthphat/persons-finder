package com.persons.finder.data.repository

import com.persons.finder.data.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : JpaRepository<Location, Long> {
    
    fun findByReferenceId(referenceId: Long): Location?
    
    // Custom query to find locations within a radius using the Haversine formula
    @Query("""
        SELECT l FROM Location l
        WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(l.latitude)) * 
        cos(radians(l.longitude) - radians(:longitude)) + 
        sin(radians(:latitude)) * sin(radians(l.latitude)))) <= :radiusInKm
    """)
    fun findLocationsWithinRadius(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("radiusInKm") radiusInKm: Double
    ): List<Location>
}