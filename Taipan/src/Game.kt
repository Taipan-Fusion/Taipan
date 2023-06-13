package taipan

import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.math.pow
import kotlin.math.floor
import kotlin.collections.shuffled

class Game(private val ui: UserInterface) {
    object Ship {
        /**
         * The quantity of each [Commodity] available on the [Ship].
         */
        val commodities = mutableMapOf(
            Commodity.Opium to 0,
            Commodity.Silk to 0,
            Commodity.Arms to 0,
            Commodity.General to 0
        )

        var cannons = 5
        var health = 100
        var cargoUnits = 100
        var vacantCargoSpaces = 50
        var location = Location.HongKong
    }

    object Finance {
        var moneyInBank = 0L
        var cash = 500L
        var debt = 5000L
        val prices = mutableMapOf(
            Commodity.Opium to 1000,
            Commodity.Silk to 100,
            Commodity.Arms to 10,
            Commodity.General to 1
        )
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

    object Casino {
        var monthSinceLastVisit = 0
        var moneySpent = 0L
    }

    internal var monthsPassed = 0

    private fun MutableList<BlackjackCard>.transferTopCardTo(destinationDeck: MutableList<BlackjackCard>) {
        destinationDeck.add(this.removeLast())
    }

    object HorseRacing {
        //structure of a horse
        //TODO: Implement Horse Racing
        data class Horse(
            val name: String,
            var lapNumber: Int = 0,
            var amountOfLapDone: Int = 0,
            var currentStamina: Double = 1.0,
            var currentHopDistance: Int = 0,
            val burstyness: Double = Random.nextDouble()
        )
    }

    internal val globalMultiplier get() = 1.0 + monthsPassed / 10000

    /**
     * Asks the user what and how much they would like to buy ([buying] = true) or sell ([buying] = false).
     */
    private fun exchangeHandler(buying: Boolean) {
        val commodity = ui.inputExchangeWhichCommodity()
        val priceOfProduct = Finance.prices[commodity]!!
        val directionMultiplier = if (buying) +1 else -1
        val numberOfProductsAffordable = Finance.cash / priceOfProduct

        while (true) {
            val numProductsExchanged = ui.inputExchangeHowMany()

            if (
                (buying && numProductsExchanged > numberOfProductsAffordable)
                || (!buying && numProductsExchanged > Ship.commodities[commodity]!!)
            ) {
                if (buying)
                    ui.outputCannotAfford(commodity, numProductsExchanged)
                else
                    ui.outputNotEnoughToSell(commodity, numProductsExchanged)
            } else if (numProductsExchanged >= 0) {
                Ship.commodities[commodity] = Ship.commodities[commodity]!! + directionMultiplier * numProductsExchanged
                Finance.cash -= directionMultiplier * numProductsExchanged * priceOfProduct
                Ship.vacantCargoSpaces -= directionMultiplier * numProductsExchanged
                break
            }
        }
    }

    /**
     * Returns `false` if the ship went down during combat.
     * Always check the output of this function and terminate the main loop if `false`.
     */
    private fun combat(damageC: Double, gunKnockoutChance: Double, numberOfPirates: Int, pirateResistanceC: Double): Boolean {
        val pirateList =
            MutableList(numberOfPirates) {
                (
                    20
                        * (Random.nextDouble() + 0.5)
                        * (globalMultiplier + 0.6)
                        * (pirateResistanceC + 0.5)
                    ).roundToInt()
            }

        combatLoop@ while (true) {
            println("${pirateList.size} ships attacking, Taipan!")
            when (ui.inputFightOrRun()) {
                UserInterface.FightOrRun.Fight -> {
                    if (Ship.cannons > 0) {
                        var piratesSank = 0

                        // Taipan is firing on the pirates
                        repeat(Ship.cannons) {
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
                                Util.pirateGenerator(
                                    1 + pirateList.size / 10,
                                    1 + floor((0.35 * pirateList.size.toDouble())).roundToInt(),
                                    globalMultiplier
                                )
                            println("$numberRanAway buggers ran away, Taipan!")

                            repeat(numberRanAway) {
                                pirateList.removeLast()
                            }
                        }

                        if (pirateList.isEmpty()) {
                            println("We got them all, Taipan!")
                            val booty =
                                (numberOfPirates * Random.nextInt(5, 50) * Random.nextInt(
                                    1,
                                    10
                                ) * (monthsPassed + 1) / 4 +
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

                UserInterface.FightOrRun.Run -> {
                    // Attempt to run away
                    if (Random.nextDouble() <= Ship.cargoUnits.toDouble()
                            .pow(0.9) / (Ship.cargoUnits + 5 * pirateList.size)
                    ) {
                        val numberRanCounter = Util.pirateGenerator(pirateList.size / 5, pirateList.size, globalMultiplier)
                        if (numberRanCounter == pirateList.size) {
                            println("We got away from them, Taipan!")
                            repeat(numberRanCounter) {
                                pirateList.removeLast()
                            }
                        } else if (numberRanCounter > 0) {
                            println("Can't escape them, Taipan, but we managed to lose $numberRanCounter of them!")
                            repeat(numberRanCounter) {
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

            if (pirateList.size <= 0) {
                break@combatLoop
            }

            println("$pirateList")

            val damageToPlayerShip =
                (
                    (25 + monthsPassed) / Ship.cargoUnits.toDouble().pow(1.11)
                        * (damageC + 0.5)
                        * (Random.nextDouble() + 1)
                        * numberOfPirates.toDouble().pow(0.7)
                        * 5
                        * pirateList.size / numberOfPirates
                    ).roundToInt()

            println("They're firing on us, Taipan!")

            if (Random.nextDouble() < gunKnockoutChance && Ship.cannons > 0) {
                Ship.cannons--
                Ship.vacantCargoSpaces += 10
                ui.outputCombatPiratesHitGun()
            } else {
                Ship.health -= damageToPlayerShip
                ui.outputCombatTookDamage(damageToPlayerShip)
            }

            ui.outputCombatDisplayStats()

            if (Ship.health <= 0) {
                ui.outputPiratesSankShip()
                return false
            }
        }

        return true
    }

    fun gameLoop() {
        // Each iteration is a different port.
        mainLoop@ while (true) {
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

                    val amountPaid = ui.inputPayMcHenryHowMuch(shipFixPrice)

                    if (amountPaid > Finance.cash) {
                        ui.outputNotEnoughCashToPayMcHenry()
                    } else if (amountPaid >= shipFixPrice) {
                        // If the player paid what the captain asked for, completely repair the ship
                        Ship.health = 100
                        Finance.cash -= amountPaid
                    } else if (amountPaid >= 0) {
                        // If the player pays x% of what was asked, repair x% of the damaged ship
                        Ship.health += (100 - Ship.health) * amountPaid / shipFixPrice
                        Finance.cash -= amountPaid
                    }
                }

                // Decide whether to activate Li Yuen.
                if (Random.nextDouble() <= LiYuen.chanceOfExtortion && Finance.cash >= 500) {
                    val amountRequested = (
                        1.1 * LiYuen.extortionMultiplier
                            * Finance.cash
                            * (Random.nextDouble() + 0.1)
                        ).roundToInt()
                    if (ui.inputPayLiYuen()) {
                        if (amountRequested > Finance.cash) {
                            if (ui.inputElderBrotherWuMakeUpDifference()) {
                                Finance.debt += amountRequested - Finance.cash
                                Finance.cash = 0
                                LiYuen.chanceOfExtortion = 0.1
                                LiYuen.chanceOfAttack = 0.025
                                LiYuen.extortionMultiplier = 1.0
                            } else {
                                LiYuen.chanceOfExtortion = 0.8
                                LiYuen.chanceOfAttack = 0.5
                            }
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
                }

                LiYuen.chanceOfExtortion += 0.01

                // Money lender
                if (ui.inputBusinessWithElderBrotherWu()) {
                    if (Finance.debt > 0) {
                        val repayAmount = ui.inputRepayElderBrotherWuHowMuch()
                        if (repayAmount < 0) {
                            ui.outputCannotRepayWuNegative(repayAmount)
                        } else if (repayAmount > Finance.cash) {
                            ui.outputNotEnoughCashToRepayWu(repayAmount)
                        } else {
                            Finance.cash -= repayAmount
                            Finance.debt = if (repayAmount > Finance.debt) 0 else repayAmount.toLong()
                        }
                    }
                    if (Finance.cash > 0) {
                        val borrowAmount = ui.inputBorrowElderBrotherWuHowMuch()
                        if (borrowAmount > Finance.cash) {
                            println("He won't loan you so much, Taipan!")
                        } else if (borrowAmount < 0) {
                            println("You can't do that, Taipan!")
                        } else {
                            Finance.cash += borrowAmount
                            Finance.debt += borrowAmount
                        }
                    }
                }
            } else if (LiYuen.chanceOfAttack >= 0.5 && Random.nextDouble() < 0.3) {
                println("Taipan! It's Li Yuen's lieutenant!")
                println("He says that his admiral wishes to see you, posthaste!")
            }

            if (Random.nextDouble() <= Probabilities.portEvent) {
                //Options: More guns, A new ship, Robbed for some amount of money, Opium confiscated from cargo, Opium confiscated from warehouse
                val severity = Random.nextDouble(0.01, 1.0)

                if (Random.nextDouble() <= 0.35 && Finance.cash >= 500) {
                    val shipCost = ((Random.nextDouble() + 0.1) * Finance.cash * 0.35).roundToInt()
                    if (ui.inputTradeShip()) {
                        Ship.cargoUnits += 50
                        Ship.vacantCargoSpaces += 50
                        Finance.cash -= shipCost
                        Ship.health = 100
                    }
                }

                if (Random.nextDouble() <= 0.5 && Finance.cash >= 100) {
                    val gunCost = ((Random.nextDouble() + 0.1) * Finance.cash * 0.5 * 0.3).roundToInt()
                    if (ui.inputAnotherGun()) {
                        if (Ship.vacantCargoSpaces < 10) {
                            println("Your ship will be overburdened, Taipan!")
                        } else {
                            Ship.cannons++
                            Ship.vacantCargoSpaces -= 10
                            Finance.cash -= gunCost
                        }
                    }
                }

                if (Random.nextDouble() <= (Ship.commodities[Commodity.Opium]!! / (Ship.cargoUnits - Ship.cannons * 10))) {
                    val amount = (Finance.cash * severity).roundToInt()
                    Finance.cash -= amount
                    Ship.vacantCargoSpaces += Ship.commodities[Commodity.Opium]!!
                    Ship.commodities[Commodity.Opium] = 0
                    ui.outputFinedForOpium(amount)
                }

                if (Random.nextDouble() <= 0.15) {
                    val robbedAmount = (Random.nextDouble(0.05, 1.0) * Finance.cash).roundToInt()
                    Finance.cash -= robbedAmount
                    ui.outputRobbed(robbedAmount)
                }

                if (Random.nextDouble() <= (Warehouse.commodities[Commodity.Opium]!!) / Warehouse.totalCargoSpaces) {
                    if (Warehouse.commodities[Commodity.Opium]!! > 0.1 * Warehouse.totalCargoSpaces) {
                        val opiumConfiscated = (Warehouse.commodities[Commodity.Opium]!! * severity).roundToInt()
                        Warehouse.commodities[Commodity.Opium] =
                            Warehouse.commodities[Commodity.Opium]!! - opiumConfiscated
                        Warehouse.vacantCargoSpaces += opiumConfiscated
                        ui.outputWarehouseRaided(opiumConfiscated)
                    } else {
                        ui.outputWarehouseRaided(null)
                    }
                }

                Probabilities.portEvent = (Random.nextDouble() + 0.5) * 0.5
            }

            val maxes = mapOf(
                Commodity.Opium to 1000,
                Commodity.Silk to 100,
                Commodity.Arms to 10,
                Commodity.General to 1
            )
            val mins = mapOf(
                Commodity.Opium to 2,
                Commodity.Silk to 2,
                Commodity.Arms to 1,
                Commodity.General to 1
            )

            if (Random.nextDouble() <= 0.9) {
                for (commodity in Commodity.values()) {
                    Finance.prices[commodity] = Util.priceGenerator(maxes[commodity]!!, mins[commodity]!!, globalMultiplier)
                }
            } else {
                val commoditySelected = Commodity.values().random()
                for (commodity in Commodity.values()) {
                    Finance.prices[commodity] =
                        if (commodity == commoditySelected)
                            Util.randomPriceGenerator(maxes[commodity]!!, globalMultiplier)
                        else
                            Util.priceGenerator(maxes[commodity]!!, mins[commodity]!!, globalMultiplier)
                }
                ui.outputPricesAreWild(commoditySelected)
            }

            tradingLoop@ while (true) {
                ui.outputDisplayInformationBeforeTradingLoopAction()

                val inHongKong = Ship.location == Location.HongKong

                // Prompt the user.
                when (ui.inputTradingLoopAction()) {
                    UserInterface.TradingLoopAction.Buy -> exchangeHandler(buying = true)
                    UserInterface.TradingLoopAction.Sell -> exchangeHandler(buying = false)
                    UserInterface.TradingLoopAction.VisitCasino -> if (Ship.location == Location.Shanghai) {
                        ui.outputEnteringCasino()
                        casinoLoop@ while (true) {
                            when (ui.inputWhichGame()) {
                                UserInterface.Game.Blackjack -> {
                                    val deck =
                                        mutableListOf(
                                            BlackjackCard.A, BlackjackCard.A, BlackjackCard.A, BlackjackCard.A,
                                            BlackjackCard._2, BlackjackCard._2, BlackjackCard._2, BlackjackCard._2,
                                            BlackjackCard._3, BlackjackCard._3, BlackjackCard._3, BlackjackCard._3,
                                            BlackjackCard._4, BlackjackCard._4, BlackjackCard._4, BlackjackCard._4,
                                            BlackjackCard._5, BlackjackCard._5, BlackjackCard._5, BlackjackCard._5,
                                            BlackjackCard._6, BlackjackCard._6, BlackjackCard._6, BlackjackCard._6,
                                            BlackjackCard._7, BlackjackCard._7, BlackjackCard._7, BlackjackCard._7,
                                            BlackjackCard._8, BlackjackCard._8, BlackjackCard._8, BlackjackCard._8,
                                            BlackjackCard._9, BlackjackCard._9, BlackjackCard._9, BlackjackCard._9,
                                            BlackjackCard._10, BlackjackCard._10, BlackjackCard._10, BlackjackCard._10,
                                            BlackjackCard.J, BlackjackCard.J, BlackjackCard.J, BlackjackCard.J,
                                            BlackjackCard.Q, BlackjackCard.Q, BlackjackCard.Q, BlackjackCard.Q,
                                            BlackjackCard.K, BlackjackCard.K, BlackjackCard.K, BlackjackCard.K,
                                        )

                                    while (true) {
                                        while (true) {
                                            when (val bet = ui.inputBlackjackBet()) {
                                                0 -> break
                                                else -> {
                                                    if (bet > Finance.cash) {
                                                        ui.outputBlackjackBetTooLarge(bet)
                                                    } else {
                                                        val gameDeck = deck.shuffled().toMutableList()
                                                        val playerDeckNotVisible = mutableListOf<BlackjackCard>().also {
                                                            gameDeck.transferTopCardTo(it)
                                                        }
                                                        val dealerDeckVisible = mutableListOf<BlackjackCard>().also {
                                                            gameDeck.transferTopCardTo(it)
                                                        }
                                                        val playerDeckVisible = mutableListOf<BlackjackCard>().also {
                                                            gameDeck.transferTopCardTo(it)
                                                        }
                                                        val dealerDeckNotVisible = mutableListOf<BlackjackCard>().also {
                                                            gameDeck.transferTopCardTo(it)
                                                        }
                                                        var bust = false
                                                        var stay = false
                                                        var doubleMultiplier = 1L
                                                        var playerSum = 0
                                                        var dealerSum = 0

                                                        fun youHadABlackjack() {
                                                            println(
                                                                "You won ${
                                                                    (bet * 5.0 / 2.0 * doubleMultiplier).roundToInt().toLong()
                                                                } cash!"
                                                            )
                                                            Finance.cash += (bet * 5.0 / 2.0).roundToInt().toLong() * doubleMultiplier
                                                        }

                                                        fun bust() {
                                                            println("Your hidden card: $playerDeckNotVisible")
                                                            println("Your visible deck: $playerDeckVisible")
                                                            println("The dealer's visible card: $dealerDeckVisible")
                                                            println("Your sum: $playerSum")
                                                            println("You went bust!")
                                                            println("You lost $bet cash!")
                                                            bust = true
                                                        }

                                                        println("Elder Brother He has dealt the cards.")

                                                        while (!bust && !stay) {
                                                            playerSum = Util.blackjackGetSum(playerDeckNotVisible, playerDeckVisible)
                                                            println("Your hidden card: $playerDeckNotVisible")
                                                            println("Your visible deck: $playerDeckVisible")
                                                            println("The dealer's visible card: $dealerDeckVisible")
                                                            println("Your sum: $playerSum")

                                                            when (ui.inputBlackjackWhatAction()) {
                                                                UserInterface.BlackjackAction.Hit -> {
                                                                    gameDeck.transferTopCardTo(playerDeckVisible)
                                                                    playerSum = Util.blackjackGetSum(playerDeckNotVisible, playerDeckVisible)

                                                                    if (playerSum > 21) {
                                                                        bust()
                                                                        Finance.cash -= bet
                                                                    }
                                                                }

                                                                UserInterface.BlackjackAction.Stay -> {
                                                                    stay = true
                                                                }

                                                                UserInterface.BlackjackAction.DoubleDown -> {
                                                                    if (playerDeckVisible.size + playerDeckNotVisible.size == 2) {
                                                                        gameDeck.transferTopCardTo(playerDeckVisible)
                                                                        playerSum = Util.blackjackGetSum(playerDeckNotVisible, playerDeckVisible)
                                                                        val newBet = bet * 2

                                                                        if (playerSum > 21) {
                                                                            bust()
                                                                            if (Finance.cash - newBet < 0) {
                                                                                Finance.debt = newBet - Finance.cash
                                                                                Finance.cash = 0
                                                                            } else {
                                                                                Finance.cash -= newBet
                                                                            }
                                                                        }
                                                                        doubleMultiplier = 2L
                                                                        stay = true
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        while (dealerSum <= 17 && !bust) {
                                                            gameDeck.transferTopCardTo(dealerDeckNotVisible)
                                                            dealerSum = Util.blackjackGetSum(dealerDeckVisible, dealerDeckNotVisible)
                                                            if (dealerSum > 21) {
                                                                println("The dealer went bust!")
                                                                if (BlackjackCard.J in playerDeckVisible && BlackjackCard.A in playerDeckNotVisible || BlackjackCard.A in playerDeckVisible && BlackjackCard.J in playerDeckNotVisible) {
                                                                    println("You had a blackjack!")
                                                                    youHadABlackjack()
                                                                } else {
                                                                    println("You won ${bet * doubleMultiplier} cash!")
                                                                    Finance.cash += bet * doubleMultiplier
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
                                                                if (BlackjackCard.J in playerDeckVisible && BlackjackCard.A in playerDeckNotVisible || BlackjackCard.A in playerDeckVisible && BlackjackCard.J in playerDeckNotVisible) {
                                                                    println("Your sum was greater and you had a blackjack!")
                                                                    youHadABlackjack()
                                                                } else {
                                                                    println("Your sum was greater!")
                                                                    println("You won ${bet * doubleMultiplier} cash!")
                                                                    Finance.cash += bet * doubleMultiplier
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

                                                        break
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                UserInterface.Game.Doubles -> while (true) {
                                    when (val bet = ui.inputDoublesBetAmount()) {
                                        0 -> break
                                        else -> {
                                            if (bet !in 10..Finance.cash) {
                                                ui.outputDoublesInvalidBet(bet)
                                            } else {
                                                var times = 0
                                                while (Random.nextDouble() <= 0.5) {
                                                    times++
                                                }
                                                val cashWon =
                                                    if (times == 0) 0
                                                    else bet / 10 * 2.0.pow(times).toLong()
                                                Finance.cash += cashWon - bet.toLong()
                                                ui.outputDoublesResult(cashWon)
                                                break
                                            }
                                        }
                                    }
                                }

                                UserInterface.Game.Slots -> {
                                }

                                UserInterface.Game.Poker -> {
                                }

                                UserInterface.Game.Roulette -> {
                                }

                                UserInterface.Game.Keno ->
                                    while (true) {
                                        when (val bet = ui.inputKenoBetAmount()) {
                                            0 -> break
                                            else -> {
                                                if (bet !in 10..Finance.cash) {
                                                    ui.outputKenoInvalidBet(bet)
                                                } else {
                                                    Finance.cash -= bet.toLong()
                                                    val guesses = mutableListOf<Int>()
                                                    val answers = (0 until 5)
                                                        .map { (1..10).random() }

                                                    repeat(answers.size) {
                                                        while (true) {
                                                            val guess = ui.inputKenoGuess()
                                                            if (guess !in 1..10) {
                                                                ui.outputKenoInvalidGuess(guess)
                                                            } else {
                                                                guesses += guess
                                                                break
                                                            }
                                                        }
                                                    }

                                                    val numCorrectAnswers = answers.indices.count { guesses[it] == answers[it] }
                                                    val cashWon =
                                                        if (numCorrectAnswers == 0) 0L
                                                        else (5.0).pow(numCorrectAnswers.toDouble()).toLong() * bet

                                                    ui.outputKenoResults(answers, numCorrectAnswers)

                                                    Finance.cash += cashWon
                                                    break
                                                }
                                            }
                                        }
                                    }

                                UserInterface.Game.ExitCasino -> break@casinoLoop
                            }
                        }
                    }
                    UserInterface.TradingLoopAction.VisitBank -> if (inHongKong) {
                        while (true) {
                            val cashToDeposit = ui.inputDepositHowMuch()

                            if (cashToDeposit > Finance.cash) {
                                ui.outputNotEnoughCashToDeposit()
                            } else if (cashToDeposit >= 0) {
                                Finance.cash -= cashToDeposit
                                Finance.moneyInBank += cashToDeposit
                                break
                            }
                        }

                        while (true) {
                            val cashToWithdraw = ui.inputWithdrawHowMuch()

                            if (cashToWithdraw > Finance.moneyInBank) {
                                ui.outputNotEnoughCashToWithdraw()
                            } else if (cashToWithdraw >= 0) {
                                Finance.cash += cashToWithdraw
                                Finance.moneyInBank -= cashToWithdraw
                                break
                            }
                        }
                    }

                    UserInterface.TradingLoopAction.TransferCargo -> if (inHongKong) {
                        if ((Ship.commodities.values + Warehouse.commodities.values)
                                .all { it == 0 }
                        ) {
                            ui.outputNoCargoToMove()
                        } else {
                            for (toWarehouse in listOf(true, false)) {
                                for (commodity in Commodity.values()) {
                                    val directionMultiplier = if (toWarehouse) +1 else -1
                                    val amountAvailableToMove =
                                        (if (toWarehouse) Ship.commodities else Warehouse.commodities)[commodity]!!

                                    if (amountAvailableToMove > 0) {
                                        while (true) {
                                            val amountToMove = ui.inputMoveHowMuch()
                                            if (amountToMove > amountAvailableToMove) {
                                                ui.outputNotEnoughToMoveToWarehouse(amountAvailableToMove)
                                            } else if (Warehouse.vacantCargoSpaces - amountToMove < 0 && toWarehouse) {
                                                ui.outputNotEnoughSpaceInWarehouse()
                                            } else if (amountToMove >= 0) {
                                                Ship.vacantCargoSpaces += directionMultiplier * amountToMove
                                                Warehouse.vacantCargoSpaces -= directionMultiplier * amountToMove
                                                Warehouse.commodities[commodity] =
                                                    Warehouse.commodities[commodity]!! + directionMultiplier * amountToMove
                                                Ship.commodities[commodity] =
                                                    Ship.commodities[commodity]!! - directionMultiplier * amountToMove
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    UserInterface.TradingLoopAction.Quit -> if (Ship.vacantCargoSpaces < 0) {
                        ui.outputCannotQuitOverburdened()
                    } else {
                        while (true) {
                            val newLocation = ui.inputGoWhere()

                            if (newLocation == Ship.location) {
                                ui.outputAlreadyHere()
                            } else {
                                Ship.location = newLocation
                                break
                            }
                        }

                        break
                    }

                    UserInterface.TradingLoopAction.Retire -> if (inHongKong && Finance.moneyInBank + Finance.cash >= 1000000) {
                        break@mainLoop
                    }
                }
            }

            val isPirateFleetLiYuen = Random.nextDouble() <= LiYuen.chanceOfAttack

            // Pirate attack by Li Yuen
            if (isPirateFleetLiYuen) {
                val number = Util.pirateGenerator(
                    floor((monthsPassed + 1) / 4.0 + floor(Ship.cargoUnits / 50.0)).roundToInt(),
                    (10 + 2 * floor((monthsPassed + 1) / 4.0 + Ship.cargoUnits / 50.0).roundToInt()),
                    globalMultiplier
                )

                ui.outputLiYuenPirateAttack(number)

                if (!combat(2.0, 0.2, number, 2.0))
                    break@mainLoop
                LiYuen.chanceOfAttack = 0.5
                LiYuen.chanceOfExtortion = 0.8
            }

            // Other pirate attack
            if (Random.nextDouble() <= Probabilities.pirateAttack && !isPirateFleetLiYuen) {
                val number = Util.pirateGenerator(
                    floor((monthsPassed + 1) / 6.0 + floor(Ship.cargoUnits / 100.0)).roundToInt(),
                    (5 + 2 * floor((monthsPassed + 1) / 6.0 + Ship.cargoUnits / 75.0).roundToInt() + floor(Ship.commodities[Commodity.Opium]!!.toDouble() * 0.1 * Random.nextDouble()).roundToInt()),
                    globalMultiplier
                )
                ui.outputPirateAttack(numShips = number)
                if (!combat(1.5, 0.1, number, 1.5))
                    break@mainLoop
                Probabilities.pirateAttack =
                    (Random.nextDouble() + 0.05 + Ship.commodities[Commodity.Opium]!!.toDouble() / Ship.vacantCargoSpaces.toDouble()) * 0.25
            }

            // Storm
            if (Random.nextDouble() <= Probabilities.storm) {
                ui.outputStorm()
                if (Random.nextDouble() <= (100.0 - Ship.health) / 1000.0) {
                    ui.outputStormGoingDown()
                    break@mainLoop
                } else {
                    ui.outputSurvivedStorm()
                    // Storm moves ship to different location
                    if (Random.nextDouble() < 0.35) {
                        Ship.location = Location.values().random()
                        ui.outputBlownOffCourse()
                    }
                    Probabilities.storm = (Random.nextDouble() + 0.05) * 0.5
                }
            }

            // Adjust values for next location.
            ++monthsPassed
            Finance.debt = (Finance.debt * 1.2).toLong()
            Finance.moneyInBank = (Finance.moneyInBank * 1.05).toLong()
            Casino.monthSinceLastVisit++
            ui.outputArrivingNextLocation()
        }

        val netWorth = Finance.cash + Finance.moneyInBank - Finance.debt

        ui.outputFinalStats(score = netWorth / (monthsPassed + 1) / 100)
    }
}