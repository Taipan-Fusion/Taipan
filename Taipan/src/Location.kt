package taipan

/**
 * The ship can be at any of the below ports at any given time.
 *
 * TODO (Issue #7) Change the name of [Location] to `Port` and add information and unique characteristics to each `Port`.
 */
@Suppress("unused")
enum class Location(val id: Int) {
    HongKong(1),
    Shanghai(2),
    Nagasaki(3),
    Saigon(4),
    Manila(5),
    Singapore(6),
    Batavia(7)
}