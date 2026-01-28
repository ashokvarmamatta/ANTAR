package com.ashes.dev.works.system.core.internals.antar.data.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Camera
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CameraRepository

class CameraRepositoryImpl : CameraRepository {
    override fun getCamera(): Camera {
        return Camera(
            aberrationModes = "- - -", antibandingModes = "- - -", autoExposureModes = "- - -",
            targetFpsRanges = "- - -", compensationRange = "- - -", compensationStep = "- - -",
            autoFocusModes = "- - -", effects = "- - -", sceneModes = "- - -",
            videoStabilizationModes = "- - -", autoWhiteBalanceModes = "- - -",
            maxAutoExposureRegions = "- - -", maxAutoFocusRegions = "- - -",
            maxAutoWhiteBalanceRegions = "- - -", edgeModes = "- - -", flashAvailable = "- - -",
            hotPixelModes = "- - -", hardwareLevel = "- - -", thumbnailSizes = "- - -",
            lensPlacement = "- - -", apertures = "- - -", filterDensities = "- - -",
            focalLengths = "- - -", opticalStabilization = "- - -", focusDistanceCalibration = "- - -",
            cameraCapabilities = "- - -", maxOutputStreams = "- - -", maxOutputStreamsStalling = "- - -",
            maxRawOutputStreams = "- - -", partialResults = "- - -", maxDigitalZoom = "- - -",
            croppingType = "- - -", supportedResolutions = "- - -", testPatternModes = "- - -",
            colorFilterArrangement = "- - -", sensorSize = "- - -", pixelArraySize = "- - -",
            timestampSource = "- - -", orientation = "- - -"
        )
    }
}
