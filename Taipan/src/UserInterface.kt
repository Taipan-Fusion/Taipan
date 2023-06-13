package taipan

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
    fun inputPayMcHenryHowMuch(): Int
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

    fun output
}