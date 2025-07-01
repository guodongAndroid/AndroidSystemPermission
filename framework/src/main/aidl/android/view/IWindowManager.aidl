/*
** Copyright 2006, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

package android.view;

import android.window.ScreenCapture;

/**
 * System private interface to the window manager.
 *
 * {@hide}
 */
interface IWindowManager {

    /**
     * Captures the entire display specified by the displayId using the args provided. If the args
     * are null or if the sourceCrop is invalid or null, the entire display bounds will be captured.
     */
    oneway void captureDisplay(int displayId, in @nullable ScreenCapture.CaptureArgs captureArgs,
        in ScreenCapture.ScreenCaptureListener listener);
}