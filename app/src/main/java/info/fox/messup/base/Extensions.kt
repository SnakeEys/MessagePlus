package info.fox.messup.base

import android.support.annotation.IdRes
import android.view.View

/**
 * Created by
 * snake on 2017/9/16.
 */

/**
 * Extension function for View's findViewById().
 */
inline fun <reified T: View> View.findWidget(@IdRes id: Int): T = findViewById(id) as T