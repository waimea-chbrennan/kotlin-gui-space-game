import java.io.File
import com.google.gson.Gson
import jdk.jfr.Enabled
import kotlin.math.absoluteValue

const val DATA_DIR = "data/"

val inventory = mutableListOf<Item>()
enum class Direction { //Cardinal directions are the valid moves for LocationNode travel
    UP,
    DOWN,
    LEFT,
    RIGHT,
}

enum class LateralDirection { //Lateral directions are for moving back and forth between planets
    LEFT,
    RIGHT
}

/**
 * Stores modifies and provides ways to access game data.
 */
class Game {
    val planets = mutableListOf<Planet>()
    val items = mutableListOf<Item>()

    val locations = mutableListOf<LocationNode>()
    var currentPlanet: Planet

    var currentTime = COUNTDOWN_TIME_SECONDS



    var currentLocation: LocationNode
    val currentPlanetIndex: Int
        get() = planets.indexOf(currentPlanet)

    val nextPlanet: Planet?
        get() = planets.getOrNull(currentPlanetIndex + 1)

    val previousPlanet: Planet?
        get() = planets.getOrNull(currentPlanetIndex - 1)



    val locationNorth: LocationNode?
        get() = locations.find { it.id == currentLocation.upId }

    val locationEast: LocationNode?
        get() = locations.find { it.id == currentLocation.rightId }

    val locationSouth: LocationNode?
        get() = locations.find { it.id == currentLocation.downId }

    val locationWest: LocationNode?
        get() = locations.find { it.id == currentLocation.leftId }


    val locationItem: Item?
        get() = items.find { it.locationId == currentLocation.id }




    init {
        loadPlanets()
        loadItems()
        loadLocations()

        currentPlanet = planets.random()
        currentLocation = locations.find { it.id == currentPlanet.startLocationId } ?: error("Planet or location Data appears to be corrupted.")
        println(currentLocation.northId)
        println(currentLocation.eastId)
        println(currentLocation.southId)
        println(currentLocation.westId)

    }
    fun loadPlanets() {
        val stream = ClassLoader.getSystemResourceAsStream(DATA_DIR + "planets.json")
        val content: String? = stream?.bufferedReader()?.readText()

        if (content.isNullOrEmpty()) error("Planets data is needed to run the game")
        val newPlanets = Gson().fromJson(content, Array<Planet>::class.java)
        planets.addAll(newPlanets)

    }

    /**
     * Reads items from JSON into list of objects.
     * @see loadPlanets
     */
    fun loadItems() {
        val stream = ClassLoader.getSystemResourceAsStream(DATA_DIR + "items.json")
        val content: String? = stream?.bufferedReader()?.readText()
        if (content.isNullOrEmpty()) error("Items data is needed to run the game")
        val newItems = Gson().fromJson(content, Array<Item>::class.java)
        if(newItems.isEmpty()) error("Items data does not appear to have any items")
        items.addAll(newItems)

        if(items.find{it.id==WINNING_ITEM_ID}==null) error("Invalid or no winning item, game is not winnable") //No win state is no fun for player: exit
    }

    /**
     * Reads locations from JSON into list of locations.
     * @see loadPlanets
     */
    fun loadLocations() {
        val stream = ClassLoader.getSystemResourceAsStream(DATA_DIR + "locations.json")
        val content: String? = stream?.bufferedReader()?.readText()
        if (content.isNullOrEmpty()) error("Locations data is needed to run the game")
        val newLocations = Gson().fromJson(content, Array<LocationNode>::class.java)
        if(newLocations.isEmpty()) error("Locations data does not contain any locations")
        locations.addAll(newLocations)
    }

    /**
     * Travels left or right to the directed planet
     *
     * @param direction LEFT or RIGHT from LateralDirection
     */
    fun travelPlanetRelative(direction: LateralDirection) {
        //Safe to handle only left and right cases
        when (direction) {
            LateralDirection.LEFT -> {
                if (currentPlanetIndex + 1 !in planets.indices) return
                currentPlanet = planets[currentPlanetIndex+1]
            }
            LateralDirection.RIGHT -> {
                if (currentPlanetIndex - 1 !in planets.indices) return
                currentPlanet = planets[currentPlanetIndex-1]
            }
        }
        currentLocation = locations.find { it.id == currentPlanet.startLocationId }!!
    }

    /**
     * Moves on-planet currentLocation to the specified direction.
     * @param direction as Direction enum cardinal direction
     */
    fun travelLocation(direction: Direction) { //TODO: reliable checking
        currentLocation = when (direction) {
            Direction.UP -> {locationNorth!!}
            Direction.DOWN -> {locationSouth!!}
            Direction.LEFT -> {locationWest!!}
            Direction.RIGHT -> {locationEast!!}
        }
    }

    /**
     * Moves an item that may be contained in current location to our inventory.
     */
    fun pickupItem() {
        if(locationItem==null) return
        inventory.add(locationItem!!)
        items.remove(locationItem) //Don't want to pick up same item multiple times
    }


}

/**
 * Locations are child of planet and store the possible cardinal moves and whether it can be moved to.
 */
class LocationNode (
    val id: String,
    val name: String,
    val lockedByItemId: String,
    val upId: String,
    val rightId: String,
    val downId: String,
    val leftId: String,

) {
    /**
     * Calculates whether the location is can be travelled to based on inventory
     *
     * Checks for a specific item needed to progress AND if this is item is enabled, which can only happen if
     * all the items that it depends on are also enabled.
     *
     * @return true if locked, player cannot travel here. false if unlocked, is a valid move
     */
    fun isLocked(): Boolean {
        if (lockedByItemId.isEmpty()) return false
        return inventory.find {it.id == lockedByItemId && it.enabled}==null
    }


}

/**
 *  Planet provides a collection of locations to travel between, referenced by startLocationId
 */
class Planet(
    val name: String,
    val description: String,
    val startLocationId: String,
    val imageFile: String?
)

/**
 * Items reference a location until picked up and recursively check whether they can be enabled.
 */
class Item (
    val id: String,
    val name: String,
    private val enabledDescription: String,
    private val disabledDescription: String,
    val dependsOn: String?,
    val locationId: String,
) {
    val enabled: Boolean
        //Enabled if all item dependencies are enabled
        get() = dependsOn == null || inventory.find { it.id == dependsOn && it.enabled }!=null

    fun getDescription(): String { //Used to get the correct description rather than checking in the UI
        return if (enabled) {enabledDescription} else {disabledDescription}
    }

}