package taipan

import kotlin.math.roundToInt
import kotlin.random.Random

internal object Util {
    val monthNames = listOf(
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

    /**
     * Generates a price for a particular commodity in the port that Taipan is currently in.
     * Ranges from 5 to 25, with the multiplier depending on the commodity.
     * globalMultiplier acts as natural inflation.
     */
    fun priceGenerator(max: Int, mul: Int, multiplier: Double): Int =
        (
                ((5 * mul)..(25 * mul))
                    .random()
                    .toDouble()
                        * max.toDouble()
                        * multiplier
                        / mul
                ).roundToInt()

    /**
     * Generates a number of pirates immediately before a battle.
     * Depends on multiple factors: type of pirate, amount of commodities onboard, etc...
     * Pirates become more numerous as time passes.
     */
    fun pirateGenerator(min: Int, max: Int, multiplier: Double): Int =
        (
                (min..max)
                    .random()
                    .toDouble()
                        * multiplier
                ).roundToInt()

    /**
     * Generates a random price for a particular commodity.
     * Happens rarely; prices range from 1 to 4 or 50 to 1000 with a multiplier.
     */
    fun randomPriceGenerator(max: Int, multiplier: Double): Int =
        ((if (Random.nextDouble() <= 0.5) (1..4) else (50..1000))
            .random()
            .toDouble()
                * max.toDouble()
                * multiplier
                ).roundToInt()

    fun blackjackGetSum(hiddenDeck: MutableList<BlackjackCard>, visibleDeck: MutableList<BlackjackCard>): Int {
        val sum = hiddenDeck.first().value + visibleDeck.sumOf { it.value }
        return sum +
                if ((BlackjackCard.A in hiddenDeck || BlackjackCard.A in visibleDeck) && sum < 12) 10 else 0
    }
}