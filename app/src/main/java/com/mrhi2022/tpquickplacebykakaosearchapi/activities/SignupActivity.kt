package com.mrhi2022.tpquickplacebykakaosearchapi.activities

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.mrhi2022.tpquickplacebykakaosearchapi.R
import com.mrhi2022.tpquickplacebykakaosearchapi.databinding.ActivitySignupBinding


class SignupActivity : AppCompatActivity() {

    val binding:ActivitySignupBinding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        binding.btnSignup.setOnClickListener { clickSignUp() }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun clickSignUp(){


        var email:String = binding.etEmail.text.toString()
        var password:String = binding.etPassword.text.toString()
        var passwordConfirm:String = binding.etPasswordConfirm.text.toString()


        if( password != passwordConfirm ){
            AlertDialog.Builder(this).setMessage("비밀번호 확인에 문제가 있습니다. 다시 확인하여 입력해주시기 바랍니다.").show()
            binding.etPasswordConfirm.selectAll()
            return
        }


        var db:FirebaseFirestore = FirebaseFirestore.getInstance()


        db.collection("emailUsers")
            .whereEqualTo("email", email)
            .get().addOnSuccessListener {

                if( it.documents.size > 0 ){
                    AlertDialog.Builder(this).setMessage("중복된 이메일이 있습니다. 다시 확인하여 입력해주시기 바랍니다.").show()
                    binding.etEmail.requestFocus() //selectAll() 하려면 포커스가 있어야 함.
                    binding.etEmail.selectAll()
                }else{

                    var user: MutableMap<String, String> = mutableMapOf()
                    user.put("email", email)
                    user.put("password", password)


                    db.collection("emailUsers").add(user).addOnSuccessListener {
                        AlertDialog.Builder(this)
                            .setMessage("축하합니다.\n회원가입이 완료되었습니다.")
                            .setPositiveButton("확인", object : DialogInterface.OnClickListener{
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    finish()
                                }
                            }).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "회원가입 실패 : ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }

}



































































































