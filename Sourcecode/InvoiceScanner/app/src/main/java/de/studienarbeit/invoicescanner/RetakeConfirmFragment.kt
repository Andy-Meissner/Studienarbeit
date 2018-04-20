package de.studienarbeit.invoicescanner

import android.content.Context
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by andym on 20.04.2018.
 */
class RetakeConfirmFragment : Fragment() , View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback
{

    lateinit var mListener : onButtonClickedListener

    override fun onClick(v: View) {
        when(view!!.id)
        {
            R.id.dismiss -> mListener.onButtonDismiss()
            R.id.analyze -> mListener.onButtonAnalyze()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                                       container: ViewGroup?,
                                       savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.confirm_retake_fragment, container, false)

    interface onButtonClickedListener {
        fun onButtonDismiss();
        fun onButtonAnalyze();
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as onButtonClickedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement OnArticleSelectedListener")
        }
    }
}