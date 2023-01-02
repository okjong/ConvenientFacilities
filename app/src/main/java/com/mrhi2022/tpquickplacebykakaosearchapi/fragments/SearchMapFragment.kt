package com.mrhi2022.tpquickplacebykakaosearchapi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrhi2022.tpquickplacebykakaosearchapi.activities.MainActivity
import com.mrhi2022.tpquickplacebykakaosearchapi.activities.PlaceUrlActivity
import com.mrhi2022.tpquickplacebykakaosearchapi.databinding.FragmentSearchListBinding
import com.mrhi2022.tpquickplacebykakaosearchapi.databinding.FragmentSearchMapBinding
import com.mrhi2022.tpquickplacebykakaosearchapi.model.Place
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class SearchMapFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    val binding: FragmentSearchMapBinding by lazy { FragmentSearchMapBinding.inflate(layoutInflater) }

    val mapView: MapView by lazy { MapView(context) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.containerMapview.addView(mapView)


        mapView.setPOIItemEventListener(markerEventListener)


        setMapAndMarkers()
    }

    private fun setMapAndMarkers(){

        var latitude: Double = (activity as MainActivity).mylocation?.latitude ?: 37.566805
        var longitude: Double = (activity as MainActivity).mylocation?.longitude ?: 126.9784147

        var myMapPoint: MapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
        mapView.setMapCenterPointAndZoomLevel(myMapPoint, 5, true)
        mapView.zoomIn(true)
        mapView.zoomOut(true)


        var marker= MapPOIItem()
        marker.apply {
            itemName="ME"
            mapPoint= myMapPoint
            markerType= MapPOIItem.MarkerType.BluePin
            selectedMarkerType= MapPOIItem.MarkerType.YellowPin
        }
        mapView.addPOIItem(marker)


        val documents: MutableList<Place>? = (activity as MainActivity).searchPlaceResponse?.documents
        documents?.forEach {
            val point:MapPoint= MapPoint.mapPointWithGeoCoord(it.y.toDouble(), it.x.toDouble())


            val marker:MapPOIItem= MapPOIItem().apply {
                itemName= it.place_name
                mapPoint= point
                markerType= MapPOIItem.MarkerType.RedPin
                selectedMarkerType= MapPOIItem.MarkerType.YellowPin


                userObject= it
            }
            mapView.addPOIItem(marker)
        }//forEach

    }


    private val markerEventListener: MapView.POIItemEventListener = object: MapView.POIItemEventListener{
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

        }


        override fun onCalloutBalloonOfPOIItemTouched(
            p0: MapView?,
            p1: MapPOIItem?,
            p2: MapPOIItem.CalloutBalloonButtonType?
        ) {


            if(p1?.userObject == null ) return

            var place: Place= p1?.userObject as Place


            val intent: Intent= Intent(context, PlaceUrlActivity::class.java)
            intent.putExtra("place_url", place.place_url)
            startActivity(intent)

        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

        }

    }


}