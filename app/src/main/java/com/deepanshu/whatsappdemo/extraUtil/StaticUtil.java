package com.deepanshu.whatsappdemo.extraUtil;

import android.content.Context;
import androidx.biometric.BiometricManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.deepanshu.whatsappdemo.R;

import static android.content.Context.FINGERPRINT_SERVICE;

public class  StaticUtil {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Boolean hasFingerPrintAdded(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BiometricManager biometricManager = BiometricManager.from(context);
            switch (biometricManager.canAuthenticate()) {
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    return false;
            }
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                return false;
            }
        }
        return true;
    }

    public static void showCustomToast(Context ctx, String message) {
        Toast toast = new Toast(ctx);
        toast.setGravity(Gravity.BOTTOM, 0, 180);
        toast.setDuration(Toast.LENGTH_LONG);

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast_layout, null);
        TextView txtToastMessage = (TextView) view.findViewById(R.id.toast_message);
        if (txtToastMessage != null && !TextUtils.isEmpty(message)) {
            txtToastMessage.setText(message);
            toast.setView(view);
            toast.show();
        }
    }


}
