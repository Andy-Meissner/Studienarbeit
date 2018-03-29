package de.studienarbeit.invoicescanner

import android.app.Fragment
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * Created by andym on 29.03.2018.
 */

class CameraFragment : Fragment(), View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback
{
    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.camera_fragment, container, false)
    }

}