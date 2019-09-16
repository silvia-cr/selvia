package com.tfm.digitalevidencemanager.activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.os.CancellationSignal
import android.support.v4.app.ActivityCompat
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.tfm.digitalevidencemanager.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        accessByBiometric()
    }

    fun onClick(view: View) {
        when (view.id) {
            button_try_again.id ->
                showBiometricalPrompt()
        }
    }

    private fun accessByBiometric() {
        val fingerprintManager = FingerprintManagerCompat.from(this)

        if (!fingerprintManager.isHardwareDetected) {
            // the device has not fingerprint sensor
            text_reason_not_access.text = getString(R.string.login_device_no_sensor)

        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            // the device has not saved fingerprints
            text_reason_not_access.text = getString(R.string.login_device_no_enrolled_fingerprints)

        } else if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // our app has not the permissions to use the fingerprint
            text_reason_not_access.text = getString(R.string.login_app_no_permissions)

        } else {
            // our app can use the sensor
            button_try_again.visibility = View.VISIBLE
            showBiometricalPrompt()
        }
    }

    private fun showBiometricalPrompt() {
        val biometricPromptBuilder = BiometricPrompt.Builder(this)
        biometricPromptBuilder.setTitle(getString(R.string.biometric_prompt_title))
        biometricPromptBuilder.setSubtitle(getString(R.string.biometric_prompt_subtitle))
        biometricPromptBuilder.setDescription(getString(R.string.biometric_prompt_description))
        biometricPromptBuilder.setNegativeButton(
            getString(R.string.biometric_prompt_negative_button),
            this.mainExecutor,
            DialogInterface.OnClickListener({ _, _ -> })
        )
        val biometricPrompt = biometricPromptBuilder.build()
        val authenticationCallback = this.getAuthenticationCallback()

        biometricPrompt.authenticate(CancellationSignal(), this.mainExecutor, authenticationCallback)
    }

    private fun getAuthenticationCallback(): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val menuIntent = Intent(this@LoginActivity, MainMenuActivity::class.java)
                startActivity(menuIntent)
            }
        }
    }
}

