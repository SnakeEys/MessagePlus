package info.fox.messup

import android.support.v4.app.Fragment

/**
 * Created by snake
 * on 17/6/9.
 */
class PlaceholderFragment : Fragment() {


    companion object {
        fun newInstance(): PlaceholderFragment {
            val fragment =  PlaceholderFragment()
            return fragment
        }
    }
}