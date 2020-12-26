package com.deepanshu.whatsappdemo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;

import androidx.biometric.BiometricManager;

import com.deepanshu.whatsappdemo.extraUtil.SharedPreferencesFactory;
import com.deepanshu.whatsappdemo.extraUtil.StaticUtil;
import com.deepanshu.whatsappdemo.interfaces.BiometricPromptCallBack;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class BiometricAuthentication {
    private Context mContext;
    private SharedPreferencesFactory sharedPreferencesFactory;
    private int authFailedCount = 0;
    private BiometricPromptCallBack callBack = null;
    private Boolean isDashBoardOpened = false;
    Executor executor = Executors.newSingleThreadExecutor();
    private CancellationSignal cancellationSignal;
    private String TAG = "BioMetricAuth";

    public BiometricAuthentication(Context context, Boolean isDashBoardOpened) {
        mContext = context;
        sharedPreferencesFactory = SharedPreferencesFactory.getInstance(mContext);
        sharedPreferencesFactory.getSharedPreferences(MODE_PRIVATE);
        this.isDashBoardOpened  = isDashBoardOpened;
    }

    public boolean checkBiometricSupportFromPie() {
        KeyguardManager keyguardManager =
                (KeyguardManager) mContext.getSystemService(KEYGUARD_SERVICE);
        if (!keyguardManager.isKeyguardSecure()) {
            if (!isDashBoardOpened){
                if (callBack != null) {
                    callBack.showAddFingerPrintToDevicePrompt();
                }
            }
            return false;
        }

        BiometricManager biometricManager = BiometricManager.from(mContext);
        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_SUCCESS:
                return true;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                updateUi(false, "The Biometric Sensor currently Unavailable");
                return false;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                updateUi(false, "Your Device Doesn't have Finger Print Scanner");
                return false;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                if(!isDashBoardOpened)
                    if(callBack!=null){
                        callBack.showAddFingerPrintToDevicePrompt();
                    }
                return false;

        }
        return true;

    }

    public void authenticateUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(mContext)
                    .setTitle("Device Authentication for \"My Whatsapp\"")
                    .setDescription("Use FingerPrint or Face Id registered to Sign in")
                    .setNegativeButton("Cancel", executor,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    updateUi(false,"Device Authentication Failed");
                                }
                            })
                    .build();
            biometricPrompt.authenticate(getCancellationSignal(), executor, getAuthenticationCallback());
        }
    }
    private CancellationSignal getCancellationSignal() {

        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                Log.i(TAG, "Cancelled via signal");
            }
        });
        return cancellationSignal;
    }
    @TargetApi(Build.VERSION_CODES.P)
    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {

        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              CharSequence errString) {
                Log.i(TAG, "Authentication error: " + errString);
                updateUi(false, "Authentication error: " + errString);
            }

            @Override
            public void onAuthenticationFailed() {
//                updateUi(false, "Authentication failed");
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                Log.i(TAG, "Authentication help: " + helpString);
                updateUi(false, "Authentication help: " + helpString);
            }

            @Override
            public void onAuthenticationSucceeded(
                    BiometricPrompt.AuthenticationResult result) {
                Log.i(TAG, "Authentication Succeeded: " + result);
                updateUi(true, "Authentication Succeeded: " + result);
            }
        };
    }


    public void setBioMetricCallBack(BiometricPromptCallBack callBack) {
        this.callBack = callBack;

    }

    private void updateUi(final boolean isAuthenticateSuccess, final String authenticateStatus){
        Activity activity = (Activity)mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(isAuthenticateSuccess){
                    if(isDashBoardOpened) {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        mContext.startActivity(intent);
                    }

                    else if(callBack!=null){
                        callBack.biometricPromptCallBack(true,authenticateStatus);
                    }
                }
                else{
                    StaticUtil.showCustomToast(mContext,authenticateStatus);
                    if(callBack!=null) {
                        callBack.biometricPromptCallBack(false,authenticateStatus);
                    }
                }
            }
        });
    }

}
