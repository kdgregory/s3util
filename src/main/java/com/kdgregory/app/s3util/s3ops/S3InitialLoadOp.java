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

import com.amazonaws.services.s3.model.AmazonS3Exception;

import com.kdgregory.app.s3util.dialogs.BucketDialogController;
import com.kdgregory.app.s3util.main.Concierge;


/**
 *  Called when the application starts, to verify that the configured bucket
 *  exists and either (1) show the bucket selection dialog (if it doesn't),
 *  or (2) refresh the file list.
 */
public class S3InitialLoadOp
extends AbstractS3Op<Object>
{
    public S3InitialLoadOp(Concierge concierge)
    {
        super(concierge, "Initial Load");
    }

//----------------------------------------------------------------------------
//  Operation
//----------------------------------------------------------------------------

    @Override
    protected Object performOperation()
    throws Exception
    {
        String bucketName = getConcierge().getConfig().getAmazonBucketName();

        logger.debug("verifying that bucket {} exists", bucketName);

        // this will throw if the bucket doesn't exist
        getClient().getBucketLocation(bucketName);

        return null;
    }


    @Override
    protected void onSuccess(Object result)
    {
        new S3RefreshOp(getConcierge()).start();
    }


    @Override
    protected void onFailure(Throwable ex)
    {
        if (ex instanceof AmazonS3Exception)
        {
            if ("NoSuchBucket".equals(((AmazonS3Exception)ex).getErrorCode()))
            {
                new BucketDialogController(getConcierge()).show();
                return;
            }
        }

        super.onFailure(ex);
    }



}
