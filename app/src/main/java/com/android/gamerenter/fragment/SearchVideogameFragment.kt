package com.android.gamerenter.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.android.gamerenter.R
import com.android.gamerenter.adapter.GenericVideogameAdapter
import com.android.gamerenter.viewmodel.SearchViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchVideogameFragment : Fragment() {

    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var searchContainer: TextInputLayout
    private lateinit var searchField: TextInputEditText
    private lateinit var btnSearch: Button
    private lateinit var resultTitle: MaterialTextView
    private lateinit var resultList: RecyclerView
    private lateinit var llNotFound: LinearLayout

    @Inject
    lateinit var adapter: GenericVideogameAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_videogame, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchContainer = view.findViewById(R.id.search_container)
        searchField = view.findViewById(R.id.search_edittext)
        btnSearch = view.findViewById(R.id.search_action)
        resultTitle = view.findViewById(R.id.result_title)
        resultList = view.findViewById(R.id.rv_result)
        llNotFound = view.findViewById(R.id.ll_not_found)

        btnSearch.text = getString(R.string.search_action)
        btnSearch.setOnClickListener {
            if (searchField.text.isNullOrBlank()) {
                searchContainer.error = getString(R.string.insert_valid_name)
            } else {
                val query = searchField.text.toString()
                searchViewModel.search(query)
            }
        }

        resultTitle.text = getString(R.string.cronology)

        searchViewModel.recentSearchVideogamesLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            resultList.adapter = adapter
        }

        searchViewModel.searchResult.observe(viewLifecycleOwner) {
            resultTitle.text = String.format(getString(R.string.result_search), it.size )
            llNotFound.visibility = if (it.isEmpty()) VISIBLE else INVISIBLE
            adapter.submitList(it)
        }
    }

}