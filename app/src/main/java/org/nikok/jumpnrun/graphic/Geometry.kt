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

typealias Line = Pair<Point, Point>

fun Line.intersects(circle: Circle): Boolean {
    val deltaX = second.x - first.x
    val deltaY = second.y - first.y
    val a = deltaX * deltaX + deltaY * deltaY
    val b = 2 * (deltaX * (first.x - circle.cx) + deltaY * (first.y - circle.cy))
    val c =
        (first.x - circle.cx) * (first.x - circle.cx) + (first.y - circle.cy) * (first.y - circle.cy) - circle.radius * circle.radius
    val discriminant = b * b - 4.0 * a * c
    if (discriminant < 0)
        return false
    val quad1 = (-b + Math.sqrt(discriminant)) / (2 * a)
    val quad2 = (-b - Math.sqrt(discriminant)) / (2 * a)
    if (quad1 in 0.0..1.0)
        return true
    else if (quad2 in 0.0..1.0)
        return true
    return false
}

val Rect.upperLeft get() = Point(x, y)
val Rect.upperRight get() = Point(x + width, y)

val Rect.upperSide get() = upperLeft to upperRight