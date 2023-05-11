import kotlin.random.Random*

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

    public fun run() {
        println("Welcome to Taipan!")

        while (isRunning) {
            if (Player.location == Location.HongKong) {
                if (Ship.health < 100) {
                    // TODO Shipyard
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
