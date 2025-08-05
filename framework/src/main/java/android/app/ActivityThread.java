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

package android.app;

import androidx.annotation.Nullable;

/**
 * Created by john.wick on 2025/7/2
 */
public class ActivityThread {

    @Nullable
    public static ActivityThread currentActivityThread() {
        return null;
    }

    @Nullable
    public static String currentPackageName() {
        return null;
    }

    @Nullable
    public static String currentProcessName() {
        return null;
    }

    @Nullable
    public static Application currentApplication() {
        return null;
    }
}
