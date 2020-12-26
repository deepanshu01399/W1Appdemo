package com.deepanshu.whatsappdemo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class CommonSpinnerAdapter extends ArrayAdapter {

    public CommonSpinnerAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public class ICommonSpinnerAdapter extends View {

        public ICommonSpinnerAdapter(Context context) {
            super(context);

        }
    }
}
