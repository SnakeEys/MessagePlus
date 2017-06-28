package info.fox.messup.base

import android.database.Cursor
import android.widget.Filter

/**
 * Created by snake
 * on 17/6/27.
 */
class CursorFilter(var mClient:  CursorFilterClient) : Filter() {


    interface CursorFilterClient {
        fun convertToString(cursor: Cursor?): CharSequence
        fun runQueryOnBackgroundThread(constraint: CharSequence): Cursor?
        fun getCursor(): Cursor?
        fun changeCursor(cursor: Cursor)
    }

    override fun convertResultToString(resultValue: Any): CharSequence {
        return mClient.convertToString(resultValue as Cursor)
    }

    override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
        val cursor = mClient.runQueryOnBackgroundThread(constraint)

        val results = Filter.FilterResults()
        if (cursor != null) {
            results.count = cursor.count
            results.values = cursor
        } else {
            results.count = 0
            results.values = null
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
        val oldCursor = mClient.getCursor()

        if (results.values != null && results.values !== oldCursor) {
            mClient.changeCursor(results.values as Cursor)
        }
    }
}