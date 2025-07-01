/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.os;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;

import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;

/**
 * Gives access to the system properties store.  The system properties
 * store contains a list of string key-value pairs.
 *
 * <p>Use this class only for the system properties that are local. e.g., within
 * an app, a partition, or a module. For system properties used across the
 * boundaries, formally define them in <code>*.sysprop</code> files and use the
 * auto-generated methods. For more information, see <a href=
 * "https://source.android.com/devices/architecture/sysprops-apis">Implementing
 * System Properties as APIs</a>.</p>
 * <p>
 * {@hide}
 */
@Keep
public class SystemProperties {
    private static final String TAG = "SystemProperties";
    private static final boolean TRACK_KEY_ACCESS = false;

    /**
     * @hide
     */
    public static final int PROP_VALUE_MAX = 91;

    // The one-argument version of native_get used to be a regular native function. Nowadays,
    // we use the two-argument form of native_get all the time, but we can't just delete the
    // one-argument overload: apps use it via reflection, as the UnsupportedAppUsage annotation
    // indicates. Let's just live with having a Java function with a very unusual name.
    private static String native_get(String key) {
        return native_get(key, "");
    }

    @FastNative
    private static native String native_get(String key, String def);

    @FastNative
    private static native int native_get_int(String key, int def);

    @FastNative
    private static native long native_get_long(String key, long def);

    @FastNative
    private static native boolean native_get_boolean(String key, boolean def);

    @FastNative
    private static native long native_find(String name);

    @FastNative
    private static native String native_get(long handle);

    @CriticalNative
    private static native int native_get_int(long handle, int def);

    @CriticalNative
    private static native long native_get_long(long handle, long def);

    @CriticalNative
    private static native boolean native_get_boolean(long handle, boolean def);

    // _NOT_ FastNative: native_set performs IPC and can block
    private static native void native_set(String key, String def);

    private static native void native_add_change_callback();

    private static native void native_report_sysprop_change();

    /**
     * Get the String value for the given {@code key}.
     *
     * @param key the key to lookup
     * @return an empty string if the {@code key} isn't found
     * @hide
     */
    public static String get(@NonNull String key) {
        return native_get(key);
    }

    /**
     * Get the String value for the given {@code key}.
     *
     * @param key the key to lookup
     * @param def the default value in case the property is not set or empty
     * @return if the {@code key} isn't found, return {@code def} if it isn't null, or an empty
     * string otherwise
     * @hide
     */
    public static String get(@NonNull String key, @Nullable String def) {
        return native_get(key, def);
    }

    /**
     * Get the value for the given {@code key}, and return as an integer.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as an integer, or def if the key isn't found or
     * cannot be parsed
     * @hide
     */
    public static int getInt(@NonNull String key, int def) {
        return native_get_int(key, def);
    }

    /**
     * Get the value for the given {@code key}, and return as a long.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a long, or def if the key isn't found or
     * cannot be parsed
     * @hide
     */
    public static long getLong(@NonNull String key, long def) {
        return native_get_long(key, def);
    }

    /**
     * Get the value for the given {@code key}, returned as a boolean.
     * Values 'n', 'no', '0', 'false' or 'off' are considered false.
     * Values 'y', 'yes', '1', 'true' or 'on' are considered true.
     * (case sensitive).
     * If the key does not exist, or has any other value, then the default
     * result is returned.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a boolean, or def if the key isn't found or is
     * not able to be parsed as a boolean.
     * @hide
     */
    public static boolean getBoolean(@NonNull String key, boolean def) {
        return native_get_boolean(key, def);
    }

    /**
     * Set the value for the given {@code key} to {@code val}.
     *
     * @throws IllegalArgumentException for non read-only properties if the {@code val} exceeds
     *                                  91 characters
     * @throws RuntimeException         if the property cannot be set, for example, if it was blocked by
     *                                  SELinux. libc will log the underlying reason.
     * @hide
     */
    public static void set(@NonNull String key, @Nullable String val) {
        if (val != null && !key.startsWith("ro.") && val.getBytes(StandardCharsets.UTF_8).length
                > PROP_VALUE_MAX) {
            throw new IllegalArgumentException("value of system property '" + key
                    + "' is longer than " + PROP_VALUE_MAX + " bytes: " + val);
        }
        native_set(key, val);
    }

    private SystemProperties() {
    }
}
