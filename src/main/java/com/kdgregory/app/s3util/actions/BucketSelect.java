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

package com.kdgregory.app.s3util.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdgregory.app.s3util.dialogs.BucketDialogController;
import com.kdgregory.app.s3util.main.Concierge;


/**
 *  Displays the "Select Bucket" dialog.
 */
public class BucketSelect
extends AbstractAction
{
    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private BucketDialogController dialog;


    public BucketSelect(Concierge concierge)
    {
        super("Select...");
        this.dialog = new BucketDialogController(concierge);
    }

//----------------------------------------------------------------------------
//  ActionListener
//----------------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent evt)
    {
        logger.info("invoked");
        dialog.show();
    }
}
