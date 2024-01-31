package com.kkkwatt.giffgaffhook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedBridge;


public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                // 在这里，应用的大部分类已经被加载，你可以尝试 hook SimCardsManagerModule

                // Hook EuiccManager's isEnabled method
                XposedHelpers.findAndHookMethod(
                        "android.telephony.euicc.EuiccManager", // 类名
                        lpparam.classLoader, // 类加载器
                        "isEnabled", // 方法名
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                // 在方法执行前可以添加逻辑
                                XposedBridge.log("[*] Hooking giffgaff app");
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                // 修改 isEnabled 方法的返回值
                                param.setResult(true);
                            }
                        }
                );

                // Hook SimCardsManagerModule's setupEsim method
                XposedHelpers.findAndHookMethod(
                        "com.reactnativesimcardsmanager.SimCardsManagerModule", // Full class name
                        lpparam.classLoader,
                        "setupEsim", // Method name
                        "com.facebook.react.bridge.ReadableMap", // First parameter type
                        "com.facebook.react.bridge.Promise", // Second parameter type
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                // This will be called before the setupEsim method execution
                                Object readableMap = param.args[0];
                                Object promise = param.args[1];

                                // Assuming 'getString' is a method of 'readableMap' that returns a String
                                // Use XposedHelpers to call 'getString' and then cast the result to a String
                                String confirmationCode = (String) XposedHelpers.callMethod(readableMap, "getString", "confirmationCode");

                                // Log the call and any information you need
                                XposedBridge.log("[*] eSIM profile confirmationCode: " + confirmationCode);

                                // You can add any additional logic you want to execute before the original method runs
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                // This will be called after the setupEsim method execution
                                // You can modify the behavior after the method call if needed
                            }
                        }
                );
            }
        });

    }
}
