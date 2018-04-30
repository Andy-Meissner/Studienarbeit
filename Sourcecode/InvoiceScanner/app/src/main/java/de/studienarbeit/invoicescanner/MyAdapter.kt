package de.studienarbeit.invoicescanner

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.studienarbeit.invoicescanner.fragments.RecyclerViewFragment
import kotlinx.android.synthetic.main.row_item_invoice.view.*
import java.io.File


class MyAdapter(private val myDataset: List<Invoice>, private val recyclerViewFragment: RecyclerViewFragment) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val container: View) : RecyclerView.ViewHolder(container)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.ViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_invoice, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return ViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.container.saved_iban.text = myDataset[position].iban
        holder.container.saved_receiver.text = myDataset[position].receiver
        holder.container.saved_amount.text = myDataset[position].amount.toString()
        holder.container.toggle_favorite.setOnClickListener({
            myDataset[position].isFavorite = !myDataset[position].isFavorite

            if(myDataset[position].isFavorite) {
                holder.container.toggle_favorite.setBackgroundResource(R.drawable.ic_star_black)
            } else {
                holder.container.toggle_favorite.setBackgroundResource(R.drawable.ic_star_border_black)
            }
            recyclerViewFragment.onInvoiceChanged(myDataset[position])
        })
        if(myDataset[position].isFavorite) {
            holder.container.toggle_favorite.setBackgroundResource(R.drawable.ic_star_black)
        } else {
            holder.container.toggle_favorite.setBackgroundResource(R.drawable.ic_star_border_black)
        }

        val file = File(myDataset[position].imagePath)
        if (file.exists()) {
            val currentImage = BitmapFactory.decodeFile(file.absolutePath)

            holder.container.saved_invoice_picture.setImageBitmap(currentImage)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}
