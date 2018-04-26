package de.studienarbeit.invoicescanner.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.studienarbeit.invoicescanner.R

class FavoritesFragment : Fragment(), FragmentAttributeInterface {

    override var actionBarTitle = ""
    override var fullScreen = false
    override var isMenuAvailable = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? = inflater.inflate(R.layout.fragment_favorites, container, false)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        actionBarTitle = getString(R.string.favorites)
    }
}