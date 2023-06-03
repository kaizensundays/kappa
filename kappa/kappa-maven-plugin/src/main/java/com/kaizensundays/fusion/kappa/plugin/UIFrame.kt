package com.kaizensundays.fusion.kappa.plugin

import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.concurrent.CompletableFuture
import javax.swing.DefaultCellEditor
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.SpringLayout
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableModel

/**
 * Created: Saturday 12/31/2022, 5:39 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class UIFrame(private val tableModel: TableModel) {

    fun build(): CompletableFuture<String> {

        val done = CompletableFuture<String>()

        val frame = JFrame()

        frame.title = "Kappa"
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(900, 300)
        frame.setLocationRelativeTo(null)

        val content = frame.contentPane
        content.background = Color(231, 231, 231)

        val layout = SpringLayout()
        content.layout = layout

        val button1 = JButton("Start")
        button1.isEnabled = false
        button1.preferredSize = Dimension(64, 32)
        frame.add(button1)

        val button2 = JButton("Stop")
        button2.preferredSize = Dimension(64, 32)
        frame.add(button2)

        val table = JTable(tableModel)
        table.tableHeader.font = Font("Segoe UI Symbol", Font.PLAIN, 16)
        table.font = Font(table.font.name, table.font.style, 16)
        table.rowHeight = 24

        val checkColumn = table.columnModel.getColumn(0)

        // Set the renderer for the first column to render checkboxes
        checkColumn.cellRenderer = object : DefaultTableCellRenderer() {
            override fun getTableCellRendererComponent(
                table: JTable,
                value: Any,
                isSelected: Boolean,
                hasFocus: Boolean,
                row: Int,
                column: Int
            ): Component {
                return JCheckBox().apply { this.isSelected = value as Boolean }
            }
        }

        // Set the editor for the first column to edit checkboxes
        checkColumn.cellEditor = object : DefaultCellEditor(JCheckBox()) {}

        val scrollPane = JScrollPane(table)
        content.add(scrollPane)

        layout.putConstraint(SpringLayout.NORTH, button1, 10, SpringLayout.NORTH, content)
        layout.putConstraint(SpringLayout.WEST, button1, 10, SpringLayout.WEST, content)

        layout.putConstraint(SpringLayout.NORTH, button2, 10, SpringLayout.NORTH, content)
        layout.putConstraint(SpringLayout.WEST, button2, 10, SpringLayout.EAST, button1)

        layout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.SOUTH, button1)
        layout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, content)
        layout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, content)
        layout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, content)

        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                done.complete("Done")
            }
        })

        SwingUtilities.invokeLater { frame.isVisible = true }

        return done;
    }


}