/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.survey.utils.geo

import kotlin.math.max

data class BoundingRectangleCorners(
    val topLeft: PointF = PointF(),
    val topRight: PointF = PointF(),
    val bottomRight: PointF = PointF(),
    val bottomLeft: PointF = PointF(),
)

class BoundingRectanglePolygon(vertices: List<PointF>) {
    val corners = if (vertices.isEmpty()) {
        BoundingRectangleCorners()
    } else {
        BoundingRectangleCorners(
            PointF(vertices.minOf { it.x }, vertices.minOf { it.y }),
            PointF(vertices.maxOf { it.x }, vertices.minOf { it.y }),
            PointF(vertices.maxOf { it.x }, vertices.maxOf { it.y }),
            PointF(vertices.minOf { it.x }, vertices.maxOf { it.y }),
        )
    }

    fun getCenterPoint(): PointF {
        val centerX = (corners.topRight.x - corners.topLeft.x) * 0.5f + corners.topLeft.x
        val centerY = (corners.bottomRight.y - corners.topRight.y) * 0.5f + corners.topRight.y

        return PointF(centerX, centerY)
    }

    fun getSquareEnlargedByFactor(factor: Float): BoundingRectangleCorners {
        val center = getCenterPoint()
        val maxDist =
            max(
                corners.topRight.x - corners.topLeft.x,
                corners.bottomRight.y - corners.topRight.y
            ) * factor

        return BoundingRectangleCorners(
            PointF(center.x - maxDist * 0.5f, center.y - maxDist * 0.5f),
            PointF(center.x + maxDist * 0.5f, center.y - maxDist * 0.5f),
            PointF(center.x + maxDist * 0.5f, center.y + maxDist * 0.5f),
            PointF(center.x - maxDist * 0.5f, center.y + maxDist * 0.5f)
        )
    }
}
