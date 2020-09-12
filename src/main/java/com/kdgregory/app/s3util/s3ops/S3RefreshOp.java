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

import java.util.SortedSet;
import java.util.TreeSet;

import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import com.kdgregory.app.s3util.filelist.FileListTableModel;
import com.kdgregory.app.s3util.filelist.S3File;
import com.kdgregory.app.s3util.main.Concierge;


/**
 *  Retrieves the list of files from S3.
 */
public class S3RefreshOp
extends AbstractS3Op<FileListTableModel>
{
    public S3RefreshOp(Concierge concierge)
    {
        super(concierge, "Refreshing list of files");
    }

//----------------------------------------------------------------------------
//  Operation
//----------------------------------------------------------------------------

    @Override
    protected FileListTableModel performOperation()
    throws Exception
    {
        logger.debug("starting refresh");

        SortedSet<S3File> result = new TreeSet<>();
        ListObjectsRequest request = new ListObjectsRequest().withBucketName(getBucketName());
        ObjectListing response = null;
        do
        {
            response = getClient().listObjects(request);
            for (S3ObjectSummary info : response.getObjectSummaries())
            {
                // the Console creates zero-length objects as "folders"
                if (info.getSize() > 0)
                {
                    result.add(new S3File(info.getKey(), info.getSize(), info.getLastModified()));
                }
            }
            request.setMarker(response.getNextMarker());
        }
        while (response.isTruncated());

        logger.debug("finished refresh: {} files", result.size());
        return new FileListTableModel(result);
    }


    @Override
    protected void onSuccess(FileListTableModel result)
    {
        getConcierge().getMainFrame().resetList(result);
    }
}
