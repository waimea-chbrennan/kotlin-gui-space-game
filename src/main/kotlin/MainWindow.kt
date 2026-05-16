import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.BoxLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListModel
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.Timer
import javax.swing.UIManager
import javax.swing.border.MatteBorder



val crtGreen= java.awt.Color(0, 200, 70)
val dimGreen = java.awt.Color(5, 110, 50)

val darkGreen = java.awt.Color(10, 30, 10)
val spaceBlack = java.awt.Color(25, 10, 14)


/**
 * Helper function for scaling image in one line
 *
 * @author waimea-cpy
 *
 * @param width new width of image
 * @param height new height of image
 */
fun ImageIcon.scaled(width: Int, height: Int): ImageIcon =
    ImageIcon(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH))

/**
 * Main UI window shown during gameplay with game, planet, location, and inventory info
 *
 * @param game the game state object
 */
class MainWindow(val game: Game) {
    val frame = JFrame("Space Game")
    private val gameCountdownTimer = Timer(1000, null)

    val introWindow = IntroWindow(this)

    // declare parent panels ----------------------------
    private val panel = JPanel().apply { layout = GridBagLayout() }
    private val locationPanel = JPanel().apply { layout = GridBagLayout() }
    private val planetPanel = JPanel().apply { layout = GridBagLayout() }
    private val itemsPanel = JPanel().apply { layout = GridBagLayout() }
    private val infoPanel = JPanel().apply { layout = GridBagLayout() }



    // declare locationPanel elements ---------------------------
    private val upButton = JButton("West")
    private val rightButton = JButton("East")
    private val leftButton = JButton("North")
    private val downButton = JButton("South")

    private val currentLocationNodeLabel = JLabel("")
    private val itemPickupButton = JButton("Pick up")



    // declare planetPanel elements ---------------------------
    private val currentPlanetNameLabel = JLabel("")
    private val currentPlanetDescriptionLabel = JLabel("<html>")
    private val currentPlanetImageLabel = JLabel()

    private val nextPlanetButton = JButton("")
    private val previousPlanetButton = JButton("")




    // declare itemsPanel elements ---------------------------
    private val itemsTitle = JLabel("Inventory:")

    private val model = DefaultListModel<String>()
    private val inventoryList = JList(model)



    // declare infoPanel elements ---------------------------
    private val titleLabel = JLabel("Space Game")
    private val currentTimeLabel = JLabel("")


    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(1280, 720)
        panel.layout = GridBagLayout()

        // Helper to reduce GridBagConstraints boilerplate
        fun gbc(
            gridx: Int, gridy: Int,
            gridwidth: Int = 1, gridheight: Int = 1,
            weightx: Double = 0.0, weighty: Double = 0.0,
            fill: Int = GridBagConstraints.BOTH,
            insets: Insets = Insets(8, 8, 8, 8)
        ) = GridBagConstraints().also {
            it.gridx = gridx
            it.gridy = gridy
            it.gridwidth = gridwidth
            it.gridheight = gridheight
            it.weightx = weightx
            it.weighty = weighty
            it.fill = fill
            it.insets = insets
            it.anchor = GridBagConstraints.CENTER
        }

        //Main Panel setup --------------------------------
        panel.add(planetPanel,  gbc(0, 0, gridwidth=2, weightx=0.7, weighty=0.6, insets=Insets(16, 24, 8, 8)))
        panel.add(itemsPanel,   gbc(2, 0, gridheight=2, weightx=0.2, weighty=1.0))
        panel.add(infoPanel,    gbc(0, 1, weightx=0.2, weighty=0.4))
        panel.add(locationPanel,gbc(1, 1, weightx=0.4, weighty=0.4))


        //Planet Panel setup -----------------------------------
        planetPanel.add(currentPlanetImageLabel,gbc(0,0,weightx=0.1, weighty=1.0, gridheight = 3))
        planetPanel.add(currentPlanetNameLabel,gbc(1,0, weightx=0.9, weighty=0.1))
        planetPanel.add(currentPlanetDescriptionLabel,gbc(1,1, weightx=0.6, weighty=0.5))

        //Need a simple FlowLayout panel to have the next|previous buttons nicely together
        planetPanel.add(JPanel(FlowLayout(FlowLayout.CENTER, 30, 8)).apply {
            this.isOpaque = false
            this.add(previousPlanetButton)
            this.add(nextPlanetButton)
        },gbc(1,2, weightx=0.6, weighty=1.0))


        // Info/title panel setup -------------------------------
        infoPanel.add(titleLabel,       gbc(0, 0, weightx=1.0, weighty=0.5))
        infoPanel.add(currentTimeLabel, gbc(0, 1, weightx=1.0, weighty=0.5))



        // Items/inventory panel setup -----------------------------
        itemsPanel.layout = BorderLayout(0, 6)
        itemsPanel.add(itemsTitle, BorderLayout.NORTH)
        itemsPanel.add(inventoryList, BorderLayout.CENTER)



        //Location panel setup ---------------------------------
        fun compassGbc(gridx: Int, gridy: Int) = GridBagConstraints().apply {//helper to reduce boilerplate again
            this.gridx = gridx
            this.gridy = gridy
            this.insets = Insets(8, 8, 8, 8)
            this.anchor = GridBagConstraints.CENTER
        }


        val centerCell = JPanel().apply { //Simple vertical layout needed to avoid gridBag conflict with 2 center elements
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            currentLocationNodeLabel.alignmentX = java.awt.Component.CENTER_ALIGNMENT
            itemPickupButton.alignmentX = java.awt.Component.CENTER_ALIGNMENT
            add(currentLocationNodeLabel)
            add(javax.swing.Box.createVerticalStrut(6))
            add(itemPickupButton)
        }
        locationPanel.add(upButton,  compassGbc(1, 0))
        locationPanel.add(leftButton,   compassGbc(0, 1))
        locationPanel.add(centerCell,   compassGbc(1, 1))
        locationPanel.add(rightButton,   compassGbc(2, 1))
        locationPanel.add(downButton,  compassGbc(1, 2))
    }

    private fun setupStyles() {
        // Text ------------------------------------
        titleLabel.font = Font(Font.MONOSPACED, Font.BOLD, 32)
        titleLabel.horizontalAlignment = SwingConstants.CENTER

        currentPlanetNameLabel.font = Font(Font.MONOSPACED, Font.PLAIN, 24)
        currentPlanetDescriptionLabel.font = Font(Font.MONOSPACED, Font.PLAIN, 14)

        currentLocationNodeLabel.font = Font(Font.MONOSPACED, Font.BOLD, 16)
        currentTimeLabel.font = Font(Font.MONOSPACED, Font.BOLD, 24)
        currentTimeLabel.horizontalAlignment = SwingConstants.CENTER

        itemsTitle.font = Font(Font.MONOSPACED, Font.BOLD, 20)
        itemsTitle.horizontalAlignment = SwingConstants.CENTER
        inventoryList.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        inventoryList.isOpaque = false


        //Colours ----------------------------------
        panel.background = spaceBlack
        planetPanel.background = darkGreen
        locationPanel.background = darkGreen
        itemsPanel.background = darkGreen


        
        //Borders -----------------------------------
        planetPanel.border = MatteBorder(0,3,0,0, crtGreen)
        locationPanel.border = MatteBorder(3,3,0,0, crtGreen)
        itemsPanel.border = MatteBorder(0, 3, 0, 0, crtGreen)

        //Buttons -----------------------------------
        for (btn in listOf(nextPlanetButton, previousPlanetButton, leftButton, rightButton, upButton, downButton, itemPickupButton)) {
            btn.background = dimGreen
            btn.foreground = crtGreen
            btn.isFocusPainted = false
            btn.border = MatteBorder(1, 1, 1, 1, dimGreen)
            btn.font = Font(Font.MONOSPACED, Font.PLAIN, 14)
        }


    }

    private fun setupWindow() {
        frame.isResizable = false
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane = panel
        frame.pack()
        frame.setLocationRelativeTo(null)
    }

    private fun setupActions() {
        nextPlanetButton.addActionListener { handlePlanetClick(LateralDirection.LEFT) }
        previousPlanetButton.addActionListener { handlePlanetClick(LateralDirection.RIGHT) }

        itemPickupButton.addActionListener { handlePickupItem() }

        leftButton.addActionListener { handleLocationClick(Direction.LEFT) }
        rightButton.addActionListener { handleLocationClick(Direction.RIGHT) }
        upButton.addActionListener { handleLocationClick(Direction.UP) }
        downButton.addActionListener { handleLocationClick(Direction.DOWN) }


        gameCountdownTimer.addActionListener { handleGameTimerTick() }
    }


    private fun handleGameTimerTick() {
        game.currentTime--
        updateUI()
        if (game.currentTime == 0) { // Game lose condition
            gameCountdownTimer.stop()
            frame.isVisible = false
            val loseWindow = LoseWindow()
            loseWindow.show()

        }
    }

    private fun handlePlanetClick(direction: LateralDirection) {
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
        //Check Win State!
        if(inventory.find{ it.id==WINNING_ITEM_ID }!=null) {
            gameCountdownTimer.stop()
            frame.isVisible = false
            val winWindow = WinWindow()
            winWindow.show()

        }
    }




    fun updateUI() {
        currentPlanetNameLabel.text = "Planet: ${game.currentPlanet.name}"
        currentPlanetDescriptionLabel.text = "<html>${game.currentPlanet.description}"
        currentLocationNodeLabel.text = game.currentLocation.name

        itemPickupButton.isEnabled = game.locationItem != null
        itemPickupButton.text = if (game.locationItem!=null) "Pick Up: ${game.locationItem?.name}" else "Nothing Here"

//        leftButton.background = if(game.locationWest!=null && game.locationWest!!.isLocked()) java.awt.Color(100,0,0) else java.awt.Color(5,100,5)
//        upButton.background = if(game.locationNorth!=null && game.locationNorth!!.isLocked()) java.awt.Color(100,0,0) else java.awt.Color(5,100,5)
//        rightButton.background = if(game.locationEast!=null && game.locationEast!!.isLocked()) java.awt.Color(100,0,0) else java.awt.Color(5,100,5)
//        downButton.background = if(game.locationSouth!=null && game.locationSouth!!.isLocked()) java.awt.Color(100,0,0) else java.awt.Color(5,100,5)

        leftButton.isEnabled = game.locationWest!=null// && !game.locationWest!!.isLocked()
        upButton.isEnabled = game.locationNorth!=null// && !game.locationNorth!!.isLocked()
        rightButton.isEnabled = game.locationEast!=null //&& !game.locationEast!!.isLocked()
        downButton.isEnabled = game.locationSouth!=null //&& !game.locationSouth!!.isLocked()



        nextPlanetButton.text = if(game.nextPlanet==null) "No Next" else "Next: ${game.nextPlanet!!.name}"
        previousPlanetButton.text = if(game.previousPlanet==null) "No Previous" else "Prev: ${game.previousPlanet!!.name}"


        nextPlanetButton.isEnabled = game.nextPlanet!=null
        previousPlanetButton.isEnabled = game.previousPlanet!=null

        //Inventory items' description can change so we have to remove them all and re add them.
        model.removeAllElements()
        inventory.forEachIndexed { index, item ->
            model.add(index,"<html><strong>${item.name}</strong> - ${item.getDescription()}")
        }


        if(game.currentPlanet.imageFile!=null) {
            currentPlanetImageLabel.isVisible = true
            currentPlanetImageLabel.icon = ImageIcon(ClassLoader.getSystemResource(game.currentPlanet.imageFile)).scaled(460,460)
        } else {
            currentPlanetImageLabel.isVisible = false
        }

        currentTimeLabel.text = "${game.currentTime/60}:${(game.currentTime%60).toString().padEnd(2,'0')}s"

    }

    //Show the main window
    fun show() {
        frame.isVisible = true
        gameCountdownTimer.start()
    }

    //Called before show() to show IntroWindow before MainWindow
    fun startup() {
        frame.isVisible = false
        introWindow.show()

    }
}






