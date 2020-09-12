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
import java.util.List;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdgregory.app.s3util.filelist.S3File;
import com.kdgregory.app.s3util.main.Concierge;


/**
 *  Copies the current file's URL to system clipboard; should only be
 *  enabled when there's a single selected file.
 */
public class EditCopyUrl
extends AbstractAction
{
    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Concierge concierge;

//----------------------------------------------------------------------------
//  Instance Variables and Constructor
//----------------------------------------------------------------------------

    public EditCopyUrl(Concierge concierge)
    {
        super("Copy URL");
        putValue(MNEMONIC_KEY, Integer.valueOf('U'));

        this.concierge = concierge;
    }


//----------------------------------------------------------------------------
//  ActionListener
//----------------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent ignored)
    {
        logger.info("invoked");

        List<S3File> selection = concierge.getMainFrame().getSelectedFiles();
        if (selection.size() == 0)
        {
            logger.debug("nothing selected; ignored (but why was I enabled?");
            return;
        }

        // FIXME
        // if more than one item selected, we'll punt and take first
//        S3Factory s3Fact = _concierge.getS3Factory();
//        String url = s3Fact.getUrl(
//                        _concierge.getConfig().getAmazonBucketName(),
//                        selection.get(0).getKey());
//        logger.debug("selected url: " + url);
//
//        ClipManager.putString(url);
    }
}
