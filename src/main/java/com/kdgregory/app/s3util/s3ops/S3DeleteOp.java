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

import java.util.List;
import javax.swing.SwingUtilities;

import com.kdgregory.app.s3util.filelist.S3File;
import com.kdgregory.app.s3util.main.Concierge;


/**
 *  Deletes one or more files from S3, reporting each back to the main frame.
 */
public class S3DeleteOp
extends AbstractS3Op<Object>
{
    private List<S3File> filesToDelete;

    public S3DeleteOp(Concierge concierge, List<S3File> files)
    {
        super(concierge, "Deleting file(s)");
        this.filesToDelete = files;
    }

//----------------------------------------------------------------------------
//  Operation
//----------------------------------------------------------------------------

    @Override
    protected Object performOperation()
    throws Exception
    {
        logger.debug("deleting {} files", filesToDelete.size());
        for (S3File file : filesToDelete)
        {
            String key = file.getKey();
            logger.debug("deleting {}", key);
            getClient().deleteObject(getBucketName(), key);
            reportFileDeleted(file);
        }
        logger.debug("deletion complete");
        return null;
    }

//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private void reportFileDeleted(final S3File file)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                getConcierge().getMainFrame().removeFileFromList(file);
            }
        });
    }
}
