package taipan

import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.math.pow
import kotlin.math.floor
import kotlin.collections.shuffled

/**
 * The ship can be at any of the below ports at any given time ([Ship.location]).
 *
 * TODO (Issue #7) Change the name of [Location] to `Port` and add information and unique characteristics to each `Port`.
 */
@Suppress("unused")
enum class Location (val id: Int) {
    HongKong (1),
    Shanghai (2),
    Nagasaki (3),
    Saigon (4),
    Manila (5),
    Singapore (6),
    Batavia (7)
}

/**
 * A commodity that can be traded in the game.
 */
enum class Commodity {
    Opium, Silk, Arms, General;

    companion object {
        /**
         * Given user [input], returns the corresponding [Commodity].
         * Throws [UnknownAbbreviationException] if the input is invalid.
         */
        fun fromAbbreviation(input: String): Commodity =
            when (input) {
                "o" -> Opium
                "s" -> Silk
                "a" -> Arms
                "g" -> General
                else -> throw UnknownAbbreviationException("")
            }
    }

    /**
     * Thrown if the user is asked for a commodity but types an invalid response.
     */
    class UnknownAbbreviationException (input: String): Exception("`$input` is not a valid commodity.")
}

object Ship {
    var cannons = 5
    var health = 100
    var cargoUnits = 100
    var vacantCargoSpaces = 50

    /**
     * The quantity of each [Commodity] available on the [Ship].
     */
    val commodities = mutableMapOf(
        Commodity.Opium to 0,
        Commodity.Silk to 0,
        Commodity.Arms to 0,
        Commodity.General to 0
    )

    var location = Location.HongKong
}

object Warehouse {
    /**
     * The quantity of each [Commodity] currently stored in the [Warehouse].
     */
    var commodities = mutableMapOf(
        Commodity.Opium to 0,
        Commodity.Silk to 0,
        Commodity.Arms to 0,
        Commodity.General to 0
    )

    var vacantCargoSpaces = 10000
    const val totalCargoSpaces = 10000

    val occupiedCargoSpaces get() = totalCargoSpaces - vacantCargoSpaces
}

/**
 * TODO Explain who Li Yuen is
 */
object LiYuen {
    /**
     *
     */
    var chanceOfAttack = 0.5

    /**
     *
     */
    var chanceOfExtortion = 0.8

    /**
     *
     */
    var extortionMultiplier = 1.0
}

object Probabilities {
    var portEvent = 0.25
    var pirateAttack = 0.3
    var storm = 0.11
}

object Finance {
    val prices = mutableMapOf(
        Commodity.Opium to 1000,
        Commodity.Silk to 100,
        Commodity.Arms to 10,
        Commodity.General to 1
    )

    var moneyInBank = 0L
    var cash = 500L
    var debt = 5000L
}

object Time {
    private val monthNames = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    var monthsPassed = 0

    val yearsPassed get() = monthsPassed / 12

    val currentYear get() = 1860 + yearsPassed

    val monthName get() = monthNames[monthsPassed % 12]
}

object Casino {
    var moneySpent = 0L
    private var moneyEarned = 0L
    var monthSinceLastVisit = 0
    var visitedBefore = false

    val profit get() = moneyEarned - moneySpent

    object Blackjack {
        enum class Card (val value: Int, val stringName: String) {
            A(1, "A"),
            _2(2, "2"),
            _3(3, "3"),
            _4(4, "4"),
            _5(5, "5"),
            _6(6, "6"),
            _7(7, "7"),
            _8(8, "8"),
            _9(9, "9"),
            _10(10, "10"),
            J(10, "J"),
            Q(10, "Q"),
            K(10, "K")
        }

        private infix fun MutableList<Card>.transferTopCardTo(destinationDeck: MutableList<Card>) {
            destinationDeck.add(this.removeLast())
        }

        private fun getSum(hiddenDeck: MutableList<Card>, visibleDeck: MutableList<Card>): Int {
            val sum = hiddenDeck.first().value + visibleDeck.sumOf { it.value }
            return sum +
                if ((Card.A in hiddenDeck || Card.A in visibleDeck) && sum < 12) 10 else 0
        }

        fun play() {
            val deck =
                mutableListOf(
                    Card.A, Card.A, Card.A, Card.A,
                    Card._2, Card._2, Card._2, Card._2,
                    Card._3, Card._3, Card._3, Card._3,
                    Card._4, Card._4, Card._4, Card._4,
                    Card._5, Card._5, Card._5, Card._5,
                    Card._6, Card._6, Card._6, Card._6,
                    Card._7, Card._7, Card._7, Card._7,
                    Card._8, Card._8, Card._8, Card._8,
                    Card._9, Card._9, Card._9, Card._9,
                    Card._10, Card._10, Card._10, Card._10,
                    Card.J, Card.J, Card.J, Card.J,
                    Card.Q, Card.Q, Card.Q, Card.Q,
                    Card.K, Card.K, Card.K, Card.K,
                )

            while(true) {
                var exitBlackjack = false

                intInputLoop ("How much do you want to bet? Enter 0 to exit blackjack.") { bet ->
                    when (bet) {
                        0 -> {
                            exitBlackjack = true
                            false
                        }

                        // Keep playing
                        else -> { 
                            if (bet > Finance.cash) {
                                println("You can't bet that much!")
                                true
                            } else {
                                val gameDeck = deck.shuffled().toMutableList()
                                val playerDeckNotVisible = mutableListOf<Card>().also {
                                    gameDeck transferTopCardTo it
                                }
                                val dealerDeckVisible = mutableListOf<Card>().also {
                                    gameDeck transferTopCardTo it
                                }
                                val playerDeckVisible = mutableListOf<Card>().also {
                                    gameDeck transferTopCardTo it
                                }
                                val dealerDeckNotVisible = mutableListOf<Card>().also {
                                    gameDeck transferTopCardTo it
                                }
                                var bust = false
                                var stay = false
                                var doubleMultiplier = 1L
                                var playerSum: Int = 0
                                var dealerSum: Int = 0

                                println("Elder Brother He has dealt the cards.")

                                while (!bust && !stay) {
                                    playerSum = getSum(playerDeckNotVisible, playerDeckVisible)
                                    println("Your hidden card: $playerDeckNotVisible")
                                    println("Your visible deck: $playerDeckVisible")
                                    println("The dealer's visible card: $dealerDeckVisible")
                                    println("Your sum: $playerSum")

                                    inputLoop ("Do you want to hit${if (playerDeckVisible.size + playerDeckNotVisible.size == 2) ", double down," else ""} or stay?") {
                                        when (it) {
                                            "h" -> {
                                                gameDeck transferTopCardTo playerDeckVisible
                                                playerSum = getSum(playerDeckNotVisible, playerDeckVisible)

                                                if (playerSum > 21) {
                                                    println("Your hidden card: $playerDeckNotVisible")
                                                    println("Your visible deck: $playerDeckVisible")
                                                    println("The dealer's visible card: $dealerDeckVisible")
                                                    println("Your sum: $playerSum")
                                                    println("You went bust!")
                                                    println("You lost $bet cash!")
                                                    bust = true
                                                    Finance.cash -= bet
                                                    moneySpent += bet
                                                    
                                                }

                                                false
                                            }
                                            "s" -> {
                                                stay = true

                                                false
                                            }
                                            "d" -> {
                                                if (playerDeckVisible.size + playerDeckNotVisible.size == 2) {
                                                    gameDeck transferTopCardTo playerDeckVisible
                                                    playerSum = getSum(playerDeckNotVisible, playerDeckVisible)
                                                    val newBet = bet * 2

                                                    if (playerSum > 21) {
                                                        println("Your hidden card: $playerDeckNotVisible")
                                                        println("Your visible deck: $playerDeckVisible")
                                                        println("The dealer's visible card: $dealerDeckVisible")
                                                        println("Your sum: $playerSum")
                                                        println("You went bust!")
                                                        println("You lost $newBet cash!")
                                                        bust = true
                                                        if (Finance.cash - newBet < 0) {
                                                            Finance.debt = newBet - Finance.cash
                                                            Finance.cash = 0
                                                        } else {
                                                            Finance.cash -= newBet
                                                        }
                                                        moneySpent += newBet
                                                    }
                                                    doubleMultiplier = 2L 
                                                    stay = true
                                                }
                                                false
                                            }
                                            else -> true
                                        }
                                    }
                                }

                                while (dealerSum <= 17 && !bust) {
                                    gameDeck transferTopCardTo dealerDeckNotVisible
                                    dealerSum = getSum(dealerDeckVisible, dealerDeckNotVisible)
                                    if (dealerSum > 21) {
                                        println("The dealer went bust!")
                                        if (Card.J in playerDeckVisible && Card.A in playerDeckNotVisible || Card.A in playerDeckVisible && Card.J in playerDeckNotVisible) {  
                                            println("You had a blackjack!")
                                            println("You won ${(bet * 5.0 / 2.0 * doubleMultiplier).roundToInt().toLong()} cash!")
                                            Finance.cash += (bet * 5.0 / 2.0).roundToInt().toLong() * doubleMultiplier
                                            Casino.moneyEarned += (bet * 5.0 / 2.0).roundToInt().toLong() * doubleMultiplier
                                        } else {
                                            println("You won ${bet * doubleMultiplier} cash!")
                                            Finance.cash += bet * doubleMultiplier
                                            Casino.moneyEarned += bet * doubleMultiplier
                                        }
                                        break
                                    }
                                }

                                if (dealerSum <= 21 && !bust) {
                                    println("Your hidden card: $playerDeckNotVisible")
                                    println("Your visible deck: $playerDeckVisible")
                                    println("Dealer's visible card: $dealerDeckVisible")
                                    println("Dealer's hidden deck: $dealerDeckNotVisible")
                                    println("Your sum: $playerSum")
                                    println("Dealer's sum: $dealerSum")
                                    if (playerSum > dealerSum) {
                                        if (Card.J in playerDeckVisible && Card.A in playerDeckNotVisible || Card.A in playerDeckVisible && Card.J in playerDeckNotVisible) {  
                                            println("Your sum was greater and you had a blackjack!")
                                            println("You won ${(bet * 5.0 / 2.0 * doubleMultiplier).roundToInt().toLong()} cash!")
                                            Finance.cash += (bet * 5.0 / 2.0).roundToInt().toLong() * doubleMultiplier
                                            Casino.moneyEarned += (bet * 5.0 / 2.0).roundToInt().toLong() * doubleMultiplier
                                        } else {
                                            println("Your sum was greater!")
                                            println("You won ${bet * doubleMultiplier} cash!")
                                            Finance.cash += bet * doubleMultiplier
                                            Casino.moneyEarned += bet * doubleMultiplier
                                        }
                                    } else if (playerSum == dealerSum) {
                                        println("You and the dealer (Brother He) tied.")
                                    } else {
                                        println("The dealer had a larger sum! You lost ${bet * doubleMultiplier} cash!")
                                        if (Finance.cash - bet * doubleMultiplier < 0) {
                                            Finance.debt = bet * doubleMultiplier - Finance.cash
                                            Finance.cash = 0
                                        } else {
                                            Finance.cash -= bet * doubleMultiplier
                                        }
                                        Casino.moneySpent += bet * doubleMultiplier
                                    }
                                }

                                false
                            }
                        }
                    }
                }

                if (exitBlackjack) return
            }
        }
    }

    object Doubles {
        private fun doubles(amount: Long, times: Int): Long {
            if (Random.nextDouble() <= 0.5) {
                if (times == 0) {
                    return 0
                } else {
                    return amount
                }
            } else {
                return doubles(amount * 2, times + 1)
            }
        }
        fun play() {
            var exitDoubles = true
            while (exitDoubles) {
                intInputLoop("How much do you want to bet? Enter 0 to exit Doubles.") { bet ->
                    when (bet) {
                        0 -> {
                            exitDoubles = false
                            false
                        }
                        else -> {
                            if (bet < 10 || bet > Finance.cash) {
                                println("You can't do that!")
                                true
                            } else {
                                Finance.cash -= bet.toLong()
                                var doublesRound = doubles((bet / 10.0).roundToInt().toLong(), 0)
                                Finance.cash += doublesRound
                                println("You won $doublesRound cash!")
                                false
                            }
                        }
                    }
                }
            }
        }
    }
}

val globalMultiplier get() = 1.0 + Time.monthsPassed / 10000

/**
 * Functional pipe operator.
 *
 * `X sendTo Y` is the same as `Y(X)`.
 */
infix fun<Tin, Tout> Tin.sendTo(lambda: (Tin) -> Tout) =
    lambda(this)



/**
 * Prints the [prompt] with no newline and gets a line of input from the user.
 */
fun input(prompt: String): String {
    print("$prompt ")
    return readln()
}

/**
 * Repeatedly takes user input until [handler] returns false. That is, [handler] should return whether to continue looping.
 */
fun inputLoop(prompt: String, handler: (String) -> Boolean) {
    while (true) {
        if (!(input(prompt) sendTo handler)) break
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

/**
 * Generates a price for a particular commodity in the port that Taipan is currently in.
 * Ranges from 5 to 25, with the multiplier depending on the commodity.
 * globalMultiplier acts as natural inflation.
 */
fun priceGenerator(max: Int, mul: Int): Int =
    (
        ((5 * mul)..(25 * mul))
            .random()
            .toDouble()
        * max.toDouble()
        * globalMultiplier
        / mul
    ).roundToInt()

/**
 * Generates a number of pirates immediately before a battle.
 * Depends on multiple factors: type of pirate, amount of commodities onboard, etc...
 * Pirates become more numerous as time passes.
 */
fun pirateGenerator(min: Int, max: Int): Int =
    (
        (min..max)
            .random()
            .toDouble()
        * globalMultiplier
    ).roundToInt()

/**
 * Generates a random price for a particular commodity.
 * Happens rarely; prices range from 1 to 4 or 50 to 1000 with a multiplier.
 */
fun randomPriceGenerator(max: Int): Int =
    ((if (Random.nextDouble() <= 0.5) (1..4) else (50..1000))
        .random()
        .toDouble()
        * max.toDouble()
        * globalMultiplier
    ).roundToInt()

/**
 * Asks the user what and how much they would like to buy ([buying] = true) or sell ([buying] = false).
 */
fun exchangeHandler(buying: Boolean) {
    val actionString = if (buying) "buy" else "sell"

    inputLoop ("What do you wish to $actionString, Taipan?") whichCommodity@{ commodity ->
        try {
            val product = Commodity.fromAbbreviation(commodity)
            val priceOfProduct = Finance.prices[product]!!
            val directionMultiplier = if(buying) +1 else -1
            val numberOfProductsAffordable = Finance.cash / priceOfProduct

            intInputLoop ("How many units of ${product.name} do you want to $actionString?${(if(buying) " You can afford $numberOfProductsAffordable." else "") + ""}") {
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
                    Finance.cash -= directionMultiplier * it * priceOfProduct
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
 * Returns `false` if the ship went down during combat.
 * Always check the output of this function and terminate the main loop if `false`.
 */
fun combat(damageC: Double, gunKnockoutChance: Double, numberOfPirates: Int, pirateResistanceC: Double): Boolean {
    val pirateList =
        MutableList(numberOfPirates) {
            (
                20
                * (Random.nextDouble() + 0.5)
                * (globalMultiplier + 0.6)
                * (pirateResistanceC + 0.5)
            ).roundToInt()
        }

    combatLoop@while (true) {
        println("${pirateList.size} ships attacking, Taipan!")
        inputLoop("Shall we fight or run, Taipan?") {
            when (it) {
                "f" -> {
                    if (Ship.cannons > 0) {
                        var piratesSank = 0

                        // Taipan is firing on the pirates
                        repeat (Ship.cannons) {
                            val damageToPirateShip =
                                    ((Random.nextDouble(0.0, 1.0) + 0.3) * 50 * (1.5 * globalMultiplier - 0.5))
                                            .roundToInt()
                            if (pirateList.isEmpty()) {
                                return@repeat
                            }

                            pirateList[pirateList.lastIndex] -= damageToPirateShip

                            if (pirateList[pirateList.lastIndex] <= 0) {
                                pirateList.removeLast()
                                piratesSank++
                            }
                        }

                        println("Sank $piratesSank buggers, Taipan!")

                        if (pirateList.isNotEmpty() && piratesSank >= pirateList.size / 2) {
                            val numberRanAway =
                                    pirateGenerator(
                                        1 + pirateList.size / 10,
                                        1 + floor((0.35 * pirateList.size.toDouble())).roundToInt()
                                    )
                            println("$numberRanAway buggers ran away, Taipan!")

                            repeat (numberRanAway) {
                                pirateList.removeLast()
                            }
                        }

                        if (pirateList.isEmpty()) {
                            println("We got them all, Taipan!")
                            val booty =
                                    (numberOfPirates * Random.nextInt(5, 50) * Random.nextInt(1, 10) * (Time.monthsPassed + 1) / 4 +
                                            250)
                            Finance.cash += booty
                            println("We got $booty in booty, Taipan!")
                        }
                        false
                    } else {
                        println("Taipan! We have no guns!")
                        true
                    }
                }
                "r" -> {
                    // Attempt to run away
                    if (Random.nextDouble() <= Ship.cargoUnits.toDouble().pow(0.9) / (Ship.cargoUnits + 5 * pirateList.size)) {
                        val numberRanCounter = pirateGenerator(pirateList.size / 5, pirateList.size)
                        if (numberRanCounter == pirateList.size) {
                            println("We got away from them, Taipan!")
                            repeat (numberRanCounter) {
                                pirateList.removeLast()
                            }
                        } else if (numberRanCounter > 0) {
                            println("Can't escape them, Taipan, but we managed to lose $numberRanCounter of them!")
                            repeat (numberRanCounter) {
                                pirateList.removeLast()
                            }
                        } else {
                            if (pirateList.size > 1) {
                                println("Can't escape them, Taipan!, but we managed to lose 1 of them!")
                            }
                            pirateList.removeLast()
                            if (pirateList.size == 0) {
                                println("We got away from them, Taipan!")
                            } else {
                                println("${pirateList.size} remain, Taipan!")
                            }
                        }
                    } else {
                        println("Can't escape them, Taipan!")
                        println("${pirateList.size} remain, Taipan!")
                    }
                    false
                }
                else -> true
            }
        }

        if (pirateList.size <= 0) {
            break@combatLoop
        }

        println("$pirateList")

        val damageToPlayerShip =
            (
                (25 + Time.monthsPassed) / Ship.cargoUnits.toDouble().pow(1.11)
                * (damageC + 0.5)
                * (Random.nextDouble() + 1)
                * numberOfPirates.toDouble().pow(0.7)
                * 5
               * pirateList.size / numberOfPirates
            ).roundToInt()

        println("They're firing on us, Taipan!")

        if (Random.nextDouble() < gunKnockoutChance && Ship.cannons > 0) {
            println("They hit a gun, Taipan!")
            Ship.cannons--
            Ship.vacantCargoSpaces += 10
        } else {
            println("We took $damageToPlayerShip damage, Taipan!")
            Ship.health -= damageToPlayerShip
        }

        println("Ship---------------------------Ship")
        println("Cannons: ${Ship.cannons}")
        println("Health: ${Ship.health}")
        println("Units: ${Ship.cargoUnits}")

        if (Ship.health <= 0) {
            println("The buggers got us, Taipan!!")
            println("It's all over, Taipan!!")
            println("We're going down, Taipan!!")
            return false
        }
    }

    return true
}

/**
 * Casino function in Shanghai.
 * Taipan can gamble and play various games.
 */
fun casino() {
    println(
        if (Casino.visitedBefore) {
            if (Casino.monthSinceLastVisit < 4)
                "Welcome back, Taipan! The Master of the House has given you your favorite table!\nPick where you left off and have fun!"
            else
                "It's been a while, Taipan! Enjoy yourself!"
        } else
            "Welcome to the Shanghai Casino Club, Taipan!"
    )

    Casino.monthSinceLastVisit = 0
    Casino.visitedBefore = true

    var leftCasino = false

    while (!leftCasino) {
        println("Games available: ")
        println("Blackjack, Doubles, Slots, Poker, Roulette, and Keno.")

        when (input("What games would you like to play? Lowercase E to Exit the Casino.")) {
            "b" -> Casino.Blackjack.play()
            "d" -> Casino.Doubles.play()
            "s" -> {

            }
            "p" -> {

            }
            "r" -> {

            }
            "k" -> {

            }
            "e" -> {
                leftCasino = true
            }
        }
    }
}
fun main() {
    println("Welcome to Taipan!")
    println("How to play Taipan")
    println("1. Use single, lowercase letters for commands: f for fight, b for buy, o for opium, h for hit, etc...")
    println("2. One turn equals one month, bank and moneylender interest rates are at 5% and 20% respectively")
    println("3. Try to make as much money as possible!")

    // Each iteration is a different port.
    mainLoop@while (true) {
        // The shipyard and moneylender only bother you if you're in Hong Kong.
        if (Ship.location == Location.HongKong) {
            // If low on health, go to the shipyard.
            if (Ship.health < 100) {
                val shipIstTotScalar = 1 + (1 - (100 - Ship.health) / 100.0)
                val shipFixPrice =
                    (
                        Random.nextInt(1, Ship.cargoUnits)
                        * shipIstTotScalar
                        * globalMultiplier
                        * Random.nextInt(1, 5)
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
                println("I'll fix you up to full workin' order for $shipFixPrice pound sterling.")

                intInputLoop ("Taipan, how much will you pay Captain McHenry? You have ${Finance.cash} pound sterling on hand.") payCaptain@{ amountPaid ->
                    if (amountPaid > Finance.cash) {
                        println("Taipan, you only have ${Finance.cash} cash.")
                        true
                    } else if (amountPaid >= shipFixPrice) {
                        // If the player paid what the captain asked for, completely repair the ship
                        Ship.health = 100
                        Finance.cash -= amountPaid
                        false
                    } else if (amountPaid >= 0) {
                        // If the player pays x% of what was asked, repair x% of the damaged ship
                        Ship.health += (100 - Ship.health) * amountPaid / shipFixPrice
                        Finance.cash -= amountPaid
                        false
                    } else true
                }
            }

            // Decide whether to activate Li Yuen.
            if (Random.nextDouble() <= LiYuen.chanceOfExtortion && Finance.cash >= 500) {
                val amountRequested = (
                    1.1 * LiYuen.extortionMultiplier
                    * Finance.cash
                    * (Random.nextDouble() + 0.1)
                ).roundToInt()
                boolInputLoop ("Li Yuen asks $amountRequested in donation to the temple of Tin Hau, the Sea Goddess. Will you pay?") willYouPay@{
                    if (it) {
                        if (amountRequested > Finance.cash) {
                            var iWouldBeWaryOfPirates = false

                            boolInputLoop ("Taipan! You do not have enough cash! Do you want Elder Brother Wu to make up the difference?") doYouWantWu@{ makeUpDifference ->
                                if (makeUpDifference) {
                                    Finance.debt += amountRequested - Finance.cash
                                    Finance.cash = 0
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
                            Finance.cash -= amountRequested
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
                    if (Finance.debt > 0) {
                        intInputLoop("How much do you wish to repay him?") { 
                            repayAmount -> if (repayAmount > Finance.cash || repayAmount < 0) {
                                println("You can't do that, Taipan!")
                                true
                            } else {
                                if (repayAmount > Finance.debt) {
                                    Finance.cash -= repayAmount
                                    Finance.debt = 0
                                } else {
                                    Finance.cash -= repayAmount
                                    Finance.debt -= repayAmount
                                }
                                false
                            }        
                        }
                    }
                    if (Finance.cash > 0) {
                        intInputLoop("How much do wish to borrow?") {
                            borrowAmount -> if (borrowAmount > Finance.cash) {
                                println("He won't loan you so much, Taipan!")
                                true
                            } else if (borrowAmount < 0) {
                                println("You can't do that, Taipan!")
                                true
                            } else {
                                Finance.cash += borrowAmount
                                Finance.debt += borrowAmount
                                false
                            }
                        }
                    }
                }
                false
            }
        }

        // TODO TEST Port event
        if (Random.nextDouble() <= Probabilities.portEvent) {
            //Options: More guns, A new ship, Robbed for some amount of money, Opium confiscated from cargo, Opium confiscated from warehouse
            val severity = Random.nextDouble(0.01, 1.0)
            val amount = (Finance.cash * severity).roundToInt()
            val amountOfWarehouseOpiumLost = (Warehouse.commodities[Commodity.Opium]!! * severity).roundToInt()
            if (Random.nextDouble() <= 0.35 && Finance.cash >= 500) {
                val shipCost = ((Random.nextDouble() + 0.1) * Finance.cash * 0.35).roundToInt()
                boolInputLoop ("Would you like to trade your ${if(Ship.health < 100) "damaged " else ""}ship for $shipCost cash?") {
                    if (it) {
                        Ship.cargoUnits += 50
                        Ship.vacantCargoSpaces += 50
                        Finance.cash -= shipCost
                        Ship.health = 100
                    }
                    false
                }
            }
            if (Random.nextDouble() <= 0.5 && Finance.cash >= 100) {
                val gunCost = ((Random.nextDouble() + 0.1) * Finance.cash * 0.5 * 0.3).roundToInt()
                boolInputLoop("Would you like another gun for $gunCost cash?") {
                    if (it) {
                        if (Ship.vacantCargoSpaces < 10) {
                            println("Your ship will be overburdened, Taipan!")
                        } else {
                            Ship.cannons++
                            Ship.vacantCargoSpaces -= 10
                            Finance.cash -= gunCost
                        }
                    }
                    false
                 }
            }
            
            if (Random.nextDouble() <= (Ship.commodities[Commodity.Opium]!! / (Ship.cargoUnits - Ship.cannons * 10))) {
                println("Bad joss! Officials confiscated your opium and fined you $amount cash!")
                Finance.cash -= amount
                Ship.vacantCargoSpaces += Ship.commodities[Commodity.Opium]!!
                Ship.commodities[Commodity.Opium] = 0
            }

            if (Random.nextDouble() <= 0.15) {
                val robbedAmount = (Random.nextDouble(0.05, 1.0) * Finance.cash).roundToInt()
                println("Bad joss! You were beaten up and robbed of $robbedAmount cash!")
                Finance.cash -= robbedAmount
            }
            
            if (Random.nextDouble() <= (Warehouse.commodities[Commodity.Opium]!!) / (Warehouse.totalCargoSpaces)) {
                if (Warehouse.commodities[Commodity.Opium]!! > 0.1 * Warehouse.totalCargoSpaces) {
                    println("Taipan! The police raided our warehouse down in Kwun Tong overnight and confiscated $amountOfWarehouseOpiumLost units of opium!")
                    Warehouse.commodities[Commodity.Opium] = Warehouse.commodities[Commodity.Opium]!! - amountOfWarehouseOpiumLost
                    Warehouse.vacantCargoSpaces += amountOfWarehouseOpiumLost
                } else {
                    println("Taipan! The police raided our warehouse down in Kwun Tong overnight. We didn't have enough opium there to arouse suspicion, so they left without hubbub.")
                }
            }

            Probabilities.portEvent = (Random.nextDouble() + 0.5) * 0.5
        }

        if (Random.nextDouble() <= 0.9) {
            Finance.prices[Commodity.Opium] = priceGenerator(1000, 2)
            Finance.prices[Commodity.Silk] = priceGenerator(100, 2)
            Finance.prices[Commodity.Arms] = priceGenerator(10, 1)
            Finance.prices[Commodity.General] = priceGenerator(1, 1)
        } else {
            var commodityList = listOf("Opium", "Silk", "Arms", "General")
            var num = Random.nextInt(0, 4)
            var commoditySelected = commodityList[num]
            if (commoditySelected == "Opium") {
                Finance.prices[Commodity.Opium] = randomPriceGenerator(1000)
            } else {
                Finance.prices[Commodity.Opium] = priceGenerator(1000, 2)
            }
            if (commoditySelected == "Silk") {
                Finance.prices[Commodity.Silk] = randomPriceGenerator(100)
            } else {
                Finance.prices[Commodity.Silk] = priceGenerator(100, 2)
            }
            if (commoditySelected == "Arms") {
                Finance.prices[Commodity.Arms] = randomPriceGenerator(10)
            } else {
                Finance.prices[Commodity.Arms] = priceGenerator(10, 1)
            }
            if (commoditySelected == "General") {
                Finance.prices[Commodity.General] = randomPriceGenerator(1)
            } else {
                Finance.prices[Commodity.General] = priceGenerator(1, 1)
            }
            println("Taipan!!!")
            println("Prices for $commoditySelected are wild!!!")
        }

        tradingLoop@while (true) {
            // Display all known information.
            println("Player---------------------------Player")
            println("Bank: ${Finance.moneyInBank}")
            println("Cash: ${Finance.cash}")
            println("Debt: ${Finance.debt}")
            println("Location: ${Ship.location}")
            println("Date: ${Time.monthName} ${Time.currentYear}")
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
                println("${commodity.name}: ${Finance.prices[commodity]}")
            }

            val inHongKong = Ship.location == Location.HongKong

            // Prompt the user.
            when (input("Shall I Buy, Sell,${if (Ship.location == Location.Shanghai) " Visit Casino" else ""}${if (Ship.location == Location.HongKong) " Visit Bank, Transfer Cargo${if (Finance.moneyInBank + Finance.cash < 1000000) {", or Quit Trading?"} else {", Quit Trading, or Retire?"}}" else " or Quit Trading?"}")) {
                "b" -> exchangeHandler(buying = true)
                "s" -> exchangeHandler(buying = false)
                "c" -> if (Ship.location == Location.Shanghai) casino()
                "v" -> if (inHongKong) {
                    intInputLoop ("How much will you deposit?") { cashToDeposit ->
                        if (cashToDeposit > Finance.cash) {
                            println("Taipan, you only have ${Finance.cash} in your wallet.")
                            true
                        } else if (cashToDeposit >= 0) {
                            Finance.cash -= cashToDeposit
                            Finance.moneyInBank += cashToDeposit
                            false
                        } else false
                    }
                    intInputLoop ("How much will you withdraw?") { cashToWithdraw ->
                        if (cashToWithdraw > Finance.moneyInBank) {
                            println("Taipan, you only have ${Finance.moneyInBank} in your bank.")
                            true
                        } else if (cashToWithdraw >= 0) {
                            Finance.cash += cashToWithdraw
                            Finance.moneyInBank -= cashToWithdraw
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
                        for (toWarehouse in listOf(true, false)) {
                            for (commodity in Commodity.values()) {
                                val directionMultiplier = if (toWarehouse) +1 else -1
                                val actionString = if (toWarehouse) "to the warehouse" else "aboard ship"
                                val amountAvailableToMove = (if (toWarehouse) Ship.commodities else Warehouse.commodities)[commodity]!!

                                if (amountAvailableToMove > 0) {
                                    intInputLoop("How much ${commodity.name} shall I move $actionString, Taipan?") {
                                        if (it > amountAvailableToMove) {
                                            println("You only have $amountAvailableToMove, Taipan.")
                                        } else if (Warehouse.vacantCargoSpaces - it < 0 && toWarehouse ) {
                                            println("There's not enough space in the warehouse, Taipan!")
                                        } else if (it >= 0) {
                                            Ship.vacantCargoSpaces += directionMultiplier * it
                                            Warehouse.vacantCargoSpaces -= directionMultiplier * it
                                            Warehouse.commodities[commodity] =
                                                Warehouse.commodities[commodity]!! + directionMultiplier * it
                                            Ship.commodities[commodity] =
                                                Ship.commodities[commodity]!! - directionMultiplier * it
                                        }
                                        false
                                    }
                                }
                            }
                        }
                    }
                }
                "q" -> if (Ship.vacantCargoSpaces < 0) {
                    println("Your ship would be overburdened, Taipan!")
                } else {
                    intInputLoop ("Taipan, do you wish to go to: \n"
                        + "1) Hong Kong, 2) Shanghai, 3) Nagasaki, 4) Saigon, 5) Manila, 6) Singapore, 7) Batavia?"
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
                "r" -> if (inHongKong && Finance.moneyInBank + Finance.cash >= 1000000) {
                    break@mainLoop
                }
            }
        }

        val isPirateFleetLiYuen = Random.nextDouble() <= LiYuen.chanceOfAttack

        // Pirate attack by Li Yuen
        if (isPirateFleetLiYuen) {
            val number = pirateGenerator(
                floor((Time.monthsPassed + 1) / 4.0 + floor(Ship.cargoUnits / 50.0)).roundToInt(),
                (10 + 2 * floor((Time.monthsPassed + 1) / 4.0 + Ship.cargoUnits / 50.0).roundToInt())
            )

            println("Li Yuen's pirates, Taipan!")
            println("$number ships of Li Yuen's pirate fleet!")

            if (!combat(2.0, 0.2, number, 2.0)) break@mainLoop
            LiYuen.chanceOfAttack = 0.5
            LiYuen.chanceOfExtortion = 0.8
        }

        // Other pirate attack
        if (Random.nextDouble() <= Probabilities.pirateAttack && !isPirateFleetLiYuen) {
            val number = pirateGenerator(
                floor((Time.monthsPassed + 1) / 6.0 + floor(Ship.cargoUnits / 100.0)).roundToInt(),
                (5 + 2 * floor((Time.monthsPassed + 1) / 6.0 + Ship.cargoUnits / 75.0).roundToInt() + floor(Ship.commodities[Commodity.Opium]!!.toDouble() * 0.1 * Random.nextDouble()).roundToInt())
            )
            println("$number hostile ships approaching, Taipan!")
            if (!combat(1.5, 0.1, number, 1.5)) break@mainLoop
            Probabilities.pirateAttack = (Random.nextDouble() + 0.05 + Ship.commodities[Commodity.Opium]!!.toDouble() / Ship.vacantCargoSpaces.toDouble()) * 0.25
        }

        // Storm
        if (Random.nextDouble() <= Probabilities.storm) {
            println("Storm, Taipan!")
            if (Random.nextDouble() <= (100.0 - Ship.health) / 1000.0) {
                println("We're going down, Taipan!")
                break@mainLoop
            } else {
                println("We survived, Taipan!")
                // Storm moves ship to different location
                if (Random.nextDouble() < 0.35) {
                    Ship.location = Location.values().random()
                    println("We've been blown off course to ${Ship.location}")
                }
                Probabilities.storm = (Random.nextDouble() + 0.05) * 0.5
            }
        }
        
        // Adjust values for next location.
        println("Arriving at ${Ship.location}")
        ++Time.monthsPassed
        Finance.debt = (Finance.debt * 1.2).toLong()
        Finance.moneyInBank = (Finance.moneyInBank * 1.05).toLong()
        Casino.monthSinceLastVisit++
    }

    val netWorth = Finance.cash + Finance.moneyInBank - Finance.debt
    val score = netWorth / (Time.monthsPassed + 1) / 100

    println("FINAL STATS")
    println("Net cash: $netWorth")
    println("Ship size: ${Ship.cargoUnits} units with ${Ship.cannons} guns")
    println("You traded for ${Time.yearsPassed} year(s)")
    println("Your score is $score")
    println("Rank: ${
        if (score >= 50000) "Ma Tsu"
        else if (score >= 8000) "Master Taipan"
        else if (score >= 1000) "Taipan"
        else if (score >= 500) "Compradore"
        else "Galley Hand"
    }")
}