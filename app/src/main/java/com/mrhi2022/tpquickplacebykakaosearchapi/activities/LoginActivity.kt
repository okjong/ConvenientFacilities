package com.mrhi2022.tpquickplacebykakaosearchapi.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.mrhi2022.tpquickplacebykakaosearchapi.G
import com.mrhi2022.tpquickplacebykakaosearchapi.databinding.ActivityLoginBinding
import com.mrhi2022.tpquickplacebykakaosearchapi.model.NidUserInfoResponse
import com.mrhi2022.tpquickplacebykakaosearchapi.model.UserAccount
import com.mrhi2022.tpquickplacebykakaosearchapi.network.RetrofitApiService
import com.mrhi2022.tpquickplacebykakaosearchapi.network.RetrofitHelper
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        binding.tvGo.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }


        binding.layoutEmail.setOnClickListener {
            startActivity(Intent(this, EmailLoginActivity::class.java))
        }


        binding.btnLoginKakao.setOnClickListener { clickedLoginKakao() }
        binding.btnLoginGoogle.setOnClickListener { clickedLoginGoogle() }
        binding.btnLoginNaver.setOnClickListener { clickedLoginNaver() }


        var keyHash:String = Utility.getKeyHash(this);
        Log.i("keyHash", keyHash)

    }

    private fun clickedLoginKakao(){

        val callback:( OAuthToken? , Throwable?)->Unit = { token, error ->
            if( error != null ) {
                Toast.makeText(this, "?????????????????? ??????", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "?????????????????? ??????", Toast.LENGTH_SHORT).show()


                UserApiClient.instance.me { user, error ->
                    if(user!=null){
                        var id:String = user.id.toString()
                        var email:String = user.kakaoAccount?.email ?: ""  //?????? null?????? ???????????? ????????? ""

                        Toast.makeText(this, "????????? ????????? ?????? : $email", Toast.LENGTH_SHORT).show()
                        G.userAccount= UserAccount(id, email)


                        startActivity( Intent(this, MainActivity::class.java) )
                        finish()
                    }
                }
            }
        }


        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            UserApiClient.instance.loginWithKakaoTalk(this, callback= callback )
        }else{
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback )
        }

    }

    private fun clickedLoginGoogle(){



        val signInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()  //?????????????????? ?????? ??? ?????? ???????????????
            .build()


        val intent:Intent= GoogleSignIn.getClient(this, signInOptions).signInIntent

        googleResultLauncher.launch(intent)
    }


    val googleResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), object : ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {

            val intent:Intent? = result?.data

            val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)

            val account:GoogleSignInAccount= task.result

            var id:String= account.id.toString()
            var email:String= account.email ?: ""

            Toast.makeText(this@LoginActivity, "Google ????????? ?????? : $email", Toast.LENGTH_SHORT).show()
            G.userAccount= UserAccount(id, email)


            startActivity( Intent(this@LoginActivity, MainActivity::class.java) )
            finish()
        }
    })

    private fun clickedLoginNaver(){

        NaverIdLoginSDK.initialize(this, "piT_RShtVAWe3Ahahe6d", "Mu0rjNjsU4", "QuickPlace")


        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback{
            override fun onError(errorCode: Int, message: String) {
                Toast.makeText(this@LoginActivity, "error : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(this@LoginActivity, "failure : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(this@LoginActivity, "????????? ??????", Toast.LENGTH_SHORT).show()


                val accessToken:String? = NaverIdLoginSDK.getAccessToken()


                Log.i("token", accessToken.toString())


                val retrofit= RetrofitHelper.getRetrofitInstance("https://openapi.naver.com")
                retrofit.create(RetrofitApiService::class.java).getNidUserInfo("Bearer $accessToken").enqueue(object : Callback<NidUserInfoResponse>{
                    override fun onResponse(
                        call: Call<NidUserInfoResponse>,
                        response: Response<NidUserInfoResponse>
                    ) {
                        val userInfo: NidUserInfoResponse? = response.body()
                        var id:String= userInfo?.response?.id ?: ""
                        var email:String= userInfo?.response?.email ?: ""

                        Toast.makeText(this@LoginActivity, "$email", Toast.LENGTH_SHORT).show()
                        G.userAccount= UserAccount(id, email)


                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }

                    override fun onFailure(call: Call<NidUserInfoResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "???????????? ???????????? ?????? ${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })



            }
        })


    }

}