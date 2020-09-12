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

import com.kdgregory.app.s3util.actions.*;


/**
 *  A central location for access to shared action instances. All
 *  instances are public members, set when the assocated GUI object
 *  is constructed.
 */
public class ActionRegistry
{
    public FileRefresh          fileRefresh;
    public FileUpload           fileUpload;
    public FileDownload         fileDownload;
    public FileDelete           fileDelete;
    public FileQuit             fileQuit;

    public EditSelectAll        editSelectAll;
    public EditSelectNone       editSelectNone;
    public EditCopyUrl          editCopyUrl;

    public BucketSelect         bucketSelect;

//----------------------------------------------------------------------------
//  Only one constructor, meant to be called within the package
//----------------------------------------------------------------------------

    protected ActionRegistry(Concierge concierge)
    {
        fileRefresh = new FileRefresh(concierge);
        fileUpload = new FileUpload(concierge);
        fileDownload = new FileDownload(concierge);
        fileDelete = new FileDelete(concierge);
        fileQuit = new FileQuit();

        editSelectAll = new EditSelectAll(concierge);
        editSelectNone = new EditSelectNone(concierge);
        editCopyUrl = new EditCopyUrl(concierge);

        bucketSelect = new BucketSelect(concierge);
    }

//----------------------------------------------------------------------------
//  Public methods - used to enable/disable groups of actions
//----------------------------------------------------------------------------

    /**
     *  Called when the selection state changes (either some row is selected
     *  or no rows are selected).
     */
    public void updatePerSelection(int count)
    {
        fileDownload.setEnabled(count > 0);
        fileDelete.setEnabled(count > 0);
        editSelectNone.setEnabled(count > 0);
        editCopyUrl.setEnabled(count == 1);
    }
}
