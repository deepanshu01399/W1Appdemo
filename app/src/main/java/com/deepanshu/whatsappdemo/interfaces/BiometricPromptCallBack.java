package com.deepanshu.whatsappdemo.interfaces;

public interface BiometricPromptCallBack {
    void biometricPromptCallBack(Boolean isFingerPrintMatch,String message);
    void showAddFingerPrintToDevicePrompt();
}
