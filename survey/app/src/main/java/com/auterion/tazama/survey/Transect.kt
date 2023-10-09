/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.survey

import com.auterion.tazama.survey.utils.geo.Line
import com.auterion.tazama.survey.utils.geo.LocalProjection
import com.mapbox.mapboxsdk.geometry.LatLng

class Transect(
    val photoLine: Line,
    val projection: LocalProjection,
) {

    val directionIndicator = TransectDirectionIndicator(
        projection.reproject(photoLine.start + photoLine.getNormalizedDirection() * 0.25f * photoLine.getLength()),
        photoLine.getAzimuth()
    )
    val startLat = projection.reproject(photoLine.start)
    val endLat = projection.reproject(photoLine.end)
}

data class TransectDirectionIndicator(
    val position: LatLng,
    val rotation: Float,
)
