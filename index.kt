import kotlin.random.Random
import kotlin.math.roundToInt*

class Game {
    enum class Location(val location: String) {
        HongKong  ("Hong Kong"),
        Shanghai  ("Shanghai"),
        Nagasaki  ("Nagasaki"),
        Saigon    ("Saigon"),
        Manila    ("Manila"),
        Singapore ("Singapore"),
        Batavia   ("Batavia")
    }

    data class Commodities(
        var Opium:    Int,
        var Silk:     Int,
        var Arms:     Int,
        var General:  Int
    )

    object Ship {
        var cannons:      Int = 5
        var health:       Int = 100
        var cargoUnits:   Int = 150
        var hold:         Int = 100
        val commodities:  Commodities = Commodities(0, 0, 0, 0)
    }

    object Player {
        var moneyInBank:  Int = 0
        var cashHoldings: Int = 500
        var debt:         Int = 5000
        var location:     Location = Location.HongKong
    }

    object Prices {
        var commodities:  Commodities = Commodities(0, 0, 0, 0)
        var isRandom:     Boolean = false
    }

    object Warehouse {
        var commodities:        Commodities = Commodities(0, 0, 0, 0)
        var vacantCargoSpaces:  Int = 10000
        val totalCargoSpaces:   Int = 10000
    }

    object LiYuen {
        var chanceOfAttack:       Double = 0.5
        var chanceOfExtortion:    Double = 0.8
        var extortionMultiplier:  Double = 1.0

        public fun becomePainInTheAss() {
            // TODO
        }
    }

    /**************************************************************************/

    val monthNames = listOf<String>("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    var month:              Int = 0
    var year:               Int = 1860
    var chanceOfSeaEvent:   Double = 0.5
    var chanceOfPortEvent:  Double = 0.25
    var isRunning:          Boolean = true

    val monthName: String
        get() = monthNames[month]

    val globalMultiplier: Double
        get() = 1.0 + month / 10000

    /**************************************************************************/
    
    fun priceGenerator(max: Int): Int {
        return ((5..25).random().toDouble() * max.toDouble() * globalMultiplier).roundToInt()
    }

    fun pirateGenerator(min: Int, max: Int): Int {
        return ((min..max).random().toDouble() * globalMultiplier).roundToInt()
    }

    fun priceDisplay() {
        Prices.commodities = Commodities(priceGenerator(1000), priceGenerator(100), priceGenerator(10), priceGenerator(1))
    }

    fun randomPriceDisplay(product: String) {
        // TODO
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
        // TODO
    }

    fun buy() {
        // TODO
    }

    fun sellHandler(product: String) {
        // TODO
    }

    fun sell() {
        // TODO
    }

    fun visitBank() {
        // TODO
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

    fun eventAtSea() {

    }

    fun LiYuen() {

    }

    fun eventAtPort() {

    }

    fun newShip() {

    }

    fun moreGuns() {

    }

    fun opiumConfiscationChance() {
        // Could rewrite to get format
    }

    fun randomPriceGenerator() {

    }

    fun shipyard() {
        val shipIstTotScalar: Double = 1 + (1 - (100 - Ship.health) / 100.0)
        val shipPrice: Int =
                (Random.nextInt(1, Ship.cargoUnits) * shipIstTotScalar * globalMultiplier * (1..5).random())
                        .roundToInt()
        println("Captain McHenry of the Hong Kong Consolidated Repair Corporation walks over to your ship and says: <<")
        if (Ship.health < 30) {
            println("Matey! That ship of yours is 'bout to rot away like a peice of driftwood in Kolwoon bay! Dont worry, it's nothing I cant fix. For a price, that is!")
        } else if (Ship.health < 50) {
            println("That there ship's taken quite a bit of damage matey! You best get it fixed before you go out to sea again! I can get you sailing the friendly waves in no time! For a price, that is!")
        } else {
            println("What a mighty fine ship you have there, matey! Or, shall I say, had... It could really use some of what I call 'Tender Love n' Care'. 'Tis but a scratch, as they say, but I take any job, no matter how small. For a price, that is!")
        }
        println("I'll fix you up to full workin' order for " + shipPrice.toString() + " pound sterling>>")
        println("Taipan, how much will you pay Captain McHenry? You have " + Player.cashHoldings.toString() + " pound sterling on hand.")
        // TODO: Take user input and proceed accordingly
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

    /**************************************************************************/

    public fun run() {
  
                    
        println("Welcome to Taipan!")

        while (isRunning) {
            if (Player.location == Location.HongKong) {
                if (Ship.health < 100) {
                    // TODO Shipyard, put into shipyard function later
                    shipyard()
                }

                if (Random.nextDouble() <= LiYuen.chanceOfExtortion) {
                    LiYuen.becomePainInTheAss()
                }

                LiYuen.chanceOfExtortion += 0.01

                // TODO Money lender
            }

            if (Random.nextDouble() <= chanceOfPortEvent) {
                // TODO Port event
            }

            Prices.isRandom = false

            if (Random.nextDouble() <= 0.1) {
                // TODO Random price
                Prices.isRandom = true
                // TODO Random price display
            } else {
                // TODO Price display
            }

            // TODO General prompt

            if (!isRunning) {
                break
            }

            // TODO Sea event
            // TODO Turn progression
        }

        println("Game terminated.")
    }
}
