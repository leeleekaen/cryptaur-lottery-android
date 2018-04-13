package com.cryptaur.lottery.transport.model;

public class Login {
    public final CharSequence login;
    public final CharSequence password;
    public final CharSequence pin;

    public Login(CharSequence login, CharSequence password, CharSequence pin) {
        this.login = login;
        this.password = password;
        this.pin = pin;
    }
}
