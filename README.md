# AndroidSystemPermission

[![License: Apache 2.0](https://img.shields.io/github/license/guodongAndroid/AndroidSystemPermission?color=yellow)](./LICENSE.txt) 

本项目是Android系统权限工具集，需依赖系统签名并配置 `android:sharedUserId="android.uid.system"`。

> 目前仅适配了瑞芯微系列板卡

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

| 接口名称                        | 接口描述                           | 备注           |
| ------------------------------- | ---------------------------------- | -------------- |
| `getVersion`                    | 获取SDK版本                        |                |
| `setEthernetStaticAddress`      | 设置以太网静态地址                 |                |
| `setEthernetDhcpAddress`        | 设置以太网DHCP                     |                |
| `getEthernetNetworkAddress`     | 获取以太网网络地址                 |                |
| `getEthernetMacAddress`         | 获取以太网MAC地址                  |                |
| `reboot`                        | 重启设备                           |                |
| `factoryReset`                  | 恢复出厂设置                       |                |
| `grantRuntimePermission`        | 授予运行时权限和特殊权限           |                |
| `setLauncher`                   | 设置系统桌面                       |                |
| `openSystemLauncher`            | 打开系统桌面                       |                |
| `openSystemSettings`            | 打开系统设置                       |                |
| `openSystemDevelopmentSettings` | 打开系统开发者选项设置             |                |
| `setScreenBright`               | 设置屏幕百分比亮度                 | [1, 100]       |
| `getScreenBright`               | 获取屏幕百分比亮度                 | [1, 100]       |
| `enableAutoBrightness`          | 是否启用自动调节亮度               |                |
| `isAutoBrightnessEnabled`       | 自动调节亮度是否启用               |                |
| `enableScreenNeverOff`          | 是否启用永不关闭屏幕               |                |
| `isScreenNeverOffEnabled`       | 永不关闭屏幕是否启用               |                |
| `setScreenOn`                   | 亮屏                               |                |
| `setScreenOff`                  | 息屏                               |                |
| `enableAdb`                     | 是否启用ADB                        |                |
| `isAdbEnabled`                  | ADB是否启用                        |                |
| `hideSystemBar`                 | 隐藏状态栏和导航栏                 |                |
| `showSystemBar`                 | 显示状态栏和导航栏                 |                |
| `setDate`                       | 设置系统日期                       |                |
| `setTime`                       | 设置系统时间                       |                |
| `setOrientation`                | 设置屏幕旋转方向，顺时针旋转       |                |
| `clearApplicationUserData`      | 清除应用程序用户数据，包含缓存数据 |                |
| `installPackage`                | 静默安装                           |                |
| `uninstallPackage`              | 静默卸载                           |                |
| `installOTAPackage`             | OTA升级                            |                |
| `takeScreenShot`                | 屏幕截图                           | 传入保存路径   |
| `takeScreenShot`                | 屏幕截图                           | 返回 `Bitmap?` |
| `getFirmwareVersion`            | 获取固件版本                       |                |
| `getNtpTime`                    | 获取NTP服务器时间                  |                |
| `currentPackageName`            | 获取当前包名                       |                |
| `currentProcessName`            | 获取当前进程名                     |                |
| `currentApplication`            | 获取当前 `Application`             |                |

## 示例

### 获取以太网地址

```kotlin
val address = SystemPermissionCompat.getEthernetNetworkAddress()
```

### 重启设置

```kotlin
val reboot = SystemPermissionCompat.reboot()
```

