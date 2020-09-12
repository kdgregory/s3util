// Copyright Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.kdgregory.app.s3util.main;

import java.util.prefs.Preferences;


/**
 *  Provides bean-style access to application configuration data, backed by the
 *  Preferences API. Each preference has the following methods:
 *  <ul>
 *  <li> a getter, with the name <code>getXXX()</code>
 *  <li> a setter, with the name <code>setXXX()</code>
 *  <li> a static method that returns validation regex, <code>xxxRegex()</code>
 *       (may be null, in which case no validation occurs)
 *  <li> a static method that returns a description of the preference, <code>xxxInfo()</code>,
 *       used for tooltip text
 *  <ul>
 */
public class ConfigBean
{
    private final static String KEY_S3_BUCKET       = "S3BucketName";

    private final static String KEY_UPLOAD_DIR      = "LastUploadDirectory";
    private final static String KEY_DOWNLOAD_DIR    = "LastDownloadDirectory";

//----------------------------------------------------------------------------
//  Instance Data and Constructors
//----------------------------------------------------------------------------

    Preferences _prefs = Preferences.userNodeForPackage(this.getClass());

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    public String getLastDownloadDirectory()
    {
        return _prefs.get(KEY_DOWNLOAD_DIR, null);
    }


    public void setLastDownloadDirectory(String name)
    {
        _prefs.put(KEY_DOWNLOAD_DIR, name);
    }


    public static String lastDownloadDirectoryInfo()
    {
        return "The directory last used for a download operation.";
    }


    public static String lastDownloadDirectoryRegex()
    {
        return null;
    }


    public static String lastUploadDirectoryInfo()
    {
        return "The directory last used for an upload operation.";
    }


    public String getLastUploadDirectory()
    {
        return _prefs.get(KEY_UPLOAD_DIR, null);
    }


    public void setLastUploadDirectory(String name)
    {
        _prefs.put(KEY_UPLOAD_DIR, name);
    }


    public static String lastUploadDirectoryRegex()
    {
        return null;
    }


    public String getAmazonBucketName()
    {
        return _prefs.get(KEY_S3_BUCKET, "");
    }


    public void setAmazonBucketName(String name)
    {
        _prefs.put(KEY_S3_BUCKET, name);
    }


    public static String amazonBucketNameInfo()
    {
        return "The name of the bucket to be used to store backups. Multiple "
             + "backups can use the same bucket; we recommend one bucket per "
             + "machine being backed up, which holds all backups for that "
             + "machine.";
    }


    public static String amazonBucketNameRegex()
    {
        return "[-A-Za-z0-9]+";
    }
}
