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
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.amazonaws.services.s3.model.CannedAccessControlList;

import com.kdgregory.app.s3util.filelist.S3File;
import com.kdgregory.app.s3util.main.Concierge;


/**
 *  Uploads a file to S3. If given a directory, will recurse through
 *  the contents of that directory.
 */
public class S3UploadOp
extends AbstractS3Op<Object>
{
    private List<S3File> files;
    private boolean makePublic;

    public S3UploadOp(
            Concierge concierge,
            File[] files,
            String destination,
            boolean makePublic)
    {
        super(concierge, "Uploading file(s)");

        this.files = new ArrayList<S3File>(files.length);
        for (File file : files)
            this.files.add(new S3File(file, destination));
        this.makePublic = makePublic;
    }

//----------------------------------------------------------------------------
//  Operation
//----------------------------------------------------------------------------

    @Override
    protected Object performOperation()
    throws Exception
    {
        int count = 0;
        logger.debug("starting upload; public read: {}", makePublic);
        for (S3File file : files)
        {
            count += uploadFileRecursive(file);
        }
        logger.debug("upload complete; {} files uploaded", count);
        return null;
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private int uploadFileRecursive(S3File fileToUpload)
    throws Exception
    {
        int count = 0;

        File localFile = fileToUpload.getFile();
        if (localFile.isDirectory())
        {
            String s3Path = fileToUpload.getS3Path() + "/" + localFile.getName();
            for (File child : localFile.listFiles())
            {
                count += uploadFileRecursive(new S3File(child, s3Path));
            }
        }
        else
        {
            logger.debug("uploading: {}", fileToUpload);
            getClient().putObject(getBucketName(), fileToUpload.getKey(), localFile);
            if (makePublic)
            {
                getClient().setObjectAcl(getBucketName(), fileToUpload.getKey(), CannedAccessControlList.PublicRead);
            }
            reportFileUploaded(fileToUpload);
            count++;
        }

        return count;
    }


    private void reportFileUploaded(final S3File file)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                getConcierge().getMainFrame().addFileToList(file);
            }
        });
    }
}
