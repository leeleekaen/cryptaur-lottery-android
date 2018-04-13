package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.Session;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SessionRequestQueue {
    NetworkRequest currentRequest;
    Queue<NetworkRequest> requestQueue = new LinkedList<>();

    Session currentSession;

    public void doRequest(NetworkRequest request) {
        synchronized (this) {
            if (currentRequest == null) {
                currentRequest = request;
                request.execute();
            } else {
                requestQueue.add(request);
            }
        }
    }

    public void onNetworkRequestDone() {
        synchronized (this) {
            currentRequest = requestQueue.poll();

            if (currentRequest != null) {
                if (currentRequest instanceof ISessionRequest && currentSession != null) {
                    ((ISessionRequest) currentRequest).setSession(currentSession);
                }
                currentRequest.execute();
            }
        }
    }

    public void setCurrentSession(Session currentSession) {
        synchronized (this) {
            this.currentSession = currentSession;
        }
    }

    public void clear() {
        List<NetworkRequest> requests = new ArrayList<>();
        synchronized (this) {
            if (currentRequest != null)
                requests.add(currentRequest);

            requests.addAll(requestQueue);
            requestQueue.clear();
        }

        for (NetworkRequest request : requests) {
            request.cancel();
        }
    }
}
