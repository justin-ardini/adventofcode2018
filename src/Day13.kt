val NORTH = Vec2d(0, -1)
val SOUTH = Vec2d(0, 1)
val EAST = Vec2d(1, 0)
val WEST = Vec2d(-1, 0)

data class Tile(val pos: Vec2d, val type: Char) {
}

data class Cart(val pos: Vec2d, val dir: Vec2d, var turns: Int = 0) {
}

fun main() {
  /** Returns tiles + cart positions. */
  fun parseTracks(input: List<String>): Pair<Map<Vec2d, Tile>, List<Cart>> {
    val tracks = mutableMapOf<Vec2d, Tile>()
    val carts = mutableListOf<Cart>()
    input.forEachIndexed {
      y, line -> line.forEachIndexed {
        x, v -> 
          val pos = Vec2d(x, y)
          when (v) {
            '>' -> carts.add(Cart(pos, EAST))
            '<' -> carts.add(Cart(pos, WEST))
            '^' -> carts.add(Cart(pos, NORTH))
            'v' -> carts.add(Cart(pos, SOUTH))
          }
          if (v != ' ') {
            tracks[pos] = Tile(pos, v)
          }
      }
    }
    return tracks to carts
  }

  fun sortedCarts(carts: List<Cart>): List<Cart> {
    return carts.sortedWith(compareBy(
      { it.pos.y },
      { it.pos.x }
    ))
  }

  fun turnLeft(v: Vec2d) = Vec2d(v.y, -v.x)

  fun turnRight(v: Vec2d) = Vec2d(-v.y, v.x)

  fun step(tracks: Map<Vec2d, Tile>, carts: List<Cart>): Triple<List<Cart>, Boolean, Vec2d?> {
    val nextCarts = mutableListOf<Cart>()
    val positions = carts.map { it.pos } .toMutableSet()
    var crashes = mutableSetOf<Vec2d>()
    for (cart in carts) {
      if (cart.pos in crashes) {
        continue
      }
      var nextTile = tracks[cart.pos + cart.dir]!!
      positions.remove(cart.pos)
      if (!positions.add(nextTile.pos)) {
        crashes.add(nextTile.pos)
        continue
      }

      var nextDir: Vec2d
      var nextTurns = cart.turns
      when (nextTile.type) {
        '/' -> {
          if (cart.dir == NORTH || cart.dir == SOUTH)
            nextDir = turnRight(cart.dir)
          else
            nextDir = turnLeft(cart.dir)
        }
        '\\' -> {
          if (cart.dir == NORTH || cart.dir == SOUTH)
            nextDir = turnLeft(cart.dir)
          else 
            nextDir = turnRight(cart.dir)
        }
        '+' -> {
          when (cart.turns % 3) {
            0 -> nextDir = turnLeft(cart.dir)
            1 -> nextDir = cart.dir
            else -> nextDir = turnRight(cart.dir)
          }
          nextTurns += 1
        }
        else -> nextDir = cart.dir
      }
      nextCarts.add(Cart(nextTile.pos, nextDir, nextTurns))
    }
    return Triple(sortedCarts(nextCarts.filterNot { it.pos in crashes }), !crashes.isEmpty(), crashes.firstOrNull())
  }

  fun part1(tracks: Map<Vec2d, Tile>, initCarts: List<Cart>): Vec2d {
    var carts = sortedCarts(initCarts)
    (1..100_000).forEach {
      var result = step(tracks, carts)
      if (result.second) {
        return result.third!!
      }
      carts = result.first
    }
    return Vec2d(-1, -1)
  }

  fun part2(tracks: Map<Vec2d, Tile>, initCarts: List<Cart>): Vec2d {
    var carts = sortedCarts(initCarts)
    (1..100_000).forEach {
      var result = step(tracks, carts)
      if (result.first.size == 1) {
        return result.first.first().pos
      }
      carts = result.first
    }
    return Vec2d(-1, -1)
  }

  val (tracks, carts) = parseTracks(readInput("Day13"))
  println(part1(tracks, carts))
  println(part2(tracks, carts))
}
