/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

var argscheck = require('cordova/argscheck'),
    channel = require('cordova/channel'),
    utils = require('cordova/utils'),
    exec = require('cordova/exec'),
    cordova = require('cordova');

channel.createSticky('onCordovaInfoReady');
// Tell cordova channel to wait on the CordovaInfoReady event
channel.waitForInitialization('onCordovaInfoReady');

/**
 * SLC-Mico EasyLink plugin
 */
// export class SLCMicoEasyLink {
//     static shared = new SLCMicoEasyLink()

//     wifiSSID(successCallback, errorCallback) {
//         exec(successCallback, errorCallback, "SLCMicoEasyLink", "wifiSSID", [])
//     }

//     startWifiConfigWithPwd(password, successCallback, errorCallback) {
//         exec(successCallback, errorCallback, "SLCMicoEasyLink", "startWifiConfigWithPwd", [password])
//     }

//     stopWifiConfig(successCallback, errorCallback) {
//         exec(successCallback, errorCallback, "SLCMicoEasyLink", "stopWifiConfig", [])
//     }

// }

function SLCMicoEasyLink() {

    this.wifiName = ""
    this.available = false
    var me = this
    channel.onCordovaReady.subscribe(function() {
        me.wifiSSID(function(info) {
            //ignoring info.cordova returning from native, we should use value from cordova.version defined in cordova.js
            //TODO: CB-5105 native implementations should not return info.cordova
            me.available = true
            me.wifiName = info
            channel.onCordovaInfoReady.fire()
        }, function(e) {
            me.available = false;
            utils.alert("[ERROR] Error initializing Cordova: " + e);
        });
    });
}

SLCMicoEasyLink.prototype.wifiSSID = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SLCMicoEasyLink", "wifiSSID", []);
};

SLCMicoEasyLink.prototype.startWifiConfigWithPwd = function(password, info, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SLCMicoEasyLink", "startWifiConfigWithPwd", [password, info]);
};

SLCMicoEasyLink.prototype.stopWifiConfig = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SLCMicoEasyLink", "stopWifiConfig", []);
};

module.exports = new SLCMicoEasyLink();