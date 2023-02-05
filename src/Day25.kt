data class Vec4d(val w: Int, val x: Int, val y: Int, val z: Int) {
  constructor() : this(0f, 0f, 0f, 0f)
  constructor(w: Number, x: Number, y: Number, z: Number): this(w.toInt(), x.toInt(), y.toInt(), z.toInt())

  operator fun times(scalar: Number) = Vec4d(w * scalar.toInt(), x * scalar.toInt(), y * scalar.toInt(), z * scalar.toInt())
  operator fun div(scalar: Number) = Vec4d(w / scalar.toInt(), x / scalar.toInt(), y / scalar.toInt(), z / scalar.toInt())
  operator fun plus(vec: Vec4d) = Vec4d(w + vec.w, x + vec.x, y + vec.y, z + vec.z)
  operator fun minus(vec: Vec4d) = Vec4d(w - vec.w, x - vec.x, y - vec.y, z - vec.z)
  operator fun unaryMinus() = this * -1f
  fun distance(vec: Vec4d) = Math.abs(w - vec.w) + Math.abs(x - vec.x) + Math.abs(y - vec.y) + Math.abs(z - vec.z)

  companion object {
    val origin = Vec4d(0, 0, 0, 0)

    fun parse(s: String) =
        s.split(",")
            .map { it.trim().toInt() }
            .run { Vec4d(this[0], this[1], this[2], this[3]) }
  }
}

fun main() {
  fun findNeighbors(point: Vec4d, stars: List<Vec4d>): Set<Vec4d> =
    stars.filter { it.distance(point) <= 3 }.toSet()

  fun formConstellation(point: Vec4d, stars: MutableList<Vec4d>): Set<Vec4d> {
    val constellation = mutableSetOf(point)
    val q = ArrayDeque<Vec4d>()
    q.add(point)
    while (!q.isEmpty()) {
      var point = q.removeFirst()
      var neighbors = findNeighbors(point, stars)
      stars.removeAll(neighbors)
      constellation.addAll(neighbors)
      q.addAll(neighbors)
    }
    return constellation
  }

  fun part1(input: List<Vec4d>): Int {
    val constellations = mutableListOf<Set<Vec4d>>()
    val remaining = input.toMutableList()
    while (!remaining.isEmpty()) {
      var point = remaining.removeLast()
      constellations.add(formConstellation(point, remaining))
    }
    return constellations.size
  }

  val points = readInput("Day25").map { Vec4d.parse(it) }
  println(part1(points))
}
