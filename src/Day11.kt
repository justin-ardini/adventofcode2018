fun main() {
  val SIZE = 300

  fun powerLevel(x: Int, y: Int, serialNumber: Int): Int {
    val rackId = x + 10
    var power = y * rackId + serialNumber
    power *= rackId
    return (power % 1000 - power % 100) / 100 - 5
  }

  fun buildGrid(serialNumber: Int): List<List<Int>> =
    (1..SIZE).map {
      x -> (1..SIZE).map {
        y -> powerLevel(x, y, serialNumber)
      }.toList()
    }.toList()

  fun sum3x3(grid: List<List<Int>>, x: Int, y: Int): Int =
    (x-1..x+1).sumOf {
      sx -> (y-1..y+1).sumOf {
        sy -> grid.getOrNull(sx)?.getOrNull(sy) ?: 0
      }
    }

  fun part1(input: Int): String {
    val grid = buildGrid(input)
    return (1..SIZE-3).maxOfWith(
      compareBy { it.first },
      { x -> (1..SIZE-3).maxOfWith(
        compareBy { it.first },
        { y -> sum3x3(grid, x, y) to (x to y) })}
    ).second.toList().joinToString(separator=",")
  }

  fun buildSummedAreas(grid: List<List<Int>>): List<List<Int>> {
    val summedAreas = mutableListOf<List<Int>>()
    (0..SIZE-1).forEach {
      x ->
        val row = mutableListOf<Int>()
        (0..SIZE-1).forEach {
          y -> row.add(grid[x][y] + (row.getOrNull(y-1) ?: 0) +
            (summedAreas.getOrNull(x-1)?.getOrNull(y) ?: 0) - (summedAreas.getOrNull(x-1)?.getOrNull(y-1) ?: 0))
        }
        summedAreas.add(row)
    }
    return summedAreas
  }

  fun getArea(areas: List<List<Int>>, x: Int, y: Int, xn: Int, yn: Int): Int =
    (areas.getOrNull(x-1)?.getOrNull(y-1) ?: 0) + areas[x+xn-1][y+yn-1] -
      (areas[x+xn-1].getOrNull(y-1) ?: 0) - (areas.getOrNull(x-1)?.getOrNull(y+yn-1) ?: 0)

  fun maxArea(areas: List<List<Int>>, size: Int): Pair<Int, Pair<Int, Int>> =
    (0..SIZE-size).maxOfWith(
      compareBy { it.first },
      { x -> (0..SIZE-size).maxOfWith(
        compareBy { it.first },
        { y -> getArea(areas, x, y, size, size) to (x+1 to y+1) })}
    )

  fun part2(input: Int): String {
    val grid = buildGrid(input)
    val areas = buildSummedAreas(grid)
    val best = (1..SIZE).maxOfWith(
      compareBy { it.first.first },
      { s -> maxArea(areas, s) to s }
    )
    return mutableListOf(best.first.second.first, best.first.second.second, best.second).joinToString(separator=",")
  }

  val input = readInput("Day11").get(0).toInt()
  println(part1(input))
  println(part2(input))
}
