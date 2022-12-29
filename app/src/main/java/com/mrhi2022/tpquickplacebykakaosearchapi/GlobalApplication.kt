package com.mrhi2022.tpquickplacebykakaosearchapi

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //카카오 SDK 초기화 - 플랫폼에서 발급된 "네이티브앱키" 필요
        KakaoSdk.init(this, "7c6c9a6ba12d9eaef85ec1bf54d72075")
    }
}