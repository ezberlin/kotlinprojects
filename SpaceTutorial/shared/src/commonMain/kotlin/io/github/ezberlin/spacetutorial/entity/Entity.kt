package io.github.ezberlin.spacetutorial.entity

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RocketLaunch(
    @SerialName("flight_number")
    val flightNumber: Int,

    @SerialName("name")
    val missionName: String,

    @SerialName("date_utc")
    val launchDateUtc: String,

    @SerialName("details")
    val details: String?,

    @SerialName("success")
    val launchSuccess: Boolean?,

    @SerialName("links")
    val links: Links
) {
    var launchYear = Instant.parse(launchDateUtc).toLocalDateTime(TimeZone.UTC)
}

@Serializable
data class Links(
    @SerialName("patch")
    val patch: Patch?,

    @SerialName("article")
    val article: String?
)
