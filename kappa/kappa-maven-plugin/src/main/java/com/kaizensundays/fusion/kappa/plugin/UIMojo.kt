package com.kaizensundays.fusion.kappa.plugin

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.util.concurrent.TimeUnit
import javax.swing.UIManager

/**
 * Created: Friday 12/30/2022, 7:31 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@SuppressWarnings("kotlin:S6518")
@Mojo(name = "ui", defaultPhase = LifecyclePhase.NONE)
class UIMojo : AbstractKappaMojo() {

    override fun doExecute() {

        UIManager.setLookAndFeel(Plastic3DLookAndFeel())

        val tableModel = UITableModel()

        val done = UIFrame(tableModel).build()

        println(done.get(1000, TimeUnit.SECONDS))
    }

}

fun main() {

    UIMojo().execute()
}
