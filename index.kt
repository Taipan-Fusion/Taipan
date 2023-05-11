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
                    // TODO Shipyard
                    //fixing whole ship will cost random from 1 to 200 * (1 + (usedcargo / totalcargo))
                    val shipIstTotScalar:Double = 1 + (1 - (100 - Ship.health)/100)
                    val shipPrice:Double = Random.nextInt(1, Ship.cargoUnits) * shipIstTotScalar
                    println("Captain McHenry of the Hong Kong Consolidated Repair Corporation walks over to your ship and says: <<")
                    if(Ship.health < 30){
                        println("Matey! That ship of yours is 'bout to rot away like a peice of driftwood in Kolwoon bay! Dont worry, it's nothing I cant fix. For a price, that is!")
                    } else if(Ship.health < 50){
                        prinln("That there ship's taken quite a bit of damage matey! You best get it fixed before you go out to sea again! I can get you sailing the friendly waves in no time! For a price, that is!")
                    } else {
                        println("What a mighty fine ship you have there, matey! Or, shall I say, had... It could really use some of what I call 'Tender Love n' Care'. 'Tis but a scratch, as they say, but I take any job, no matter how small. For a price, that is!")   
                    }
                    //idk string concat. Ist das richtig?
                    printlin("I'll fix you up to full workin' order for " + shipPrice.toString() + " pound sterling>>")
                    
                    println("Taipan, how much will you pay Captain McHenry? You have " + Player.cashHoldings + " pound sterling on hand.")
                    
                    //
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
