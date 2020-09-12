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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import com.kdgregory.swinglib.AsynchronousOperation;
import com.kdgregory.swinglib.CursorManager;



/**
 *  Provides access to common services. Some of these are read-only after
 *  startup, some may be configured during operation.
 *  <p>
 *  A single instance of the Concierge is created at application startup,
 *  and initialized with dependent objects. This single instance will be
 *  passed as a constructor parameter to everything that needs it.
 */
public class Concierge
{
    private ConfigBean config;

    private ExecutorService threadPool;
    private CursorManager cursorManager;
    private JFrame dialogOwner;
    private MainFrameController mainFrame;

    private AmazonS3 s3Client;


    public Concierge(ConfigBean config)
    {
        this.config = config;
    }

//----------------------------------------------------------------------------
//  Public Accessor Methods -- these may be called from anywhere, at any time
//----------------------------------------------------------------------------

    /**
     *  Adds an operation to the shared background thread pool. This pool is
     *  created at instantiation, and cannot be changed.
     */
    public void execute(AsynchronousOperation<?> op)
    {
        if (threadPool == null)
            threadPool = Executors.newFixedThreadPool(1);
        threadPool.execute(op);
    }


    /**
     *  Returns the configuration bean.
     */
    public ConfigBean getConfig()
    {
        return config;
    }


    /**
     *  Returns the "main frame" controller, for update from actions.
     */
    public MainFrameController getMainFrame()
    {
        return mainFrame;
    }


    /**
     *  Returns the "owner frame" for all dialogs created by the app (nominally
     *  the main frame).
     */
    public JFrame getDialogOwner()
    {
        return dialogOwner;
    }


    /**
     *  Returns the cursor manager, allowing operations/actions to set a temporary
     *  cursor on any component in the application.
     */
    public CursorManager getCursorManager()
    {
        if (cursorManager == null)
            cursorManager = new CursorManager();
        return cursorManager;
    }


    /**
     *  Returns the S3 client object.
     */
    public AmazonS3 getS3Client()
    {
        if (s3Client == null)
        {
            s3Client = AmazonS3ClientBuilder.defaultClient();
        }
        return s3Client;
    }

//----------------------------------------------------------------------------
//  Methods called during initialization; these are all protected, on the
//  assumption that all initialization takes place in the "main" package
//----------------------------------------------------------------------------

    protected void setMainFrame(MainFrameController controller, JFrame frame)
    {
        mainFrame = controller;
        dialogOwner = frame;
    }
}
