package de.studienarbeit.invoicescanner.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.studienarbeit.invoicescanner.R

class FavoritesFragment : Fragment(), FragmentAttributeInterface {

    override var isMenuAvailable: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override var actionBarTitle = ""
    override var actionBarTitle: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override var fullScreen: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? = inflater.inflate(R.layout.fragment_favorites, container, false)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fullScreen = false
        isMenuAvailable = true
        actionBarTitle = getString(R.string.favorites)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        actionBarTitle = getString(R.string.favorites)
    }
}