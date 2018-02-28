package com.siemens.dolphin.SLCMicoEasyLink;

import android.content.Context;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.fogcloud.sdk.easylink.api.EasylinkP2P;
import io.fogcloud.sdk.easylink.helper.EasyLinkCallBack;
import io.fogcloud.sdk.easylink.helper.EasyLinkParams;

public class SLCMicoEasyLink extends CordovaPlugin {

    private static final String TAG = "SLCMicoEasyLink";

    private static final String ACTION_WIFI_SSID = "wifiSSID";
    private static final String ACTION_START_WIFI_CONFIG_WITH_PWD = "startWifiConfigWithPwd";
    private static final String ACTION_STOP_WIFI_CONFIG = "stopWifiConfig";

    private Context context;

    EasylinkP2P easylink;
    EasyLinkParams easyLinkParams;

    /**
     * Constructor.
     */
    public SLCMicoEasyLink() {

    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = this.cordova.getActivity().getApplicationContext();
        easylink = new EasylinkP2P(context);
        easyLinkParams = new EasyLinkParams();
    }

    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) {
        if (action.equals(ACTION_WIFI_SSID)) {
            callbackContext.success(getWifiSSID());
            return true;
        }

        if (action.equals(ACTION_START_WIFI_CONFIG_WITH_PWD)) {
            try {
                startWifiConfigWithPwd(callbackContext, args.getString(0), args.getString(1));
            } catch (JSONException e) {
                Log.e(TAG, "execute JSON error", e);
            }
            return true;
        }

        if (action.equals(ACTION_STOP_WIFI_CONFIG)) {
            stopWifiConfig();
            return true;
        }

        return false;
    }

    public String getWifiSSID() {
        String ssid = easylink.getSSID();
        return (ssid.length() > 0 ? ssid : "No ssid found.");
    }

    public void startWifiConfigWithPwd(final CallbackContext callbackContext, String password, String info) {
        easyLinkParams.ssid = easylink.getSSID();
        easyLinkParams.password = password;
        easyLinkParams.isSendIP = true;
        easyLinkParams.extraData = info;
        Log.d(TAG, "wifi info: " + password + ", " + info);
        easylink.startEasyLink(easyLinkParams, new EasyLinkCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    callbackContext.success(object);
                    Log.d(TAG, "startWifiConfig success: " + s);
                } catch (JSONException e) {
                    callbackContext.error("");
                    Log.d(TAG, "parse wifi config callback error", e);
                }
            }

            @Override
            public void onFailure(int i, String s) {
                callbackContext.error(s);
                Log.d(TAG, "start wifi config failed: " + s);
            }
        });
    }

    public void stopWifiConfig() {
        easylink.stopEasyLink(new EasyLinkCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                Log.d(TAG, "stopWifiConfig success.");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG, "stopWifiConfig failed.");
            }
        });
    }
}
