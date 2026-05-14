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
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class WinWindow() {

    val frame  = JFrame("YOU WIN!")

    private val panel = JPanel().apply { layout = GridBagLayout() }

    private val winMessageLabel = JLabel("You Win!")

    private val winMessageParagraphLabel = JLabel("You escaped the Quases system and have a nice life!")



    init {
        setupLayout()
        setupStyles()
        setupWindow()
    }

    fun setupLayout() {
        frame.preferredSize = java.awt.Dimension(1280, 720)

        frame.add(panel)
        val gbc = GridBagConstraints().apply {}
        panel.add(winMessageLabel, gbc)
        gbc.gridy = 1
        panel.add(winMessageParagraphLabel, gbc)
    }

    fun setupStyles() {
        winMessageLabel.font = Font(Font.MONOSPACED, Font.PLAIN, 46)
        winMessageParagraphLabel.font = Font(Font.MONOSPACED, Font.PLAIN, 14)
    }

    fun setupWindow() {
        frame.isResizable = false
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isAlwaysOnTop = true
        frame.pack()
        frame.setLocationRelativeTo(null)
    }

    fun show() {
        frame.isVisible = true
    }
}