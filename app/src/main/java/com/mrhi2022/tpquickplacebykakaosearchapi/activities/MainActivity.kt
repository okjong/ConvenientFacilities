package com.mrhi2022.tpquickplacebykakaosearchapi.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayout
import com.mrhi2022.tpquickplacebykakaosearchapi.R
import com.mrhi2022.tpquickplacebykakaosearchapi.databinding.ActivityMainBinding
import com.mrhi2022.tpquickplacebykakaosearchapi.fragments.SearchListFragment
import com.mrhi2022.tpquickplacebykakaosearchapi.fragments.SearchMapFragment
import com.mrhi2022.tpquickplacebykakaosearchapi.model.KakaoSearchPlaceResponse
import com.mrhi2022.tpquickplacebykakaosearchapi.model.Place
import com.mrhi2022.tpquickplacebykakaosearchapi.model.PlaceMeta
import com.mrhi2022.tpquickplacebykakaosearchapi.network.RetrofitApiService
import com.mrhi2022.tpquickplacebykakaosearchapi.network.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }


    var searchQuery:String= "화장실"

    var mylocation:Location?= null


    val providerClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }


    var searchPlaceResponse: KakaoSearchPlaceResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)


        supportFragmentManager.beginTransaction().add(R.id.container_fragment, SearchListFragment()).commit()


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab?.text=="LIST"){
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment, SearchListFragment()).commit()
                }else if(tab?.text=="MAP"){
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment, SearchMapFragment()).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })


        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            searchQuery= binding.etSearch.text.toString()
            searchPlaces()


            false
        }


        setChoiceButtonsListener()


        val permissions:Array<String> = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if(  checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED  ){

            requestPermissions(permissions, 10)
        }else{

            requestMyLocation()
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==10 && grantResults[0]==PackageManager.PERMISSION_GRANTED) requestMyLocation()
        else Toast.makeText(this, "내 위치정보를 제공하지 않아 검색기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
    }



    private fun requestMyLocation(){


        val request:LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        providerClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }


    private val locationCallback:LocationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {

            mylocation= p0.lastLocation


            providerClient.removeLocationUpdates(this) //this : locationCallback


            searchPlaces()
        }
    }


    private fun searchPlaces(){


        Toast.makeText(this, " ${searchQuery} : ${mylocation?.latitude} , ${mylocation?.longitude} ", Toast.LENGTH_SHORT).show()


        val retrofit: Retrofit = RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitApiService: RetrofitApiService = retrofit.create(RetrofitApiService::class.java)
        retrofitApiService.searchPlaces(searchQuery, mylocation?.longitude.toString(), mylocation?.latitude.toString()).enqueue(object :  Callback<KakaoSearchPlaceResponse>{
            override fun onResponse(
                call: Call<KakaoSearchPlaceResponse>,
                response: Response<KakaoSearchPlaceResponse>
            ) {

                searchPlaceResponse= response.body()


                var meta: PlaceMeta?= searchPlaceResponse?.meta
                var documents:MutableList<Place>? = searchPlaceResponse?.documents

                AlertDialog.Builder(this@MainActivity).setMessage("${meta?.total_count} \n ${ documents?.get(0)?.place_name }").show()


                supportFragmentManager.beginTransaction().replace(R.id.container_fragment, SearchListFragment()).commit()


                binding.tabLayout.getTabAt(0)?.select()

            }

            override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "서버 오류가 있습니다.\n잠시뒤에 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        })

    }



    private fun setChoiceButtonsListener(){
        binding.layoutChoice.choiceWc.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceMovie.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceGas.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceEv.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePark.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePharmacy.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFood.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceMart.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceHospital.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceSubway.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceConvenienceStore.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceCafe.setOnClickListener { clickChoice(it) }
    }


    var choiceID= R.id.choice_wc

    private fun clickChoice(view: View){


        findViewById<ImageView>(choiceID).setBackgroundResource(R.drawable.bg_choice)


        view.setBackgroundResource(R.drawable.bg_choice_selected)


        choiceID= view.id


        when( view.id ){
            R.id.choice_wc-> searchQuery="화장실"
            R.id.choice_movie-> searchQuery="영화관"
            R.id.choice_gas-> searchQuery="주유소"
            R.id.choice_ev-> searchQuery="전기차충전소"
            R.id.choice_park-> searchQuery="공원"
            R.id.choice_pharmacy-> searchQuery="약국"
            R.id.choice_food-> searchQuery="맛집"
            R.id.choice_mart-> searchQuery="마트"
            R.id.choice_hospital-> searchQuery="병원"
            R.id.choice_subway-> searchQuery="지하철역"
            R.id.choice_convenience_store-> searchQuery="편의점"
            R.id.choice_cafe-> searchQuery="카페"
        }


        searchPlaces()


        binding.etSearch.text.clear()
        binding.etSearch.clearFocus()

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when( item.itemId ){
            R.id.menu_help-> Toast.makeText(this, "도움말", Toast.LENGTH_SHORT).show()
            R.id.menu_logout-> Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}