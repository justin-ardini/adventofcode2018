data class Vec2d(val x: Int, val y: Int) {
  constructor() : this(0f, 0f)
  constructor(x: Number, y: Number): this(x.toInt(), y.toInt())

  operator fun times(scalar: Number) = Vec2d(x * scalar.toInt(), y * scalar.toInt())
  operator fun div(scalar: Number) = Vec2d(x / scalar.toInt(), y / scalar.toInt())
  operator fun plus(vec: Vec2d) = Vec2d(x + vec.x, y + vec.y)
  operator fun minus(vec: Vec2d) = Vec2d(x - vec.x, y - vec.y)
  operator fun unaryMinus() = this * -1f
  fun distance(vec: Vec2d) = Math.abs(x - vec.x) + Math.abs(y - vec.y)

  companion object {
    fun parse(s: String) =
        s.split(",")
            .map { it.trim().toInt() }
            .run { Vec2d(this[0], this[1]) }
  }
}
