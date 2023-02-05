val GOBLIN = 'G'
val ELF = 'E'
val WALL = '#'
val OPEN = '.'

enum class ActorType(val c: Char) {
  GOBLIN('G'), ELF('E')
}

data class Actor(val type: ActorType, var pos: Vec2d, var hp: Int = 200, var attack: Int = 3): Comparable<Actor> {
  constructor(actor: Actor) : this(actor.type, actor.pos, actor.hp, actor.attack) {}

  override fun compareTo(other: Actor): Int =
    when {
        pos.y < other.pos.y -> -1
        pos.y > other.pos.y -> 1
        pos.x < other.pos.x -> -1
        pos.x > other.pos.x -> 1
        else -> 0
    }
}

fun main() {
  fun buildGrid(input: List<String>): Pair<Map<Vec2d, List<Vec2d>>, List<Actor>> {
    val openTiles = setOf(OPEN, GOBLIN, ELF)
    val tiles = mutableMapOf<Vec2d, List<Vec2d>>()
    val actors = mutableListOf<Actor>()
    input.forEachIndexed {
      y, line -> line.forEachIndexed {
        x, v -> 
          if (v != WALL) {
            val pos = Vec2d(x, y)
            // Note: order of neighbors list matters!
            tiles.put(
              pos,
              listOf(Vec2d(x, y-1), Vec2d(x-1, y), Vec2d(x+1, y), Vec2d(x, y+1))
                .filter { input.getOrNull(it.y)?.getOrNull(it.x) in openTiles})
            if (v == GOBLIN || v == ELF) {
              actors.add(Actor(if (v == GOBLIN) ActorType.GOBLIN else ActorType.ELF, pos))
            }
          }
        }
      }
    return tiles to actors.sorted()
  }

  fun shortestStep(start: Vec2d, goals: Set<Vec2d>, tiles: Map<Vec2d, List<Vec2d>>, blocked: Set<Vec2d>): Vec2d? {
    val q = ArrayDeque<List<Vec2d>>()
    q.add(listOf(start))
    val visited = mutableSetOf<Vec2d>()
    while (!q.isEmpty()) {
      var path = q.removeFirst()
      var pos = path.last()
      if (pos in goals) {
        return path[1]
      }
      for (neighbor in tiles[pos]!!) {
        if (neighbor !in blocked && neighbor !in visited) {
          visited.add(neighbor)
          q.addLast(path + neighbor)
        }
      }
    }
    return null
  }

  /** Returns true to continue the game. */
  fun runTurn(actor: Actor, tiles: Map<Vec2d, List<Vec2d>>, actors: List<Actor>): Boolean {
    if (actor.hp <= 0) {
      return true
    }
    val targets = actors.filter { it.type != actor.type }
    if (targets.isEmpty()) {
      return false
    }
    var adjacentTargets = targets.filter { it.pos in tiles[actor.pos]!! }
    if (adjacentTargets.isEmpty()) {
      val blocked = actors.map { it.pos }.toSet()
      val inRange = targets.flatMap { tiles[it.pos]!! }.filter { it !in blocked }.toSet()
      if (inRange.isEmpty()) {
        return true
      }
      // Move
      val step = shortestStep(actor.pos, inRange, tiles, blocked)
      if (step == null) {
        return true
      }
      actor.pos = step
      adjacentTargets = targets.filter { it.pos in tiles[actor.pos]!! }
    }

    // Attack
    val target = adjacentTargets.minByOrNull { it.hp }
    if (target != null) {
      target.hp -= actor.attack
    }
    return true
  }

  fun runRound(tiles: Map<Vec2d, List<Vec2d>>, actors: List<Actor>): Pair<Boolean, List<Actor>> {
    for (actor in actors.sorted()) {
      if (!runTurn(actor, tiles, actors.filter { it.hp > 0 }.sorted())) {
        return false to actors.filter { it.hp > 0 }
      }
    }
    return true to actors.filter { it.hp > 0 }
  }

  fun gameOver(actors: List<Actor>): Boolean {
    return actors.isEmpty() || actors.all { it.type == actors[0].type }
  }

  fun runGame(tiles: Map<Vec2d, List<Vec2d>>, startActors: List<Actor>): Pair<Int, List<Actor>> {
    var rounds = 0
    var actors = startActors
    while (!gameOver(actors)) {
      var result = runRound(tiles, actors)
      actors = result.second
      if (!result.first) {
        // Partial rounds don't count towards outcome!
        break
      }
      ++rounds
    }
    return rounds * actors.sumOf { it.hp } to actors
  }

  fun part1(tiles: Map<Vec2d, List<Vec2d>>, startActors: List<Actor>): Int {
    var actors = startActors.map(::Actor)
    return runGame(tiles, actors).first
  }

  fun part2(tiles: Map<Vec2d, List<Vec2d>>, startActors: List<Actor>): Int {
    val numElves = startActors.count { it.type == ActorType.ELF }
    (4..100).forEach { attack ->
      var actors = startActors.map(::Actor)
      actors.forEach {
        if (it.type == ActorType.ELF) {
          it.attack = attack
        }
      }
      var (score, endActors) = runGame(tiles, actors)
      if (endActors.size == numElves && endActors.all { it.type == ActorType.ELF }) {
        return score
      }
    }
    return -1
  }

  val (tiles, actors) = buildGrid(readInput("Day15"))
  println(part1(tiles, actors))
  println(part2(tiles, actors))
}
