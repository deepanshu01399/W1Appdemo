package com.deepanshu.whatsappdemo.extraUtil;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deepanshu.whatsappdemo.R;
import com.google.android.material.snackbar.Snackbar;

public class ColoredSnackbar {
    private static final int red = 0xfff3776e;
    private static final int green = 0xff4caf50;
    private static final int blue = 0xff2195f3;
    private static final int orange = 0xffff600a;
    //    private static final int black = 0xff383535;
    private static final int black = 0xff6E6B6B;
    private static View getSnackBarLayout(Snackbar snackbar) {
        if (snackbar != null) {
            return snackbar.getView();
        }
        return null;
    }
    private static Snackbar colorSnackBar(Snackbar snackbar, int colorId) {
        View snackBarView = getSnackBarLayout(snackbar);
        if (snackBarView != null) {
            snackBarView.setBackgroundColor(colorId);
            snackBarView.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
            TextView tv = null;
            View v = snackBarView.findViewById(R.id.snackbar_text);
            if (v instanceof TextView)
                tv = (TextView) v;
            if (tv != null) {
                tv.setMaxLines(3);
            }
        }

        return snackbar;
    }

    public static Snackbar info(Snackbar snackbar) {
        return colorSnackBar(snackbar, blue);
    }

    public static Snackbar warning(Snackbar snackbar) {
        return colorSnackBar(snackbar, orange);
    }

    public static Snackbar alert(Snackbar snackbar) {
        return colorSnackBar(snackbar, red);
    }

    public static Snackbar confirm(Snackbar snackbar) {
        return colorSnackBar(snackbar, green);
    }

    public static Snackbar dark(Snackbar snackbar) {
        return colorSnackBar(snackbar, black);
    }
}
