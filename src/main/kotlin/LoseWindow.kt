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

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class LoseWindow() {

    val frame  = JFrame("YOU LOSE!")

    private val panel = JPanel().apply { layout = GridBagLayout() }

    private val loseMessageLabel = JLabel("You Lose!")

    private val loseMessageParagraphLabel = JLabel("Quases has exploded and you have died. Try Again?")



    init {
        setupLayout()
        setupStyles()
        setupWindow()
    }

    /**
     * Adds elements to the window at correct positioning with correct size
     */
    fun setupLayout() {
        frame.preferredSize = Dimension(1280, 720)
        frame.add(panel)


        val gbc = GridBagConstraints().apply {}
        //Title above paragraph
        panel.add(loseMessageLabel, gbc)
        gbc.gridy = 1
        panel.add(loseMessageParagraphLabel, gbc)
    }

    /**
     * Sets color, font and any other non-layout styling of elements
     */
    fun setupStyles() {
        loseMessageLabel.font = Font(Font.MONOSPACED, Font.PLAIN, 46)
        loseMessageParagraphLabel.font = Font(Font.MONOSPACED, Font.PLAIN, 14)
    }

    /**
     * Sets window parameters
     */
    fun setupWindow() {
        frame.isResizable = false
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isAlwaysOnTop = true
        frame.pack()
        frame.setLocationRelativeTo(null)
    }

    /**
     * Shows the initialized window
     */
    fun show() {
        frame.isVisible = true
    }
}