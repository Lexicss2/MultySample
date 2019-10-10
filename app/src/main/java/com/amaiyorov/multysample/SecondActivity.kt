package com.amaiyorov.multysample

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.facebook.share.ShareApi
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.ShareMediaContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareButton
import com.facebook.share.widget.ShareDialog

class SecondActivity : AppCompatActivity() {
    private lateinit var fbLoginButton: LoginButton
    private lateinit var shareButton: ShareButton
    private lateinit var firstButton: Button
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_second)

        callbackManager = CallbackManager.Factory.create()
        fbLoginButton = findViewById(R.id.login_button)
        fbLoginButton.apply {
            setReadPermissions("email")
            registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    Log.i("qaz", "Success")
                    sharePhotoToFacebook()
                }

                override fun onCancel() {
                    Log.d("qaz", "Cancel")
                }

                override fun onError(error: FacebookException?) {
                    Log.e("qaz", "error: ${error.toString()}")
                }
            })
        }

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            Log.i("qaz", "Logged In")
        } else {
            Log.d("qaz", "not logged in")
        }


        val linkContent = ShareLinkContent.Builder()
            .setContentUrl(Uri.parse("tut.by"))
            .build()

//
//        val shareContent = ShareMediaContent.Builder()
//            .addMedium(sharedPhoto)
//            .build()
//
//        val shareDialog = ShareDialog(this)
//        shareDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC)

        shareButton = findViewById(R.id.share_button)
        shareButton.shareContent = linkContent

        firstButton = findViewById(R.id.first_button)
        firstButton.setOnClickListener {
            sharePhotoToFacebook()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("qaz", "onActivityResult")
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sharePhotoToFacebook() {
        val image = BitmapFactory.decodeResource(resources, R.drawable.aaa)
        val sharedPhoto = SharePhoto.Builder()
            .setBitmap(image)
            .setCaption("AaaaaaaaaaBbbb")
            .build()
        val photoContent = SharePhotoContent.Builder()
            .addPhoto(sharedPhoto)
            .build()

        Log.d("qaz", "sharePhoto to Facebook called")
        ShareApi.share(photoContent, null)
    }
}