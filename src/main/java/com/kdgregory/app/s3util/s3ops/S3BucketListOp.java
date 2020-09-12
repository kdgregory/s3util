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

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.model.Bucket;

import com.kdgregory.app.s3util.main.Concierge;


/**
 *  Retrieves a list of buckets associated with the current user.
 */
public class S3BucketListOp
extends AbstractS3Op<List<String>>
{
    /**
     *  The recipient of this list must implement this interface. At present
     *  there's only one recipient, the bucket selection dialog. When I was
     *  originally working on this operation, I thought there might be more.
     *  I left the callback interface in place because it's a better design.
     */
    public interface Callback
    {
        void setBucketList(List<String> list);
    }


//----------------------------------------------------------------------------
//  Instance Data and Constructor
//----------------------------------------------------------------------------

    private Callback callback;


    public S3BucketListOp(Concierge concierge, Callback callback)
    {
        super(concierge, "Retrieving list of buckets");
        this.callback = callback;
    }

//----------------------------------------------------------------------------
//  Operation
//----------------------------------------------------------------------------

    @Override
    protected List<String> performOperation()
    throws Exception
    {
        List<String> result = new ArrayList<>();

        logger.debug("retrieving bucket list");
        for (Bucket bucket : getClient().listBuckets())
        {
            result.add(bucket.getName());
        }
        logger.debug("found {} buckets", result.size());

        return result;
    }


    @Override
    protected void onSuccess(List<String> buckets)
    {
        callback.setBucketList(buckets);
    }
}
