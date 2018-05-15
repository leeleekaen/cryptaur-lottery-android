package com.cryptaur.lottery.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.transport.SessionTransport;
import com.cryptaur.lottery.view.WalletViewHolder;

import java.util.Locale;

public class MenuDialogFragment extends FixCloseDialogFragment implements View.OnClickListener {

    private ViewGroup mRoot;
    private WalletViewHolder wallet;
    private AppCompatImageView avatarView;
    private TextView addressView;
    private Button logoutButton;
    private Button myTicketsButton;
    private Button changePinCodeButton;
    private Button howToPlayButton;
    private View closeButton;


    public MenuDialogFragment() {
    }

    public static MenuDialogFragment showDialog(FragmentManager fm) {
        return (MenuDialogFragment) showDialog(fm, MenuDialogFragment.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRoot = (ViewGroup) inflater.inflate(R.layout.dialog_menu_dialog, container, false);
        wallet = new WalletViewHolder(mRoot.findViewById(R.id.wallet), mListener);
        avatarView = mRoot.findViewById(R.id.avatar);
        addressView = mRoot.findViewById(R.id.address);
        logoutButton = mRoot.findViewById(R.id.logoutButton);
        myTicketsButton = mRoot.findViewById(R.id.myTicketButton);
        changePinCodeButton = mRoot.findViewById(R.id.changePinCodeButton);
        howToPlayButton = mRoot.findViewById(R.id.howToPlayButton);
        closeButton = mRoot.findViewById(R.id.closeButton);

        String address = SessionTransport.INSTANCE.getAddress();
        avatarView.setImageResource(address == null ? R.drawable.ic_avatar : R.drawable.ic_avatar_filled);
        addressView.setText(shortAddress(address));

        myTicketsButton.setEnabled(address != null);
        changePinCodeButton.setEnabled(address != null);

        if (address == null) {
            logoutButton.setText(address == null ? R.string.login : R.string.logout);
        } else {
            Drawable drw = VectorDrawableCompat.create(mRoot.getResources(), R.drawable.ic_content_copy, null);
            drw.setBounds(0, 0, drw.getIntrinsicWidth(), drw.getIntrinsicHeight());
            addressView.setCompoundDrawables(null, null, drw, null);
            addressView.setOnClickListener(this);
        }

        logoutButton.setOnClickListener(this);
        myTicketsButton.setOnClickListener(this);
        changePinCodeButton.setOnClickListener(this);
        howToPlayButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);

        return mRoot;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logoutButton:
                String address = SessionTransport.INSTANCE.getAddress();
                if (address == null)
                    mListener.doAction(InteractionListener.Action.Login, this);
                else
                    mListener.doAction(InteractionListener.Action.Logout, this);
                dismiss();
                break;

            case R.id.changePinCodeButton:
                mListener.doAction(InteractionListener.Action.UseLoginAndPassword, this);
                break;

            case R.id.myTicketButton:
                mListener.doAction(InteractionListener.Action.MyTickets, this);
                dismiss();
                break;

            case R.id.howToPlayButton:
                mListener.doAction(InteractionListener.Action.HowToPlay, this);
                dismiss();
                break;

            case R.id.closeButton:
                dismiss();
                break;

            case R.id.address:
                address = SessionTransport.INSTANCE.getAddress();
                if (address != null) {
                    Context context = v.getContext();
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(address, address);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, R.string.addressCopied, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private String shortAddress(String address) {
        if (address == null)
            return "";
        if (address.length() < 18)
            return address;
        return String.format(Locale.US, "%s...%s", address.substring(0, 8), address.substring(address.length() - 8));
    }
}
