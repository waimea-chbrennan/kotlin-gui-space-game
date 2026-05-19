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



import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JLayeredPane
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.Timer



/**
 * Window shown first with instructions and lore
 *
 * @param owner the MainWindow object to be able to show it.
 */
class IntroWindow(val owner: MainWindow) {

    private val characterTypingDelayTimer = Timer(CHARACTER_TYPING_DELAY, null)
    val frame  = JFrame("Welcome to Space Game")

    private val titleWelcomeLabel = JLabel("Welcome to Space Game")

    private val animationParagraphLabel = JLabel("", SwingConstants.CENTER)

    private var fullAnimationParagraphText = INTRO_LORE_PARAGRAPH

    private val proceedButton = JButton("Proceed")


    private val layerPanel = JLayeredPane() //JLayeredPane needed for proper layering rather than JPanel

    private val bgImage = JLabel(ImageIcon(ClassLoader.getSystemResource("images/scan_lines.png")))

    private val foregroundPanel = JPanel().apply { layout = GridBagLayout() }


    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    /**
     * Setup the foreground panel showing over the background image in fullscreen
     *
     * additional foregroundPanel/layerPanel needed because grid bag layout cannot do z ordering.
     */
    private fun setupLayout() {
        frame.preferredSize = java.awt.Dimension(1280, 720)
        //Foreground and bgimage need to take up entire screen
        foregroundPanel.setBounds(0,0,1280,720)
        bgImage.setBounds(0,0,1280,720)

        val gbc = GridBagConstraints().apply {}
        gbc.insets = Insets(10, 10, 10, 10)

        //Setup foreground
        foregroundPanel.isOpaque = false
        foregroundPanel.add(titleWelcomeLabel, gbc)
        gbc.gridy = 1
        foregroundPanel.add(animationParagraphLabel, gbc)
        gbc.gridy = 2
        foregroundPanel.add(proceedButton, gbc)

        //Add and order the image and foreground to the frame
        layerPanel.add(bgImage, JLayeredPane.DEFAULT_LAYER-1)
        layerPanel.add(foregroundPanel)
        layerPanel.moveToBack(bgImage)

        frame.add(layerPanel)
    }

    //Make the font more thematic.
    private fun setupStyles() {
        titleWelcomeLabel.font = Font(Font.MONOSPACED, Font.ITALIC, 30)
        animationParagraphLabel.font = Font(Font.MONOSPACED, Font.PLAIN, 14)
        proceedButton.font = Font(java.awt.Font.MONOSPACED, Font.PLAIN, 15)
    }

    //Timer and proceed button need to have actions on them
    private fun setupActions() {
        characterTypingDelayTimer.addActionListener { updateUI() } //Reveal a new character periodically for text animation
        proceedButton.addActionListener { handleProceedClick() }

    }

    //Window configuration
    private fun setupWindow() {
        characterTypingDelayTimer.start()
        frame.isResizable = false
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.setLocationRelativeTo(null)
    }

    /**
     * Replace animated paragraph and button with second set of information.
     */
    private fun handleProceedClick() {
        animationParagraphLabel.text = ""
        fullAnimationParagraphText = INTRO_INSTRUCTION_PARAGRAPH
        proceedButton.text = "Start"
        proceedButton.addActionListener { handleStartClick()}
    }

    /**
     * Closes IntroWindow and shows the MainWindow
     */
    private fun handleStartClick() {
        frame.isVisible = false
        owner.show()

    }

    /**
     * Handles the character
     */
    private fun updateUI() {
        //Add one character to the animated paragraph
        animationParagraphLabel.text = fullAnimationParagraphText.take(animationParagraphLabel.text.length+1)

        //Only show button when text animation completed
        proceedButton.isVisible = animationParagraphLabel.text == fullAnimationParagraphText
    }

    /**
     * Shows the initialized window
     */
    fun show() {
        frame.isVisible = true
    }
}