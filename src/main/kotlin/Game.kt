import java.io.File
import com.google.gson.Gson
import jdk.jfr.Enabled
import kotlin.math.absoluteValue

const val dataDir = "data/"

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
    var currentPlanetIndex: Int = 0
        get() = planets.indexOf(currentPlanet)

    var nextPlanet: Planet? = null
        get() = planets.getOrNull(currentPlanetIndex + 1)

    var previousPlanet: Planet? = null
        get() = planets.getOrNull(currentPlanetIndex - 1)

    var isLocationNorth = false
        get() = currentLocation!!.northId.isNotEmpty()

    var isLocationEast = false
        get() = currentLocation!!.eastId.isNotEmpty()

    var isLocationSouth = false
        get() = currentLocation!!.southId.isNotEmpty()

    var isLocationWest = false
        get() = currentLocation!!.westId.isNotEmpty()




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
       //dont travel more than one planet for now
        if (position.absoluteValue!=1) return
        if (currentPlanetIndex + position !in planets.indices) return
        currentPlanet = planets[currentPlanetIndex+position]
        currentLocation = locations.find{ it.id == currentPlanet.startLocationId }!!
    }

    fun travelLocation(direction: Direction) {
        currentLocation = when (direction) {
            Direction.UP -> {locations.find { it.id == currentLocation.northId }!! }
            Direction.DOWN -> {locations.find { it.id == currentLocation.southId }!! }
            Direction.LEFT -> {locations.find { it.id == currentLocation.eastId }!! }
            Direction.RIGHT -> {locations.find { it.id == currentLocation.westId }!! }
        }
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
    fun isLocked(inventory: List<Item>): Boolean {
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
    val enabledDescription: String,
    val disabledDescription: String,
    val dependsOn: String?
) {
    var enabled: Boolean = false

    fun checkEnable(inventory: List<Item>) {
        if (dependsOn == null) {enabled=true; return}
        for (item in inventory) {
            if (item.id == dependsOn && item.enabled) {enabled=true}
        }
    }

    fun getDescription(): String {
        return if (enabled) {enabledDescription} else {disabledDescription}
    }

}