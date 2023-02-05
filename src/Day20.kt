sealed interface Step

data class Move(val dir: Char): Step {}

data class Branch(val options: List<List<Step>>): Step {}

fun main() {
  val NORTH = 'N'
  val SOUTH = 'S'
  val EAST = 'E'
  val WEST = 'W'

  fun parseRe(s: String): Pair<Int, List<Step>> {
    val re = mutableListOf<Step>()
    var i = 0
    while (i < s.length) {
      var c = s[i]
      when (c) {
        '$' -> return i + 1 to re
        ')' -> return i + 1 to re
        '(' -> {
          var options = mutableListOf<List<Step>>()
          ++i
          while (s[i - 1] != ')') {
            var (size, option) = parseRe(s.drop(i))
            options.add(option)
            i += size
          }
          re.add(Branch(options))
        }
        '|' -> {
          return i + 1 to re
        }
        else -> {
          re.add(Move(c))
          i += 1
        }
      }
    }
    return i to re
  }

  fun parseRegex(s: String): List<Step> {
    return parseRe(s.drop(1)).second
  }

  fun Vec2d.move(dir: Char): Vec2d =
    when (dir) {
      NORTH -> this + Vec2d(0, 1)
      SOUTH -> this + Vec2d(0, -1)
      EAST -> this + Vec2d(1, 0)
      WEST -> this + Vec2d(-1, 0)
      else -> throw Exception("Bad dir")
    }

  fun farthestRoom(allSteps: List<Step>): Int {
    val start = Vec2d(0, 0)
    val q = ArrayDeque<Pair<List<Vec2d>, List<Step>>>()
    q.addLast(listOf(start) to allSteps)
    val visited = mutableSetOf<Vec2d>()
    visited.add(start)
    var maxSteps = 0
    while (!q.isEmpty()) {
      var (path, steps) = q.removeFirst()
      var pos = path.last()
      maxSteps = Math.max(path.size - 1, maxSteps)
      if (steps.isEmpty()) {
        continue
      }
      var step = steps.first()
      when (step) {
        is Move -> {
          pos = pos.move(step.dir)
          if (pos !in visited) {
            visited.add(pos)
            q.addLast(path + pos to steps.drop(1))
          }
        }
        is Branch -> {
          for (option in step.options) {
            q.addLast(path to option + steps.drop(1))
          }
        }
      }
    }
    return maxSteps
  }

  fun part1(input: List<Step>): Int {
    return farthestRoom(input)
  }

  fun findFarAwayRooms(allSteps: List<Step>): Int {
    val start = Vec2d(0, 0)
    val q = ArrayDeque<Pair<List<Vec2d>, List<Step>>>()
    q.addLast(listOf(start) to allSteps)
    val visited = mutableSetOf<Vec2d>()
    visited.add(start)
    var farAwayRooms = mutableSetOf<Vec2d>()
    while (!q.isEmpty()) {
      var (path, steps) = q.removeFirst()
      var pos = path.last()
      if (path.size > 1000) {
        farAwayRooms.add(pos)
      }
      if (steps.isEmpty()) {
        continue
      }
      var step = steps.first()
      when (step) {
        is Move -> {
          pos = pos.move(step.dir)
          if (pos !in visited) {
            visited.add(pos)
            q.addLast(path + pos to steps.drop(1))
          }
        }
        is Branch -> {
          for (option in step.options) {
            q.addLast(path to option + steps.drop(1))
          }
        }
      }
    }
    return farAwayRooms.size
  }

  fun part2(input: List<Step>): Int {
    return findFarAwayRooms(input)
  }

  val re = parseRegex(readInput("Day20")[0])
  // printPath(re)
  println(part1(re))
  println(part2(re))
}
