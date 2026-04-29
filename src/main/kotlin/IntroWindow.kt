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
import javax.swing.SwingConstants
import javax.swing.Timer

class IntroWindow(val owner: MainWindow) {

    private val characterTypingDelayTimer = Timer(50, null)
    val frame  = JFrame("Welcome to Space Game")

    private val panel = JPanel().apply { layout = GridBagLayout() }

    private val titleWelcomeLabel = JLabel("Welcome to Space Game")

    private val instructionParagraphLabel = JLabel("", SwingConstants.CENTER)
    private val instructionParagraphLabelText = "<html><div style='text-align: center;'>You are a space person with a space ship stuck in the Quases system with an unstable star. <br>Can you navigate the evacuated system and collect what you need to stabilize the star? <br>You have 5 minutes. <br>They are relying on you. <br>Good Luck."

    private val proceedButton = JButton("Proceed")
    private val startButton = JButton("Start")

    private val bgImage = JLabel(ImageIcon(ClassLoader.getSystemResource("images/scan_lines.png")))



    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(1280, 720)

        val gbc = GridBagConstraints().apply {}
        gbc.insets = Insets(10, 10, 10, 10)

        frame.add(panel)
        panel.add(bgImage, gbc, JLayeredPane.DEFAULT_LAYER)

        panel.add(titleWelcomeLabel, gbc,JLayeredPane.DEFAULT_LAYER)
        gbc.gridy = 1
        panel.add(instructionParagraphLabel, gbc,JLayeredPane.DEFAULT_LAYER)
        gbc.gridy = 2
        panel.add(startButton, gbc,JLayeredPane.DEFAULT_LAYER)

    }

    private fun setupStyles() {
        titleWelcomeLabel.font = Font(Font.MONOSPACED, Font.ITALIC, 30)
        instructionParagraphLabel.font = Font(Font.MONOSPACED, Font.PLAIN, 14)

    }

    private fun setupActions() {
        characterTypingDelayTimer.addActionListener { handleAddIntroCharacter() }
        startButton.addActionListener { handleStartClick() }

    }

    private fun handleAddIntroCharacter() {
        updateUI()
    }
    private fun setupWindow() {
        characterTypingDelayTimer.start()
        frame.isResizable = false
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane = panel
        frame.pack()
        frame.setLocationRelativeTo(null)
    }

    private fun handleStartClick() {
        panel.isVisible = false
        owner.show()

        updateUI()
    }

    private fun updateUI() {
        instructionParagraphLabel.text = instructionParagraphLabelText.take(instructionParagraphLabel.text.length+1)
        startButton.isVisible = instructionParagraphLabel.text == instructionParagraphLabelText
    }

    fun show() {
        frame.isVisible = true
    }
}