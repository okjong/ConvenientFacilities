package com.mrhi2022.tpquickplacebykakaosearchapi

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()


        KakaoSdk.init(this, "7c6c9a6ba12d9eaef85ec1bf54d72075")
    }
}