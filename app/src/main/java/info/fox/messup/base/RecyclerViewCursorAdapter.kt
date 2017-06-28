package info.fox.messup.base

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.database.DataSetObserver
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.widget.FilterQueryProvider
import android.widget.Filterable


/**
 * Created by snake
 * on 17/6/27.
 */
abstract class RecyclerViewCursorAdapter<VH : RecyclerView.ViewHolder>(context: Context, c: Cursor?, flags: Int) : RecyclerView.Adapter<VH>(), Filterable, CursorFilter.CursorFilterClient {

    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected var mDataValid: Boolean = false
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected var mCursor: Cursor? = null
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected var mContext: Context? = null
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected var mRowIDColumn: Int = 0
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected var mChangeObserver: ChangeObserver? = null
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected var mDataSetObserver: DataSetObserver? = null
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected var mCursorFilter: CursorFilter? = null
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected var mFilterQueryProvider: FilterQueryProvider? = null


    /**
     * If set the adapter will call requery() on the cursor whenever a content change
     * notification is delivered. Implies {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     *
     * @deprecated This option is discouraged, as it results in Cursor queries
     * being performed on the application's UI thread and thus can cause poor
     * responsiveness or even Application Not Responding errors.  As an alternative,
     * use {@link android.app.LoaderManager} with a {@link android.content.CursorLoader}.
     */
    @Deprecated("")
    val FLAG_AUTO_REQUERY = 0x01

    /**
     * If set the adapter will register a content observer on the cursor and will call
     * [.onContentChanged] when a notification comes in.  Be careful when
     * using this flag: you will need to unset the current Cursor from the adapter
     * to avoid leaks due to its registered observers.  This flag is not needed
     * when using a CursorAdapter with a
     * [android.content.CursorLoader].
     */
    val FLAG_REGISTER_CONTENT_OBSERVER = 0x02

    init {
        val cursorPresent = c != null
        mCursor = c
        mDataValid = cursorPresent
        mContext = context
        mRowIDColumn = if (cursorPresent) c?.getColumnIndex("_id") ?: -1 else -1
        if ((flags and FLAG_REGISTER_CONTENT_OBSERVER) == FLAG_REGISTER_CONTENT_OBSERVER) {
            mChangeObserver = ChangeObserver()
            mDataSetObserver = MyDataSetObserver()
        } else {
            mChangeObserver = null
            mDataSetObserver = null
        }

        if (cursorPresent) {
            if (mChangeObserver != null) c?.registerContentObserver(mChangeObserver)
            if (mDataSetObserver != null) c?.registerDataSetObserver(mDataSetObserver)
        }

        setHasStableIds(true)//这个地方要注意一下，需要将表关联ID设置为true
    }

    override fun getFilter() = mCursorFilter ?: CursorFilter(this)

    override fun getCursor() = mCursor

    override fun getItemCount(): Int {
        return if (mDataValid) mCursor?.count ?: 0 else 0
    }

    override fun getItemId(position: Int): Long {
        return if (mDataValid && mCursor?.moveToPosition(position) ?: false) mCursor?.getLong(mRowIDColumn) ?: 0 else 0
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (!mDataValid) {
            throw IllegalStateException("this should only be called when the cursor is valid")
        }
        if (!(mCursor?.moveToPosition(position) ?: false)) {
            throw IllegalStateException("couldn't move cursor to position " + position)
        }
        onBindViewHolder(holder, mCursor)
    }

    override fun changeCursor(cursor: Cursor) {
        val old = swapCursor(cursor)
        old?.close()
    }

    override fun convertToString(cursor: Cursor?) = cursor?.toString() ?: ""

    override fun runQueryOnBackgroundThread(constraint: CharSequence) = mFilterQueryProvider?.let { mFilterQueryProvider?.runQuery(constraint) } ?: mCursor

    /**
     * Returns the query filter provider used for filtering. When the
     * provider is null, no filtering occurs.

     * @return the current filter query provider or null if it does not exist
     * *
     * @see .setFilterQueryProvider
     * @see .runQueryOnBackgroundThread
     */
    fun getFilterQueryProvider(): FilterQueryProvider? {
        return mFilterQueryProvider
    }

    /**
     * Sets the query filter provider used to filter the current Cursor.
     * The provider's
     * [android.widget.FilterQueryProvider.runQuery]
     * method is invoked when filtering is requested by a client of
     * this adapter.

     * @param filterQueryProvider the filter query provider or null to remove it
     * *
     * @see .getFilterQueryProvider
     * @see .runQueryOnBackgroundThread
     */
    fun setFilterQueryProvider(filterQueryProvider: FilterQueryProvider) {
        mFilterQueryProvider = filterQueryProvider
    }

    protected fun onContentChanged() {

    }

    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor === mCursor) {
            return null
        }
        val oldCursor = mCursor
        oldCursor?.let {
            mChangeObserver?.let { oldCursor.unregisterContentObserver(mChangeObserver) }
            mDataSetObserver?.let { oldCursor.unregisterDataSetObserver(mDataSetObserver) }
        }
        mCursor = newCursor
        if (newCursor != null) {
            mChangeObserver?.let { newCursor.registerContentObserver(mChangeObserver) }
            mDataSetObserver?.let { newCursor.registerDataSetObserver(mDataSetObserver) }
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id")
            mDataValid = true
            // notify the observers about the new cursor
            notifyDataSetChanged()
        } else {
            mRowIDColumn = -1
            mDataValid = false
            // notify the observers about the lack of a data set
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
            notifyDataSetChanged()
        }
        return oldCursor
    }

    abstract fun onBindViewHolder(holder: VH, cursor: Cursor?)

    inner class ChangeObserver : ContentObserver(Handler()) {

        override fun deliverSelfNotifications(): Boolean {
            return true
        }

        override fun onChange(selfChange: Boolean) {
            onContentChanged()
        }
    }

    inner class MyDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            mDataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            mDataValid = false
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
            notifyDataSetChanged()
        }
    }

}