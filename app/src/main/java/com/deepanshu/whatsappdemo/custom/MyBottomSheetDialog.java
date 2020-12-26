package com.deepanshu.whatsappdemo.custom;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.deepanshu.whatsappdemo.R;
import com.deepanshu.whatsappdemo.fragment.DeviceAuthBottomSheetFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MyBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    public static final String TAG = "MyBottomSheetDialog";
    private MyBottomSheetDialog.ICallBack mListener;
    private Fragment fragment;
    private boolean showToolbar = false;
    private String title = "";



    public static MyBottomSheetDialog newInstance() {
        return new MyBottomSheetDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        if (showToolbar) {
            view.findViewById(R.id.toolbarLay).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.txtTitle)).setText(title);
            view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                    .add(R.id.content, fragment)
                    .commit();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void setCallback(int layout, ICallBack iCallBack) {
        /* this.mLayout = layout;*/
        this.mListener = iCallBack;


    }

    public void setFragment(DeviceAuthBottomSheetFragment frag) {
        this.fragment = frag;
    }

    public void replaceFragment(Fragment frag) {
        if (frag != null) {
            this.fragment = frag;
            getChildFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                    .replace(R.id.content, fragment)
                    .commit();
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onClick(View view) {
        /*TextView tvSelected = (TextView) view;
        mListener.onItemClick(tvSelected.getText().toString());*/
        dismiss();
    }

    public void showToolbar(String title) {
        showToolbar = true;
        this.title = title;
    }



    public interface ICallBack {
        void init(View view, int containerId);


    }
}
