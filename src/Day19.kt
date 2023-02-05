import kotlin.reflect.KCallable

data class Device(val reg: IntArray = intArrayOf(0, 0, 0, 0, 0, 0)) {
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

data class Instruction(val op: KCallable<Unit>, val a: Int, val b: Int, val c: Int) {
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

fun main() {
  fun parseRegisters(input: String): IntArray {
    val regex = """\[(\d), (\d), (\d), (\d)]""".toRegex()
    return regex.find(input)!!.destructured.toList().map { it.toInt() }.toIntArray()
  }

  fun parseInstruction(input: String): Instruction {
    val parts = input.split(" ")
    val op = Device::class.members.firstOrNull { it.name == parts[0] } as KCallable<Unit>
    val args = parts.drop(1).map { it.toInt() }
    return Instruction(op, args[0], args[1], args[2])
  }

  fun parseInput(input: List<String>): Pair<Int, List<Instruction>> {
    val program = input.drop(1).map { parseInstruction(it) }
    return input[0].drop(4).toInt() to program
  }

  fun runProgram(ipr: Int, program: List<Instruction>, device: Device): Int {
    var ip = 0
    while (true) {
      val instr = program.getOrNull(ip)
      if (instr == null) {
        return device.reg[0]
      }
      device.reg[ipr] = ip
      instr.op.call(device, instr.a, instr.b, instr.c)
      ip = device.reg[ipr] + 1
    }
  }

  fun part1(ip: Int, program: List<Instruction>): Int {
    val device = Device()
    return runProgram(ip, program, device)
  }

  fun sumFactors(n: Int): Int =
    (1..n).filter {n % it == 0 }.sum()

  fun part2(ipr: Int, program: List<Instruction>): Int {
    val device = Device()
    device.reg[0] = 1
    var ip = 0
    (1..100).forEach {
      val instr = program.getOrNull(ip)!!
      device.reg[ipr] = ip
      instr.op.call(device, instr.a, instr.b, instr.c)
      ip = device.reg[ipr] + 1
    }
    return sumFactors(device.reg[2])
  }

  val (ip, program) = parseInput(readInput("Day19"))
  println(part1(ip, program))
  println(part2(ip, program))
}
