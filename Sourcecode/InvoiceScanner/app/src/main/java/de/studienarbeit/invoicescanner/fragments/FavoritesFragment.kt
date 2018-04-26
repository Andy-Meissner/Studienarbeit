package de.studienarbeit.invoicescanner.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.studienarbeit.invoicescanner.R
import de.studienarbeit.invoicescanner.TITLE_FAVORITES

class FavoritesFragment : Fragment(), FragmentAttributeInterface {

    override var actionBarTitle = TITLE_FAVORITES
    override var fullScreen = false
    override var isMenuAvailable = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? = inflater.inflate(R.layout.fragment_favorites, container, false)
}