val month: Int = 1
val months =
    listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
class Ship(
  var cannons: Int,
  var health: Int,
  var cargoUnits: Int,
  var hold: Int,
  var Opium: Int,
  var Silk: Int,
  var Arms: Int,
  var General: Int
)
class Player(
  var bank: Int,
  var cash: Int,
  var debt: Int,
  var location: String
)
class Prices(
  var Opium: Int,
  var Silk: Int,
  var Arms: Int,
  var General: Int,
  var Type: String
)
class Warehouse(
  var Opium: Int,
  var Silk: Int,
  var Arms: Int,
  var General: Int,
  var inUse: Int,
  var vacant: Int 
)
class GameAttributes(
  var month: Int,
  var yearTime: Int,
  var monthLabel: String,
  var eventChanceSea: Double,
  var eventChancePort: Double,
  var liYuenFactor: Double,
  var liYuenExtortionFactor: Double,
  var liYuenMultiplier: Double,
  var status: String
)

var locationsMap = mutableMapOf(
  "1" to "Hong Kong",
  "2" to "Shanghai",
  "3" to "Nagasaki",
  "4" to "Saigon",
  "5" to "Manila",
  "6" to "Singapore",
  "7" to "Batavia"
)
fun main() {
  println("Welcome to Taipan!")
  var gameAttributes = GameAttributes(1, 1860, months[month - 1], 0.5, 0.25, 0.5, 0.8, 1.0, "Running")
  var player = Player(0, 500, 5000, "Hong Kong")
  var warehouse = Warehouse(0, 0, 0, 0, 0, 10000)
  var prices = Prices(0, 0, 0, 0, "Regular")
  var ship = Ship(5, 100, 150, 100, 0, 0, 0, 0)
  while (gameAttributes.status != "Terminated") {
    if (player.location == "Hong Kong") {

    }
  }
}

fun time() {

}

fun priceGenerator(max: Int) {

}

fun getRandomInt(min: Int, max: Int) {

}

fun pirateGenerator(min: Int, max: Int) {

}

fun priceDisplay() {

}

fun priceRandomDisplay(product: String) {

}

fun generalPrompt() {

}

fun buyHandler(product: String) {

}

fun buy() {

}

fun sellHandler(product: String) {

}

fun sell() {

}

fun visitBank() {

}

fun transferCargoHandlerToWarehouse(product: String) {

}

fun transferCargoHandlerToShip(product: String) {

}

fun transferCargo() {

}

fun handleLocationInput(input: String) {

}

fun quitTrading() {

}

fun retire() {

}

fun compareRange(variable: Int, n1: Int, n2: Int) {

}

fun finalStats() {

}

fun turnProgression() {

}

fun eventSea(status: String) {

}

fun LiYuen() {

}

fun eventPort() {

}

fun newShip() {

}

fun moreGuns() {

}

fun opiumConfiscationChance() {

}

fun randomPrice() {

}

fun shipyard() {

}

fun pirates(type: String, number: Int) {

}

fun pirateHealthGenerator(pirateResistanceCoefficient: Double) {

}

fun damageToPirateShip() {

}

fun combat(damageCoefficient: Double, gunKnockoutChance: Double, number: Double, pirateResistanceCoefficient: Double) {

}

fun storm() {

}

fun moneylender() {
  
}