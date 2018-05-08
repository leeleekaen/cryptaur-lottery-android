package com.cryptaur.lottery.transport.base;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cryptaur.lottery.BuildConfig;
import com.cryptaur.lottery.Const;

import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by babay on 14.11.2017.
 */

public abstract class NetworkRequest<Result> implements Runnable {
    public static final int CONNECT_TIMEOUT = 10_000;
    public static final int READ_TIMEOUT = 30_000;
    public static final String TAG = Const.TAG + "NetRequest";
    protected final NetworkRequestListener<Result> listener;
    private final OkHttpClient client;
    protected RequestLog debugData;
    protected String charset = "UTF-8";
    protected volatile boolean cancelled;
    protected volatile boolean finished;
    private int status;

    public NetworkRequest(OkHttpClient client, NetworkRequestListener<Result> listener) {
        this.client = client;
        this.listener = listener;
    }

    public void execute() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(this);
    }

    protected Request createRequest(String method, RequestType requestType, String requestBody) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(Const.SERVER_URL + method);
        if (requestType.isEnclosing() && requestBody != null) {
            RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), requestBody);
            builder.method(requestType.requestTypeString(), body);
        } else {
            builder.method(requestType.requestTypeString(), null);
        }
        //builder.addHeader("Accept", "application/json");

        Request request = builder.build();

        if (BuildConfig.DEBUG) {
            debugData = new RequestLog(requestType);
            debugData.onStart(request);
        }

        return request;
    }

    protected void writeRequestBody(HttpURLConnection urlConnection, String requestBody) throws IOException {
        if (debugData != null)
            debugData.requestBody = requestBody;

        try (OutputStream outputStream = urlConnection.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, charset))) {
            writer.write(requestBody);
            writer.flush();
        }
    }

    protected String postRequest(String method, RequestType requestType, @Nullable String requestBody) throws IOException, JSONException {
        Request request = createRequest(method, requestType, requestBody);

        if (debugData != null) {
            Log.d(TAG, "sending " + debugData.toString());
        }
        try (Response response = client.newCall(request).execute()) {
            status = response.code();
            String result = response.body().string();
            if (debugData != null)
                debugData.onGotResponse(result, response.code());
            return result;
        }
    }

    protected void execSimpleRequest(String method, RequestType requestType, @Nullable String requestBody) {
        try {
            String responceStr = postRequest(method, requestType, requestBody);
            if (status != 200) {
                handleException(parseException(responceStr, status));
                return;
            }

            Result responce = parse(responceStr, status);

            if (debugData != null)
                debugData.log();

            if (finish() && listener != null) {
                listener.onNetworkRequestDone(this, responce);
            }

        } catch (Exception ex) {
            handleException(ex);
        }
    }

    protected void handleException(Exception ex) {
        if (debugData != null) {
            debugData.setException(ex).log();
        }
        if (finish() && listener != null) {
            listener.onNetworkRequestError(this, ex);
        }
    }

    protected abstract Result parse(String source, int status) throws IOException, JSONException;

    protected abstract Exception parseException(String responceStr, int code);

    public void cancel() {
        cancelled = true;
        if (finish() && listener != null)
            listener.onCancel(this);
    }

    /**
     * finishes request
     *
     * @return true if request was not finished
     */
    protected boolean finish() {
        boolean wasFinished;
        synchronized (this) {
            wasFinished = finished;
            finished = true;
        }
        return !wasFinished;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public interface NetworkRequestListener<T> {
        void onNetworkRequestStart(NetworkRequest request);

        void onNetworkRequestDone(NetworkRequest request, T responce);

        void onNetworkRequestError(NetworkRequest request, Exception e);

        void onCancel(NetworkRequest request);
    }
}