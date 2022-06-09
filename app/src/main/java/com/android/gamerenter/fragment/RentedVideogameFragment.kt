package com.android.gamerenter.fragment

import android.os.Bundle
import android.view.Gravity.*
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.android.gamerenter.R
import com.android.gamerenter.adapter.RentedVideogamesAdapter
import com.android.gamerenter.viewmodel.RentedViewModel
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RentedVideogameFragment : Fragment() {

    private val rentedViewModel: RentedViewModel by viewModels()

    private lateinit var titleRented: MaterialTextView
    private lateinit var rvRentedList: RecyclerView

    @Inject
    lateinit var adapter: RentedVideogamesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rented_videogames, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleRented = view.findViewById(R.id.result_title)
        rvRentedList = view.findViewById(R.id.rv_rented)

        rentedViewModel.rentedVideogamesLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            rvRentedList.adapter = adapter

            titleRented.text =
                String.format(resources.getString(R.string.rented_total_title), it.size)
        }
    }
}