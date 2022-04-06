package com.example.inoutshoppers.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.inoutshoppers.databinding.ItemListBinding
import com.example.inoutshoppers.item.recyclerview.ItemListAdapter
import com.example.inoutshoppers.shopping_navigation.ShoppingNavigation
import com.google.android.libraries.places.api.model.Place


class ItemList : Fragment() {

    private val itemList = mutableListOf<String>()
    private lateinit var itemListAdapter: ItemListAdapter
    private lateinit var binding: ItemListBinding
    private var store: Place? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = ItemListBinding.inflate(inflater, container, false)

        store = arguments?.getParcelable("selected_store")
        binding.selectedStoreInfo.text = "You're shopping at: \n ${store?.name} \n ${store?.address}"

        initRecyclerView()
        setClickListeners()

        return binding.root
    }

    private fun setClickListeners() {

        binding.shopButton.setOnClickListener { view : View ->
            if (store != null) {
                val bundle = Bundle()
                bundle.putParcelable(ShoppingNavigation.STORE, store)
                bundle.putStringArray(ShoppingNavigation.SHOPPING_ITEMS, itemList.toTypedArray())
                view.findNavController().navigate(ItemListDirections.actionItemListToShoppingNavigation(bundle))
            }
        }

        binding.addItem.setOnClickListener {
            val text = binding.searchEditText.text.toString()
            if (text.isNotEmpty()) {
                itemList.add(text)
                itemListAdapter.notifyItemInserted(itemList.lastIndex)
            }
        }
    }

    private fun initRecyclerView() {
        itemListAdapter = ItemListAdapter(items = itemList)
        binding.shoppingListRecyclerView.adapter = itemListAdapter
    }


}