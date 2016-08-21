package com.sdkmodule.Operation;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sdkmodule.netWorkCheck.NetworkUtil;
import com.sdkmodule.responsePakage.RequestStatus;
import com.sdkmodule.responsePakage.ResponseHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.SocketException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.InputStreamEntity;

/**
 * Created by zeiad.allam on 16/08/2016.
 */
public class CreateSmartLink {


    private static CreateSmartLink instance = null;
    private AsyncHttpClient client;
    private String contentType;
    private Dialog progressDialog;
    ProgressDialog progress;
      private Gson gson;
    private int timeout = 90 * 60 * 1000;
    /**
     * A private Constructor prevents any other class from instantiating.
     */
    private CreateSmartLink() {
        client = new AsyncHttpClient();
        contentType = "application/json";
        gson = new GsonBuilder().serializeNulls().create();
    }
    /**
     * Make sure that there is only one Connection instance.
     *
     * @return Returns only one instance of Connection.
     */
    public static CreateSmartLink getInstance() {
        if (instance == null) {
            instance = new CreateSmartLink();
        }
        return instance;
    }
    /**
     * Perform a HTTP GET request.
     *
     * @param requestID            A unique id to simply identify request.
     * @param context              The android Context instance associated to the request.
     * @param url                  The URL to send the request to.
     * @param responseHandler      The response handler instance that should handle the response.
    // * @param showLoadingIndicator True if you want to show loading indicator otherwise false.
    // * @param style                You can choose from 28 different types of indicators.
     */
    public void get(final int requestID, Context context, final String url,
                    final ResponseHandler responseHandler) {
        progress=new ProgressDialog(context);
        progress.setMessage("Please Wait...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        switch (NetworkUtil.getConnectivityStatus(context)) {
            case OFFLINE:
                responseHandler.onRequestFinished(requestID, RequestStatus.NO_CONNECTION, 0, null);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progress.dismiss();
                }
                break;
            case WIFI_CONNECTED_WITHOUT_INTERNET:
                responseHandler.onRequestFinished(requestID, RequestStatus.NO_INTERNET, 0, null);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progress.dismiss();
                }
                break;
            case MOBILE_DATA_CONNECTED:
            case WIFI_CONNECTED_WITH_INTERNET:
                client.setTimeout(timeout);
                client.setConnectTimeout(timeout);
                ServerSocket serverSocket = null;

                try {
                    serverSocket = new ServerSocket(3333);
                    serverSocket.setSoTimeout(timeout);
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        Log.v("URL2", url);
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (responseBody != null) {
                            progress.dismiss();
                            String response = new String(responseBody);
                            responseHandler.onRequestFinished(requestID,
                                    RequestStatus.SUCCEED, statusCode, response);
                        } else {
                            responseHandler.onRequestFinished(requestID,
                                    RequestStatus.SUCCEED, statusCode, null);
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();

                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        error.printStackTrace();
                        if (responseBody != null) {
                            progress.dismiss();
                            String response = new String(responseBody);
                            Log.v("Response", response + "");
                            responseHandler.onRequestFinished(requestID,
                                    RequestStatus.FAILED, statusCode, response);
                        } else {
                            responseHandler.onRequestFinished(requestID,
                                    RequestStatus.FAILED, statusCode, null);
                        }

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                });
                break;
        }
    }

    /**
     * Perform a HTTP POST request.
     *
     * @param requestID            A unique id to simply identify request.
     * @param context              The android Context instance associated to the request.
     * @param url                  The URL to send the request to.
     * @param json                 Json which will send with the request.
     * @param responseHandler      The response handler instance that should handle the response.
   //  * @param showLoadingIndicator True if you want to show loading indicator otherwise false.
    // * @param style                You can choose from 28 different types of indicators.
     */
    public void createSmartLink(final int requestID, final Context context, final String url,
                                final String json, final ResponseHandler responseHandler) {
        switch (NetworkUtil.getConnectivityStatus(context)) {
            case OFFLINE:
                responseHandler.onRequestFinished(requestID, RequestStatus.NO_CONNECTION, 0, null);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                break;
            case WIFI_CONNECTED_WITHOUT_INTERNET:
                responseHandler.onRequestFinished(requestID, RequestStatus.NO_INTERNET, 0, null);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                break;
            case MOBILE_DATA_CONNECTED:
            case WIFI_CONNECTED_WITH_INTERNET:
                InputStream is = null;
                InputStreamEntity entity = null;
                try {
                    is = new ByteArrayInputStream(json.getBytes("UTF-8"));
                    entity = new InputStreamEntity(is, is.available());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.setTimeout(timeout);
                client.addHeader("Authorization","Basic dGVzdEB0ZXN0LmNvbToxMjM=");
                client.post(context, url, entity, contentType,new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        Log.v("URL", url);
                        Log.v("Json Request1", json + "");
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (responseBody != null) {
                            String response = new String(responseBody);
                            Log.v("Response", response + "");
                            responseHandler.onRequestFinished(requestID,
                                    RequestStatus.SUCCEED, statusCode, response);
                        } else {
                            responseHandler.onRequestFinished(requestID,
                                    RequestStatus.SUCCEED, statusCode, null);
                        }

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        if (responseBody != null) {
                            String response = new String(responseBody);
                            Log.v("Response Failure", response + "");
                            responseHandler.onRequestFinished(requestID,
                                    RequestStatus.FAILED, statusCode, response);
                        } else {
                            responseHandler.onRequestFinished(requestID,
                                    RequestStatus.FAILED, statusCode, null);
                        }

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                });
                break;
        }
    }
    public Object parseJsonToObject(String response, Class<?> modelClass) {

        try {
            return gson.fromJson(response, modelClass);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public void getdata(String Url){
        client.get(Url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) {
                    String response = new String(responseBody);

                    Log.v("Response1", response + "");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }
        });

    }

}
