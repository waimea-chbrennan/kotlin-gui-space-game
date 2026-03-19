import java.io.File
import com.google.gson.Gson

const val dataDir = "data/"


class Game {
    val planets = mutableListOf<Planet>()
    val resources = mutableListOf<Resource>()

    init {
        loadPlanets()
        loadResources()
    }
    fun loadPlanets() {
        val file = File(dataDir + "planets.json")
        if (!file.exists()) error("Planets data is needed to run the game")
        val newPlanets = Gson().fromJson(file.readText(), Array<Planet>::class.java)
        planets.addAll(newPlanets)

    }
    fun loadResources() {
        val file = File(dataDir + "resources.json")
        if (!file.exists()) error("Resource data is needed to run the game")
        val newResources = Gson().fromJson(file.readText(), Array<Resource>::class.java)
        resources.addAll(newResources)
    }


}


class Planet(
    val name: String,
    val diameter: Long,
    val description: String,
    val distance: Long, //Distance to sun
    val gravity: Float,

    ) {


}


class Resource(
    val name: String,
    val description: String,
    val dependsOn: List<Resource>,
    val discoverableBy: List<Resource>,


) {

}