package com.fifo.match.model


import com.google.gson.annotations.SerializedName


data class CompleteProfileListBean(
    @SerializedName("relationship_status_data")
    val relationshipStatusData: List<RelationshipStatusData?>?,
    @SerializedName("occupations_data")
    val occupationsData: List<OccupationsData?>?,
    @SerializedName("education_data")
    val educationData: List<EducationData?>?
) {
    data class RelationshipStatusData(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?
    )

    data class OccupationsData(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?
    )

    data class EducationData(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?
    )

}