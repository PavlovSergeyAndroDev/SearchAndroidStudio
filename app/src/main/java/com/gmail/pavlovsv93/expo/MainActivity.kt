package com.gmail.pavlovsv93.expo

import android.app.SearchManager
import android.app.SearchableInfo
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar

const val TAG_SEARCH = "SearchView"
const val ARG_SEARCH = "ARG_SEARCH"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Инициализация Toolbar
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (Intent.ACTION_SEARCH == intent?.action) {
            intent.getStringExtra(SearchManager.QUERY)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)

        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager

        //Инициализация поисковой строки
        val searchView = menu?.findItem(R.id.search_bar)?.actionView as SearchView
        val searchInfo = searchManager.getSearchableInfo(componentName)
        searchView.setSearchableInfo(searchInfo)
        //Слушатель набираемого текста
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // переопределить два метода
            // поиск введенной строки по нажатию на клавишу "поиск" на клавиатуре
            override fun onQueryTextSubmit(query: String?): Boolean {
                val searchRecentSuggestion = SearchRecentSuggestions(
                    this@MainActivity,
                    MySuggestionProvider.AUTHORITY,
                    MySuggestionProvider.MODE
                )
                searchRecentSuggestion.saveRecentQuery(query, null)
                // для работы с line2 необходимо включить двухстрочный режим DATABASE_MODE_2LINES в MySuggestionProvider
                Log.d(TAG_SEARCH, "onQueryTextSubmit: $query")
                return false
            }

            // поиск посимвольного ввода
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG_SEARCH, "onQueryTextChange: $newText")
                return false
            }
        })

        val clearBtn = findViewById<View>(R.id.clean_history)
        clearBtn.setOnClickListener {
            clearHistory()
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun clearHistory() {
        Log.d(TAG_SEARCH, "clearHistory")
        SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE)
            .clearHistory()
    }
}
