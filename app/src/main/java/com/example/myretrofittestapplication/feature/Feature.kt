package com.example.myretrofittestapplication.feature

import org.geojson.GeoJsonObject

data class Feature<T>(
    val id: Long,
    val type: String,
    val geometry: GeoJsonObject,
    val properties: T,
)

