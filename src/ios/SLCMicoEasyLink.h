//
//  SLCMicoEasyLink.h
//  EasyLink
//
//  Created by Ryan on 13/10/2017.
//  Copyright © 2017 Siemens CT. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "EasyLink.h"
#import "Cordova/CDV.h"

@interface SLCMicoEasyLink : CDVPlugin<EasyLinkFTCDelegate>

@property (nonatomic, strong) EASYLINK *easylinkConfig;

+ (instancetype)shared;
- (void)wifiSSID: (CDVInvokedUrlCommand*)command;
- (void)startWifiConfigWithPwd: (CDVInvokedUrlCommand*)command;
- (void)stopWifiConfig: (CDVInvokedUrlCommand*)command;
@end
