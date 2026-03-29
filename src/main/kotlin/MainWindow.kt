import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * Main UI window, handles user clicks, etc.
 *
 * @param game the game state object
 */
class MainWindow(val game: Game) {
    val frame = JFrame("Space Game")

    private val panel = JPanel().apply { layout = FlowLayout() }

    private val titleLabel = JLabel("Space Game")

    private val leftButton = JButton("Left")
    private val rightButton = JButton("Right")
    private val upButton = JButton("Up")
    private val downButton = JButton("Down")

    private val currentPlanetNameLabel = JLabel("")
    private val currentPlanetDescriptionLabel = JLabel("<html>")
    private val currentLocationNodeLabel = JLabel("")


    private val nextPlanetButton = JButton("")
    private val previousPlanetButton = JButton("")


    private val locationPanel = JPanel().apply { layout = GridBagLayout() }

    private val planetPanel = JPanel().apply { layout = GridBagLayout() }





    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(1280, 720)

//        titleLabel.setBounds(30, 30, 340, 30)

//        previousPlanetButton.setBounds(30, 70, 30, 30)
//        nextPlanetButton.setBounds(65, 70, 30, 30)

        val gbc = GridBagConstraints().apply {}

        panel.add(titleLabel)
        locationPanel.add(leftButton)
        locationPanel.add(rightButton)
        locationPanel.add(upButton)
        locationPanel.add(downButton)
        locationPanel.add(currentLocationNodeLabel)


        gbc.gridx = 0
        gbc.gridy = 0
        gbc.insets = Insets(10, 10, 10, 10)
        gbc.fill = GridBagConstraints.HORIZONTAL
        planetPanel.add(currentPlanetNameLabel, gbc)

        gbc.gridx = 0
        gbc.gridy = 1
        gbc.fill = GridBagConstraints.HORIZONTAL
        planetPanel.add(currentPlanetDescriptionLabel, gbc)

        gbc.gridx = 0
        gbc.gridy = 2
        gbc.fill = GridBagConstraints.NONE
        planetPanel.add(previousPlanetButton, gbc)
        gbc.gridx = 1
        gbc.gridy = 2
        planetPanel.add(nextPlanetButton,gbc)


        panel.add(locationPanel)
        panel.add(planetPanel)

    }

    private fun setupStyles() {
        titleLabel.font = Font(Font.SANS_SERIF, Font.BOLD, 32)
        currentPlanetNameLabel.font = Font(Font.SANS_SERIF, Font.PLAIN, 24)
        planetPanel.background = java.awt.Color.RED
        locationPanel.background = java.awt.Color.BLUE

    }

    private fun setupWindow() {
        frame.isResizable = false                           // Can't resize
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE  // Exit upon window close
        frame.contentPane = panel                           // Define the main content
        frame.pack()
        frame.setLocationRelativeTo(null)                   // Centre on the screen
    }

    private fun setupActions() {
        nextPlanetButton.addActionListener { handlePlanetClick(1) }
        previousPlanetButton.addActionListener { handlePlanetClick(-1) }

        leftButton.addActionListener { handleLocationClick(Direction.LEFT) }
        rightButton.addActionListener { handleLocationClick(Direction.RIGHT) }
        upButton.addActionListener { handleLocationClick(Direction.UP) }
        downButton.addActionListener { handleLocationClick(Direction.DOWN) }
    }

    private fun handlePlanetClick(position: Int) {
        game.travelPlanetRelative(position)
        updateUI()
    }

    private fun handleLocationClick(direction: Direction) {
        game.travelLocation(direction)
        updateUI()
    }




    fun updateUI() {
        currentPlanetNameLabel.text = "Planet: ${game.currentPlanet.name}"
        currentPlanetDescriptionLabel.text = "<html>${game.currentPlanet.description}"
        currentLocationNodeLabel.text = game.currentLocation!!.id.toString()

        leftButton.isEnabled = game.isLocationWest
        upButton.isEnabled = game.isLocationNorth
        rightButton.isEnabled = game.isLocationEast
        downButton.isEnabled = game.isLocationSouth

        nextPlanetButton.text = if(game.nextPlanet==null) "No Next" else "Next: ${game.nextPlanet!!.name}"
        previousPlanetButton.text = if(game.previousPlanet==null) "No Previous" else "Prev: ${game.previousPlanet!!.name}"

        nextPlanetButton.isEnabled = game.nextPlanet!=null
        previousPlanetButton.isEnabled = game.previousPlanet!=null

    }

    fun show() {
        frame.isVisible = true
    }
}

