data class Claim(val id: String, val pos: Vec2d, val size: Vec2d) {
  companion object {
    private val regex = """^#(\d+) @ (\d+),(\d+): (\d+)x(\d+)$""".toRegex()

    fun parse(s: String): Claim {
      val match = regex.matchEntire(s)
      val (id, left, top, width, height) = match!!.destructured
      return Claim(id, Vec2d(left.toInt(), top.toInt()), Vec2d(width.toInt(), height.toInt()))
    }
  }

  fun tileSequence(): Sequence<Vec2d> = sequence {
    for (x in pos.x until pos.x + size.x) {
      for (y in pos.y until pos.y + size.y) {
        yield(Vec2d(x, y))
      }
    }
  }
}

fun main() {
  fun part1(claims: List<Claim>): Int {
    return claims.flatMap { it.tileSequence() }
        .groupingBy { it }
        .eachCount()
        .count { it.value > 1 }
  }

  fun part2(claims: List<Claim>): String {
    val counts = claims.flatMap { it.tileSequence() }
        .groupingBy { it }
        .eachCount()
    return claims.first {
      it.tileSequence().all { counts.get(it) == 1 }
    }.id
  }

  val claims = readInput("Day03").map { Claim.parse(it) }
  println(part1(claims))
  println(part2(claims))
}
