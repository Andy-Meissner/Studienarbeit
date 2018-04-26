package de.studienarbeit.invoicescanner.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.studienarbeit.invoicescanner.R

class AboutFragment : Fragment(), FragmentAttributeInterface {

    override var fullScreen = false
    override var isMenuAvailable = true
    override var actionBarTitle = getString(R.string.about) + " " + getString(R.string.app_name)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? = inflater.inflate(R.layout.fragment_about, container, false)

}