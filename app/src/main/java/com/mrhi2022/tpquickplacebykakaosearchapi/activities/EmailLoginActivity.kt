package com.mrhi2022.tpquickplacebykakaosearchapi.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.mrhi2022.tpquickplacebykakaosearchapi.G
import com.mrhi2022.tpquickplacebykakaosearchapi.R
import com.mrhi2022.tpquickplacebykakaosearchapi.databinding.ActivityEmailLoginBinding
import com.mrhi2022.tpquickplacebykakaosearchapi.model.UserAccount

class EmailLoginActivity : AppCompatActivity() {

    val binding: ActivityEmailLoginBinding by lazy { ActivityEmailLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        binding.btnSignIn.setOnClickListener { clickSignIn() }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun clickSignIn(){

        var email= binding.etEmail.text.toString()
        var password= binding.etPassword.text.toString()


        val db:FirebaseFirestore = FirebaseFirestore.getInstance()

        db.collection("emailUsers")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get().addOnSuccessListener {
                if( it.documents.size > 0 ){

                    var id= it.documents[0].id
                    G.userAccount= UserAccount(id, email)


                    val intent:Intent = Intent(this, MainActivity::class.java)


                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }else{

                    AlertDialog.Builder(this).setMessage("이메일과 비밀번호를 다시 확인해주시기 바랍니다.").show()
                    binding.etEmail.requestFocus()
                    binding.etEmail.selectAll()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "서버오류 : ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
}

























































