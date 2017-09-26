package info.fox.messup

import android.app.Application
import info.fox.messup.domain.Contact

/**
 * Created by jince
 * on 2017/9/26.
 */
class MessApp : Application() {


    override fun onCreate() {
        super.onCreate()
        Contact.init(this)
    }

    fun getCurrentCountryIso(): String {
        return ""
    }


    companion object {
        private lateinit var sInstance: MessApp

        fun getApplication(): MessApp = sInstance
    }
}