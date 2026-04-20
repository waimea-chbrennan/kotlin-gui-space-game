import java.io.File
import com.google.gson.Gson
import jdk.jfr.Enabled
import kotlin.math.absoluteValue

const val dataDir = "data/"

val inventory = mutableListOf<Item>()
enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
}

class Game {
    val planets = mutableListOf<Planet>()
    val items = mutableListOf<Item>()

    val locations = mutableListOf<LocationNode>()
    var currentPlanet: Planet



    var currentLocation: LocationNode
    val currentPlanetIndex: Int
        get() = planets.indexOf(currentPlanet)

    val nextPlanet: Planet?
        get() = planets.getOrNull(currentPlanetIndex + 1)

    val previousPlanet: Planet?
        get() = planets.getOrNull(currentPlanetIndex - 1)



    val locationNorth: LocationNode?
        get() = locations.find { it.id == currentLocation.northId }

    val locationEast: LocationNode?
        get() = locations.find { it.id == currentLocation.eastId }

    val locationSouth: LocationNode?
        get() = locations.find { it.id == currentLocation.southId }

    val locationWest: LocationNode?
        get() = locations.find { it.id == currentLocation.westId }


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

        inventory.add(Item("test", "Test", "yes", "no", null, "invalid"))

    }
    fun loadPlanets() {
        val file = File(dataDir + "planets.json")
        if (!file.exists()) error("Planets data is needed to run the game")
        val newPlanets = Gson().fromJson(file.readText(), Array<Planet>::class.java)
        planets.addAll(newPlanets)

    }
    fun loadItems() {
        val file = File(dataDir + "items.json")
        if (!file.exists()) error("Item data is needed to run the game")
        val newItems = Gson().fromJson(file.readText(), Array<Item>::class.java)
        items.addAll(newItems)
    }

    fun loadLocations() {
        val file = File(dataDir + "locations.json")
        if (!file.exists()) error("Location data is needed to run the game")
        val newLocations = Gson().fromJson(file.readText(), Array<LocationNode>::class.java)
        locations.addAll(newLocations)
    }

    fun travelPlanetRelative(position: Int) {
       //don't travel more than one planet for now
        if (position.absoluteValue!=1) return
        if (currentPlanetIndex + position !in planets.indices) return
        currentPlanet = planets[currentPlanetIndex+position]
        currentLocation = locations.find{ it.id == currentPlanet.startLocationId }!!
    }

    fun travelLocation(direction: Direction) { //TODO: reliable checking
        currentLocation = when (direction) {
            Direction.UP -> {locationNorth!!}
            Direction.DOWN -> {locationSouth!!}
            Direction.LEFT -> {locationWest!!}
            Direction.RIGHT -> {locationEast!!}
        }
    }

    fun pickupItem() {
        if(locationItem==null) return
        inventory.add(locationItem!!)
        items.remove(locationItem)
        println("DEBUG: INVENTORY=${inventory.size}")
    }


}

// add base feat if time at end of project
//class Base (
//    val name: String,
//    val description: String,
//    val startLocationNode: LocationNode,
//
//) {
//
//}

class LocationNode (
    val id: String,
    val name: String,
    val lockedByItemId: String,
    val northId: String,
    val eastId: String,
    val southId: String,
    val westId: String,

) {
    var currentItem: Item? = null
    fun isLocked(): Boolean {
        if (lockedByItemId.isEmpty()) return false
        return inventory.find {it.id == lockedByItemId && it.enabled}==null
    }


}


class Planet(
    val name: String,
    val description: String,
    val distance: Long, //Distance to sun
    val startLocationId: String,

    ) {


}


class Item (
    val id: String,
    val name: String,
    private val enabledDescription: String,
    private val disabledDescription: String,
    val dependsOn: String?,
    val locationId: String
) {
    var enabled: Boolean = false
        get() = dependsOn == null || inventory.find { it.id == dependsOn && it.enabled }!=null


    fun getDescription(): String {
        return if (enabled) {enabledDescription} else {disabledDescription}
    }

}