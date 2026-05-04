import com.formdev.flatlaf.themes.FlatMacDarkLaf
import javax.swing.*

/**
 * Application entry point
 */
fun main() {
    FlatMacDarkLaf.setup()

    val game = Game()
    val window = MainWindow(game)
    SwingUtilities.invokeLater { window.startup() }
}