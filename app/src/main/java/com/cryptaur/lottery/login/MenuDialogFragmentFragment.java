package com.cryptaur.lottery.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.util.FixCloseDialogFragment;

public class MenuDialogFragmentFragment extends FixCloseDialogFragment {

    private InteractionListener mListener;

    public MenuDialogFragmentFragment() {
    }

    public static MenuDialogFragmentFragment showDialog(FragmentManager fm) {
        return (MenuDialogFragmentFragment) showDialog(fm, MenuDialogFragmentFragment.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        return inflater.inflate(R.layout.fragment_menu_dialog, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
