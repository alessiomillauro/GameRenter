package com.android.gamerenter.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.android.gamerenter.R
import com.android.gamerenter.adapter.VideogameAdapter
import com.android.gamerenter.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private lateinit var rvUpcomingVideogames: RecyclerView

    private lateinit var upcomingVideogamesAdapter: VideogameAdapter

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

        rvUpcomingVideogames = view.findViewById(R.id.upcoming_list)

        upcomingVideogamesAdapter = VideogameAdapter()

        dashboardViewModel.upcomingVideogamesLiveData.observe(
            viewLifecycleOwner
        ) { t ->
            upcomingVideogamesAdapter.submitList(t)
            rvUpcomingVideogames.apply {
                adapter = upcomingVideogamesAdapter
                layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            }
        }

        dashboardViewModel.platformLiveData.observe(viewLifecycleOwner, Observer {
            upcomingVideogamesAdapter.updatePlatformList(it)
        })
    }
}