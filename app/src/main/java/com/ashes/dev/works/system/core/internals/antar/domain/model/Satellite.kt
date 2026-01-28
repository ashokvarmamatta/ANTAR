package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Satellite(
    val constellation: String,
    val svid: Int,
    val cn0DbHz: Float,
    val elevationDegrees: Float,
    val azimuthDegrees: Float,
    val hasEphemerisData: Boolean,
    val hasAlmanacData: Boolean,
    val usedInFix: Boolean
)
