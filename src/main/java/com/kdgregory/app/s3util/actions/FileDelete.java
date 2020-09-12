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
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdgregory.app.s3util.filelist.S3File;
import com.kdgregory.app.s3util.main.Concierge;
import com.kdgregory.app.s3util.s3ops.S3DeleteOp;


/**
 *  Displays a confirmation dialog, then initiates a delete for the selected
 *  files.
 */
public class FileDelete
extends AbstractAction
{
    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Concierge concierge;


    public FileDelete(Concierge concierge)
    {
        super("Delete Selected");
        setEnabled(false);
        putValue(MNEMONIC_KEY, Integer.valueOf('l'));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));

        this.concierge = concierge;
    }

//----------------------------------------------------------------------------
//  ActionListener
//----------------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent evt)
    {
        logger.info("invoked");
        int confirm = JOptionPane.showConfirmDialog(
                            concierge.getDialogOwner(),
                            "Deletes are irrevocable. Are you sure you want "
                                + "to do this?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.NO_OPTION)
            return;

        List<S3File> files = concierge.getMainFrame().getSelectedFiles();
        new S3DeleteOp(concierge, files).start();
    }
}

