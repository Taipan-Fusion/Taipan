package taipan

import kotlin.math.roundToInt
import kotlin.random.Random

enum class Location {
    HongKong, Shanghai, Nagasaki, Saigon, Manila, Singapore, Batavia
}

enum class Commodity {
    Opium, Silk, Arms, General;

    companion object {
        fun fromAbbreviation(input: String): Commodity =
            when (input) {
                "o" -> Opium
                "s" -> Silk
                "a" -> Arms
                "g" -> General
                else -> throw UnknownAbbreviationException("")
            }
    }

    class UnknownAbbreviationException (message: String): Exception(message)
}

object Ship {
    var cannons = 5
    var health = 100
    var cargoUnits = 150
    var hold = 100
    val commodities = mutableMapOf(
        Commodity.Opium to 0,
        Commodity.Silk to 0,
        Commodity.Arms to 0,
        Commodity.General to 0
    )
}

object Player {
    var moneyInBank = 0
    var cash = 500
    var debt = 5000
    var location = Location.HongKong
}

object Prices {
    var commodities = mutableMapOf(
        Commodity.Opium to 0,
        Commodity.Silk to 0,
        Commodity.Arms to 0,
        Commodity.General to 0
    )
    var isRandom: Boolean = false
}

object Warehouse {
    var commodities: MutableMap<Commodity, Int> = mutableMapOf(
        Commodity.Opium to 0,
        Commodity.Silk to 0,
        Commodity.Arms to 0,
        Commodity.General to 0
    )
    var vacantCargoSpaces = 10000
    const val totalCargoSpaces = 10000

    val occupiedCargoSpaces: Int
        get() = totalCargoSpaces - vacantCargoSpaces
}

object LiYuen {
    var chanceOfAttack = 0.5
    var chanceOfExtortion = 0.8
    var extortionMultiplier = 1.0

    fun becomePainInTheAss() {
        // TODO
    }
}

val monthNames = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
var month = 0
var year = 1860
var chanceOfSeaEvent = 0.5
var chanceOfPortEvent = 0.25
var isRunning = true

// Originally gameAttributes.monthLabel
val monthName: String
    get() = monthNames[month]

// Originally time()
val globalMultiplier: Double
    get() = 1.0 + month / 10000

/**
 * Repeatedly takes user input until [handler] returns false. That is, [handler] should return whether to continue looping.
 */
fun inputLoop(prompt: String, handler: (String) -> Boolean) {
    while (true) {
        if (!handler(input(prompt))) break
    }
}

fun input(prompt: String): String {
    print("$prompt ")
    return readLine()!!
}

fun priceGenerator(max: Int): Int =
    (
        (5..25)
            .random()
            .toDouble()
        * max.toDouble()
        * globalMultiplier
    ).roundToInt()

fun pirateGenerator(min: Int, max: Int): Int =
    (
        (min..max)
            .random()
            .toDouble()
        * globalMultiplier
    ).roundToInt()

fun randomPriceGenerator() {

}

fun exchangeHandler(buying: Boolean) {
    val actionString = if (buying) "buy" else "sell"

    inputLoop ("What do you wish to $actionString, Taipan? ") whichCommodity@{ commodity ->
        try {
            val product                     = Commodity.fromAbbreviation(commodity)
            val priceOfProduct              = Prices.commodities[product]!!
            val directionMultiplier         = if(buying) +1 else -1
            val numberOfProductsAffordable  = Player.cash / priceOfProduct

            inputLoop (
                "How many units of ${product.name} do you want to $actionString?"
                        + if(buying) "You can afford $numberOfProductsAffordable" else ""
                        + "."
            ) howMany@{ numberOfProductsToTransferStr ->
                try {
                    val numberOfProductsToTransfer = numberOfProductsToTransferStr.toInt()

                    if (numberOfProductsToTransfer >= 0) {
                        Ship.commodities[product] = Ship.commodities[product]!! + directionMultiplier * numberOfProductsToTransfer
                        Player.cash -= directionMultiplier * numberOfProductsToTransfer * priceOfProduct
                        Ship.hold -= directionMultiplier * numberOfProductsToTransfer
                    }

                    return@howMany false
                } catch (_: NumberFormatException) {}

                return@howMany true
            }

            return@whichCommodity false
        } catch (_: Commodity.UnknownAbbreviationException) {}

        return@whichCommodity true
    }
}

fun transferCargoHandler(product: Commodity, toWarehouse: Boolean) {
    val directionMultiplier = if(toWarehouse) +1 else -1
    val actionString = if(toWarehouse) "to the warehouse" else "aboard ship"

    // TODO
}

fun pirates(type: String, number: Int) {

}

/**************************************************************************/

fun main(args: Array<String>) {
    println("Welcome to Taipan!")

    while (isRunning) {
        if (Player.location == Location.HongKong) {
            if (Ship.health < 100) {
                /* Shipyard */
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
                println("I'll fix you up to full workin' order for $shipPrice pound sterling>>")
                println("Taipan, how much will you pay Captain McHenry? You have ${Player.cash} pound sterling on hand.")
                // TODO: Take user input and proceed accordingly
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

        // Main loop
        while (true) {
            // Display all known information.
            println("Player---------------------------Player")
            println("Bank: ${Player.moneyInBank}")
            println("Cash: ${Player.cash}")
            println("Debt: ${Player.debt}")
            println("Location: ${Player.location}")
            println("Date: $monthName of $year")
            println("Ship---------------------------Ship")
            println("Cannons: ${Ship.cannons}")
            println("Health: ${Ship.health}")
            println("Units: ${Ship.cargoUnits}")
            println("Hold: ${Ship.hold}")
            for (commodity in Commodity.values()) {
                println("${commodity.name}: ${Ship.commodities[commodity]}")
            }
            println("Warehouse---------------------------Warehouse")
            for (commodity in Commodity.values()) {
                println("${commodity.name}: ${Warehouse.commodities[commodity]}")
            }
            println("In Use: ${Warehouse.occupiedCargoSpaces}")
            println("Vacant: ${Warehouse.vacantCargoSpaces}")
            println("Prices-----------------------------Prices")
            println("Taipan, prices per unit here are:")
            for (commodity in Commodity.values()) {
                println("${commodity.name}: ${Prices.commodities[commodity]}")
            }

            /*
            Prompt the user.
             */

            val inHongKong = Player.location == Location.HongKong

            when (input("Shall I Buy, Sell, Visit Bank, Transfer Cargo, Quit Trading, or Retire? ")) {
                "b" -> exchangeHandler(buying = true)
                "s" -> exchangeHandler(buying = false)
                "v" -> if (inHongKong) {
                    inputLoop("How much will you deposit?") { cashToDepositStr ->
                        val cashToDeposit = cashToDepositStr.toInt()
                        if (cashToDeposit > Player.cash) {
                            println("Taipan, you only have ${Player.cash} in your wallet.")
                            true
                        } else if (cashToDeposit >= 0) {
                            Player.cash -= cashToDeposit
                            Player.moneyInBank += cashToDeposit
                            false
                        } else false
                    }
                    inputLoop("How much will you withdraw?") { cashToWithdrawStr ->
                        val cashToWithdraw = cashToWithdrawStr.toInt()
                        if (cashToWithdraw > Player.moneyInBank) {
                            println("Taipan, you only have ${Player.moneyInBank} in your bank.")
                            true
                        } else if (cashToWithdraw >= 0) {
                            Player.cash += cashToWithdraw
                            Player.moneyInBank -= cashToWithdraw
                            false
                        } else false
                    }
                }
                "t" -> if (inHongKong) {
                    // TODO Transfer cargo
                }
                "q" -> if (Ship.hold < 0) {
                    println("Your ship would be overburdened, Taipan!")
                } else {
                    // TODO Quit trading
                    break
                }
                "r" -> if (inHongKong && Player.moneyInBank + Player.cash >= 1000000) {
                    // TODO Retire
                    break
                }
            }
        }

        if (!isRunning) {
            break
        }

        // TODO Sea event
        // TODO Turn progression
    }

    println("Game terminated.")
}
