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
        //setContentView(R.layout.activity_login)
        setContentView(binding.root)

        //둘러보기 글씨 클릭으로 로그인없이 Main 화면으로 이동
        binding.tvGo.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        //회원가입 버튼 클릭
        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        //이메일 로그인 버튼 클릭
        binding.layoutEmail.setOnClickListener {
            startActivity(Intent(this, EmailLoginActivity::class.java))
        }

        //간편로그인 버튼들
        binding.btnLoginKakao.setOnClickListener { clickedLoginKakao() }
        binding.btnLoginGoogle.setOnClickListener { clickedLoginGoogle() }
        binding.btnLoginNaver.setOnClickListener { clickedLoginNaver() }

        // 카카오 SDK용 키해시 값 얻어오기
        var keyHash:String = Utility.getKeyHash(this);
        Log.i("keyHash", keyHash)

    }

    private fun clickedLoginKakao(){
        // Kakao Login API를 이용하여 사용자 정보 취득

        // 로그인 시도한 결과를 받았을때 발동하는 콜백함수를 별도로 만들기
        val callback:( OAuthToken? , Throwable?)->Unit = { token, error ->
            if( error != null ) {
                Toast.makeText(this, "카카오로그인 실패", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "카카오로그인 성공", Toast.LENGTH_SHORT).show()

                //사용자 정보 요청
                UserApiClient.instance.me { user, error ->
                    if(user!=null){
                        var id:String = user.id.toString()
                        var email:String = user.kakaoAccount?.email ?: ""  //혹시 null이면 이메일의 기본값 ""

                        Toast.makeText(this, "사용자 이메일 정보 : $email", Toast.LENGTH_SHORT).show()
                        G.userAccount= UserAccount(id, email)

                        //로그인이 성공했으니.. Main 화면으로 전환
                        startActivity( Intent(this, MainActivity::class.java) )
                        finish()
                    }
                }
            }
        }

        // 카카오톡 로그인을 권장하지만 설치가 되어 있지 않다면 카카오계정으로 로그인 시도.
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            UserApiClient.instance.loginWithKakaoTalk(this, callback= callback )
        }else{
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback )
        }

    }

    private fun clickedLoginGoogle(){
        // 구글 로그인 화면(액티비티)를 실행하여 결과를 받아와서 사용자정보 취득 - android google login 검색

        //구글 로그인 옵션객체 생성 - Builder 이용
        val signInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()  //이메일정보를 받을 수 있는 로그인옵션
            .build()

        //구글 로그인화면(Activity)가 이미 라이브러리에 만들어져 있음.
        //그러니 그 액티비티를 실행시켜주는 Intent객체를 소환하기
        val intent:Intent= GoogleSignIn.getClient(this, signInOptions).signInIntent
        //로그인 결과를 받기위해 액티비티를 실행.
        googleResultLauncher.launch(intent)
    }

    //구글 로그인 화면액티비티를 실행시키고 그 결과를 되돌려받는 작업을 관리하는 객체 생성
    val googleResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), object : ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {
            //로그인 결과를 가져온 인텐트 객체 소환
            val intent:Intent? = result?.data
            //돌아온 인텐트객체에게 구글 계정정보를 빼오기
            val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)

            val account:GoogleSignInAccount= task.result

            var id:String= account.id.toString()
            var email:String= account.email ?: "" //혹시 null이면 이메일 기본값 ""

            Toast.makeText(this@LoginActivity, "Google 로그인 성공 : $email", Toast.LENGTH_SHORT).show()
            G.userAccount= UserAccount(id, email)

            //Main 화면으로 전환
            startActivity( Intent(this@LoginActivity, MainActivity::class.java) )
            finish()
        }
    })

    private fun clickedLoginNaver(){
        // 사용자정보를 취득하는 토큰값을 발급받아 REST API 방식으로 사용자정보 취득
        // 네이버 개발자 센터 가이드 문서 참고 - 애플리케이션 등록 완료.

        // 네아로 SDK 초기화
        NaverIdLoginSDK.initialize(this, "piT_RShtVAWe3Ahahe6d", "Mu0rjNjsU4", "QuickPlace")

        // 네아로 전용버튼 뷰 사용 대신에.. 직접 로그인 요청 메소드를 사용
        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback{
            override fun onError(errorCode: Int, message: String) {
                Toast.makeText(this@LoginActivity, "error : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(this@LoginActivity, "failure : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                // 사용자를 정보를 가져오려면 서버와 HTTP REST API 통신을 해야함.
                // 단, 필요한 요청파라미터가 있음. 사용자정보에 접속할 수 있는 인증키 같은 값 - 토큰 이라고 부름
                val accessToken:String? = NaverIdLoginSDK.getAccessToken()

                //토큰값을 확인해보기 - 토큰값을 그때그때 마다 갱신됨. [즉, 1회용 접속 키]
                Log.i("token", accessToken.toString())

                //Retrofit 작업을 통해 사용자 정보 가져오기
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

                        // main 화면으로 이동
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }

                    override fun onFailure(call: Call<NidUserInfoResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "회원정보 불러오기 실패 ${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })



            }
        })


    }

}