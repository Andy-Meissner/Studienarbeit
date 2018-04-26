package de.studienarbeit.invoicescanner.fragments

import android.support.v4.app.Fragment
import de.studienarbeit.invoicescanner.R

open class FragmentAttributeInterface : Fragment()
{
    open var fullScreen = false
    open var isMenuAvailable = true
    open var actionBarTitle = getString(R.string.about) + " " + getString(R.string.app_name)
}