package com.auterion.tazama.survey.utils.geo

class Polygon(val vertices: List<PointF>) {
    private val lines = this.vertices.windowed(2, step = 1, partialWindows = true) {
        if (it.size > 1) {
            Line(it[0], it[1])
        } else {
            Line(it[0], this.vertices.first())
        }
    }

    fun getIntersectionPoints(line: Line): List<LineIntersectionPoint> {
        return lines.mapNotNull { it.intersect(line) }
    }
}
