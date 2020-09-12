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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdgregory.app.s3util.dialogs.UploadDialogController;
import com.kdgregory.app.s3util.main.Concierge;
import com.kdgregory.app.s3util.s3ops.S3UploadOp;


/**
 *  Initiates the backup process, displaying a file chooser and kicking
 *  off the upload operation with its results.
 */
public class FileUpload
extends AbstractAction
{
    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Concierge concierge;
    private UploadDialogController dialogController;


    public FileUpload(Concierge concierge)
    {
        super("Upload...");
        putValue(MNEMONIC_KEY, Integer.valueOf('U'));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

        this.concierge = concierge;
        this.dialogController = new UploadDialogController(concierge);
    }

//----------------------------------------------------------------------------
//  ActionListener
//----------------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent evt)
    {
        logger.info("invoked");
        if (!dialogController.show())
        {
            logger.debug("no files selected");
            return;
        }

        new S3UploadOp(
                concierge,
                dialogController.getSelectedFiles(),
                dialogController.getDestination(),
                dialogController.getMakePublic())
        .start();
    }
}
