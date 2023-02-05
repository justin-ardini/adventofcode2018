fun main() {
  data class Device(val reg: IntArray = intArrayOf(0, 0, 0, 0)) {
    fun addr(a: Int, b: Int, c: Int) {
      reg[c] = reg[a] + reg[b]
    }

    fun addi(a: Int, b: Int, c: Int) {
      reg[c] = reg[a] + b
    }

    fun mulr(a: Int, b: Int, c: Int) {
      reg[c] = reg[a] * reg[b]
    }

    fun muli(a: Int, b: Int, c: Int) {
      reg[c] = reg[a] * b
    }

    fun banr(a: Int, b: Int, c: Int) {
      reg[c] = reg[a] and reg[b]
    }

    fun bani(a: Int, b: Int, c: Int) {
      reg[c] = reg[a] and b
    }

    fun borr(a: Int, b: Int, c: Int) {
      reg[c] = reg[a] or reg[b]
    }

    fun bori(a: Int, b: Int, c: Int) {
      reg[c] = reg[a] or b
    }

    fun setr(a: Int, @Suppress("UNUSED_PARAMETER") b: Int, c: Int) {
      reg[c] = reg[a]
    }

    fun seti(a: Int, @Suppress("UNUSED_PARAMETER") b: Int, c: Int) {
      reg[c] = a
    }

    fun gtir(a: Int, b: Int, c: Int) {
      reg[c] = if (a > reg[b]) 1 else 0
    }

    fun gtri(a: Int, b: Int, c: Int) {
      reg[c] = if (reg[a] > b) 1 else 0
    }

    fun gtrr(a: Int, b: Int, c: Int) {
      reg[c] = if (reg[a] > reg[b]) 1 else 0
    }

    fun eqir(a: Int, b: Int, c: Int) {
      reg[c] = if (a == reg[b]) 1 else 0
    }

    fun eqri(a: Int, b: Int, c: Int) {
      reg[c] = if (reg[a] == b) 1 else 0
    }

    fun eqrr(a: Int, b: Int, c: Int) {
      reg[c] = if (reg[a] == reg[b]) 1 else 0
    }
  }

  data class Instruction(val opcode: Int, val a: Int, val b: Int, val c: Int) {
  }

  data class TestCase(val before: IntArray, val instruction: Instruction, val after: IntArray) {
  }

  val fns: List<Device.(Int, Int, Int) -> Unit> = listOf(
    Device::addr,
    Device::addi,
    Device::mulr,
    Device::muli,
    Device::banr,
    Device::bani,
    Device::borr,
    Device::bori,
    Device::setr,
    Device::seti,
    Device::gtir,
    Device::gtri,
    Device::gtrr,
    Device::eqir,
    Device::eqri,
    Device::eqrr)

  fun parseRegisters(input: String): IntArray {
    val regex = """\[(\d), (\d), (\d), (\d)]""".toRegex()
    return regex.find(input)!!.destructured.toList().map { it.toInt() }.toIntArray()
  }

  fun parseInstruction(input: String): Instruction {
    val parts = input.split(" ").map { it.toInt() }
    return Instruction(parts[0], parts[1], parts[2], parts[3])
  }

  fun parseTestCase(input: List<String>): TestCase {
    val before = parseRegisters(input[0])
    val instruction = parseInstruction(input[1])
    val after = parseRegisters(input[2])
    return TestCase(before, instruction, after)
  }

  fun parseInput(input: List<String>): Pair<List<TestCase>, List<Instruction>> {
    val testCases = input.chunked(4).takeWhile { it[0].startsWith("Before") }
        .map { parseTestCase(it) }
    val program = input.drop(4 * testCases.size + 2).map { parseInstruction(it) }
    return testCases to program
  }

  /** Returns matching opcodes for the given test. */
  fun findOpcodes(t: TestCase): List<Int> =
    fns.mapIndexedNotNull { i, fn ->
      val device = Device(t.before.copyOf() )
      device.fn(t.instruction.a, t.instruction.b, t.instruction.c)
      if (device.reg.contentEquals(t.after)) i else null
    }

  /** Returns matching (opcode to index) pairs for the given test. */
  fun findOpcodes(t: TestCase, unsolved: Set<Int>): List<Pair<Int, Int>?> =
    unsolved.mapNotNull { i ->
      val device = Device(t.before.copyOf())
      device.(fns[i])(t.instruction.a, t.instruction.b, t.instruction.c)
      if (device.reg.contentEquals(t.after)) t.instruction.opcode to i else null
    }

  fun part1(testCases: List<TestCase>): Int {
    val unsolved = List(16) { it }.toMutableSet()
    return testCases.map { findOpcodes(it, unsolved) }.count { it.size >= 3 }
  }

  fun part2(allTestCases: List<TestCase>, program: List<Instruction>): Int {
    val unsolved = List(16) { it }.toMutableSet()
    val solved = arrayOfNulls<Int>(16)
    var testCases = allTestCases
    (1..16).forEach {
      val next = testCases.map { findOpcodes(it, unsolved) }
          .find { it.size == 1 }?.get(0)
      if (next == null) {
        return -1
      }
      solved[next.first] = next.second
      unsolved.remove(next.second)
      testCases = testCases.filterNot { it.instruction.opcode == next.first }
    }
    val device = Device()
    program.forEach {
      val op = fns[solved[it.opcode]!!]
      device.op(it.a, it.b, it.c)
    }
    return device.reg[0]
  }

  val (testCases, program) = parseInput(readInput("Day16"))
  println(part1(testCases))
  println(part2(testCases, program))
}
