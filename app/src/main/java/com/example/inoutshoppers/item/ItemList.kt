package com.example.inoutshoppers.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.inoutshoppers.R
import com.example.inoutshoppers.databinding.ItemListBinding
import com.example.inoutshoppers.databinding.LoginBinding


class ItemList : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = ItemListBinding.inflate(inflater, container, false)

        binding.shopButton.setOnClickListener { view : View ->
            view.findNavController().navigate(ItemListDirections.actionItemListToShoppingNavigation())
        }

        return binding.root
    }

}