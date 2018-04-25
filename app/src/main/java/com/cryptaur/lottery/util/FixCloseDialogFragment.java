package com.cryptaur.lottery.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 29.11.13
 * Time: 9:10
 */
public abstract class FixCloseDialogFragment extends DialogFragment implements DialogInterface {
    private static final String FRAGMENT_TAG = "dialog";
    protected Handler handler = new Handler();
    protected DialogInterface.OnDismissListener onDismissListener;
    protected InteractionListener mListener;
    private boolean closeMe;

    protected static <T extends FixCloseDialogFragment> FixCloseDialogFragment showDialog(FragmentManager fm, Class<T> clazz) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(FRAGMENT_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);

        try {
            T newFragment = clazz.getConstructor().newInstance();
            newFragment.setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog);
            //newFragment.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            newFragment.show(ft, FRAGMENT_TAG);
            return newFragment;
        } catch (Exception e) {
        }
        return null;
    }

    protected static <T extends FixCloseDialogFragment> FixCloseDialogFragment showDialog(FragmentManager fm, Bundle arguments, Class<T> clazz) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(FRAGMENT_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);

        try {
            T newFragment = clazz.getConstructor().newInstance();
            newFragment.setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog);
            newFragment.setArguments(arguments);
            newFragment.show(ft, FRAGMENT_TAG);
            return newFragment;
        } catch (Exception e) {
        }
        return null;
    }

    public static Fragment findDialogFragment(FragmentManager fm) {
        return fm.findFragmentByTag(FRAGMENT_TAG);
    }

    public static void closeDialogFragment(FragmentManager fm) {
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(prev);
            ft.commit();
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }

    @Override
    public void cancel() {
        dismiss();
        mListener.doAction(InteractionListener.Action.FinishWorkflow, this);
    }

    protected void checkCloseMe() {
        if (closeMe) {
            closeMe = false;
            handler.post(this::dismiss);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkCloseMe();
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            if (onDismissListener != null)
                onDismissListener.onDismiss(this);
        } catch (Exception e) {
            closeMe = true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
