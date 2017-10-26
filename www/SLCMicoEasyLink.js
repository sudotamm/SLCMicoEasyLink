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

/**
 * SLC-Mico EasyLink plugin
 */

function SLCMicoEasyLink() {}

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