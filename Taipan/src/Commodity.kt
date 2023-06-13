package taipan

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
    class UnknownAbbreviationException(input: String) : Exception("`$input` is not a valid commodity.")
}