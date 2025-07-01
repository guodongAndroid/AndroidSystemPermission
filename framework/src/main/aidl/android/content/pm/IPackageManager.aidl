/*
**
** Copyright 2007, The Android Open Source Project
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

package android.content.pm;

import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageInstallObserver2;
import android.content.pm.IPackageDeleteObserver2;

interface IPackageManager {

    void grantRuntimePermission(String packageName, String permissionName, int userId);

    void replacePreferredActivity(in IntentFilter filter, int match,
            in ComponentName[] set, in ComponentName activity, int userId);

    /** deprecated Use PackageInstaller instead. 24 <= api < 28 */
    void installPackageAsUser(in String originPath,
            in IPackageInstallObserver2 observer,
            int flags,
            in String installerPackageName,
            int userId);

    /**
     * Delete a package for a specific user. 24 <= api < 28
     *
     * @param packageName The fully qualified name of the package to delete.
     * @param observer a callback to use to notify when the package deletion in finished.
     * @param userId the id of the user for whom to delete the package
     * @param flags - possible values: {@link #DONT_DELETE_DATA}
     */
    void deletePackage(in String packageName, IPackageDeleteObserver2 observer, int userId, int flags);

    /**
     * Clear the user data directory of an application.
     * @param packageName The package name of the application whose cache
     * files need to be deleted
     * @param observer a callback used to notify when the operation is completed.
     */
    void clearApplicationUserData(in String packageName, IPackageDataObserver observer, int userId);
}