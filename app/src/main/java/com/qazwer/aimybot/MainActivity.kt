package com.qazwer.aimybot

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.justai.aimybox.Aimybox
import com.justai.aimybox.api.aimybox.AimyboxDialogApi
import com.justai.aimybox.components.AimyboxAssistantFragment
import com.justai.aimybox.components.AimyboxProvider
import com.justai.aimybox.core.Config
import com.justai.aimybox.dialogapi.jaicf.JAICFDialogApi
import com.justai.aimybox.speechkit.google.platform.GooglePlatformSpeechToText
import com.justai.aimybox.speechkit.google.platform.GooglePlatformTextToSpeech
import com.qazwer.aimybot.scenario.MainScenario
import java.util.*

class MainActivity : AppCompatActivity(), AimyboxProvider {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.assistant_container, AimyboxAssistantFragment())
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        val assistantFragment = (supportFragmentManager.findFragmentById(R.id.assistant_container)
                as? AimyboxAssistantFragment)
        if (assistantFragment?.onBackPressed() != false) {
            return
        }
        super.onBackPressed()
    }

    override val aimybox: Aimybox by lazy { createAimybox(applicationContext) }

    private fun createAimybox(context: Context): Aimybox {

        val unitId = UUID.randomUUID().toString()

        val textToSpeech = GooglePlatformTextToSpeech(context, Locale("Ru"))
        val speechToText = GooglePlatformSpeechToText(context, Locale("Ru"))
        val dialogApi = JAICFDialogApi(unitId, MainScenario.model)

        return Aimybox(Config.create(speechToText, textToSpeech, dialogApi))
    }
}