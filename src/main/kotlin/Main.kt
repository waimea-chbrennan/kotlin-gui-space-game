/**
 * =====================================================================
 * Programming Project for NCEA Level 3, Standard 91906
 * ---------------------------------------------------------------------
 * Project Name:   kotlin-gui-space-game
 * Project Author: Connor Brennan
 * GitHub Repo: https://github.com/waimea-chbrennan/kotlin-gui-space-game
 * ---------------------------------------------------------------------
 * Notes:
 * Can you escape the solar system before the sun explodes?
 * =====================================================================
 */


import com.formdev.flatlaf.themes.FlatMacDarkLaf
import javax.swing.*

/**
 * Game Constants
 */
const val WINNING_ITEM_ID = "escape_ship"
const val COUNTDOWN_TIME_SECONDS: Int = 120
const val CHARACTER_TYPING_DELAY = 15 //Constant for animation of the typing effect for intro window. Larger number is longer animation


//Don't show seconds in paragraph if expressible with integer minutes.
val COUNTDOWN_TIME_STRING = if (COUNTDOWN_TIME_SECONDS%60==0) "${COUNTDOWN_TIME_SECONDS/60} minutes" else "${COUNTDOWN_TIME_SECONDS/60} minutes and ${COUNTDOWN_TIME_SECONDS%60} seconds"

val INTRO_LORE_PARAGRAPH = """<html><div style='text-align: center;'>You are a space person with a space ship stuck in the Quases system with an unstable star. <br>Can you navigate the evacuated system and collect what you need to stabilize the star? <br>You have $COUNTDOWN_TIME_STRING. <br>They are relying on you. <br>Good Luck."""
const val INTRO_INSTRUCTION_PARAGRAPH = """<html><div style='text-align: center;'>How To Play: <br>Travelling between planets will allow you to discover a new set of locations. <br>Some of these locations will be locked and require you to get items from other locations to unlock them. <br>Find a ship with a hyperdrive to escape the system."""



/**
 * Application entry point
 */
fun main() {
    FlatMacDarkLaf.setup()

    val game = Game()
    val window = MainWindow(game)
    SwingUtilities.invokeLater { window.startup() }
}