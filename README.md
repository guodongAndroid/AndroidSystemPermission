# AndroidSystemPermission

[![License: Apache 2.0](https://img.shields.io/github/license/guodongAndroid/AndroidSystemPermission?color=yellow)](./LICENSE.txt) [![](https://img.shields.io/maven-central/v/com.sunxiaodou.android/system-permission-api)](https://central.sonatype.com/artifact/com.sunxiaodou.android/system-permission-api)

本项目是Android系统权限工具集，需依赖系统签名并配置 `android:sharedUserId="android.uid.system"`。

> API兼容性：Android 5.1(21) - Android 16(36)
>
> 目前仅在瑞芯微系列板卡上进行了测试，相应的厂商如下：
>
> - AOSP
> - 海康威视
> - 欣威视通
> - 迪文

## 使用

首先在 Application 里初始化：

```kotlin
class PermissionApplication : Application() {

    companion object {
        private const val TAG = "PermissionApplication"
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate: currentProcessName: ${SystemPermissionCompat.currentProcessName()}")

        // 1.首先必须根据厂商选择合适的实现，进行注入
        SystemPermissionCompat.setDelegate(AospSystemPermission())
        
        // 2.然后注入Context进行初始化
        SystemPermissionCompat.setContext(this)
    }
}
```

## 特性

| 接口名称                              | 接口描述                           | Android API | 备注     |
| ------------------------------------- | ---------------------------------- | ----------- | -------- |
| `setContext`                          | 设置上下文                         |             |          |
| `getVendor`                           | 获取厂商标识                       |             |          |
| `getVersion`                          | 获取SDK版本                        |             |          |
| `enableEthernet`                      | 是否启用以太网                     |             |          |
| `isEthernetEnabled`                   | 以太网是否启用                     |             |          |
| `setEthernetStaticAddress`            | 设置以太网静态地址                 |             |          |
| `setEthernetDhcpAddress`              | 设置以太网DHCP                     |             |          |
| `getEthernetNetworkAddress`           | 获取以太网网络地址                 |             |          |
| `getEthernetMacAddress`               | 获取以太网MAC地址                  |             |          |
| `reboot`                              | 重启设备                           |             |          |
| `shutdown`                            | 关闭设备                           |             |          |
| `factoryReset`                        | 恢复出厂设置                       |             | 暂未实现 |
| `grantRuntimePermission`              | 授予运行时权限和特殊权限           |             |          |
| `getLauncher`                         | 获取系统桌面                       |             |          |
| `setLauncher`                         | 设置系统桌面                       |             |          |
| `openSystemLauncher`                  | 打开系统桌面                       |             |          |
| `openSystemSettings`                  | 打开系统设置                       |             |          |
| `openSystemDevelopmentSettings`       | 打开系统开发者选项设置             |             |          |
| `setScreenBrightness`                 | 设置屏幕百分比亮度                 |             |          |
| `getScreenBrightness`                 | 获取屏幕百分比亮度                 |             |          |
| `enableAutoBrightness`                | 是否启用自动调节亮度               |             |          |
| `isAutoBrightnessEnabled`             | 自动调节亮度是否启用               |             |          |
| `enableDarkUI`                        | 是否启用深色主题                   | API 30      |          |
| `isDarkUIEnabled`                     | 深色主题是否启用                   | API 30      |          |
| `enableScreenNeverOff`                | 是否启用永不关闭屏幕               |             |          |
| `isScreenNeverOffEnabled`             | 永不关闭屏幕是否启用               |             |          |
| `setScreenOn`                         | 亮屏                               |             |          |
| `setScreenOff`                        | 息屏                               |             |          |
| `enableScreenAutoRotation`            | 是否启用屏幕自动旋转               |             |          |
| `isScreenAutoRotationEnabled`         | 屏幕自动旋转是否启用               |             |          |
| `setScreenRotation`                   | 设置屏幕旋转，顺时针旋转           |             |          |
| `getScreenRotation`                   | 获取屏幕旋转                       |             |          |
| `enableAdb`                           | 是否启用ADB                        |             |          |
| `isAdbEnabled`                        | ADB是否启用                        |             |          |
| `setAdbPort`                          | 设置ADB端口                        |             |          |
| `getAdbPort`                          | 获取ADB端口                        |             |          |
| `enableSystemBar`                     | 是否启用状态栏和导航栏             |             |          |
| `isSystemBarEnabled`                  | 状态栏和导航栏是否启用             |             |          |
| `setTimeZone`                         | 设置系统时区                       |             |          |
| `setDate`                             | 设置系统日期                       |             |          |
| `setTime`                             | 设置系统时间                       |             |          |
| `enableTimeFormat24H`                 | 是否启用24小时制                   |             |          |
| `isTimeFormat24HEnabled`              | 24小时制是否启用                   |             |          |
| `clearApplicationUserData`            | 清除应用程序用户数据，包含缓存数据 |             |          |
| `installPackage`                      | 静默安装                           |             |          |
| `uninstallPackage`                    | 静默卸载                           |             |          |
| `killBackgroundProcesses`             | 杀死应用后台进程                   |             |          |
| `forceStopPackage`                    | 强行停止应用                       |             |          |
| `installOTAPackage`                   | OTA升级                            |             |          |
| `takeScreenShot`                      | 屏幕截图                           |             |          |
| `takeScreenShot`                      | 屏幕截图                           |             |          |
| `getFirmwareVersion`                  | 获取固件版本                       |             |          |
| `getNtpTime`                          | 获取NTP服务器时间                  |             |          |
| `addToPermanentPowerSaveAllowList`    | 忽略应用电池优化                   | API 23      |          |
| `removeToPermanentPowerSaveAllowList` | 应用电池优化                       | API 23      |          |
| `isPowerSaveWhitelistApp`             | 应用是否已忽略电池优化             | API 23      |          |
| `currentPackageName`                  | 获取当前包名                       |             |          |
| `currentProcessName`                  | 获取当前进程名                     |             |          |
| `currentApplication`                  | 获取当前 `Application`             |             |          |

## 示例

### 获取以太网地址

```kotlin
val address = SystemPermissionCompat.getEthernetNetworkAddress()
```

### 重启设置

```kotlin
val reboot = SystemPermissionCompat.reboot()
```

