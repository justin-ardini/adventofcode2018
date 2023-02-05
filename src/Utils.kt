import java.io.File

/** Reads lines from the given txt file. */
fun readInput(name: String) = File("src", "$name.txt").readLines()
