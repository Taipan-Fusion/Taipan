val month: Int = 1
val months =
    listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
val ship = mutableMapOf(
  "cannons" to 5,
  "health" to 100,
  "cargoUnits" to 150,
  "hold" to 100,
  "Opium" to 0,
  "Silk" to 0,
  "Arms" to 0,
  "General" to 0
)
val player = mutableMapOf(
  "bank" to 0,
  "cash" to 500,
  "debt" to 5000,
  "location" to "Hong Kong"
)
val test = player["debt"]

val prices = mutableMapOf(
  "Opium" to 0,
  "Silk" to 0,
  "Arms" to 0,
  "General" to 0,
  "Type" to "Regular"
)

val warehouse = mutableMapOf(
  "Opium" to 0,
  "Silk" to 0,
  "Arms" to 0,
  "General" to 0,
  "inUse" to 0,
  "vacant" to 10000
)

val gameAttributes = mutableMapOf(
  "month" to 1,
  "yearTime" to 1860,
  "monthLabel" to months[month - 1],
  "eventChanceSea" to 0.5,
  "eventChancePort" to 0.25,
  "liYuenFactor" to 0.5,
  "liYuenExtortionFactor" to 0.8,
  "liYuenMultiplier" to 1,
  "status" to "Running"
)
fun main() {
  println("Welcome to Taipan!")
}
