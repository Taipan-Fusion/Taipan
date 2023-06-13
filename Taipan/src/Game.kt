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

    internal var monthsPassed = 0

    internal val yearsPassed get() = monthsPassed / 12
    internal val currentYear get() = 1860 + yearsPassed
    internal val monthName get() = Util.monthNames[monthsPassed % 12]

    object Casino {
        var monthSinceLastVisit = 0
        var visitedBefore = true
        var moneySpent = 0L
    }

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
                println(
                    if (buying) "You can't afford that!"
                    else "You don't have that much ${commodity.name}!"
                )
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

                    println("Captain McHenry of the Hong Kong Consolidated Repair Corporation walks over to your ship and says: <<")
                    println(
                        if (Ship.health < 30)
                            "Matey! That ship of yours is 'bout to rot away like a piece of driftwood in Kowloon Bay! Don't worry, it's nothing I can't fix. For a price, that is!"
                        else if (Ship.health < 50)
                            "That there ship's taken quite a bit of damage, matey! You best get it fixed before you go out to sea again! I can get you sailing the friendly waves in no time! For a price, that is!"
                        else
                            "What a mighty fine ship you have there, matey! Or, shall I say, had... It could really use some of what I call \"Tender Love n' Care\". 'Tis but a scratch, as they say, but I take any job, no matter how small. For a price, that is!"
                    )
                    println("I'll fix you up to full workin' order for $shipFixPrice pound sterling.")

                    val amountPaid = ui.inputPayMcHenryHowMuch()

                    if (amountPaid > Finance.cash) {
                        println("Taipan, you only have ${Finance.cash} cash.")
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
                                println("Elder Brother Wu has given Li Yuen the difference, which will be added to your debt.")
                            } else {
                                println("The difference will not be paid! Elder Brother Wu says, 'I would be wary of pirates if I were you, Taipan!'")
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
                        if (repayAmount > Finance.cash || repayAmount < 0) {
                            println("You can't do that, Taipan!")
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
                val amount = (Finance.cash * severity).roundToInt()
                val amountOfWarehouseOpiumLost = (Warehouse.commodities[Commodity.Opium]!! * severity).roundToInt()
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

                if (Random.nextDouble() <= (Warehouse.commodities[Commodity.Opium]!!) / Warehouse.totalCargoSpaces) {
                    if (Warehouse.commodities[Commodity.Opium]!! > 0.1 * Warehouse.totalCargoSpaces) {
                        println("Taipan! The police raided our warehouse down in Kwun Tong overnight and confiscated $amountOfWarehouseOpiumLost units of opium!")
                        Warehouse.commodities[Commodity.Opium] =
                            Warehouse.commodities[Commodity.Opium]!! - amountOfWarehouseOpiumLost
                        Warehouse.vacantCargoSpaces += amountOfWarehouseOpiumLost
                    } else {
                        println("Taipan! The police raided our warehouse down in Kwun Tong overnight. We didn't have enough opium there to arouse suspicion, so they left without hubbub.")
                    }
                }

                Probabilities.portEvent = (Random.nextDouble() + 0.5) * 0.5
            }

            if (Random.nextDouble() <= 0.9) {
                Finance.prices[Commodity.Opium] = Util.priceGenerator(1000, 2, globalMultiplier)
                Finance.prices[Commodity.Silk] = Util.priceGenerator(100, 2, globalMultiplier)
                Finance.prices[Commodity.Arms] = Util.priceGenerator(10, 1, globalMultiplier)
                Finance.prices[Commodity.General] = Util.priceGenerator(1, 1, globalMultiplier)
            } else {
                val commodityList = listOf("Opium", "Silk", "Arms", "General")
                val num = Random.nextInt(0, 4)
                val commoditySelected = commodityList[num]
                if (commoditySelected == "Opium") {
                    Finance.prices[Commodity.Opium] = Util.randomPriceGenerator(1000, globalMultiplier)
                } else {
                    Finance.prices[Commodity.Opium] = Util.priceGenerator(1000, 2, globalMultiplier)
                }
                if (commoditySelected == "Silk") {
                    Finance.prices[Commodity.Silk] = Util.randomPriceGenerator(100, globalMultiplier)
                } else {
                    Finance.prices[Commodity.Silk] = Util.priceGenerator(100, 2, globalMultiplier)
                }
                if (commoditySelected == "Arms") {
                    Finance.prices[Commodity.Arms] = Util.randomPriceGenerator(10, globalMultiplier)
                } else {
                    Finance.prices[Commodity.Arms] = Util.priceGenerator(10, 1, globalMultiplier)
                }
                if (commoditySelected == "General") {
                    Finance.prices[Commodity.General] = Util.randomPriceGenerator(1, globalMultiplier)
                } else {
                    Finance.prices[Commodity.General] = Util.priceGenerator(1, 1, globalMultiplier)
                }
                println("Taipan!!!")
                println("Prices for $commoditySelected are wild!!!")
            }

            tradingLoop@ while (true) {
                // Display all known information.
                println("Player---------------------------Player")
                println("Bank: ${Finance.moneyInBank}")
                println("Cash: ${Finance.cash}")
                println("Debt: ${Finance.debt}")
                println("Location: ${Ship.location}")
                println("Date: $monthName $currentYear")
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
                when (ui.inputTradingLoopAction()) {
                    UserInterface.TradingLoopAction.Buy -> exchangeHandler(buying = true)
                    UserInterface.TradingLoopAction.Sell -> exchangeHandler(buying = false)
                    UserInterface.TradingLoopAction.VisitCasino -> if (Ship.location == Location.Shanghai) {
                        println(
                            if (Casino.visitedBefore) {
                                if (Casino.monthSinceLastVisit < 4)
                                    "Welcome back, Taipan! The Master of the House has given you your favorite table!\nPick where you left off and have fun!"
                                else
                                    "It's been a while, Taipan! Enjoy yourself!"
                            } else
                                "Welcome to the Shanghai Casino Club, Taipan!"
                        )

                        var leftCasino = false

                        while (!leftCasino) {
                            println("Games available: ")
                            println("Blackjack, Doubles, Slots, Poker, Roulette, and Keno.")

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
                                                        println("You can't bet that much!")
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

                                                        fun youHadABlackjack(message: String) {
                                                            println(message)
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
                                                                    youHadABlackjack("You had a blackjack!")
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
                                                                    youHadABlackjack("Your sum was greater and you had a blackjack!")
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
                                            if (bet < 10 || bet > Finance.cash) {
                                                println("You can't do that!")
                                            } else {
                                                var times = 0
                                                while (Random.nextDouble() <= 0.5) {
                                                    times++
                                                }
                                                val cashWon =
                                                    if (times == 0) 0
                                                    else bet / 10 * 2.0.pow(times).toLong()
                                                Finance.cash += cashWon - bet.toLong()
                                                println("You won $cashWon cash!")
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

                                UserInterface.Game.Keno -> {
                                    val numbersMatchedText = listOf("None", "One", "Two", "Three", "Four", "All")

                                    while (true) {
                                        when (val bet = ui.inputKenoBetAmount()) {
                                            0 -> break
                                            else -> {
                                                if (bet !in 10..Finance.cash) {
                                                    println("You can't do that!")
                                                } else {
                                                    Finance.cash -= bet.toLong()
                                                    val guesses = mutableListOf<Int>()
                                                    val answers = (0 until 5)
                                                        .map { (1..10).random() }

                                                    repeat(answers.size) {
                                                        while (true) {
                                                            val guess = ui.inputKenoGuess()
                                                            if (guess !in 1..10) {
                                                                println("You can't do that!")
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

                                                    println("Winning numbers: ${answers.joinToString(", ")}")
                                                    println("${numbersMatchedText[numCorrectAnswers]} of your numbers matched the winners. You won $cashWon cash!")
                                                    Finance.cash += cashWon
                                                    break
                                                }
                                            }
                                        }
                                    }
                                }

                                UserInterface.Game.ExitCasino -> leftCasino = true
                            }
                        }
                    }
                    UserInterface.TradingLoopAction.VisitBank -> if (inHongKong) {
                        while (true) {
                            val cashToDeposit = ui.inputDepositHowMuch()

                            if (cashToDeposit > Finance.cash) {
                                println("Taipan, you only have ${Finance.cash} in your wallet.")
                            } else if (cashToDeposit >= 0) {
                                Finance.cash -= cashToDeposit
                                Finance.moneyInBank += cashToDeposit
                                break
                            }
                        }

                        while (true) {
                            val cashToWithdraw = ui.inputWithdrawHowMuch()

                            if (cashToWithdraw > Finance.moneyInBank) {
                                println("Taipan, you only have ${Finance.moneyInBank} in your bank.")
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
                            println("You have no cargo, Taipan.")
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
                                                println("You only have $amountAvailableToMove, Taipan.")
                                            } else if (Warehouse.vacantCargoSpaces - amountToMove < 0 && toWarehouse) {
                                                println("There's not enough space in the warehouse, Taipan!")
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
                        println("Your ship would be overburdened, Taipan!")
                    } else {
                        while (true) {
                            val newLocation = ui.inputGoWhere()

                            if (newLocation == Ship.location) {
                                println("You're already here, Taipan.")
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

                println("Li Yuen's pirates, Taipan!")
                println("$number ships of Li Yuen's pirate fleet!")

                if (!combat(2.0, 0.2, number, 2.0)) break@mainLoop
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
                println("$number hostile ships approaching, Taipan!")
                if (!combat(1.5, 0.1, number, 1.5)) break@mainLoop
                Probabilities.pirateAttack =
                    (Random.nextDouble() + 0.05 + Ship.commodities[Commodity.Opium]!!.toDouble() / Ship.vacantCargoSpaces.toDouble()) * 0.25
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
            ++monthsPassed
            Finance.debt = (Finance.debt * 1.2).toLong()
            Finance.moneyInBank = (Finance.moneyInBank * 1.05).toLong()
            Casino.monthSinceLastVisit++
        }

        val netWorth = Finance.cash + Finance.moneyInBank - Finance.debt
        val score = netWorth / (monthsPassed + 1) / 100

        println("FINAL STATS")
        println("Net cash: $netWorth")
        println("Ship size: ${Ship.cargoUnits} units with ${Ship.cannons} guns")
        println("You traded for $yearsPassed year(s)")
        println("Your score is $score")
        println(
            "Rank: ${
                if (score >= 50000) "Ma Tsu"
                else if (score >= 8000) "Master Taipan"
                else if (score >= 1000) "Taipan"
                else if (score >= 500) "Compradore"
                else "Galley Hand"
            }"
        )
    }
}