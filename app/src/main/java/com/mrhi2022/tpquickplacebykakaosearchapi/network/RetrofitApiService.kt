package com.mrhi2022.tpquickplacebykakaosearchapi.network

import com.google.cloud.audit.AuthorizationInfo
import com.mrhi2022.tpquickplacebykakaosearchapi.model.KakaoSearchPlaceResponse
import com.mrhi2022.tpquickplacebykakaosearchapi.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitApiService {

    //네아로 사용자정보 API
    @GET("/v1/nid/me")
    fun getNidUserInfo(@Header("Authorization") authorization:String): Call<NidUserInfoResponse>

    //카카오 키워드 장소검색 API
    @Headers("Authorization: KakaoAK e5e670d0fed8c3433568b75602f5761e")
    @GET("/v2/local/search/keyword.json")
    fun searchPlaces(@Query("query") query:String, @Query("x") longitude:String,@Query("y") latitude:String):Call<KakaoSearchPlaceResponse>
}