fun main() {
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

  data class Instruction(val op: Device.(Int, Int, Int) -> Unit, val a: Int, val b: Int, val c: Int) {
  }

  val fnsByName: Map<String, Device.(Int, Int, Int) -> Unit> = mapOf(
    "addr" to Device::addr,
    "addi" to Device::addi,
    "mulr" to Device::mulr,
    "muli" to Device::muli,
    "banr" to Device::banr,
    "bani" to Device::bani,
    "borr" to Device::borr,
    "bori" to Device::bori,
    "setr" to Device::setr,
    "seti" to Device::seti,
    "gtir" to Device::gtir,
    "gtri" to Device::gtri,
    "gtrr" to Device::gtrr,
    "eqir" to Device::eqir,
    "eqri" to Device::eqri,
    "eqrr" to Device::eqrr)

  fun parseRegisters(input: String): IntArray {
    val regex = """\[(\d), (\d), (\d), (\d)]""".toRegex()
    return regex.find(input)!!.destructured.toList().map { it.toInt() }.toIntArray()
  }

  fun parseInstruction(input: String): Instruction {
    val parts = input.split(" ")
    val op = fnsByName[parts[0]]!!
    val args = parts.drop(1).map { it.toInt() }
    return Instruction(op, args[0], args[1], args[2])
  }

  fun parseInput(input: List<String>): Pair<Int, List<Instruction>> {
    val program = input.drop(1).map { parseInstruction(it) }
    return input[0].drop(4).toInt() to program
  }

  fun findMagicNumbers(ipr: Int, program: List<Instruction>, device: Device): Set<Int> {
    var ip = 0
    val nums = linkedSetOf<Int>()
    while (true) {
      val instr = program.getOrNull(ip)
      if (instr == null) {
        return nums
      }
      if (instr.op == Device::eqrr) {
        // b is register 0 in my input, this is not a generalized solution!
        var v = device.reg[instr.a]
        if (v in nums) {
          return nums
        }
        nums.add(v)
      }
      device.reg[ipr] = ip
      device.(instr.op)(instr.a, instr.b, instr.c)
      ip = device.reg[ipr] + 1
    }
    return nums
  }

  fun parts1And2(ip: Int, program: List<Instruction>): Pair<Int, Int> {
    val nums = findMagicNumbers(ip, program, Device())
    return nums.first() to nums.last()
  }

  val (ip, program) = parseInput(readInput("Day21"))
  println(parts1And2(ip, program))
}
