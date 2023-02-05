enum class DamageType {
  BLUDGEONING,
  COLD,
  FIRE,
  RADIATION,
  SLASHING,
}

data class UnitGroup(var size: Int, val hp: Int, val attack: Int, val attackType: DamageType, val initiative: Int,
    val immunities: List<DamageType>, val weaknesses: List<DamageType>): Comparable<UnitGroup> {

  var target: UnitGroup? = null

  fun power(): Int = size * attack

  /** Does not mutate */
  fun damageTo(other: UnitGroup): Int {
    if (attackType in other.immunities) {
      return 0
    }
    if (attackType in other.weaknesses) {
      return 2 * this.power()
    }
    return this.power()
  }

  /** DOES mutate */
  fun takeDamageFrom(attacker: UnitGroup) {
    val damage = attacker.damageTo(this)
    size -= damage / hp
  }

  override fun compareTo(other: UnitGroup): Int {
    val powerDiff = other.power() - power()
    if (powerDiff != 0) {
      return powerDiff
    }
    return other.initiative - initiative
  }

  companion object {
    val damageTypes = mapOf(
      "bludgeoning" to DamageType.BLUDGEONING,
      "cold" to DamageType.COLD,
      "fire" to DamageType.FIRE,
      "radiation" to DamageType.RADIATION,
      "slashing" to DamageType.SLASHING,
    )
    val PATTERN = """^(\d+) units each with (\d+) hit points (?:\(([\w,; ]+)\) )?with an attack that does (\d+) (\w+) damage at initiative (\d+)$""".toRegex()

    private fun parseModifiers(s: String): List<DamageType> =
      when {
        s.startsWith("i") -> s.drop("immune to ".length).split(", ").map { damageTypes[it]!! }
        s.startsWith("w") -> s.drop("weak to ".length).split(", ").map { damageTypes[it]!! }
        else -> listOf<DamageType>()
      }

    fun parse(s: String): UnitGroup {
        val (size, hp, modifiers, attack, attackType, initiative) = PATTERN.matchEntire(s)!!.destructured
        val modList = modifiers.split("; ")
        var immunities = ""
        var weaknesses = ""
        if (modifiers.startsWith("i")) {
          immunities = modList[0]
          if (modList.size == 2) {
            weaknesses = modList[1]
          }
        } else if (modifiers.startsWith("w")) {
          weaknesses = modList[0]
          if (modList.size == 2) {
            immunities = modList[1]
          }
        }
        return UnitGroup(size.toInt(), hp.toInt(), attack.toInt(), damageTypes[attackType]!!, initiative.toInt(), parseModifiers(immunities), parseModifiers(weaknesses))
    }
  }
}

fun main() {
  fun pickTargets(attackers: Set<UnitGroup>, allDefenders: Set<UnitGroup>) {
    val defenders = allDefenders.toMutableList()
    for (attacker in attackers) {
      var targetPair = defenders.mapIndexedNotNull { i, defender ->
        val damage = attacker.damageTo(defender)
        if (damage > 0) i to damage else null
      }.maxByOrNull { it.second }
      if (targetPair == null) {
        attacker.target = null
        continue
      }
      attacker.target = defenders.removeAt(targetPair.first)
    }
  }

  fun attack(immunity: Set<UnitGroup>, infection: Set<UnitGroup>) {
    val attackers = (immunity.union(infection)).toSortedSet(
      compareBy { -it.initiative })
    for (attacker in attackers) {
      if (attacker.target != null) {
        attacker.target!!.takeDamageFrom(attacker)
      }
    }
  }

  fun runRound(immunity: Set<UnitGroup>, infection: Set<UnitGroup>): Pair<Set<UnitGroup>, Set<UnitGroup>> {
    pickTargets(immunity, infection)
    pickTargets(infection, immunity)
    attack(immunity, infection)
    return immunity.filter { it.size > 0 }.toSortedSet() to infection.filter { it.size > 0 }.toSortedSet()
  }

  fun gameOver(immunity: Set<UnitGroup>, infection: Set<UnitGroup>): Boolean =
    immunity.isEmpty() || infection.isEmpty()

  fun playGame(immunityGroups: List<UnitGroup>, infectionGroups: List<UnitGroup>): Pair<Boolean, Int> {
    var immunity: Set<UnitGroup> = immunityGroups.map { it.copy() }.toSortedSet()
    var infection: Set<UnitGroup> = infectionGroups.map { it.copy() }.toSortedSet()
    var immunityHp = immunity.sumOf { it.size }
    var infectionHp = infection.sumOf { it.size }
    while (!gameOver(immunity, infection)) {
      var (nextImmunity, nextInfection) = runRound(immunity, infection)
      var nextImmunityHp = nextImmunity.sumOf { it.size }
      var nextInfectionHp = nextInfection.sumOf { it.size }
      if (immunityHp == nextImmunityHp && infectionHp == nextInfectionHp) {
        // If no damage is done, game ends early in a draw.
        return false to -1
      }
      immunity = nextImmunity
      infection = nextInfection
      immunityHp = nextImmunityHp
      infectionHp = nextInfectionHp
    }
    return if (immunity.isEmpty()) false to infection.sumOf { it.size } else true to immunity.sumOf { it.size }
  }

  fun part1(immunity: List<UnitGroup>, infection: List<UnitGroup>): Int {
    return playGame(immunity, infection).second
  }

  fun part2(immunity: List<UnitGroup>, infection: List<UnitGroup>): Int {
    for (boost in (1..1_000_000)) {
      var boostedImmunity = immunity.map { it.copy(attack = it.attack + boost) }
      var (isWin, units) = playGame(boostedImmunity, infection)
      if (isWin) {
        return units
      }
    }
    return -1
  }

  val lines = readInput("Day24")
  val immunity = lines.slice(1..10).map { UnitGroup.parse(it) }
  val infection = lines.slice(13..22).map { UnitGroup.parse(it) }
  println(part1(immunity, infection))
  println(part2(immunity, infection))
}
