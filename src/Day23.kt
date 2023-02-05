data class Vec3d(val x: Int, val y: Int, val z: Int) {
  constructor() : this(0f, 0f, 0f)
  constructor(x: Number, y: Number, z: Number): this(x.toInt(), y.toInt(), z.toInt())

  operator fun times(scalar: Number) = Vec3d(x * scalar.toInt(), y * scalar.toInt(), z * scalar.toInt())
  operator fun div(scalar: Number) = Vec3d(x / scalar.toInt(), y / scalar.toInt(), z / scalar.toInt())
  operator fun plus(vec: Vec3d) = Vec3d(x + vec.x, y + vec.y, z + vec.z)
  operator fun minus(vec: Vec3d) = Vec3d(x - vec.x, y - vec.y, z - vec.z)
  operator fun unaryMinus() = this * -1f
  fun distance(vec: Vec3d) = Math.abs(x - vec.x) + Math.abs(y - vec.y) + Math.abs(z - vec.z)

  companion object {
    val origin = Vec3d(0, 0, 0)

    fun parse(s: String) =
        s.split(",")
            .map { it.trim().toInt() }
            .run { Vec3d(this[0], this[1], this[2]) }
  }
}

data class Nanobot(val pos: Vec3d, val r: Int) {
  companion object {
    val PATTERN = """^pos=<(-?\d+,-?\d+,-?\d+)>, r=(\d+)$""".toRegex()
    fun parse(s: String): Nanobot {
      val (pos, r) = PATTERN.matchEntire(s)!!.destructured
      return Nanobot(Vec3d.parse(pos), r.toInt())
    }
  }
}

fun main() {
  fun part1(bots: List<Nanobot>): Int {
    val strongest = bots.maxBy { it.r }
    return bots.count { it.pos.distance(strongest.pos) <= strongest.r }
  }

  fun inRange(pos: Vec3d, bots: List<Nanobot>): Int =
    bots.count { it.pos.distance(pos) <= it.r }

  class BronKerbosch(val neighbors: Map<Nanobot, Set<Nanobot>>) {
    var best = setOf<Nanobot>()

    fun findClique(): Set<Nanobot> {
      run(setOf(), neighbors.keys, setOf())
      return best
    }

    private fun run(r: Set<Nanobot>, p: Set<Nanobot>, x: Set<Nanobot>) {
      if (p.isEmpty() && x.isEmpty()) {
        if (r.size > best.size) {
          best = r
        }
        return
      }
      val pxMax = (p + x).maxBy { neighbors[it]!!.size }
      for (v in p - neighbors[pxMax]!!) {
        var nv = neighbors[v]!!
        run(r + v, p.intersect(nv), x.intersect(nv) )
      }
    }
  }

  fun part2(bots: List<Nanobot>): Int {
    val neighbors = bots.associateWith { bot ->
      bots.filterNot { it == bot }
        .filter { it.pos.distance(bot.pos) <= it.r + bot.r }.toSet()
    }
    val clique = BronKerbosch(neighbors).findClique()
    return clique.maxOf { it.pos.distance(Vec3d.origin) - it.r }
  }

  val bots = readInput("Day23").map { Nanobot.parse(it) }
  println(part1(bots))
  println(part2(bots))
}
