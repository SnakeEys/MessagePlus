package info.fox.messup.base

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.SparseArray
import android.view.MenuItem
import android.view.View
import info.fox.messup.BuildConfig

/**
 *<p>
 * Created by user
 * on 2017/8/23.
 *</p>
 */
open class AbstractViewActivity : AppCompatActivity() {
    protected val TAG = javaClass.simpleName

    private val mWidgets = SparseArray<View>(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showActivityLog("$TAG $this onCreate}")

    }

    override fun onStart() {
        super.onStart()
        showActivityLog("$TAG $this onStart}")
    }

    override fun onResume() {
        super.onResume()
        showActivityLog("$TAG $this onResume}")
    }

    override fun onRestart() {
        super.onRestart()
        showActivityLog("$TAG $this onRestart}")
    }

    override fun onPause() {
        super.onPause()
        showActivityLog("$TAG $this onPause}")
    }

    override fun onStop() {
        super.onStop()
        showActivityLog("$TAG $this onStop}")
    }

    override fun onDestroy() {
        super.onDestroy()
        showActivityLog("$TAG $this onDestroy}")
    }

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

    protected fun showActivityLog(log: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, log)
        }
    }

}