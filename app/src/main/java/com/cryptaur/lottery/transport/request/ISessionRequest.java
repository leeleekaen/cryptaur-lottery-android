package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.model.Session;

/**
 * interface for requests that utilizes session
 */
public interface ISessionRequest {
    void setSession(Session session);
}
