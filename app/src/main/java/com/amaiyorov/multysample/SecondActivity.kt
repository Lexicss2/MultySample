package com.amaiyorov.multysample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton

class SecondActivity : AppCompatActivity() {
    private lateinit var fbLoginButton: LoginButton
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_second)

        callbackManager = CallbackManager.Factory.create()
        fbLoginButton = findViewById(R.id.login_button)
        fbLoginButton.apply {
            setReadPermissions("email")
            registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    Log.i("qaz", "Success")
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}