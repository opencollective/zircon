package org.codetome.zircon.graphics.shape.factory

import org.codetome.zircon.Position
import org.codetome.zircon.graphics.shape.DefaultShape
import org.codetome.zircon.graphics.shape.Shape
import org.codetome.zircon.graphics.shape.ShapeFactory
import java.util.*

object FilledTriangleFactory : ShapeFactory<TriangleParameters> {

    override fun createShape(shapeParameters: TriangleParameters) = shapeParameters.let { (p1, p2, p3) ->

        // The algorithm described here is used
        // http://www-users.mat.uni.torun.pl/~wrona/3d_tutor/tri_fillers.html
        val points = arrayOf(p1, p2, p3)
        Arrays.sort(points, { o1, o2 -> if (o1.row < o2.row) -1 else if (o1.row == o2.row) 0 else 1 })
        var result: Shape = DefaultShape()

        val dx1: Float
        val dx2: Float
        val dx3: Float
        if (points[1].row - points[0].row > 0) {
            dx1 = (points[1].column - points[0].column).toFloat() / (points[1].row - points[0].row).toFloat()
        } else {
            dx1 = 0f
        }
        if (points[2].row - points[0].row > 0) {
            dx2 = (points[2].column - points[0].column).toFloat() / (points[2].row - points[0].row).toFloat()
        } else {
            dx2 = 0f
        }
        if (points[2].row - points[1].row > 0) {
            dx3 = (points[2].column - points[1].column).toFloat() / (points[2].row - points[1].row).toFloat()
        } else {
            dx3 = 0f
        }

        var startX: Float
        var startY: Float
        var endX: Float
        endX = points[0].column.toFloat()
        startX = endX
        startY = points[0].row.toFloat()
        if (dx1 > dx2) {
            while (startY <= points[1].row) {
                result += LineFactory.buildLine(
                        fromPoint = Position.of(startX.toInt(), startY.toInt()),
                        toPoint = Position.of(endX.toInt(), startY.toInt()))
                startY++
                startX += dx2
                endX += dx1
            }
            endX = points[1].column.toFloat()
            while (startY <= points[2].row) {
                result += LineFactory.buildLine(
                        fromPoint = Position.of(startX.toInt(), startY.toInt()),
                        toPoint = Position.of(endX.toInt(), startY.toInt()))
                startY++
                startX += dx2
                endX += dx3
            }
        } else {
            while (startY <= points[1].row) {
                result += LineFactory.buildLine(
                        fromPoint = Position.of(startX.toInt(), startY.toInt()),
                        toPoint = Position.of(endX.toInt(), startY.toInt()))
                startY++
                startX += dx1
                endX += dx2
            }
            startX = points[1].column.toFloat()
            startY = points[1].row.toFloat()
            while (startY <= points[2].row) {
                result += LineFactory.buildLine(
                        fromPoint = Position.of(startX.toInt(), startY.toInt()),
                        toPoint = Position.of(endX.toInt(), startY.toInt()))
                startY++
                startX += dx3
                endX += dx2
            }
        }
        result.offsetToDefaultPosition()
    }

    /**
     * Creates the points for a filled triangle. The triangle will be delimited by
     * positions `p1` to `p2` to `p3` and back to `p1` from there.
     *
     * *Note that** all resulting shapes will be offset to the top left (0x0) position!
     * @see [org.codetome.zircon.graphics.shape.Shape.offsetToDefaultPosition] for more info!
     */
    @JvmStatic
    fun buildFilledTriangle(params: TriangleParameters) = createShape(params)

    /**
     * Creates the points for a filled triangle. The triangle will be delimited by
     * positions `p1` to `p2` to `p3` and back to `p1` from there.
     *
     * *Note that** all resulting shapes will be offset to the top left (0x0) position!
     * @see [org.codetome.zircon.graphics.shape.Shape.offsetToDefaultPosition] for more info!
     */
    @JvmStatic
    fun buildFilledTriangle(p1: Position,
                            p2: Position,
                            p3: Position) = buildFilledTriangle(TriangleParameters(p1, p2, p3))
}