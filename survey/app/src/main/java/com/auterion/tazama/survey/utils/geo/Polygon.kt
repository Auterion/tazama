package com.auterion.tazama.survey.utils.geo

class Polygon(vertices: List<PointF>) {
    private val _vertices = vertices
    val vertices get() = _vertices

    private val _lines = _vertices.windowed(2, step = 1, partialWindows = true) {
        if (it.size > 1) {
            Line(it[0], it[1])
        } else {
            Line(it[0], _vertices.first())
        }
    }

    fun getIntersectionPoints(line: Line): List<LineInterSectionPoint> {
        return _lines.map {
            it.intersect(line)
        }.filterNotNull()
    }


}
