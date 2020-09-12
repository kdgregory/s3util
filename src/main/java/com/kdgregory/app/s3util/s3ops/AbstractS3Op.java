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

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;

import com.kdgregory.app.s3util.main.Concierge;
import com.kdgregory.swinglib.AsynchronousOperation;
import com.kdgregory.swinglib.components.ProgressMonitor;


/**
 *  Holds common code for all S3 operations.
 */
public abstract class AbstractS3Op<T>
extends AsynchronousOperation<T>
{
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Concierge concierge;
    private String description;

    private ProgressMonitor _progressMonitor;


    protected AbstractS3Op(Concierge concierge, String description)
    {
        this.concierge = concierge;
        this.description = description;
    }


//----------------------------------------------------------------------------
//  Workflow Methods
//----------------------------------------------------------------------------

    /**
     *  Initiates a wait cursor and then puts this operation on the background
     *  thread. Subclasses may override to provide addition initialization,
     *  but should always delegate to this implementation.
     */
    public void start()
    {
        setBusyState(true);
        concierge.execute(this);
    }


    /**
     *  Clears the wait cursor / progress monitor.
     */
    @Override
    protected void onComplete()
    {
        setBusyState(false);
    }


    /**
     *  Common exception handler. Turns off wait cursor, logs exception, and
     *  notifies user.
     *  <p>
     *  Note that this is marked as <code>final</code>, to prevent subclasses
     *  from overriding.
     */
    @Override
    protected void onFailure(Throwable ex)
    {
        logger.error("request failed", ex);
        setBusyState(false);
        JOptionPane.showMessageDialog(
                concierge.getDialogOwner(),
                "Unable to process this request: " + ex.getMessage()
                    + "\nSee log for more information",
                "Unable to Execute Operation",
                JOptionPane.ERROR_MESSAGE);
    }

//----------------------------------------------------------------------------
//  Support methods for subclasses
//----------------------------------------------------------------------------

    protected Concierge getConcierge()
    {
        return concierge;
    }


    /**
     *  Returns the S3 client object.
     */
    protected AmazonS3 getClient()
    {
        return concierge.getS3Client();
    }


    /**
     *  Returns the currently selected bucket name.
     */
    protected String getBucketName()
    {
        return concierge.getConfig().getAmazonBucketName();
    }


    /**
     *  Called during operation to update the progress monitor status
     *  message.
     */
    protected void updateProgressMonitor(String message)
    {
        _progressMonitor.setStatus(message);
    }

//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    /**
     *  Controls the various "wait indicators" -- pass <code>true</code> at
     *  the start of an operation, <code>false</code> at the end.
     */
    protected void setBusyState(boolean isBusy)
    {
        concierge.getMainFrame().setBusyState(isBusy);
        if (isBusy)
        {
            _progressMonitor = new ProgressMonitor(
                                    concierge.getDialogOwner(),
                                    "S3 Operation in Progress",
                                    description,
                                    ProgressMonitor.Options.MODAL,
                                    ProgressMonitor.Options.CENTER,
                                    ProgressMonitor.Options.SHOW_STATUS);
            _progressMonitor.show();
        }
        else if (_progressMonitor != null)
        {
            _progressMonitor.dispose();
            _progressMonitor = null;
        }
    }
}
