package com.persons.finder.data

import javax.persistence.*

@Entity
@Table(name = "locations")
data class Location(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // Person's id is used for this field
    @Column(nullable = false)
    val referenceId: Long,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double
)
