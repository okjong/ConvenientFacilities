package com.mrhi2022.tpquickplacebykakaosearchapi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrhi2022.tpquickplacebykakaosearchapi.activities.MainActivity
import com.mrhi2022.tpquickplacebykakaosearchapi.adapters.PlaceListRecyclerAdapter
import com.mrhi2022.tpquickplacebykakaosearchapi.databinding.FragmentSearchListBinding

class SearchListFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    val binding: FragmentSearchListBinding by lazy { FragmentSearchListBinding.inflate(layoutInflater) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MainActivity를 참조하기
        val ma:MainActivity= activity as MainActivity

        // 아직 MainActivity에서 파싱작업이 완료되지 않았다면 데이터가 없음.
        if( ma.searchPlaceResponse==null ) return

        binding.recyclerView.adapter= PlaceListRecyclerAdapter(requireContext(), ma.searchPlaceResponse!!.documents)
    }

}