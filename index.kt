package taipan

import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.math.pow
import kotlin.math.floor
import kotlin.collections.addAll

enum class Location (val id: Int) {
    HongKong (1),
    Shanghai (2),
    Nagasaki (3),
    Saigon (4),
    Manila (5),
    Singapore (6),
    Batavia (7)
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
    var vacantCargoSpaces = 100
    val commodities = mutableMapOf(
        Commodity.Opium to 0,
        Commodity.Silk to 0,
        Commodity.Arms to 0,
        Commodity.General to 0
    )
    var moneyInBank = 0
    var cash = 500
    var debt = 5000
    var location = Location.HongKong
}

object Prices {
    var commodities = mutableMapOf(
        Commodity.Opium to 3,
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
}

val monthNames = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
var month = 0
var year = 1860
var chanceOfSeaEvent = 0.5
var chanceOfPortEvent = 0.25
var isRunning = true
var chanceOfPirateAttack = 0.3

// Originally gameAttributes.monthLabel
val monthName: String
    get() = monthNames[month]

// Originally time()
val globalMultiplier: Double
    get() = 1.0 + month / 10000

val chanceOfSinking: Double
    get() = (100.0 - Ship.health) / 1000.0

fun input(prompt: String): String {
    print("$prompt ")
    return readLine()!!
}

/**
 * Repeatedly takes user input until [handler] returns false. That is, [handler] should return whether to continue looping.
 */
fun inputLoop(prompt: String, handler: (String) -> Boolean) {
    while (true) {
        if (!handler(input(prompt))) break
    }
}

/**
 * Repeatedly takes user input until the user types an integer AND [handler] returns false.
 * Behaves similarly to `inputLoop()`.
 */
fun intInputLoop(prompt: String, handler: (Int) -> Boolean) {
    inputLoop (prompt) {
        try {
            handler(it.toInt())
        } catch (_: NumberFormatException) {
            true
        }
    }
}

/**
 * Repeatedly takes user input until the user types y/n AND [handler] returns false.
 * Behaves similarly to `inputLoop()`.
 */
fun boolInputLoop(prompt: String, handler: (Boolean) -> Boolean) {
    inputLoop (prompt) {
        when (it) {
            "y" -> handler(true)
            "n" -> handler(false)
            else -> true
        }
    }
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

/**
 * Asks the user what and how much they would like to buy ([buying] = true) or sell ([buying] = false).
 */
fun exchangeHandler(buying: Boolean) {
    val actionString = if (buying) "buy" else "sell"

    inputLoop ("What do you wish to $actionString, Taipan? ") whichCommodity@{ commodity ->
        try {
            val product = Commodity.fromAbbreviation(commodity)
            val priceOfProduct = Prices.commodities[product]!!
            val directionMultiplier = if(buying) +1 else -1
            val numberOfProductsAffordable = Ship.cash / priceOfProduct

            intInputLoop ("How many units of ${product.name} do you want to $actionString? ${(if(buying) "You can afford $numberOfProductsAffordable" else "") + "."}") {
                // `it` is the number of products to buy/sell

                if (
                    // Buying more than the player can afford
                    (buying && it > numberOfProductsAffordable)
                    // Or selling more than the player has
                    || (!buying && it > Ship.commodities[product]!!)
                ) {
                    println(
                        if (buying) "You can't afford that!"
                        else "You don't have that much ${product.name}!"
                    )
                    true
                } else if (it >= 0) {
                    Ship.commodities[product] = Ship.commodities[product]!! + directionMultiplier * it
                    Ship.cash -= directionMultiplier * it * priceOfProduct
                    Ship.vacantCargoSpaces -= directionMultiplier * it
                    false
                } else true
            }

            return@whichCommodity false
        } catch (_: Commodity.UnknownAbbreviationException) {}

        true
    }
}

/**
 * Asks the user how much of [product] they would like to move.
 * If [toWarehouse] is true, products will be moved from the ship to the warehouse; otherwise, it will go the opposite direction.
 */
fun transferCargoHandler(product: Commodity, toWarehouse: Boolean) {
    val directionMultiplier = if(toWarehouse) +1 else -1
    val actionString = if(toWarehouse) "to the warehouse" else "aboard ship"
    val amountAvailableToMove = (if(toWarehouse) Ship.commodities else Warehouse.commodities)[product]!!

    if (amountAvailableToMove > 0) {
        intInputLoop ("How much ${product.name} shall I move $actionString, Taipan?") {
            if (it > amountAvailableToMove) {
                println("You only have $amountAvailableToMove, Taipan.")
            } else if (Warehouse.vacantCargoSpaces - it < 0) {
                println("There's not enough space in the warehouse, Taipan!")
            } else if (it >= 0) {
                Ship.vacantCargoSpaces += directionMultiplier * it
                Warehouse.vacantCargoSpaces -= directionMultiplier * it
                Warehouse.commodities[product] = Warehouse.commodities[product]!! + directionMultiplier * it
                Ship.commodities[product] = Ship.commodities[product]!! - directionMultiplier * it
            }
            false
        }
    }
}

fun combat(damageC: Double, gunKnockoutChance: Double, number: Int, pirateResistanceC: Double) {
    var resistanceRatio: Double = (25 + month) / (Ship.cargoUnits.toDouble().pow(1.11)) // Resistance to pirate attacks
    var runRatio: Double = 0.5 * 200 / (Ship.cargoUnits + 5 * number) // Chance of successfully running
    val pirateList: MutableList<Int> = mutableListOf()
    for (i in 1..number) {
        val pirateShip: Int = ((Random.nextDouble(0.0, 1.0) + 0.5) * 20 * (globalMultiplier + 0.6) * (pirateResistanceC + 0.5)).roundToInt()
        pirateList.add(pirateList.size - 1, pirateShip)
    }
    var num = pirateList.size
    var numberOfPirates = number
    println("${number} ships attacking, Taipan!")
    when (input("Shall we fight or run, Taipan? ")) { 
        "f" -> {
            var numberSank: Int = 0
            // Taipan is firing on the pirates
            for (i in 1..Ship.cannons) {
                var damageToPirateShip: Int = ((Random.nextDouble(0.0, 1.0) + 0.3) * 35 * (1.5 * globalMultiplier - 0.5)).roundToInt()
                pirateList[pirateList.size - 1] = pirateList[pirateList.size - 1] - damageToPirateShip
                if (pirateList[pirateList.size - 1] <= 0) {
                    pirateList.removeAt(pirateList.size - 1)
                    num--
                    numberSank++
                }
                if (pirateList.size <= 0) {
                    println("Sank ${numberSank} buugers, Taipan!")
                    println("We got them all, Taipan!")
                    var booty: Int = (numberOfPirates * Random.nextInt(5, 50).toInt() * Random.nextInt(1, 10).toInt() * (month + 1) / 4 + 250)
                    Ship.cash += booty
                    println("We got ${booty} in booty, Taipan!")
                    break
                }
            }
            println("Sank ${numberSank} buggers, Taipan!")
            if (numberSank >= floor(0.5 * number)) {
                var numberRanAway = pirateGenerator(1 + (0.1 * number).roundToInt(), 1 + (0.35 * number).roundToInt())
            }
            
        } 
        "r" -> {

        }
     }
}

fun main() {
    println("Welcome to Taipan!")

    // This is the main loop, each iteration of which is a different port.
    while (isRunning) {
        // The shipyard and moneylender only bother you if you're in Hong Kong.
        if (Ship.location == Location.HongKong) {
            // If low on health, go to the shipyard.
            if (Ship.health < 100) {
                val shipIstTotScalar: Double = 1 + (1 - (100 - Ship.health) / 100.0)
                val shipFixPrice: Int =
                    (
                        Random.nextInt(1, Ship.cargoUnits)
                        * shipIstTotScalar
                        * globalMultiplier
                        * (1..5).random()
                    ).roundToInt()

                println("Captain McHenry of the Hong Kong Consolidated Repair Corporation walks over to your ship and says: <<")
                println(
                    if (Ship.health < 30)
                        "Matey! That ship of yours is 'bout to rot away like a piece of driftwood in Kolwoon Bay! Don't worry, it's nothing I can't fix. For a price, that is!"
                    else if (Ship.health < 50)
                        "That there ship's taken quite a bit of damage, matey! You best get it fixed before you go out to sea again! I can get you sailing the friendly waves in no time! For a price, that is!"
                    else
                        "What a mighty fine ship you have there, matey! Or, shall I say, had... It could really use some of what I call \"Tender Love n' Care\". 'Tis but a scratch, as they say, but I take any job, no matter how small. For a price, that is!"
                )
                println("I'll fix you up to full workin' order for $shipFixPrice pound sterling>>")
                println("Taipan, how much will you pay Captain McHenry? You have ${Ship.cash} pound sterling on hand.")

                intInputLoop (">> ") payCaptain@{ amountPaid ->

                    if (amountPaid > Ship.cash) {
                        println("Taipan, you only have ${Ship.cash} cash.")
                        true
                    } else if (amountPaid > shipFixPrice) {
                        // If the player paid what the captain asked for, completely repair the ship
                        Ship.health = 100
                        Ship.cash -= amountPaid
                        false
                    } else if (amountPaid >= 0) {
                        // If the player pays x% of what was asked, repair x% of the damaged ship
                        Ship.health += (100 - Ship.health) * amountPaid / shipFixPrice
                        Ship.cash -= amountPaid
                        false
                    } else true
                }
            }

            // Decide whether to activate Li Yuen.
            // TODO TEST Li Yuen extortion
            if (Random.nextDouble() <= LiYuen.chanceOfExtortion) {
                val amountRequested = round(
                    1.1 * LiYuen.extortionMultiplier
                    * Ship.cash
                    * (Random.nextDouble() + 0.1)
                ) as Int
                boolInputLoop ("Li Yuen asks $amountRequested in donation to the temple of Tin Hau, the Sea Goddess. Will you pay?") willYouPay@{
                    if (it) {
                        if (amountRequested > Ship.cash) {
                            var iWouldBeWaryOfPirates = false

                            boolInputLoop ("Taipan! You do not have enough cash! Do you want Elder Brother Wu to make up the difference?") doYouWantWu@{
                                if (it) {
                                    Ship.debt += amountRequested - Ship.cash
                                    Ship.cash = 0
                                    println("Elder Brother Wu has given Li Yuen the difference, which will be added to your debt.")
                                } else {
                                    println("The difference will not be paid! Elder Brother Wu says, 'I would be wary of pirates if I were you, Taipan!'")
                                    LiYuen.chanceOfExtortion = 0.8
                                    LiYuen.chanceOfAttack = 0.5
                                    iWouldBeWaryOfPirates = true
                                }
                                false
                            }

                            if (iWouldBeWaryOfPirates) return@willYouPay false

                            LiYuen.chanceOfExtortion = 0.1
                            LiYuen.chanceOfAttack = 0.025
                            LiYuen.extortionMultiplier = 1.0
                        } else {
                            Ship.cash -= amountRequested
                            LiYuen.chanceOfExtortion = 0.05
                            LiYuen.chanceOfAttack = 0.01
                            LiYuen.extortionMultiplier = 1.0
                        }
                    } else {
                        LiYuen.chanceOfExtortion = 0.8
                        LiYuen.chanceOfAttack = 0.5
                    }

                    false
                }
            }

            LiYuen.chanceOfExtortion += 0.01

            // Money lender
            boolInputLoop ("Do you have business with Elder Brother Wu, the moneylender?") {
                if (it) {
                    /**
                     * TODO CLARIFY Money lender logic
                     * The original JS code contains a while(true) loop that never exits under some circumstances.
                     */
                }
                true
            }
        }

        if (Random.nextDouble() <= chanceOfPortEvent) {
            // TODO Port event testing
            //Options: Robbed for some amount of money, Opium confiscated from cargo, Opium confiscated from warehouse
            val type = Random.nextInt(1,3)
            val severity = Random.nextDouble(10.0, 70.0)
            when (type) {
                1 ->
                {println("Taipan! Robbers raided the ship while you were away and took ${Ship.cash * severity} pound sterling!")
                    Ship.cash = (Ship.cash - Ship.cash * severity).toInt()
                }
                2 -> {
                        if (Ship.commodities[Commodity.Opium]!! > 10){
                            println("Tapian! The police got to the ship while you were out trading and confiscated ${Ship.commodities[Commodity.Opium]!! * severity} units of opium!")
                            Ship.commodities[Commodity.Opium] = (Ship.commodities[Commodity.Opium]!! - Ship.commodities[Commodity.Opium]!! * severity).toInt()
                        } else {
                            println("Taipan! The police got to the ship while you were out trading, looking for opium. We didnt have enough to arouse suspicion, maybe you should buy some!")
                        }
                }
                3 -> {
                    if (Warehouse.commodities[Commodity.Opium]!! > 5){
                        println("Tapian! The police raided our warehouse down in Kwun Tong overnight and confiscated ${Warehouse.commodities[Commodity.Opium]!! * severity} units of opium!")
                        Warehouse.commodities[Commodity.Opium] = (Warehouse.commodities[Commodity.Opium]!! - Warehouse.commodities[Commodity.Opium]!! * severity).toInt()
                    } else {
                        println("Taipan! The police raided our warehouse down in Kwun Tong overnight. We didnt have enough opium there to arouse suspicion, so they left without hubbub")
                    }

                }
            }
            
        }

        Prices.isRandom = false

        if (Random.nextDouble() <= 0.1) {
            // TODO Random price
            Prices.isRandom = true
            // TODO Random price display
        } else {
            // TODO Price display
        }

        tradingLoop@while (true) {
            // Display all known information.
            println("Player---------------------------Player")
            println("Bank: ${Ship.moneyInBank}")
            println("Cash: ${Ship.cash}")
            println("Debt: ${Ship.debt}")
            println("Location: ${Ship.location}")
            println("Date: $monthName of $year")
            println("Ship---------------------------Ship")
            println("Cannons: ${Ship.cannons}")
            println("Health: ${Ship.health}")
            println("Units: ${Ship.cargoUnits}")
            println("Hold: ${Ship.vacantCargoSpaces}")
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

            val inHongKong = Ship.location == Location.HongKong

            // Prompt the user.
            when (input("Shall I Buy, Sell, Visit Bank, Transfer Cargo, Quit Trading, or Retire? ")) {
                "b" -> exchangeHandler(buying = true)
                "s" -> exchangeHandler(buying = false)
                "v" -> if (inHongKong) {
                    intInputLoop ("How much will you deposit?") { cashToDeposit ->
                        if (cashToDeposit > Ship.cash) {
                            println("Taipan, you only have ${Ship.cash} in your wallet.")
                            true
                        } else if (cashToDeposit >= 0) {
                            Ship.cash -= cashToDeposit
                            Ship.moneyInBank += cashToDeposit
                            false
                        } else false
                    }
                    intInputLoop ("How much will you withdraw?") { cashToWithdraw ->
                        if (cashToWithdraw > Ship.moneyInBank) {
                            println("Taipan, you only have ${Ship.moneyInBank} in your bank.")
                            true
                        } else if (cashToWithdraw >= 0) {
                            Ship.cash += cashToWithdraw
                            Ship.moneyInBank -= cashToWithdraw
                            false
                        } else false
                    }
                }
                "t" -> if (inHongKong) {
                    if((Ship.commodities.values + Warehouse.commodities.values)
                        .all { it == 0 }
                    ) {
                        println("You have no cargo, Taipan.")
                    } else {
                        for (commodity in Commodity.values()) {
                            transferCargoHandler(commodity, toWarehouse = true)
                        }
                        for (commodity in Commodity.values()) {
                            transferCargoHandler(commodity, toWarehouse = false)
                        }
                    }
                }
                "q" -> if (Ship.vacantCargoSpaces < 0) {
                    println("Your ship would be overburdened, Taipan!")
                } else {
                    intInputLoop ("Taipan, do you wish to go to: \n"
                        + "1) Hong Kong, 2) Shanghai, 3) Nagasaki, 4) Saigon, 5) Manila, 6) Singapore, 7) Batavia ?"
                    ) { input ->
                        when (val newLocation = Location
                            .values()
                            .find { it.id == input }
                        ) {
                            null -> true
                            else -> if (newLocation == Ship.location) {
                                println("You're already here, Taipan.")
                                true
                            } else {
                                Ship.location = newLocation
                                false
                            }
                        }
                    }
                    break
                }
                "r" -> if (inHongKong && Ship.moneyInBank + Ship.cash >= 1000000) {
                    // TODO Retire
                    break
                }
            }
        }

        if (!isRunning) break

        // TODO Sea event
        var isPirateFleetLiYuen = false
        // Pirate attack by Li Yuen
        if (Random.nextDouble() <= LiYuen.chanceOfAttack) {
            isPirateFleetLiYuen = true
        }
        // Other pirate attack
        if (Random.nextDouble() <= chanceOfPirateAttack && !isPirateFleetLiYuen) {
        
        }
        // Storm
        if (Random.nextDouble() <= 0.3 && isRunning) {
            if (Random.nextDouble() <= chanceOfSinking) {
                println("We're going down, Taipan!")
                isRunning = false
            } else {
                println("We survived, Taipan!")
                // Storm moves ship to different location
                if (Random.nextDouble() < 0.35) {
                    Ship.location = Location.values()[(0..6).random()]
                    println("We've been blown off course to ${Ship.location}")
                }
            }
        }
        
        // Adjust values for next location.
        println("Arriving at ${Ship.location}")
        ++month
        if (month == 0) ++year
        Ship.debt = (Ship.debt * 1.2) as Int
        Ship.moneyInBank = (Ship.moneyInBank * 1.05) as Int
    }

    println("Game terminated.")
}
