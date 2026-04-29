import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListModel
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun ImageIcon.scaled(width: Int, height: Int): ImageIcon =
    ImageIcon(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH))
/**
 * Main UI window, handles user clicks, etc.
 *
 * @param game the game state object
 */
class MainWindow(val game: Game) {
    val frame = JFrame("Space Game")

    private val panel = JPanel().apply { layout = GridBagLayout() }

    private val titleLabel = JLabel("Space Game")
    private val itemsTitle = JLabel("Inventory:")

    private val westButton = JButton("West")
    private val eastButton = JButton("East")
    private val northButton = JButton("North")
    private val southButton = JButton("South")




    private val currentPlanetNameLabel = JLabel("")
    private val currentPlanetDescriptionLabel = JLabel("<html>")

    private val currentPlanetImageLabel = JLabel()

    private val nextPlanetButton = JButton("")
    private val previousPlanetButton = JButton("")




    private val currentLocationNodeLabel = JLabel("")

    private val itemPickupButton = JButton("Pick up")





    private val locationPanel = JPanel().apply { layout = GridBagLayout() }

    private val planetPanel = JPanel().apply { layout = GridBagLayout() }

    private val itemsPanel = JPanel().apply { layout = GridBagLayout() }

    private val model = DefaultListModel<String>()
    private val inventoryList = JList(model)


    val introWindow = IntroWindow(this)





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



        locationPanel.add(northButton)
        locationPanel.add(eastButton)
        locationPanel.add(southButton)
        locationPanel.add(westButton)

        gbc.gridy = 1
        locationPanel.add(currentLocationNodeLabel, gbc)

        gbc.gridy = 2
        locationPanel.add(itemPickupButton ,gbc)


        gbc.gridx = 0
        gbc.gridy = 0
        gbc.insets = Insets(10, 10, 10, 10)
        gbc.fill = GridBagConstraints.HORIZONTAL
        planetPanel.add(currentPlanetNameLabel, gbc)


        gbc.gridx = 0
        gbc.gridy = 2
        gbc.fill = GridBagConstraints.HORIZONTAL
        planetPanel.add(currentPlanetDescriptionLabel, gbc)

        gbc.gridy = 3
        gbc.fill = GridBagConstraints.NONE
        planetPanel.add(previousPlanetButton, gbc)
        gbc.gridx = 1
        gbc.gridy = 3
        planetPanel.add(nextPlanetButton,gbc)

        gbc.gridx = 1
        gbc.gridy = 4
        planetPanel.add(currentPlanetImageLabel, gbc)


        itemsPanel.add(itemsTitle)
        itemsPanel.add(inventoryList)


        gbc.gridx = 0
        gbc.gridy = 1
        panel.add(locationPanel, gbc)
        gbc.gridy = 2
        panel.add(planetPanel, gbc)
//        gbc.gridx = 1
        gbc.gridy = 3
        panel.add(itemsPanel, gbc)

    }

    private fun setupStyles() {
        titleLabel.font = Font(Font.SANS_SERIF, Font.BOLD, 32)
        currentPlanetNameLabel.font = Font(Font.SANS_SERIF, Font.PLAIN, 24)
        planetPanel.background = java.awt.Color.RED
        locationPanel.background = java.awt.Color.BLUE
        itemsPanel.background = java.awt.Color(10,30,100)

    }

    private fun setupWindow() {
        frame.isResizable = false                           // Can't resize
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE  // Exit northon window close
        frame.contentPane = panel                           // Define the main content
        frame.pack()
        frame.setLocationRelativeTo(null)                   // Centre on the screen
    }

    private fun setupActions() {
        nextPlanetButton.addActionListener { handlePlanetClick(Direction.LEFT) }
        previousPlanetButton.addActionListener { handlePlanetClick(Direction.RIGHT) }

        itemPickupButton.addActionListener { handlePickupItem() }

        westButton.addActionListener { handleLocationClick(Direction.LEFT) }
        eastButton.addActionListener { handleLocationClick(Direction.RIGHT) }
        northButton.addActionListener { handleLocationClick(Direction.UP) }
        southButton.addActionListener { handleLocationClick(Direction.DOWN) }
    }

    private fun handlePlanetClick(direction: Direction) {
        game.travelPlanetRelative(direction)
        updateUI()
    }

    private fun handleLocationClick(direction: Direction) {
        game.travelLocation(direction)
        updateUI()
    }

    private fun handlePickupItem() {
        game.pickupItem()
        updateUI()
    }




    fun updateUI() {
        currentPlanetNameLabel.text = "Planet: ${game.currentPlanet.name}"
        currentPlanetDescriptionLabel.text = "<html>${game.currentPlanet.description}"
        currentLocationNodeLabel.text = game.currentLocation.name

        itemPickupButton.isEnabled = game.locationItem != null
        itemPickupButton.text = if (game.locationItem!=null) "Pick Up: ${game.locationItem?.name}" else "Nothing Here"

//        westButton.background = if(game.locationWest!=null && game.locationWest!!.isLocked()) java.awt.Color(100,0,0) else java.awt.Color(5,100,5)
//        northButton.background = if(game.locationNorth!=null && game.locationNorth!!.isLocked()) java.awt.Color(100,0,0) else java.awt.Color(5,100,5)
//        eastButton.background = if(game.locationEast!=null && game.locationEast!!.isLocked()) java.awt.Color(100,0,0) else java.awt.Color(5,100,5)
//        southButton.background = if(game.locationSouth!=null && game.locationSouth!!.isLocked()) java.awt.Color(100,0,0) else java.awt.Color(5,100,5)

        westButton.isEnabled = game.locationWest!=null// && !game.locationWest!!.isLocked()
        northButton.isEnabled = game.locationNorth!=null// && !game.locationNorth!!.isLocked()
        eastButton.isEnabled = game.locationEast!=null //&& !game.locationEast!!.isLocked()
        southButton.isEnabled = game.locationSouth!=null //&& !game.locationSouth!!.isLocked()

        //if(game.locationNorth?.isLocked() ?: false)
            UIManager.put("northButton.background", java.awt.Color.RED)



        nextPlanetButton.text = if(game.nextPlanet==null) "No Next" else "Next: ${game.nextPlanet!!.name}"
        previousPlanetButton.text = if(game.previousPlanet==null) "No Previous" else "Prev: ${game.previousPlanet!!.name}"

        nextPlanetButton.isEnabled = game.nextPlanet!=null
        previousPlanetButton.isEnabled = game.previousPlanet!=null

        model.removeAllElements()
        inventory.forEachIndexed { index, item ->
            model.add(index,"${item.name} - ${item.getDescription()}")
        }

        if(game.currentPlanet.imageFile!=null) {
            currentPlanetImageLabel.isVisible = true
            currentPlanetNameLabel.icon = ImageIcon(ClassLoader.getSystemResource(game.currentPlanet.imageFile)).scaled(220,220)
        } else {
            currentPlanetImageLabel.isVisible = false
        }

    }

    fun show() {
        frame.isVisible = true
    }

    fun startup() {
        frame.isVisible = false
        introWindow.show()

    }
}






