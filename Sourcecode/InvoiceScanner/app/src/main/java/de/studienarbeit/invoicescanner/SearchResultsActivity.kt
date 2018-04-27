package de.studienarbeit.invoicescanner

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.support.v4.media.session.MediaButtonReceiver.handleIntent

class SearchResultsActivity : Activity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        handleIntent(intent)
        super.onCreate(savedInstanceState)
    }


    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {

        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data somehow
        }
    }
}