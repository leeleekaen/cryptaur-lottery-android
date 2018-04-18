package com.cryptaur.lottery.login;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.util.FixCloseDialogFragment;
import com.cryptaur.lottery.view.CheckableImageButton;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnterPinCodeDialogFragment extends FixCloseDialogFragment implements View.OnClickListener {

    private static final String ARG_MESSAGE_ID = "messageId";
    private static final String ARG_SHOW_USE_LOGIN = "showUseLoginButton";
    int digits[] = new int[]{-1, -1, -1, -1};
    private int messageId;
    private boolean showUseLogin;
    private ViewGroup mRoot;
    private TextView messageView;
    private CheckableImageButton[] digitImageButtons = new CheckableImageButton[4];
    private View progress;

    public EnterPinCodeDialogFragment() {
    }

    public static EnterPinCodeDialogFragment showDialog(FragmentManager fm, int mesageId, boolean showLogin) {
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE_ID, mesageId);
        args.putBoolean(ARG_SHOW_USE_LOGIN, showLogin);
        return (EnterPinCodeDialogFragment) showDialog(fm, args, EnterPinCodeDialogFragment.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            cancel();
        } else {
            messageId = args.getInt(ARG_MESSAGE_ID);
            showUseLogin = args.getBoolean(ARG_SHOW_USE_LOGIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_enter_pin_code_dialog, container, false);
        messageView = mRoot.findViewById(R.id.enterAccountLabel);
        progress = mRoot.findViewById(R.id.progressLayout);
        digitImageButtons[0] = mRoot.findViewById(R.id.pinDigit1);
        digitImageButtons[1] = mRoot.findViewById(R.id.pinDigit2);
        digitImageButtons[2] = mRoot.findViewById(R.id.pinDigit3);
        digitImageButtons[3] = mRoot.findViewById(R.id.pinDigit4);
        digitImageButtons[0].setClickable(false);
        digitImageButtons[1].setClickable(false);
        digitImageButtons[2].setClickable(false);
        digitImageButtons[3].setClickable(false);

        mRoot.findViewById(R.id.button1).setOnClickListener(this);
        mRoot.findViewById(R.id.button2).setOnClickListener(this);
        mRoot.findViewById(R.id.button3).setOnClickListener(this);
        mRoot.findViewById(R.id.button4).setOnClickListener(this);
        mRoot.findViewById(R.id.button5).setOnClickListener(this);
        mRoot.findViewById(R.id.button6).setOnClickListener(this);
        mRoot.findViewById(R.id.button7).setOnClickListener(this);
        mRoot.findViewById(R.id.button8).setOnClickListener(this);
        mRoot.findViewById(R.id.button9).setOnClickListener(this);
        mRoot.findViewById(R.id.button0).setOnClickListener(this);
        mRoot.findViewById(R.id.buttonExit).setOnClickListener(this);
        mRoot.findViewById(R.id.closeButton).setOnClickListener(this);
        mRoot.findViewById(R.id.buttonDelete).setOnClickListener(this);
        mRoot.findViewById(R.id.useLoginAndPasswordButton).setOnClickListener(this);
        mRoot.findViewById(R.id.useLoginAndPasswordButton).setVisibility(showUseLogin ? View.VISIBLE : View.GONE);

        messageView.setText(messageId);

        return mRoot;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button0:
                addDigit(0);
                break;
            case R.id.button1:
                addDigit(1);
                break;
            case R.id.button2:
                addDigit(2);
                break;
            case R.id.button3:
                addDigit(3);
                break;
            case R.id.button4:
                addDigit(4);
                break;
            case R.id.button5:
                addDigit(5);
                break;
            case R.id.button6:
                addDigit(0);
                break;
            case R.id.button7:
                addDigit(0);
                break;
            case R.id.button8:
                addDigit(0);
                break;
            case R.id.button9:
                addDigit(0);
                break;

            case R.id.buttonDelete:
                clearDigit();
                break;

            case R.id.closeButton:
            case R.id.buttonExit:
                cancel();
                break;

            case R.id.useLoginAndPasswordButton:
                mListener.doAction(InteractionListener.Action.UseLoginAndPassword, this);
                break;
        }
    }

    private void addDigit(int digit) {
        for (int i = 0; i < digits.length; i++) {
            CheckableImageButton button = digitImageButtons[i];
            if (digits[i] == -1) {
                digits[i] = digit;
                button.setChecked(true);
                button.setSelected(true);
                if (i == 3) {
                    onDonePinInput();
                }
                return;
            } else {
                button.setSelected(false);
            }
        }
    }

    private void clearDigit() {
        for (int i = digits.length - 1; i >= 0; i--) {
            if (digits[i] != -1) {
                digits[i] = -1;
                CheckableImageButton button = digitImageButtons[i];
                button.setChecked(false);
                button.setSelected(false);
                if (i > 0) {
                    button = digitImageButtons[i - 1];
                    button.setSelected(true);
                }
                return;
            }
        }
    }

    public void showProgress(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void onDonePinInput() {
        mListener.doAction(new OnDonePinUnput(digits), this);
    }

    public static class OnDonePinUnput implements InteractionListener.IAction {
        public final int[] digits;

        public OnDonePinUnput(int[] digits) {
            this.digits = digits;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OnDonePinUnput that = (OnDonePinUnput) o;
            return Arrays.equals(digits, that.digits);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(digits);
        }

        public CharSequence toCharSequence() {
            StringBuilder builder = new StringBuilder();
            for (int digit : digits) {
                builder.append(digit);
            }
            return builder;
        }
    }
}
