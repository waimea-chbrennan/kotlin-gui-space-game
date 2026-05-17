import com.google.gson.Gson

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
        validateGameData()

        currentPlanet = planets[0]
        //Init with location of start planet, if none then data is corrupt
        currentLocation = locations.find { it.id == currentPlanet.startLocationId }!!

    }

    /**
     * gets planet data from bundled JSON rather than use stupid amounts of inline object declaration.
     *
     * Does basic checking of data integrity but nothing for validity
     */
    fun loadPlanets() {
        val stream = ClassLoader.getSystemResourceAsStream(DATA_DIR + "planets.json") //finds and gets filestream, can't hardcode due to jar bundling
        val content: String? = stream?.bufferedReader()?.readText() //convert filestream to string
        if (content.isNullOrEmpty()) error("Planets data is needed to run the game") //exit if no data, no planets will be no fun in this game
        val newPlanets = Gson().fromJson(content, Array<Planet>::class.java) //Cast string to list of planets
        if(newPlanets.isEmpty()) error("Planets data does not contain any planets") //There should always be at lright one planet
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
     * Perform linked checks between planets, locations and items to make sure our data actually gives us a valid and playable map
     */
    fun validateGameData() {
        //Each planet needs to point at a valid LocationNode to start
        planets.forEach { planet ->
            if (locations.find { it.id == planet.startLocationId } == null) error("Planet: ${planet.name} does not have a valid startLocation: ${planet.startLocationId}")
        }

        //Check that each location that is refrenced exists, and points back to the proper location
        locations.forEach { location ->
            //Iterate using lambda getters allowing to pass inverse direction into loop
            listOf(
                { location: LocationNode -> location.upId } to { location: LocationNode -> location.downId },
                { location: LocationNode -> location.rightId } to { location: LocationNode -> location.leftId },
                { location: LocationNode -> location.downId } to { location: LocationNode -> location.upId },
                { location: LocationNode -> location.leftId } to { location: LocationNode -> location.rightId },
            ).forEach { (getDirectionId, getInverseDirectionId) -> //directionId used as any of {upId, rightId, downId, leftId}
                //okay to have an empty directionId as this is a 'wall'
                if (getDirectionId(location).isEmpty()) return@forEach

                //If direction not empty, then check if the referenced directionId exists.
                val referencedLocation: LocationNode = locations.find { it.id == getDirectionId(location) } ?: error("DirectionId: ${getDirectionId(location)} from Location: ${location.id} is not a valid location.")

                //the referenced location should also point back at original location, in opposite way
                if (getInverseDirectionId(referencedLocation) != location.id) error("Location ${location.id} references ${referencedLocation.id}, but is not refrenced back.")
            }
        }
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
    fun travelLocation(direction: Direction) {
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
        //find if locked item is in inventory and enabled
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

    /**
     * Item description is different if enabled/disabled so UI needs to account for this.
     * @return appropriate item description
     */
    fun getDescription(): String { //Used to get the correct description rather than checking in the UI
        return if (enabled) {enabledDescription} else {disabledDescription}
    }

    /**
     * Instructs the UI whether we should show this item in inventory list.
     * Items that are dependencies of other items and have been used may not want to be shown by our UI.
     * @return true if item needs to be displayed, false if it can be safely hidden without confusion
     */
    fun shouldDisplay(): Boolean {
        return inventory.none { it.dependsOn?.equals(this.id) ?: false} //dependsOn is nullable which necessitates the predicate to have a null safe comparison
    }

}