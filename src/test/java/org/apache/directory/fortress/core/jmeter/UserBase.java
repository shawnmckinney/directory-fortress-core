/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.fortress.core.jmeter;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.fortress.core.*;
import org.apache.directory.fortress.core.SecurityException;
import org.apache.directory.fortress.core.model.User;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.directory.fortress.core.AdminMgr;
import org.apache.directory.fortress.core.impl.TestUtils;

import static org.junit.Assert.assertNotNull;

/**
 * Description of the Class
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class UserBase extends AbstractJavaSamplerClient
{
    protected AdminMgr adminMgr;
    protected ReviewMgr reviewMgr;
    protected static final Logger LOG = LoggerFactory.getLogger( UserBase.class );
    private static int count = 0;
    protected String hostname;
    protected String qualifier;
    protected boolean verify;

    protected enum Op
    {
        ADD,
        DEL
    }

    protected boolean verify( String userId, Op op )
    {
        boolean found = false;
        try
        {
            assertNotNull( adminMgr );
            User user = new User( userId );
            User outUser = reviewMgr.readUser( user );
            if( op == Op.DEL )
            {
                warn( "Failed del check, threadId: " + getThreadId() + ", user: " + userId );
            }
            assertNotNull( outUser );
            found = true;
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            if( op == Op.ADD )
            {
                warn( "Failed add check, threadId: " + getThreadId() + ", error reading user: " + se );
                se.printStackTrace();
            }
        }
        return found;
    }

    /**
     * Description of the Method
     *
     * @param samplerContext Description of the Parameter
     */
    public void setupTest( JavaSamplerContext samplerContext )
    {
        hostname = System.getProperty( "hostname" );
        if (StringUtils.isEmpty( hostname ))
        {
            hostname = samplerContext.getParameter( "hostname" );
        }
        System.setProperty( "fortress.host", hostname );
        qualifier = System.getProperty( "qualifier" );
        if (StringUtils.isEmpty( qualifier ))
        {
            qualifier = samplerContext.getParameter( "qualifier" );
        }
        String szVerify = System.getProperty( "verify" );
        if (StringUtils.isEmpty( szVerify ))
        {
            verify = samplerContext.getParameter( "verify" ).equalsIgnoreCase( "true" );
        }
        else
        {
            verify = szVerify.equalsIgnoreCase( "true" );
        }
        String message = "FT SETUP User TID: " + getThreadId() + ", hostname: " + hostname + ", qualifier: " + qualifier + ", verify:" + verify;
        log( message );
        System.out.println( message );
        try
        {
            adminMgr = AdminMgrFactory.createInstance( TestUtils.getContext() );
            reviewMgr = ReviewMgrFactory.createInstance( TestUtils.getContext() );
        }
        catch ( SecurityException se )
        {
            warn( "ThreadId: " + getThreadId() + ", error setting up test: " + se );
            se.printStackTrace();
        }
    }

    protected void log( String message )
    {
        LOG.info( message );
        System.out.println( message );
    }

    protected void warn( String message )
    {
        LOG.warn( message );
        System.out.println( message );
    }

    /**
     *
     * @return
     */
    synchronized int getKey( )
    {
        return ++count;
    }
    String getThreadId()
    {
        return "" + Thread.currentThread().getId();
    }

    /**
     * Description of the Method
     *
     * @param samplerContext Description of the Parameter
     */
    public void teardownTest( JavaSamplerContext samplerContext )
    {
        String message = "FT SETUP User TID: " + getThreadId();
        log( message );
    }
}