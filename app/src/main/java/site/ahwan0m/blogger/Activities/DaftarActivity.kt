package site.ahwan0m.blogger.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.AppCompatCheckBox
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*

import site.ahwan0m.blogger.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.daftar_activity.*
import site.ahwan0m.blogger.R.id.regBtn

class DaftarActivity : AppCompatActivity() {


    lateinit var ImgUserPhoto: ImageView
    internal var pickedImgUri: Uri? = null

    var userEmail: EditText? = null
    var userPassword: EditText? = null
    var userPAssword2: EditText? = null
    var userName: EditText? = null
    var loadingProgress: ProgressBar? = null
    var regBtn: Button? = null
    private var logien: TextView? = null
    private var mAuth: FirebaseAuth? = null
    var checkbox: AppCompatCheckBox? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.daftar_activity)

        //ini views
        userEmail = findViewById(R.id.regMail)
        userPassword = findViewById(R.id.regPassword)
        userPAssword2 = findViewById(R.id.regPassword2)
        userName = findViewById(R.id.regName)
        loadingProgress = findViewById(R.id.regProgressBar)
        regBtn = findViewById(R.id.regBtn)
        loadingProgress!!.visibility = View.INVISIBLE
        checkbox = findViewById(R.id.checkbox)

        checkbox!!.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (checkbox!!.isChecked) {
                // show password
                userPassword!!.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                userPAssword2!!.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            } else {
                // hide password
                userPAssword2!!.setTransformationMethod(PasswordTransformationMethod.getInstance())
                userPassword!!.setTransformationMethod(PasswordTransformationMethod.getInstance())
            }
        }
        mAuth = FirebaseAuth.getInstance()
        logien = findViewById(R.id.keloger)
        logien!!.setOnClickListener {
            val registerActivity = Intent(applicationContext, LoginActivity::class.java)
            startActivity(registerActivity)
            finish()

        }
        regBtn!!.setOnClickListener {
            regBtn!!.visibility = View.INVISIBLE
            loadingProgress!!.visibility = View.VISIBLE
            val email = userEmail!!.text.toString()
            val password = userPassword!!.text.toString()
            val password2 = userPAssword2!!.text.toString()
            val name = userName!!.text.toString()

            if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {

                // memperlihatkan error ketika object di atas kosong
                showMessage("Mohon untuk memasukan semua kolom dan Password harus cocok")
                regBtn!!.visibility = View.VISIBLE
                loadingProgress!!.visibility = View.INVISIBLE
            } else {
                // semuanya oke dan semua bidang diisi sekarang kita dapat mulai membuat akun
                // Metode membuat akun akan berjalan

                CreateUserAccount(email, name, password)
                val DaftarActivity = Intent(applicationContext, LoginActivity::class.java)
                startActivity(DaftarActivity)
                finish()
                showMessage("Akun Berhasil di buat")
            }
        }

        ImgUserPhoto = findViewById(R.id.regUserPhoto)

        ImgUserPhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 22) {

                checkAndRequestForPermission()


            } else {
                openGallery()
            }
        }


    }

    private fun CreateUserAccount(email: String, name: String, password: String) {


        // metode untuk mendaftar dengan email dan password

        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // pesan jika akun berhasil di buat
                    showMessage("Akun Berhasil di buat")
                    // setelah kita membuat kita akan mencoba merubah gambar dan nama
                    updateUserInfo(name, pickedImgUri!!, mAuth!!.currentUser)


                } else {

                    // message keluar jika akun gagal di buat
                    showMessage("account creation failed" + task.exception!!.message)
                    regBtn!!.visibility = View.VISIBLE
                    loadingProgress!!.visibility = View.INVISIBLE

                }
            }


    }


    // update user photo and name
    private fun updateUserInfo(name: String, pickedImgUri: Uri, currentUser: FirebaseUser?) {

        // pertama kita perlu mengunggah foto pengguna ke penyimpanan firebase dan mendapatkan url

        val mStorage = FirebaseStorage.getInstance().reference.child("users_photos")
        val imageFilePath = mStorage.child(pickedImgUri.lastPathSegment!!)
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener {
            //
            // now we can get our image url

            imageFilePath.downloadUrl.addOnSuccessListener { uri ->
                // uri contain user image url


                val profleUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(uri)
                    .build()


                currentUser!!.updateProfile(profleUpdate)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // user info updated successfully
                            showMessage("Register Complete")
                            updateUI()
                        }
                    }
            }
        }

    }

    private fun updateUI() {

        val homeActivity = Intent(applicationContext, MainActivity::class.java)
        startActivity(homeActivity)
        finish()


    }

    // simple method to show toast message
    private fun showMessage(message: String) {

        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()

    }

    private fun openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUESCODE)
    }

    private fun checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(
                this@DaftarActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@DaftarActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {

                Toast.makeText(this@DaftarActivity, "Please accept for required permission", Toast.LENGTH_SHORT).show()

            } else {
                ActivityCompat.requestPermissions(
                    this@DaftarActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PReqCode
                )
            }

        } else
            openGallery()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUESCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.data
            ImgUserPhoto.setImageURI(pickedImgUri)


        }


    }

    companion object {
        internal var PReqCode = 1
        internal var REQUESCODE = 1
    }
}