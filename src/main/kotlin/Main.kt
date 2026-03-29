import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.Color
import java.awt.Font
import javax.swing.*

/**
 * Application entry point
 */
fun main() {
    FlatMacDarkLaf.setup()          // Initialise the LAF

    val game = Game()
    val window = MainWindow(game)    // Spawn the UI, passing in the app state



    SwingUtilities.invokeLater { window.show() }
}





