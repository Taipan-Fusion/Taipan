import kotlin.math.floor
import kotlin.math.ceil
import turnProgression
import warehouse
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
var prices = mutableMapOf(
  "Opium" to 0,
  "Silk" to 0,
  "Arms" to 0,
  "General" to 0,
  "Type" to "Regular"
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
var gameAttributes = GameAttributes(1, 1860, months[month - 1], 0.5, 0.25, 0.5, 0.8, 1.0, "Running")
var player = Player(0, 500, 5000, "Hong Kong")
var warehouse = Warehouse(0, 0, 0, 0, 0, 10000)
var ship = Ship(5, 100, 150, 100, 0, 0, 0, 0)
fun main() {
  println("Welcome to Taipan!")
  while (gameAttributes.status != "Terminated") {
    if (player.location == "Hong Kong") {
      if (ship.health < 100) {
        shipyard()
      }
      if ((0..1).random() <= gameAttributes.liYuenExtortionFactor) {
        LiYuen()
      }
      gameAttributes.liYuenExtortionFactor += 0.01
      moneylender()
    }
    if ((0..1).random() <= gameAttributes.eventChancePort) {
      eventPort()
    }
    prices["Type"] = "Regular"
    if ((0..1).random() <= 0.1) {
      randomPrice()
      prices.Type = "Random"
    }
    if (prices.Type == "Regular") {
      priceDisplay()
    }
    generalPrompt()
    if (gameAttributes.status == "Terminated") {
      break
    }
    var status = true
    if ((0..1).random() <= gameAttributes.liYuenFactor) {
      var number: Int = pirateGenerator(1 + floor(gameAttributes.month / 4.0).toDouble() + floor(ship.cargoUnits / 50.0), 10 + 2 * (floor(gameAttributes.month / 4.0) + floor(ship.cargoUnits / 50.0)))
      println(number.toString() + " ships from Li Yuen's private fleet, Taipan!")
      pirates("Li Yuen", number)
      gameAttributes.liYuenMultiplier = ((0..1).random() + 1) * 1.5
      status = false
    }
    if ((0..1).random() <= gameAttributes.eventChanceSea) {
      eventSea(status)
    }
    turnProgression()
  }
  println("Game Terminated.")
}

fun time(): Double {
  return (gameAttributes.month * 0.0001) + 1
}

fun priceGenerator(max: Int): Int {
  fun getRandomInt(min: Int, max: Int): Int {
    var newMin = ceil(min.toDouble())
    var newMax = floor(max.toDouble())
    return (floor((0..1).random() * time() * (newMax - newMin + 1))).toInt() + newMin.toInt() 
  }
  return getRandomInt(5, 25) * max
}

fun getRandomInt(min: Double, max: Double): Int {
  var newMin = ceil(min)
  var newMax = floor(max)
  return floor((0..1).random().toDouble() * (newMax - newMin).toDouble() + newMin.toDouble()).toInt()
}

fun pirateGenerator(min: Double, max: Double): Int {
  fun getRandomInt(min: Double, max: Double): Int {
    var newMin = ceil(min.toDouble())
    var newMax = floor(max.toDouble())
    return (floor((0..1).random() * time() * (newMax - newMin + 1))).toInt() + newMin.toInt()
  }

  return getRandomInt(min, max)
}

fun priceDisplay() {
  prices.Opium = priceGenerator(1000)
  prices.Silk = priceGenerator(100)
  prices.Arms = priceGenerator(10)
  prices.General = priceGenerator(1)
}

fun priceRandomDisplay(product: String) {
  prices.Type = "Random"
  if (product == "Opium") {
    prices.Silk = priceGenerator(100)
    prices.Arms = priceGenerator(10)
    prices.General = priceGenerator(1)
  } else if (product == "Silk") {
    prices.Opium = priceGenerator(1000)
    prices.Arms = priceGenerator(10)
    prices.General = priceGenerator(1)
  } else if (product == "Arms") {
    prices.Silk = priceGenerator(100)
    prices.Opium = priceGenerator(1000)
    prices.General = priceGenerator(1)
  } else {
    prices.Silk = priceGenerator(100)
    prices.Opium = priceGenerator(1000)
    prices.Arms = priceGenerator(10)
  }
}

fun generalPrompt() {
  while (true) {
    println("Player---------------------------Player")
    println("Bank: " + player.bank.toString())
    println("Cash: " + player.cash.toString())
    println("Debt: " + player.cash.toString())
    println("Location: " + player.location.toString())
    println("Date: " + gameAttributes.monthLabel + " of " + gameAttributes.yearTime.toString())
    println("Ship---------------------------Ship")
    println("Cannons: " + ship.cannons.toString())
    println("Health: " + ship.health.toString())
    println("Units: " + ship.cargoUnits.toString())
    println("Hold: " + ship.hold.toString())
    println("Opium: " + ship.Opium.toString())
    println("Silk: " + ship.Silk.toString())
    println("Arms: " + ship.Arms.toString())
    println("General: " + ship.General.toString())
    println("Warehouse---------------------------Warehouse")
    println("Opium: " + warehouse.Opium.toString())
    println("Silk: " + warehouse.Silk.toString())
    println("Arms: " + warehouse.Arms.toString())
    println("General: " + warehouse.General.toString())
    println("In Use: " + warehouse.inUse.toString())
    println("Vacant: " + warehouse.vacant.toString())
    println("Prices-----------------------------Prices")
    println("Taipan, prices per unit here are:")
    println("Opium: " + prices.Opium.toString() + "\t" + "Silk: " + prices.Silk.toString())
    println("Arms: " + prices.Arms.toString() + "\t" + "General: " + prices.General.toString())
    if (player.location == "Hong Kong") {
      if (player.bank + player.cash >= 1000000) {
        print("Shall I Buy, Sell, Visit Bank, Transfer Cargo, Quit Trading, or Retire? ")
        val input: String? = readLine()
        if (input === "b") {
          buy()
        } else if (input === "s") {
          sell()
        } else if (input === "v") {
          visitBank()
        } else if (input === "t") {
          transferCargo()
        } else if (input === "q") {
          if (ship.hold < 0) {
            println("Your ship will be overburdened, Taipan!")
          } else {
            quitTrading()
            break
          }
        } else if (input === "r") {
          retire()
          break
        } else {

        }
      } else {
        print("Shall I Buy, Sell, Visit Bank, Transfer Cargo, or Quit Trading? ")
        val input: String? = readLine()
        if (input === "b") {
          buy()
        } else if (input === "s") {
          sell()
        } else if (input === "v") {
          visitBank()
        } else if (input === "t") {
          transferCargo()
        } else if (input === "q") {
          if (ship.hold < 0) {
            println("Your ship will be overburdened, Taipan!")
          } else {
            quitTrading()
            break
          }
        } else {

        }
      }
    } else {
      print("Shall I Buy, Sell, or Quit Trading? ")
      val input: String? = readLine()
      if (input === "b") {
        buy()
      } else if (input === "s") {
        sell()
      } else if (input === "q") {
        if (ship.hold < 0) {
          println("Your ship will be overburdened, Taipan!")
        } else {
          quitTrading()
          break
        }
      } else {

      }
    }
  }
}

fun buyHandler(product: String) {
  while (true) {
    val affordableNumber: Int = floor(player.cash / prices.product).toInt()
  }
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

fun eventSea(status: Boolean) {

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