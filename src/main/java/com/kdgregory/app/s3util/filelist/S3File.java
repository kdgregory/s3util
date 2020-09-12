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

package com.kdgregory.app.s3util.filelist;

import java.io.File;
import java.util.Date;

import net.sf.kdgcommons.lang.StringUtil;


/**
 *  An association between a local <code>File</code> and an S3 key, providing
 *  enough information to upload/download that file and display metadata about
 *  it.
 *  <p>
 *  For files to be uploaded to S3, the <code>file</code> component is an
 *  absolute path to a local file, while the <code>s3Path</code> component
 *  is a <em>relative</em> path from the root of the bucket. These are
 *  combined, with a slash between them if necessary, to produce the
 *  <code>key</code> component.
 *  <p>
 *  For files residing on S3, the <code>key</code> component is the absolute
 *  key used to retrieve the file. The <code>s3Path</code> component contains
 *  the initial substring of the key, up to but not including the final slash
 *  (if any). The <code>file</code> component is a simple filename, containing
 *  the substring of the key after the final slash (if any).
 *  <p>
 *  Note: S3 keys are <em>not</em> URL-encoded; the S34J library will handle
 *        encoding/decoding where needed.
 */
public final class S3File
implements Comparable<S3File>
{
    private File file;
    private String s3Path;
    private String key;

    private Long contentLength;
    private Date lastModified;


    /**
     *  Creates an instance from a local file, constructing the S3 key from
     *  provided destination path and local filename.
     *
     *  @param  file    The client-side filename. Used to access the physical
     *                  file during upload, and to get the file's name.
     *  @param  s3Path  The prefix that should be applied to the filename.
     */
    public S3File(File file, String s3Path)
    {
        this.file = file.getAbsoluteFile();
        this.s3Path = cleanupPath(s3Path);
        this.key = makeKey(this.s3Path, file.getName());
    }


    /**
     *  Creates an instance from an S3 key; this will construct a local
     *  (relative) file.
     */
    public S3File(String key)
    {
        this.key = key;
        this.s3Path = extractPath(key);
        this.file = extractFile(key);
    }


    /**
     *  Constructs an instance from an S3 key, along with metadata (note that
     *  the metadata is passed as strings, which is how it comes from S3).
     */
    public S3File(String key, long contentLength, Date lastModified)
    {
        this(key);
        this.contentLength = contentLength;
        this.lastModified = lastModified;
    }


//----------------------------------------------------------------------------
//  Accessor Methods
//----------------------------------------------------------------------------

    /**
     *  Returns the local file corresponding to this instance. For instances
     *  constructed locally, this will be an absolute pathname; for instances
     *  constructed from Amazon keys, it will be a single-component relative
     *  name.
     */
    public File getFile()
    {
        return file;
    }


    /**
     *  Returns the S3 path component.
     */
    public String getS3Path()
    {
        return s3Path;
    }


    /**
     *  Returns the key used to store this file on S3.
     */
    public String getKey()
    {
        return key;
    }


    /**
     *  Returns the size of the file, as it is stored on S3. This is held
     *  as a <code>Long</code> rather than an <code>long</code> because
     *  <code>JTableModel</code> deals in objects, not primitives.
     */
    public Long getContentLength()
    {
        return contentLength;
    }


    /**
     *  Returns the last modification date of the file (the last time it
     *  was uploaded to S3).
     */
    public Date getLastModified()
    {
        return lastModified;
    }

//----------------------------------------------------------------------------
//  Overrides
//----------------------------------------------------------------------------

    /**
     *  The string value of this object is the key used to store it on S3.
     */
    @Override
    public String toString()
    {
        return key;
    }


    /**
     *  Two instances are equal if they have the same S3 key.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        else if (obj instanceof S3File)
        {
            return key.equals(((S3File)obj).key);
        }
        return false;
    }


    @Override
    public int hashCode()
    {
        return key.hashCode();
    }


    @Override
    public int compareTo(S3File that)
    {
        int ret = this.s3Path.compareTo(that.s3Path);
        if (ret != 0)
            return ret;

        String thisName = this.file.getName();
        String thatName = that.file.getName();
        return thisName.compareTo(thatName);
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    /**
     *  Given a string representing an S3 pathname, removes leading and
     *  trailing slashes.
     */
    private static String cleanupPath(String path)
    {
        if (path.startsWith("/"))
            path = path.substring(1);
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        return path;
    }


    /**
     *  Given a (possibly empty) path and filename, combines them into
     *  a key.
     */
    private static String makeKey(String path, String filename)
    {
        if (StringUtil.isEmpty(path))
            return filename;
        else
            return path + "/" + filename;
    }


    private static File extractFile(String key)
    {
        int sepIdx = key.lastIndexOf('/');
        return new File(key.substring(sepIdx + 1));
    }


    private static String extractPath(String key)
    {
        int sepIdx = key.lastIndexOf('/');
        if (sepIdx < 0)
            return "";
        else
            return cleanupPath(key.substring(0, sepIdx));
    }
}
