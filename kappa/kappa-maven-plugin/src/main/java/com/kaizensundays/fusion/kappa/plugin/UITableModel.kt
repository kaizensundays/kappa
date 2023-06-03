package com.kaizensundays.fusion.kappa.plugin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import java.security.SecureRandom
import java.util.*
import javax.swing.table.AbstractTableModel

/**
 * Created: Saturday 12/31/2022, 5:34 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class UITableModel : AbstractTableModel() {

    private val swing = CoroutineScope(Dispatchers.Swing)

    private val random = SecureRandom()

    private fun pid() = random.nextInt(1000) + 50000

    private fun uuid() = UUID.randomUUID().toString()

    private val data: Array<Array<Any>> = arrayOf(
        arrayOf(false, pid(), uuid(), "kapplet"),
        arrayOf(false, pid(), uuid(), "easybox"),
        arrayOf(false, pid(), uuid(), "fusion-mu"),
    )

    private val check: Char = '\u2714'

    private val columnNames = arrayOf("✓", "PID", "Service ID", "Service Name")

    private val columnTypes = arrayOf<Class<*>>(Boolean::class.java, Int::class.java, String::class.java, String::class.java)

    override fun getColumnName(column: Int): String {
        return columnNames[column]
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return columnTypes[columnIndex]
    }

    override fun getRowCount(): Int {
        return 3
    }

    override fun getColumnCount(): Int {
        return 4
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return data[rowIndex][columnIndex]
    }

    override fun setValueAt(value: Any, row: Int, col: Int) {
        data[row][col] = value
        fireTableCellUpdated(row, col)
    }


    override fun isCellEditable(row: Int, col: Int): Boolean {
        return col == 0
    }

}