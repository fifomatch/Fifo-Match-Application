package com.fifo.match.utils.dialogbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.fifo.match.R;


public class SRProgressDialog extends AlertDialog {


    public SRProgressDialog(Activity context) {
        super(context);
        init(context);
    }

    public static SRProgressDialog show(Activity context, boolean cancelable) {
        SRProgressDialog dialog = new SRProgressDialog(context);
        dialog.setCanceledOnTouchOutside(cancelable);
        dialog.setCancelable(cancelable);
        dialog.show();
        return  dialog;
    }

    private void init(Activity context) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loader);
        Window mWindow = getWindow();
        View mView = mWindow.getDecorView();
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mWindow.setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        mView.setBackgroundResource(android.R.color.transparent);

    }







}