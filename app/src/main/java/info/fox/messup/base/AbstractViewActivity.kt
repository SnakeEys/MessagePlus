package info.fox.messup.base

import android.content.Intent
import android.support.annotation.IdRes
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import android.view.MenuItem
import android.view.View

/**
 *<p>
 * Created by user
 * on 2017/8/23.
 *</p>
 */
open class AbstractViewActivity : AppCompatActivity() {

    private val mWidgets = SparseArray<View>(10)

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                val intent = NavUtils.getParentActivityIntent(this)
                intent?.let {
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    if (NavUtils.shouldUpRecreateTask(this, intent)) {
                        TaskStackBuilder.create(this).addNextIntentWithParentStack(intent)
                    } else {
                        NavUtils.navigateUpTo(this, intent)
                    }
                } ?: finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T>findWidget(@IdRes id: Int): T {
        var view = mWidgets.get(id)
        if (view == null) {
            view = findViewById(id)
            mWidgets.put(id, view)
        }
        return view as T
    }

}