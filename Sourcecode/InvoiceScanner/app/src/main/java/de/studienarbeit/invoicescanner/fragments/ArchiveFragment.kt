package de.studienarbeit.invoicescanner.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.studienarbeit.invoicescanner.R

class ArchiveFragment : Fragment(), FragmentAttributeInterface {

    override var fullScreen = false
    override var actionBarTitle = ""
    override var isMenuAvailable = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? = inflater.inflate(R.layout.fragment_archive, container, false)

}