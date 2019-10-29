package com.amaiyorov.multysample

import android.content.Intent
import android.graphics.BitmapFactory
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
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareButton
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton

// Facebook Tutorial
// https://developers.facebook.com/docs/sharing/android/
// https://developers.facebook.com/docs/sharing/android/

// Twitter Tutorial : https://github.com/twitter-archive/twitter-kit-android/wiki/Log-In-with-Twitter
class SecondActivity : AppCompatActivity() {
    private lateinit var fbLoginButton: LoginButton
    private lateinit var shareButton: ShareButton
    private lateinit var firstButton: Button
    private lateinit var facebookCallbackManager: CallbackManager

    private lateinit var twitterLoginButton: TwitterLoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_second)

        facebookCallbackManager = CallbackManager.Factory.create()
        fbLoginButton = findViewById(R.id.facebook_login_button)
        fbLoginButton.apply {
            setReadPermissions("email")
            registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {
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

        twitterLoginButton = findViewById(R.id.twitter_login_button)
        twitterLoginButton.callback = object : com.twitter.sdk.android.core.Callback<TwitterSession>() {

            override fun success(result: Result<TwitterSession>?) {
                Log.d("qaz", "Twitter success, result: ${result?.data}")
            }

            override fun failure(exception: TwitterException) {
                Log.d("qaz", "Twitter failure: ${exception.localizedMessage}")
            }
        }

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            Log.i("qaz", "Logged In facebook")
        } else {
            Log.d("qaz", "not logged in facebook")
        }


        val linkContent = ShareLinkContent.Builder()
            .setContentUrl(Uri.parse("tut.by"))
            .build()

        val image = BitmapFactory.decodeResource(resources, R.drawable.aaa)
        val photo = SharePhoto.Builder()
            .setBitmap(image)
            .build()
        val photoContent = SharePhotoContent.Builder()
            .addPhoto(photo)
            .build()

//
//        val mediaContent = ShareMediaContent.Builder()
//            .addMedium(sharedPhoto)
//            .build()

//
//        val shareDialog = ShareDialog(this)
//        shareDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC)

        shareButton = findViewById(R.id.share_button)
        //shareButton.shareContent = linkContent
        shareButton.shareContent = photoContent

        firstButton = findViewById(R.id.first_button)
        firstButton.setOnClickListener {
            Log.d("qaz", "tryShare clicked")
            sharePhotoToFacebook()
        }


        val twitterSession = TwitterCore.getInstance().sessionManager.activeSession
        if (twitterSession != null) {
            val authToken = twitterSession.authToken
            if (authToken != null) {
                Log.d("qaz", "token: ${authToken.token}, secret: ${authToken.secret}, expired: ${authToken.isExpired}")
            } else {
                Log.w("qaz", "auth token is null")
            }
        } else {
            Log.w("qaz", "TwitterSession is null")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("qaz", "onActivityResult, requestCode: $requestCode")
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        twitterLoginButton.onActivityResult(requestCode, resultCode, data)
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