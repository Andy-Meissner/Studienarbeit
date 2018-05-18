package de.studienarbeit.invoicescanner.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.studienarbeit.invoicescanner.Invoice
import de.studienarbeit.invoicescanner.MyAdapter
import de.studienarbeit.invoicescanner.R
import de.studienarbeit.invoicescanner.helper.SimpleDividerItemDecoration

/**
 * Demonstrates the use of [RecyclerView] with a [LinearLayoutManager] and a
 * [GridLayoutManager].
 */
class RecyclerViewFragment : Fragment(), FragmentAttributeInterface {

    override var isMenuAvailable = true
    override var isAddImageAvailable = true
    override var isSaveAvailable = false
    override var isEditAvailable = false
    override var actionBarTitle = ""
    override var fullScreen = false

    private lateinit var myListener : OnInvoiceChangedListener

    private lateinit var currentLayoutManagerType: LayoutManagerType
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var dataset: List<Invoice>

    enum class LayoutManagerType { GRID_LAYOUT_MANAGER, LINEAR_LAYOUT_MANAGER }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_archive,
                container, false).apply { tag = TAG}

        recyclerView = rootView.findViewById(R.id.my_recycler_view)

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        layoutManager = LinearLayoutManager(activity)

        currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            currentLayoutManagerType = savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER) as LayoutManagerType
        }
        setRecyclerViewLayoutManager(currentLayoutManagerType)

        // Set CustomAdapter as the adapter for RecyclerView.
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(activity!!.applicationContext))
        recyclerView.adapter = MyAdapter(dataset, this)
        setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER)

        return rootView
    }

    fun onInvoiceChanged(invoice : Invoice, action : String )
    {
        myListener.onInvoiceChanged(invoice, action)
    }

    interface OnInvoiceChangedListener
    {
        fun onInvoiceChanged(invoice: Invoice, action : String)
        fun openDetails(invoice: Invoice)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            myListener = context as OnInvoiceChangedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement OnArticleSelectedListener")
        }
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    private fun setRecyclerViewLayoutManager(layoutManagerType: LayoutManagerType) {
        var scrollPosition = 0

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.layoutManager != null) {
            scrollPosition = (recyclerView.layoutManager as LinearLayoutManager)
                    .findFirstCompletelyVisibleItemPosition()
        }

        when (layoutManagerType) {
            RecyclerViewFragment.LayoutManagerType.GRID_LAYOUT_MANAGER -> {
                layoutManager = GridLayoutManager(activity, SPAN_COUNT)
                currentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER
            }
            RecyclerViewFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER -> {
                layoutManager = LinearLayoutManager(activity)
                currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER
            }
        }

        with(recyclerView) {
            layoutManager = this@RecyclerViewFragment.layoutManager
            scrollToPosition(scrollPosition)
        }

    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {

        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, currentLayoutManagerType)
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    fun initDataset(Invoices : List<Invoice>) {
        dataset = Invoices
    }

    fun openDetails(invoice : Invoice)
    {
        myListener.openDetails(invoice)
    }

    fun updateRecyclerView()
    {
        try {
            (recyclerView.adapter as MyAdapter).myDataset = dataset
            recyclerView.adapter.notifyDataSetChanged()
        }
        catch (e: UninitializedPropertyAccessException)
        {
            Log.w("Fragment not active", e)
        }
    }

    companion object {
        private val TAG = "RecyclerViewFragment"
        private val KEY_LAYOUT_MANAGER = "layoutManager"
        private val SPAN_COUNT = 2
        private val DATASET_COUNT = 60
    }
}