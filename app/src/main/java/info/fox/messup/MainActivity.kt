package info.fox.messup

import android.os.Bundle
import info.fox.messup.base.DrawerActivity

class MainActivity : DrawerActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpDrawer()
    }
}
