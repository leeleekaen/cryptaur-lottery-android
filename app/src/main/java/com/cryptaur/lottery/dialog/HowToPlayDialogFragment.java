package com.cryptaur.lottery.dialog;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptaur.lottery.R;
import com.cryptaur.lottery.util.FixCloseDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HowToPlayDialogFragment extends FixCloseDialogFragment {

    public HowToPlayDialogFragment() {
    }

    public static HowToPlayDialogFragment showDialog(FragmentManager fm) {
        return (HowToPlayDialogFragment) showDialog(fm, HowToPlayDialogFragment.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.dialog_how_to_play, container, false);

        root.findViewById(R.id.closeButton).setOnClickListener(v -> dismiss());
        return root;
    }

}
