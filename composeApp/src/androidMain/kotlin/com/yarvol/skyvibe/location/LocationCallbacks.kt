package com.yarvol.skyvibe.location

data class LocationCallbacks(
    val onSuccess: (Pair<Double, Double>) -> Unit,
    val onFailure: (Exception) -> Unit,
    val onPermissionGranted: () -> Unit,
    val onPermissionDenied: () -> Unit,
    val onPermissionsRevoked: () -> Unit
)
