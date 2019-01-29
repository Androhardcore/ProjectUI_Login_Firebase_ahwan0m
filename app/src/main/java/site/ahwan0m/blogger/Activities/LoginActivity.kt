package site.ahwan0m.blogger.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

import site.ahwan0m.blogger.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private var userMail: EditText? = null
    private var userPassword: EditText? = null
    private var btnLogin: Button? = null
    private var loginProgress: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    private var HomeActivity: Intent? = null
    private var logentext: TextView? = null
    var checkbox: CheckBox? = null
    private var mGoogleApiClient: GoogleApiClient? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        userMail = findViewById(R.id.login_mail)
        userPassword = findViewById(R.id.login_password)
        btnLogin = findViewById(R.id.loginBtn)
        loginProgress = findViewById(R.id.login_progress)
        mAuth = FirebaseAuth.getInstance()
        HomeActivity = Intent(this, site.ahwan0m.blogger.Activities.MainActivity::class.java)
        checkbox = findViewById(R.id.checkbox)

        checkbox!!.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (checkbox!!.isChecked) {
                // show password
                userPassword!!.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            } else {
                // hide password
                userPassword!!.setTransformationMethod(PasswordTransformationMethod.getInstance())
            }
        }
        logentext = findViewById(R.id.daftar)
        logentext!!.setOnClickListener {
            val registerActivity = Intent(applicationContext, DaftarActivity::class.java)
            startActivity(registerActivity)
            finish()
        }

        loginProgress!!.visibility = View.INVISIBLE
        btnLogin!!.setOnClickListener {
            loginProgress!!.visibility = View.VISIBLE
            btnLogin!!.visibility = View.INVISIBLE

            val mail = userMail!!.text.toString()
            val password = userPassword!!.text.toString()

            if (mail.isEmpty() || password.isEmpty()) {
                showMessage("Jangan ada bidang yang kosong")
                btnLogin!!.visibility = View.VISIBLE
                loginProgress!!.visibility = View.INVISIBLE
            } else {
                signIn(mail, password)
            }
        }


    }

    private fun signIn(mail: String, password: String) {

        mAuth!!.signInWithEmailAndPassword(mail, password).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                loginProgress!!.visibility = View.INVISIBLE
                btnLogin!!.visibility = View.VISIBLE
                updateUI()

            } else {
                showMessage("Not Found")
                btnLogin!!.visibility = View.VISIBLE
                loginProgress!!.visibility = View.INVISIBLE
            }
        }


    }

    private fun updateUI() {

        startActivity(HomeActivity)
        finish()

    }

    private fun showMessage(text: String) {

        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }


    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser

        if (user != null) {
            //user is already connected  so we need to redirect him to home page

            val mail = userMail!!.text.toString()
            showMessage("Anda sudah login menggunakan email "+mail)
            updateUI()

        }


    }
    private fun revokeAccess() {
        // sign out Firebase
        mAuth!!.signOut()
        // revoke access Google
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback {
            updateUI()
        }
    }

    }
