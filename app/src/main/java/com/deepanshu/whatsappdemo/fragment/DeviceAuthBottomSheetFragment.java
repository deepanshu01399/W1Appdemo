package com.deepanshu.whatsappdemo.fragment;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.deepanshu.whatsappdemo.R;
import com.deepanshu.whatsappdemo.activity.FingerPrintAuthentication;
import com.deepanshu.whatsappdemo.activity.Login_Activity;
import com.deepanshu.whatsappdemo.activity.MainActivity;
import com.deepanshu.whatsappdemo.activity.SettingActivity;
import com.deepanshu.whatsappdemo.interfaces.BiometricPromptCallBack;

import javax.crypto.Cipher;

import static java.security.AccessController.getContext;

public class DeviceAuthBottomSheetFragment extends Fragment implements View.OnClickListener, BiometricPromptCallBack {
    private IDeviceAuthBottomSheetCallback callback;
    private ImageView fingerPrintImgButton,backButton;
    private Context context;
    private TextView errorMsg;
    private Button cancelButton;
    private Boolean isAuthFailed = false;

    public DeviceAuthBottomSheetFragment(Context context){
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_device_auth_bottom_sheet, container, false);
        initViews(view);
        initListener(view);
        return view;
    }

    private void initListener(View view) {
        cancelButton.setOnClickListener(this);
        fingerPrintImgButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    private void initViews(View view) {
        fingerPrintImgButton = view.findViewById(R.id.fingerPrintImgButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        errorMsg = view.findViewById(R.id.errorMsg);
        backButton = view.findViewById(R.id.backButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancelButton:
            case R.id.backButton:
                if(callback!=null){
                    callback.dismissBottomSheet();
                    if(context instanceof SettingActivity) {
                        if(!isAuthFailed)
                            ((SettingActivity) getActivity()).biometricPromptCallBack(false, "");
                    }
                    else if(context instanceof Login_Activity){

                    }
                    else  {
                        MainActivity mainActivity = new MainActivity();
                        mainActivity.biometricPromptCallBack(false, "");
                    }

                }
                break;
            case R.id.fingerPrintImgButton:
                FingerPrintAuthentication fingerPrintAuthentication;
                if(context instanceof Login_Activity) {
                    fingerPrintAuthentication = new FingerPrintAuthentication(getContext(), true);
                }
                else{
                    fingerPrintAuthentication = new FingerPrintAuthentication(getContext(), false);
                }
                fingerPrintAuthentication.setBioMetricCallBack(this);
                if (fingerPrintAuthentication.checkBiometricSupportAfterMarshmallow()) {
                    fingerPrintAuthentication.generateKey();
                    if(fingerPrintAuthentication.cipherInit()) {
                        Cipher cipher = fingerPrintAuthentication.getCipherValue();
                        if(cipher!= null){
                            FingerprintManager fingerprintManager = (FingerprintManager) getContext().getSystemService(Context.FINGERPRINT_SERVICE);
                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            fingerPrintAuthentication.startAuth(fingerprintManager, cryptoObject);
                        }
                    }
                }
                break;
        }
    }

    public void setCallback(IDeviceAuthBottomSheetCallback callback){
        this.callback = callback;
    }

    @Override
    public void biometricPromptCallBack(Boolean isFingerPrintMatch,String message) {
        errorMsg.setText(message);
        isAuthFailed = true;
        if(message.equals("Success")){
            callback.dismissBottomSheet();
        }
        if(context instanceof SettingActivity)
            ((SettingActivity)getActivity()).biometricPromptCallBack(isFingerPrintMatch,message);
//        else if(context instanceof  LoginActivity){
//
//        }
        else {
            MainActivity mainActivity = new MainActivity();
            mainActivity.biometricPromptCallBack(isFingerPrintMatch, message);
        }
    }
    @Override
    public void showAddFingerPrintToDevicePrompt() {
        if(context instanceof SettingActivity)
            ((SettingActivity)getActivity()).showAddFingerPrintToDevicePrompt();
    }

    public interface IDeviceAuthBottomSheetCallback {
        public void dismissBottomSheet();
    }
}