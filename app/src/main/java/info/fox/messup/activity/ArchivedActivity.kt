package info.fox.messup.activity

import android.os.Bundle
import info.fox.messup.base.DrawerActivity

/**
 *<p>
 * Created by user
 * on 2017/8/22.
 *</p>
 */
class ArchivedActivity : DrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}