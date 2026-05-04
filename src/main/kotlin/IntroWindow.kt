import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JLayer
import javax.swing.JLayeredPane
import javax.swing.JPanel
import javax.swing.OverlayLayout
import javax.swing.SwingConstants
import javax.swing.Timer

class IntroWindow(val owner: MainWindow) {

    private val characterTypingDelayTimer = Timer(20, null)
    val frame  = JFrame("Welcome to Space Game")

    private val panel = JPanel().apply { layout = GridBagLayout() }

    private val titleWelcomeLabel = JLabel("Welcome to Space Game")

    private val instructionParagraphLabel = JLabel("", SwingConstants.CENTER)
    private var instructionParagraphLabelText = "<html><div style='text-align: center;'>You are a space person with a space ship stuck in the Quases system with an unstable star. <br>Can you navigate the evacuated system and collect what you need to stabilize the star? <br>You have 5 minutes. <br>They are relying on you. <br>Good Luck."

    private val proceedButton = JButton("Proceed")


    private val layerPanel = JLayeredPane()

    private val bgImage = JLabel(ImageIcon(ClassLoader.getSystemResource("images/scan_lines.png")))

    private val foregroundPanel = JPanel().apply { layout = GridBagLayout() }


    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        frame.preferredSize = java.awt.Dimension(1280, 720)

        val gbc = GridBagConstraints().apply {}
        gbc.insets = Insets(10, 10, 10, 10)


        bgImage.setBounds(0,0,1280,720)
        layerPanel.add(bgImage, JLayeredPane.DEFAULT_LAYER-1)

        foregroundPanel.add(titleWelcomeLabel, gbc)
        gbc.gridy = 1
        foregroundPanel.add(instructionParagraphLabel, gbc)
        gbc.gridy = 2
        foregroundPanel.add(proceedButton, gbc)

        foregroundPanel.setBounds(0,0,1280,720)
        foregroundPanel.isOpaque = false

        layerPanel.add(foregroundPanel)

        layerPanel.moveToBack(bgImage)
        frame.add(layerPanel)
    }

    private fun setupStyles() {
        titleWelcomeLabel.font = Font(Font.MONOSPACED, Font.ITALIC, 30)
        instructionParagraphLabel.font = Font(Font.MONOSPACED, Font.PLAIN, 14)
        proceedButton.font = Font(java.awt.Font.MONOSPACED, Font.PLAIN, 15)

    }

    private fun setupActions() {
        characterTypingDelayTimer.addActionListener { handleAddIntroCharacter() }
        proceedButton.addActionListener { handleProceedClick() }

    }

    private fun handleAddIntroCharacter() {
        updateUI()
    }
    private fun setupWindow() {
        characterTypingDelayTimer.start()
        frame.isResizable = false
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.setLocationRelativeTo(null)
    }

    private fun handleProceedClick() {
        instructionParagraphLabel.text = ""
        instructionParagraphLabelText = "<html><div style='text-align: center;'>How To Play: <br>Travelling between planets will allow you to discover a new set of locations. <br>Some of these locations will be locked and require you to get items from other locations to unlock them. <br>Find a ship with a hyperdrive to escape the system."
        proceedButton.text = "Start"
        proceedButton.addActionListener { handleStartClick()}
    }

    private fun handleStartClick() {
        frame.isVisible = false
        owner.show()

    }

    private fun updateUI() {
        instructionParagraphLabel.text = instructionParagraphLabelText.take(instructionParagraphLabel.text.length+1)
        proceedButton.isVisible = instructionParagraphLabel.text == instructionParagraphLabelText
    }

    fun show() {
        frame.isVisible = true
    }
}