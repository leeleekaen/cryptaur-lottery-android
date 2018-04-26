package com.cryptaur.lottery.dialog;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.util.FixCloseDialogFragment;

import java.io.Serializable;

public class LoginDialogFragment extends FixCloseDialogFragment implements View.OnClickListener {

    private static final String ARG_LOGIN = "login";

    private ViewGroup mRoot;

    private TextInputLayout loginLayout;
    private TextInputLayout passwordLayout;

    private Button loginButton;
    private Button forgotPasswordButton;

    public LoginDialogFragment() {
    }

    public static LoginDialogFragment showDialog(FragmentManager fm) {
        return (LoginDialogFragment) showDialog(fm, LoginDialogFragment.class);
    }

    public static LoginDialogFragment showDialog(FragmentManager fm, LoginAction login) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_LOGIN, login);
        return (LoginDialogFragment) showDialog(fm, bundle, LoginDialogFragment.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = (ViewGroup) inflater.inflate(R.layout.dialog_login, container, false);
        loginLayout = mRoot.findViewById(R.id.loginInputLayout);
        passwordLayout = mRoot.findViewById(R.id.passwordInputLayout);
        loginButton = mRoot.findViewById(R.id.loginButton);
        forgotPasswordButton = mRoot.findViewById(R.id.forgotPasswordButton);
        mRoot.findViewById(R.id.closeButton).setOnClickListener(this);

        loginButton.setOnClickListener(this);
        forgotPasswordButton.setOnClickListener(this);

        Bundle args = getArguments();
        if (args != null) {
            LoginAction login = (LoginAction) args.getSerializable(ARG_LOGIN);
            loginLayout.getEditText().setText(login.login);
            passwordLayout.getEditText().setText(login.password);
        }

        return mRoot;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                doLogin();
                break;

            case R.id.forgotPasswordButton:
                mListener.doAction(InteractionListener.Action.ForgotPasswordAction, this);
                break;

            case R.id.closeButton:
                cancel();
                break;
        }
    }

    private void doLogin() {
        CharSequence login = loginLayout.getEditText().getText();
        CharSequence password = passwordLayout.getEditText().getText();

        if (login.length() == 0) {
            String mesg = mRoot.getResources().getString(R.string.inputLogin);
            loginLayout.setError(mesg);
            return;
        }
        if (password.length() == 0) {
            String mesg = mRoot.getResources().getString(R.string.inputPassword);
            passwordLayout.setError(mesg);
            return;
        }
        LoginAction action = new LoginAction(login, password);
        mListener.doAction(action, this);
    }

    public static class LoginAction implements InteractionListener.IAction, Serializable {
        public final CharSequence login;
        public final CharSequence password;

        public LoginAction(CharSequence login, CharSequence password) {
            this.login = login;
            this.password = password;
        }
    }
}
