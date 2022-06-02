package com.android.gamerenter.fragment

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.gamerenter.R
import com.android.gamerenter.adapter.UpcomingVideogameAdapter
import com.android.gamerenter.adapter.GenericVideogameAdapter
import com.android.gamerenter.model.VideogameModel
import com.android.gamerenter.viewmodel.DashboardViewModel
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private lateinit var rvRentedVideogames: RecyclerView
    private lateinit var mtRecentSearchViewMore: MaterialTextView
    private lateinit var rvRecentSearchVideogames: RecyclerView
    private lateinit var mtUpcomingViewMore: MaterialTextView
    private lateinit var rvUpcomingVideogames: RecyclerView

    @Inject
    lateinit var upcomingVideogamesAdapter: UpcomingVideogameAdapter

    @Inject
    lateinit var videogameAdapter: GenericVideogameAdapter

    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvRentedVideogames = view.findViewById(R.id.rented_list)
        mtRecentSearchViewMore = view.findViewById(R.id.recent_search_more)
        rvRecentSearchVideogames = view.findViewById(R.id.recent_search_list)
        mtUpcomingViewMore = view.findViewById(R.id.upcoming_more)
        rvUpcomingVideogames = view.findViewById(R.id.upcoming_list)

        mtRecentSearchViewMore.setOnClickListener { openGenericList() }
        mtUpcomingViewMore.setOnClickListener { openGenericList() }

        dashboardViewModel.rentedVideogamesLiveData.observe(
            viewLifecycleOwner
        ) {
            videogameAdapter.submitList(it)
            videogameAdapter.setOnItemListClickListener { videogame ->
                findNavController().navigate(
                    R.id.detailVideogameFragment,
                    VideogameDetailFragmentArgs(videogame).toBundle()
                )
            }
            rvRentedVideogames.apply {
                val pagerSnapHelper = PagerSnapHelper()

                adapter = videogameAdapter
                layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
                pagerSnapHelper.attachToRecyclerView(this)
            }
        }

        dashboardViewModel.recentSearchVideogamesLiveData.observe(viewLifecycleOwner, Observer {
            videogameAdapter.submitList(it)
            rvRecentSearchVideogames.apply {
                val pagerSnapHelper = PagerSnapHelper()

                adapter = videogameAdapter
                layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
                pagerSnapHelper.attachToRecyclerView(this)
            }
        })

        dashboardViewModel.upcomingVideogamesLiveData.observe(
            viewLifecycleOwner
        ) { t ->
            upcomingVideogamesAdapter.submitList(t)
            rvUpcomingVideogames.apply {
                val pagerSnapHelper = PagerSnapHelper()

                adapter = upcomingVideogamesAdapter
                layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
                pagerSnapHelper.attachToRecyclerView(this)
            }
        }

        dashboardViewModel.platformLiveData.observe(viewLifecycleOwner, Observer {
            upcomingVideogamesAdapter.updatePlatformList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.admin_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun openGenericList() {
        //val destination = DashboardFragmentDirections.openGenericList()
        //val navDirection = Directions
        findNavController().navigate(R.id.genericListFragment)
    }
}