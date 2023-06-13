package taipan

/**
 * A set of functions that interface the API with a command-line interface, graphical interface, etc.
 *
 * The API will call the corresponding function when some update from the UI is required and pass the entire game object
 * along with it. Any necessary status updates will have occurred prior to the call.
 */
interface UserInterface {
    enum class FightOrRun {
        Fight, Run
    }

    enum class BlackjackAction {
        Hit, DoubleDown, Stay
    }

    enum class Game {
        Blackjack, Doubles, Slots, Poker, Roulette, Keno, ExitCasino
    }

    enum class TradingLoopAction {
        Buy, Sell, VisitCasino, VisitBank, TransferCargo, Quit, Retire
    }

    fun inputBlackjackBet(): Int
    fun inputBlackjackWhatAction(): BlackjackAction
    fun inputDoublesBetAmount(): Int
    fun inputKenoBetAmount(): Int
    fun inputKenoGuess(): Int
    fun inputExchangeWhichCommodity(): Commodity
    fun inputExchangeHowMany(): Int
    fun inputFightOrRun(): FightOrRun
    fun inputWhichGame(): Game

    /**
     * ## Summary
     * When the ship has just arrived at Hong Kong with less than 100 health, Captain McHenry will offer to repair
     * the ship for a given price. The user does not have to pay the entire price, but the more they pay, the more
     * their ship will be repaired. Paying more than McHenry asks does not yield any more benefits than paying
     * exactly what he asked.
     *
     * ## Conditions
     * - Ship has just arrived at a port
     * - `Ship.location == Location.HongKong`
     * - `Ship.health < 100`
     */
    fun inputPayMcHenryHowMuch(shipFixPrice: Int): Int
    fun inputPayLiYuen(): Boolean
    fun inputElderBrotherWuMakeUpDifference(): Boolean
    fun inputBusinessWithElderBrotherWu(): Boolean
    fun inputRepayElderBrotherWuHowMuch(): Int
    fun inputBorrowElderBrotherWuHowMuch(): Int
    fun inputTradeShip(): Boolean
    fun inputAnotherGun(): Boolean
    fun inputTradingLoopAction(): TradingLoopAction
    fun inputDepositHowMuch(): Int
    fun inputWithdrawHowMuch(): Int
    fun inputMoveHowMuch(): Int
    fun inputGoWhere(): Location

    fun outputCannotAfford(commodity: Commodity, quantity: Int)
    fun outputNotEnoughToSell(commodity: Commodity, quantity: Int)
    fun outputCombatPiratesHitGun()
    fun outputCombatTookDamage(damage: Int)
    fun outputCombatDisplayStats()
    fun outputPiratesSankShip()
    fun outputNotEnoughCashToPayMcHenry()
    fun outputCannotRepayWuNegative(repayAmount: Int)
    fun outputNotEnoughCashToRepayWu(repayAmount: Int)
    fun outputFinedForOpium(cashLost: Int)
    fun outputRobbed(cashLost: Int)
    fun outputWarehouseRaided(amountOfOpiumConfiscated: Int?)
    fun outputPricesAreWild(commodity: Commodity)
    fun outputDisplayInformationBeforeTradingLoopAction()
    fun outputEnteringCasino()
    fun outputBlackjackBetTooLarge(bet: Int)
    fun outputDoublesInvalidBet(bet: Int)
    fun outputDoublesResult(cashWon: Long)
    fun outputKenoInvalidBet(bet: Int)
    fun outputKenoInvalidGuess(guess: Int)
    fun outputKenoResults(winningNumbers: List<Int>, numCorrectAnswers: Int)
    fun outputNotEnoughCashToDeposit()
    fun outputNotEnoughCashToWithdraw()
    fun outputNoCargoToMove()
    fun outputNotEnoughToMoveToWarehouse(amountAvailable: Int)
    fun outputNotEnoughSpaceInWarehouse()
    fun outputCannotQuitOverburdened()
    fun outputAlreadyHere()
    fun outputLiYuenPirateAttack(numShips: Int)
    fun outputPirateAttack(numShips: Int)
    fun outputArrivingNextLocation()
    fun outputStorm()
    fun outputSurvivedStorm()
    fun outputBlownOffCourse()
    fun outputStormGoingDown()
    fun outputFinalStats(score: Long)
}