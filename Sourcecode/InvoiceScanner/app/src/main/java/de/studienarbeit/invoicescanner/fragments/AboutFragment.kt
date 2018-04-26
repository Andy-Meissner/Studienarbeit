package de.studienarbeit.invoicescanner.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.studienarbeit.invoicescanner.R
import de.studienarbeit.invoicescanner.TITLE_ABOUT

class AboutFragment : Fragment(), FragmentAttributeInterface {

    override var isMenuAvailable = true
    override var actionBarTitle = TITLE_ABOUT
    override var fullScreen = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? = inflater.inflate(R.layout.fragment_about, container, false)
}