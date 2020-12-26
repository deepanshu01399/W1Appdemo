package com.deepanshu.whatsappdemo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import androidx.core.app.ActivityCompat;


import com.deepanshu.whatsappdemo.extraUtil.SharedPreferencesFactory;
import com.deepanshu.whatsappdemo.extraUtil.StaticUtil;
import com.deepanshu.whatsappdemo.interfaces.BiometricPromptCallBack;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

@androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
public class FingerPrintAuthentication extends FingerprintManager.AuthenticationCallback {

    private Context mContext;
    private String KEY_STORE_ALIAS = "Android Store";
    private KeyStore keyStore;
    private Cipher cipher;
    private SharedPreferences sharedpreferences;
    private int authFailedCount = 0;
    private static final String TAG = "FingerPrintAuthentcaton";
    private SharedPreferencesFactory sharedPreferencesFactory;
    private BiometricPromptCallBack callBack = null;
    private Boolean isDashBoardOpened = false;
    private CancellationSignal cancellationSignal;
    private Boolean authFailedFirstTime = false;

    public FingerPrintAuthentication(Context context, Boolean isDashBoardOpened){
        mContext = context;
        sharedPreferencesFactory = SharedPreferencesFactory.getInstance(mContext);
        sharedPreferencesFactory.getSharedPreferences(MODE_PRIVATE);
        this.isDashBoardOpened = isDashBoardOpened;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public Boolean checkBiometricSupportAfterMarshmallow() {
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
        FingerprintManager fingerprintManager = (FingerprintManager) mContext.getSystemService(FINGERPRINT_SERVICE);
        if(fingerprintManager == null){
            StaticUtil.showCustomToast(mContext, "fingerprintManager is null");
            return false;
        }

        if (!fingerprintManager.isHardwareDetected()) {
            StaticUtil.showCustomToast(mContext, "Your device doesn't support fingerprint authentication");
            return false;
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            StaticUtil.showCustomToast(mContext, "Please enable the fingerprint permission");
            return false;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {
            if(!isDashBoardOpened)
                if(callBack!=null){
                    callBack.showAddFingerPrintToDevicePrompt();
                }
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_STORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        }catch (KeyStoreException|IOException|CertificateException|NoSuchAlgorithmException
        |NoSuchProviderException|InvalidAlgorithmParameterException e){

        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit(){
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            final SecretKey key;
            final KeyStore keyStore =  KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            key = (SecretKey) keyStore.getKey(KEY_STORE_ALIAS, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public Cipher getCipherValue(){
        return cipher;
    }


    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }
    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        cancellationSignal.cancel();
        updateUI(false, errString.toString());
    }



    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        if(!authFailedFirstTime) {
            updateUI(false, "Fingerprint doesn't match.");
        }
        authFailedFirstTime = true;
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        updateUI(false,helpString.toString());
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        updateUI(true,"Success");
    }

    public void updateUI(final boolean isAuthenticateSuccess, final String msg){
        Activity activity = (Activity)mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(isAuthenticateSuccess){
                    if(isDashBoardOpened) {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        mContext.startActivity(intent);
                    }

                    if(callBack!=null){
                        callBack.biometricPromptCallBack(true,msg);
                    }
                }
                else{
                    if(callBack!=null) {
                        callBack.biometricPromptCallBack(false,msg);
                    }
                }
            }
        });
    }
    public void setBioMetricCallBack(BiometricPromptCallBack callBack){
        this.callBack = callBack;
    }

}
