package com.mrhi2022.tpquickplacebykakaosearchapi.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrhi2022.tpquickplacebykakaosearchapi.R
import com.mrhi2022.tpquickplacebykakaosearchapi.activities.PlaceUrlActivity
import com.mrhi2022.tpquickplacebykakaosearchapi.databinding.RecyclerItemListFragmentBinding
import com.mrhi2022.tpquickplacebykakaosearchapi.model.Place

class PlaceListRecyclerAdapter(val context: Context, var documents: MutableList<Place>) : RecyclerView.Adapter<PlaceListRecyclerAdapter.VH>() {

    inner class VH(itemView:View) : RecyclerView.ViewHolder(itemView){
        val binding: RecyclerItemListFragmentBinding by lazy { RecyclerItemListFragmentBinding.bind(itemView) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView:View = LayoutInflater.from(context).inflate(R.layout.recycler_item_list_fragment, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val place:Place = documents[position]

        holder.binding.tvPlaceName.text= place.place_name
        holder.binding.tvAddress.text = if(place.road_address_name=="") place.address_name else place.road_address_name
        holder.binding.tvDistance.text = "${place.distance}m"


        holder.binding.root.setOnClickListener {
            val intent:Intent= Intent(context, PlaceUrlActivity::class.java)
            intent.putExtra("place_url", place.place_url)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = documents.size

}