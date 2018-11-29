package org.nikok.jumpnrun.graphic

import kotlin.math.pow

fun overlaps(circle: Circle, rect: Rect): Boolean {
    val halfWidth = rect.width / 2
    val cx = Math.abs(circle.cx - rect.x - halfWidth)
    val r = circle.radius
    val xDist = halfWidth + r
    if (cx > xDist)
        return false
    val halfHeight = rect.height / 2
    val cy = Math.abs(circle.cy - rect.y - halfHeight)
    val yDist = halfHeight + r
    if (cy > yDist)
        return false
    if (cx <= halfWidth || cy <= halfHeight)
        return true
    val xCornerDist = cx - halfWidth
    val yCornerDist = cy - halfHeight
    val xCornerDistSq = xCornerDist * xCornerDist
    val yCornerDistSq = yCornerDist * yCornerDist
    val maxCornerDistSq = r * r
    return xCornerDistSq + yCornerDistSq <= maxCornerDistSq
}


fun overlaps(x1: Float, y1: Float, x2: Float, y2: Float, r1: Float, r2: Float): Boolean {
    val dx = x2 - x1
    val dy = y2 - y1
    val radii = r2 + r1
    return dx.pow(2) + dy.pow(2) < radii.pow(2)
}