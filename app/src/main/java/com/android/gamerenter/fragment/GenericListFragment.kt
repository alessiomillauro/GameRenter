package com.android.gamerenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.android.gamerenter.R
import com.android.gamerenter.adapter.GenericVideogameAdapter
import com.android.gamerenter.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GenericListFragment : Fragment() {

    val args: GenericListFragmentArgs by navArgs()

    var isComingVideogamesRequest = false

    private val dashboardViewModel: DashboardViewModel by viewModels()

    private lateinit var rvVideogames: RecyclerView

    @Inject
    lateinit var recentSearchAdapter: GenericVideogameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isComingVideogamesRequest = args.isComingVideogamesRequest
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_generic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvVideogames = view.findViewById(R.id.generic_list)

        if (isComingVideogamesRequest) {
            dashboardViewModel.upcomingVideogamesLiveData.observe(viewLifecycleOwner) {
                recentSearchAdapter.submitList(it)
                rvVideogames.adapter = recentSearchAdapter
            }
        } else {
            dashboardViewModel.getRecentSearchVideogameList()
            dashboardViewModel.recentSearchVideogamesLiveData.observe(viewLifecycleOwner) {
                recentSearchAdapter.submitList(it)
                rvVideogames.adapter = recentSearchAdapter
            }
        }
    }
}