package com.android.gamerenter.fragment

import android.content.Intent
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
import com.android.gamerenter.activity.AdminFlowActivity
import com.android.gamerenter.adapter.UpcomingVideogameAdapter
import com.android.gamerenter.adapter.GenericVideogameAdapter
import com.android.gamerenter.adapter.RentedVideogamesAdapter
import com.android.gamerenter.dialog.AdminCheckDialog
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
    lateinit var rentedVideogamesAdapter: RentedVideogamesAdapter

    @Inject
    lateinit var recentSearchAdapter: GenericVideogameAdapter

    @Inject
    lateinit var upcomingVideogamesAdapter: UpcomingVideogameAdapter

    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

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

        mtRecentSearchViewMore.setOnClickListener { openGenericList(false) }
        mtUpcomingViewMore.setOnClickListener { openGenericList(true) }

        dashboardViewModel.rentedVideogamesLiveData.observe(
            viewLifecycleOwner
        ) {
            rentedVideogamesAdapter.apply {
                submitList(it)

                setOnItemListClickListener { videogame ->
                    dashboardViewModel.addItemInRecentSearch(videogame)
                    findNavController().navigate(
                        R.id.detailVideogameFragment,
                        VideogameDetailFragmentArgs(videogame).toBundle()
                    )
                }
            }
            rvRentedVideogames.apply {
                val pagerSnapHelper = PagerSnapHelper()

                adapter = rentedVideogamesAdapter
                layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
                pagerSnapHelper.attachToRecyclerView(this)
            }
        }

        dashboardViewModel.getRecentSearchVideogameList()
        dashboardViewModel.recentSearchVideogamesLiveData.observe(viewLifecycleOwner) {
            recentSearchAdapter.apply {
                submitList(it)
                setOnItemListClickListener { videogameModel ->
                    findNavController().navigate(
                        R.id.detailVideogameFragment,
                        VideogameDetailFragmentArgs(videogameModel).toBundle()
                    )
                }
                setOnItemRemoveClickListener { videogameModel ->
                    dashboardViewModel.removeRecentSearchItem(
                        videogameModel.id,
                        object : OnRemoveItem {
                            override fun onSuccessRemove() {
                            }

                            override fun onFailureRemove() {
                            }
                        })

                }
            }
            rvRecentSearchVideogames.apply {
                val pagerSnapHelper = PagerSnapHelper()

                adapter = recentSearchAdapter
                layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
                onFlingListener = null;
                pagerSnapHelper.attachToRecyclerView(this)
            }
        }


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

        dashboardViewModel.platformLiveData.observe(viewLifecycleOwner) {
            upcomingVideogamesAdapter.updatePlatformList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.admin_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> AdminCheckDialog(
                resources.getString(R.string.dialog_admin_title),
                resources.getString(R.string.dialog_admin_subtitle),
                object : AdminCheckDialog.OnCheckAdmin {
                    override fun onCheckAdmin(value: String) {
                        val valid = dashboardViewModel.checkAdminCode(value)
                        if (valid) {
                            startActivity(Intent(context, AdminFlowActivity::class.java))
                        } else {
                            // TODO show error dialog
                        }
                    }
                }).show(parentFragmentManager, "")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openGenericList(isComingVideogamesRequest: Boolean) {
        findNavController().navigate(
            R.id.genericListFragment,
            GenericListFragmentArgs(isComingVideogamesRequest).toBundle()
        )
    }


    interface OnRemoveItem {
        fun onSuccessRemove()
        fun onFailureRemove()
    }
}