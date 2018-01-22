//
//  SLCMicoEasyLink.m
//  EasyLink
//
//  Created by Ryan on 13/10/2017.
//  Copyright © 2017 Siemens CT. All rights reserved.
//

#import "SLCMicoEasyLink.h"

@interface SLCMicoEasyLink()

@property (nonatomic, strong) CDVInvokedUrlCommand *configCommand;

@end

@implementation SLCMicoEasyLink

+ (instancetype)shared{
    static SLCMicoEasyLink *easyLink = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        easyLink = [[SLCMicoEasyLink alloc] init];
    });
    return easyLink;
}

#pragma mark - Public methods

- (void)wifiSSID: (CDVInvokedUrlCommand*)command
{
   NSString *ssid =  [[NSString alloc] initWithData:[EASYLINK ssidDataForConnectedNetwork] encoding:NSUTF8StringEncoding];
    CDVPluginResult *pluginResult = nil;
    if(ssid.length > 0){
        NSLog(@"SLCMicoEasyLink: ssid succeed: %@", ssid);
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:ssid];
    }else{
        NSLog(@"SLCMicoEasyLink: ssid error.");
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No ssid found."];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)startWifiConfigWithPwd: (CDVInvokedUrlCommand*)command
{
    self.easylinkConfig = [[EASYLINK alloc]initForDebug:YES WithDelegate:self];
    self.configCommand = command;
    NSString *pwd = command.arguments.firstObject;
    if(nil == pwd){
        pwd = @"";
    }
    
    NSString *userInfo = command.arguments.lastObject;
    if(nil == userInfo){
        userInfo = @"";
    }
    const char *temp = [userInfo cStringUsingEncoding:NSUTF8StringEncoding];
    
    NSMutableDictionary *wifiInfoDict = [NSMutableDictionary dictionary];
    [wifiInfoDict setObject:[EASYLINK ssidDataForConnectedNetwork] forKey:@"SSID"];
    [wifiInfoDict setObject:pwd forKey:@"PASSWORD"];
    [wifiInfoDict setObject:@YES forKey:@"DHCP"];
    [wifiInfoDict setObject:[EASYLINK getIPAddress] forKey:@"IP"];
    [wifiInfoDict setObject:[EASYLINK getNetMask] forKey:@"NETMASK"];
    [wifiInfoDict setObject:[EASYLINK getGatewayAddress] forKey:@"GATEWAY"];
    [wifiInfoDict setObject:[EASYLINK getGatewayAddress] forKey:@"DNS1"];
    
    NSLog(@"SLCMicoEasyLink: start wifi config.");
    NSLog(@"SLCMicoEasyLink: wifi config: %@", wifiInfoDict.description);
    NSLog(@"SLCMicoEasyLink: wifi info: %@", userInfo);
    
    [self.easylinkConfig prepareEasyLink:wifiInfoDict info:[NSData dataWithBytes:temp length:strlen(temp)] mode:EASYLINK_AWS];
    [self.easylinkConfig transmitSettings];
}

- (void)stopWifiConfig: (CDVInvokedUrlCommand*)command
{
    NSLog(@"SLCMicoEasyLink: stop wifi config.");
    [self.easylinkConfig stopTransmitting];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}


#pragma mark - EasyLinkFTCDelegate methods

- (void)onFound:(NSNumber *)client withName:(NSString *)name mataData: (NSDictionary *)mataDataDict{
    [self.easylinkConfig stopTransmitting];
    NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithObject:name forKey:@"Name"];
    for(NSString *key in [mataDataDict allKeys]){
        if([key isEqualToString:@"IP"]){
            continue;
        }
        id value = [mataDataDict objectForKey: key];
        NSString *valueStr = [[NSString alloc] initWithData:value encoding:NSUTF8StringEncoding];
        [dict setObject:valueStr forKey:key];
    }
    NSLog(@"SLCMicoEasyLink: wifi smart config succeed: %@", dict);
    [self.commandDelegate runInBackground:^{
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dict];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.configCommand.callbackId];
    }];
}
@end
