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

package com.kdgregory.app.s3util.s3ops;

import java.io.File;
import java.util.List;

import net.sf.kdgcommons.lang.StringUtil;

import com.amazonaws.services.s3.model.GetObjectRequest;

import com.kdgregory.app.s3util.filelist.S3File;
import com.kdgregory.app.s3util.main.Concierge;


/**
 *  Downloads one or more files from S3 into a specified directory,
 *  optionally flattening the directory structure.
 */
public class S3DownloadOp
extends AbstractS3Op<Object>
{
    private List<S3File> files;
    private File baseDir;
    private boolean flatten;

    public S3DownloadOp(Concierge concierge, List<S3File> files,
                        File dest, boolean flatten)
    {
        super(concierge, "Downloading file(s)");
        this.files = files;
        this.baseDir = dest;
        this.flatten = flatten;
    }

//----------------------------------------------------------------------------
//  Operation
//----------------------------------------------------------------------------

    @Override
    protected Object performOperation()
    throws Exception
    {
        logger.debug("downloading {} files to {}; flatten: {}", files.size(), baseDir, flatten);
        for (S3File file : files)
        {
            updateProgressMonitor(file.getS3Path());
            download(file);
        }
        logger.debug("download complete");
        return null;
    }

//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    /**
     *  Performs the actual download operation; exists as separate method to
     *  reduce clutter
     */
    private void download(S3File file)
    throws Exception
    {
        logger.debug("downloading {}", file.getKey());

        File fileDir = baseDir;
        String path = file.getS3Path();
        if (!flatten && !StringUtil.isEmpty(path))
        {
            fileDir = new File(baseDir, path);
            if (!fileDir.isDirectory() && !fileDir.mkdirs())
                throw new RuntimeException("unable to create directories: " + fileDir);
        }

        GetObjectRequest request = new GetObjectRequest(getBucketName(), file.getKey());
        File localFile = new File(fileDir, file.getFile().getName());
        getClient().getObject(request, localFile);
    }
}
