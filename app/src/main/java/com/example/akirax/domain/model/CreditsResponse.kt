package com.example.akirax.domain.model

data class CreditsResponse(
    val cast: List<CastMember>
)

data class CastMember(
    val name: String
)
