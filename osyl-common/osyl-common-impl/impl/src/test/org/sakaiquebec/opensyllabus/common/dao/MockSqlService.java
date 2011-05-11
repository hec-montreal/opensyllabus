/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.sakaiquebec.opensyllabus.common.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.exception.ServerOverloadException;

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class MockSqlService implements SqlService{
    
    String vendor;

    
    public void init() {
	
    }
    
    
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    
    

    public MockSqlService() {
	super();
    }

    @Override
    public Connection borrowConnection() throws SQLException {
	return null;
    }

    @Override
    public void returnConnection(Connection conn) {
    }

    @Override
    public boolean transact(Runnable callback, String tag) {
	return false;
    }

    @Override
    public List dbRead(String sql) {
	return null;
    }

    @Override
    public List dbRead(String sql, Object[] fields, SqlReader reader) {
	return null;
    }

    @Override
    public List dbRead(Connection conn, String sql, Object[] fields,
	    SqlReader reader) {
	return null;
    }

    @Override
    public void dbReadBinary(String sql, Object[] fields, byte[] value) {
    }

    @Override
    public void dbReadBinary(Connection conn, String sql, Object[] fields,
	    byte[] value) {
    }

    @Override
    public InputStream dbReadBinary(String sql, Object[] fields, boolean big)
	    throws ServerOverloadException {
	return null;
    }

    @Override
    public Long dbInsert(Connection callerConnection, String sql,
	    Object[] fields, String autoColumn) {
	return null;
    }

    @Override
    public Long dbInsert(Connection callerConnection, String sql,
	    Object[] fields, String autoColumn, InputStream last, int lastLength) {
	return null;
    }

    @Override
    public boolean dbWrite(String sql) {
	return false;
    }

    @Override
    public boolean dbWrite(String sql, String var) {
	return false;
    }

    @Override
    public boolean dbWriteBinary(String sql, Object[] fields, byte[] var,
	    int offset, int len) {
	return false;
    }

    @Override
    public boolean dbWrite(String sql, Object[] fields) {
	return false;
    }

    @Override
    public boolean dbWrite(Connection connection, String sql, Object[] fields) {
	return false;
    }

    @Override
    public boolean dbWriteFailQuiet(Connection connection, String sql,
	    Object[] fields) {
	return false;
    }

    @Override
    public boolean dbWrite(String sql, Object[] fields, String lastField) {
	return false;
    }

    @Override
    public void dbReadBlobAndUpdate(String sql, byte[] content) {
    }

    @Override
    public Connection dbReadLock(String sql, StringBuilder field) {
	return null;
    }

    @Override
    public void dbUpdateCommit(String sql, Object[] fields, String var,
	    Connection conn) {
    }

    @Override
    public void dbCancel(Connection conn) {
    }

    @Override
    public GregorianCalendar getCal() {
	return null;
    }

    @Override
    public String getVendor() {
	return vendor;
    }

    @Override
    public void ddl(ClassLoader loader, String resource) {
    }

    @Override
    public Long getNextSequence(String tableName, Connection conn) {
	return null;
    }

    @Override
    public String getBooleanConstant(boolean value) {
	return null;
    }

    @Override
    public Connection dbReadLock(String sql, SqlReader reader) {
	return null;
    }
    
    

}

