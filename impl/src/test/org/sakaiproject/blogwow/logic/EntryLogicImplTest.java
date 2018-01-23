/******************************************************************************
 * EntryLogicImplTest.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.stubs.ExternalLogicStub;
import org.sakaiproject.blogwow.logic.test.TestDataPreload;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import lombok.extern.slf4j.Slf4j;

/**
 * Testing the Logic implementation methods
 * 
 * @author Sakai App Builder -AZ
 */
@DirtiesContext
@ContextConfiguration(locations={
		"/hibernate-test.xml", "/spring-hibernate.xml"})	
@Slf4j
public class EntryLogicImplTest extends AbstractTransactionalJUnit4SpringContextTests {

    
    protected EntryLogicImpl logicImpl;

    private TestDataPreload tdp = new TestDataPreload();

    private ExternalLogicStub logicStub = new ExternalLogicStub();


    // run this before each test starts
    @Before
    public void onSetUpBeforeTransaction() throws Exception {
        // load the spring created dao class bean from the Spring Application Context
        BlogWowDao dao = (BlogWowDao) applicationContext.getBean("org.sakaiproject.blogwow.dao.BlogWowDao");
        if (dao == null) {
            log.error("onSetUpInTransaction: DAO could not be retrieved from spring context");
        } else {

            // init the class if needed

            // create and setup the object to be tested
            logicImpl = new EntryLogicImpl();
            logicImpl.setDao(dao);
            logicImpl.setExternalLogic(logicStub); // use the stub for testing

            // preload the DB for testing
            tdp.preloadTestData(dao);
        }
    }

    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.EntryLogicImpl#canWriteEntry(java.lang.Long, java.lang.String)}.
     */
    @Test
    public void testCanWriteEntry() {
    	Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry1_b1.getId(), TestDataPreload.USER_ID));
    	Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry2_b1.getId(), TestDataPreload.USER_ID));
    	Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry3_b1.getId(), TestDataPreload.USER_ID));
    	Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry4_b1.getId(), TestDataPreload.USER_ID));
    	Assert.assertFalse(logicImpl.canWriteEntry(tdp.entry5_b2.getId(), TestDataPreload.USER_ID));
    	Assert.assertFalse(logicImpl.canWriteEntry(tdp.entry6_b2.getId(), TestDataPreload.USER_ID));
    	Assert.assertFalse(logicImpl.canWriteEntry(tdp.entry7_b3.getId(), TestDataPreload.USER_ID));
    	
    	Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry1_b1.getId(), TestDataPreload.MAINT_USER_ID));
        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry2_b1.getId(), TestDataPreload.MAINT_USER_ID));
        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry3_b1.getId(), TestDataPreload.MAINT_USER_ID));
        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry4_b1.getId(), TestDataPreload.MAINT_USER_ID));
        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry5_b2.getId(), TestDataPreload.MAINT_USER_ID));
        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry6_b2.getId(), TestDataPreload.MAINT_USER_ID));
        Assert.assertFalse(logicImpl.canWriteEntry(tdp.entry7_b3.getId(), TestDataPreload.MAINT_USER_ID));

        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry1_b1.getId(), TestDataPreload.ADMIN_USER_ID));
        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry2_b1.getId(), TestDataPreload.ADMIN_USER_ID));
        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry3_b1.getId(), TestDataPreload.ADMIN_USER_ID));
        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry4_b1.getId(), TestDataPreload.ADMIN_USER_ID));
        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry5_b2.getId(), TestDataPreload.ADMIN_USER_ID));
        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry6_b2.getId(), TestDataPreload.ADMIN_USER_ID));
        Assert.assertTrue(logicImpl.canWriteEntry(tdp.entry7_b3.getId(), TestDataPreload.ADMIN_USER_ID));

        Assert.assertFalse(logicImpl.canWriteEntry(tdp.entry1_b1.getId(), TestDataPreload.INVALID_USER_ID));
        Assert.assertFalse(logicImpl.canWriteEntry(tdp.entry2_b1.getId(), TestDataPreload.INVALID_USER_ID));
        Assert.assertFalse(logicImpl.canWriteEntry(tdp.entry3_b1.getId(), TestDataPreload.INVALID_USER_ID));
        Assert.assertFalse(logicImpl.canWriteEntry(tdp.entry4_b1.getId(), TestDataPreload.INVALID_USER_ID));
        Assert.assertFalse(logicImpl.canWriteEntry(tdp.entry5_b2.getId(), TestDataPreload.INVALID_USER_ID));
        Assert.assertFalse(logicImpl.canWriteEntry(tdp.entry6_b2.getId(), TestDataPreload.INVALID_USER_ID));
        Assert.assertFalse(logicImpl.canWriteEntry(tdp.entry7_b3.getId(), TestDataPreload.INVALID_USER_ID));
    }

    /**
     * Test method for
     * {@link org.sakaiproject.blogwow.logic.impl.EntryLogicImpl#getAllVisibleEntries(java.lang.Long, java.lang.String, java.lang.String, boolean, int, int)}.
     */
    @Test
    public void testGetAllVisibleEntriesLongStringStringBooleanIntInt() {
        List<BlogWowEntry> l = null;

        // get all entries
        l = logicImpl.getAllVisibleEntries(tdp.blog1.getId(), TestDataPreload.ADMIN_USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(4, l.size());
        Assert.assertTrue(l.contains(tdp.entry1_b1));
        Assert.assertTrue(l.contains(tdp.entry2_b1));
        Assert.assertTrue(l.contains(tdp.entry3_b1));
        Assert.assertTrue(l.contains(tdp.entry4_b1));
        Assert.assertTrue(!l.contains(tdp.entry5_b2));
        Assert.assertTrue(!l.contains(tdp.entry6_b2));
        Assert.assertTrue(!l.contains(tdp.entry7_b3));

        l = logicImpl.getAllVisibleEntries(tdp.blog2.getId(), TestDataPreload.ADMIN_USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(2, l.size());
        Assert.assertTrue(!l.contains(tdp.entry1_b1));
        Assert.assertTrue(!l.contains(tdp.entry2_b1));
        Assert.assertTrue(!l.contains(tdp.entry3_b1));
        Assert.assertTrue(!l.contains(tdp.entry4_b1));
        Assert.assertTrue(l.contains(tdp.entry5_b2));
        Assert.assertTrue(l.contains(tdp.entry6_b2));
        Assert.assertTrue(!l.contains(tdp.entry7_b3));

        l = logicImpl.getAllVisibleEntries(tdp.blog3.getId(), TestDataPreload.ADMIN_USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(1, l.size());
        Assert.assertTrue(!l.contains(tdp.entry1_b1));
        Assert.assertTrue(!l.contains(tdp.entry2_b1));
        Assert.assertTrue(!l.contains(tdp.entry3_b1));
        Assert.assertTrue(!l.contains(tdp.entry4_b1));
        Assert.assertTrue(!l.contains(tdp.entry5_b2));
        Assert.assertTrue(!l.contains(tdp.entry6_b2));
        Assert.assertTrue(l.contains(tdp.entry7_b3));

        // get all entries for user
        l = logicImpl.getAllVisibleEntries(tdp.blog1.getId(), TestDataPreload.USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(4, l.size());
        Assert.assertTrue(l.contains(tdp.entry1_b1));
        Assert.assertTrue(l.contains(tdp.entry2_b1));
        Assert.assertTrue(l.contains(tdp.entry3_b1));
        Assert.assertTrue(l.contains(tdp.entry4_b1));

        l = logicImpl.getAllVisibleEntries(tdp.blog2.getId(), TestDataPreload.USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(1, l.size());
        Assert.assertTrue(l.contains(tdp.entry5_b2));
        Assert.assertTrue(!l.contains(tdp.entry6_b2));

        l = logicImpl.getAllVisibleEntries(tdp.blog1.getId(), TestDataPreload.MAINT_USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(3, l.size());
        Assert.assertTrue(l.contains(tdp.entry1_b1));
        Assert.assertTrue(l.contains(tdp.entry2_b1));
        Assert.assertTrue(l.contains(tdp.entry3_b1));
        Assert.assertTrue(!l.contains(tdp.entry4_b1));

        l = logicImpl.getAllVisibleEntries(tdp.blog2.getId(), TestDataPreload.MAINT_USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(2, l.size());
        Assert.assertTrue(l.contains(tdp.entry5_b2));
        Assert.assertTrue(l.contains(tdp.entry6_b2));

        // get all entries for anonymous
        l = logicImpl.getAllVisibleEntries(tdp.blog1.getId(), TestDataPreload.INVALID_USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(1, l.size());
        Assert.assertTrue(l.contains(tdp.entry1_b1));
        Assert.assertTrue(!l.contains(tdp.entry2_b1));
        Assert.assertTrue(!l.contains(tdp.entry3_b1));
        Assert.assertTrue(!l.contains(tdp.entry4_b1));

    }

    /**
     * Test method for
     * {@link org.sakaiproject.blogwow.logic.impl.EntryLogicImpl#getAllVisibleEntries(java.lang.Long[], java.lang.String, java.lang.String, boolean, int, int)}.
     */
    @Test
    public void testGetAllVisibleEntriesLongArrayStringStringBooleanIntInt() {
        List<BlogWowEntry> l = null;

        // get all entries
        l = logicImpl.getAllVisibleEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() },
                TestDataPreload.ADMIN_USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(7, l.size());
        Assert.assertTrue(l.contains(tdp.entry1_b1));
        Assert.assertTrue(l.contains(tdp.entry2_b1));
        Assert.assertTrue(l.contains(tdp.entry3_b1));
        Assert.assertTrue(l.contains(tdp.entry4_b1));
        Assert.assertTrue(l.contains(tdp.entry5_b2));
        Assert.assertTrue(l.contains(tdp.entry6_b2));
        Assert.assertTrue(l.contains(tdp.entry7_b3));

        l = logicImpl.getAllVisibleEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() },
                TestDataPreload.MAINT_USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(5, l.size());
        Assert.assertTrue(l.contains(tdp.entry1_b1));
        Assert.assertTrue(l.contains(tdp.entry2_b1));
        Assert.assertTrue(l.contains(tdp.entry3_b1));
        Assert.assertTrue(!l.contains(tdp.entry4_b1));
        Assert.assertTrue(l.contains(tdp.entry5_b2));
        Assert.assertTrue(l.contains(tdp.entry6_b2));
        Assert.assertTrue(!l.contains(tdp.entry7_b3));

        l = logicImpl.getAllVisibleEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() }, TestDataPreload.USER_ID,
                null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(5, l.size());
        Assert.assertTrue(l.contains(tdp.entry1_b1));
        Assert.assertTrue(l.contains(tdp.entry2_b1));
        Assert.assertTrue(l.contains(tdp.entry3_b1));
        Assert.assertTrue(l.contains(tdp.entry4_b1));
        Assert.assertTrue(l.contains(tdp.entry5_b2));
        Assert.assertTrue(!l.contains(tdp.entry6_b2));
        Assert.assertTrue(!l.contains(tdp.entry7_b3));

        l = logicImpl.getAllVisibleEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() },
                TestDataPreload.ACCESS_USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(3, l.size());
        Assert.assertTrue(l.contains(tdp.entry1_b1));
        Assert.assertTrue(l.contains(tdp.entry2_b1));
        Assert.assertTrue(!l.contains(tdp.entry3_b1));
        Assert.assertTrue(!l.contains(tdp.entry4_b1));
        Assert.assertTrue(l.contains(tdp.entry5_b2));
        Assert.assertTrue(!l.contains(tdp.entry6_b2));
        Assert.assertTrue(!l.contains(tdp.entry7_b3));

        l = logicImpl.getAllVisibleEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() },
                TestDataPreload.INVALID_USER_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(2, l.size());
        Assert.assertTrue(l.contains(tdp.entry1_b1));
        Assert.assertTrue(!l.contains(tdp.entry2_b1));
        Assert.assertTrue(!l.contains(tdp.entry3_b1));
        Assert.assertTrue(!l.contains(tdp.entry4_b1));
        Assert.assertTrue(l.contains(tdp.entry5_b2));
        Assert.assertTrue(!l.contains(tdp.entry6_b2));
        Assert.assertTrue(!l.contains(tdp.entry7_b3));

    }

    /**
     * Test method for
     * {@link org.sakaiproject.blogwow.logic.impl.EntryLogicImpl#getAllVisibleEntries(java.lang.Long, java.lang.String, java.lang.String, boolean, int, int)}.
     */
    @Test
    public void testGetAllVisibleEntriesCreatedSinceLongStringStringBooleanDateInt() {
        List<BlogWowEntry> l = null;

        // get all entries
        l = logicImpl.getVisibleEntriesCreatedSince(tdp.blog1.getId(), TestDataPreload.ADMIN_USER_ID, null, false, new Date(1230786000000L), 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(3, l.size());
        Assert.assertTrue(!l.contains(tdp.entry1_b1));
        Assert.assertTrue(l.contains(tdp.entry2_b1));
        Assert.assertTrue(l.contains(tdp.entry3_b1));
        Assert.assertTrue(l.contains(tdp.entry4_b1));
        Assert.assertTrue(!l.contains(tdp.entry5_b2));
        Assert.assertTrue(!l.contains(tdp.entry6_b2));
        Assert.assertTrue(!l.contains(tdp.entry7_b3));

        l = logicImpl.getVisibleEntriesCreatedSince(tdp.blog2.getId(), TestDataPreload.ADMIN_USER_ID, null, false, new Date(1230786000000L), 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(2, l.size());
        Assert.assertTrue(!l.contains(tdp.entry1_b1));
        Assert.assertTrue(!l.contains(tdp.entry2_b1));
        Assert.assertTrue(!l.contains(tdp.entry3_b1));
        Assert.assertTrue(!l.contains(tdp.entry4_b1));
        Assert.assertTrue(l.contains(tdp.entry5_b2));
        Assert.assertTrue(l.contains(tdp.entry6_b2));
        Assert.assertTrue(!l.contains(tdp.entry7_b3));

        l = logicImpl.getVisibleEntriesCreatedSince(tdp.blog3.getId(), TestDataPreload.ADMIN_USER_ID, null, false, new Date(1230786000000L), 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(1, l.size());
        Assert.assertTrue(!l.contains(tdp.entry1_b1));
        Assert.assertTrue(!l.contains(tdp.entry2_b1));
        Assert.assertTrue(!l.contains(tdp.entry3_b1));
        Assert.assertTrue(!l.contains(tdp.entry4_b1));
        Assert.assertTrue(!l.contains(tdp.entry5_b2));
        Assert.assertTrue(!l.contains(tdp.entry6_b2));
        Assert.assertTrue(l.contains(tdp.entry7_b3));

        // get all entries for user
        l = logicImpl.getVisibleEntriesCreatedSince(tdp.blog1.getId(), TestDataPreload.USER_ID, null, false, new Date(1230786000000L), 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(3, l.size());
        Assert.assertTrue(!l.contains(tdp.entry1_b1));
        Assert.assertTrue(l.contains(tdp.entry2_b1));
        Assert.assertTrue(l.contains(tdp.entry3_b1));
        Assert.assertTrue(l.contains(tdp.entry4_b1));

        l = logicImpl.getVisibleEntriesCreatedSince(tdp.blog2.getId(), TestDataPreload.USER_ID, null, false, new Date(1230786000000L), 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(1, l.size());
        Assert.assertTrue(l.contains(tdp.entry5_b2));
        Assert.assertTrue(!l.contains(tdp.entry6_b2));

        l = logicImpl.getVisibleEntriesCreatedSince(tdp.blog1.getId(), TestDataPreload.MAINT_USER_ID, null, false, new Date(1230786000000L), 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(2, l.size());
        Assert.assertTrue(!l.contains(tdp.entry1_b1));
        Assert.assertTrue(l.contains(tdp.entry2_b1));
        Assert.assertTrue(l.contains(tdp.entry3_b1));
        Assert.assertTrue(!l.contains(tdp.entry4_b1));

        l = logicImpl.getVisibleEntriesCreatedSince(tdp.blog2.getId(), TestDataPreload.MAINT_USER_ID, null, false, new Date(1230786000000L), 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(2, l.size());
        Assert.assertTrue(l.contains(tdp.entry5_b2));
        Assert.assertTrue(l.contains(tdp.entry6_b2));

        // get all entries for anonymous
        l = logicImpl.getVisibleEntriesCreatedSince(tdp.blog1.getId(), TestDataPreload.INVALID_USER_ID, null, false, new Date(1230786000000L), 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(0, l.size());
        Assert.assertTrue(!l.contains(tdp.entry1_b1));
        Assert.assertTrue(!l.contains(tdp.entry2_b1));
        Assert.assertTrue(!l.contains(tdp.entry3_b1));
        Assert.assertTrue(!l.contains(tdp.entry4_b1));

    }
    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.getVisibleEntryCount(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testGetVisibleEntryCount() {
        Integer count = null;

        count = logicImpl.getVisibleEntryCount(tdp.blog1.getId(), TestDataPreload.ADMIN_USER_ID);
        Assert.assertNotNull(count);
        Assert.assertEquals(4, count.intValue());

        count = logicImpl.getVisibleEntryCount(tdp.blog2.getId(), TestDataPreload.ADMIN_USER_ID);
        Assert.assertNotNull(count);
        Assert.assertEquals(2, count.intValue());

        count = logicImpl.getVisibleEntryCount(tdp.blog3.getId(), TestDataPreload.ADMIN_USER_ID);
        Assert.assertNotNull(count);
        Assert.assertEquals(1, count.intValue());

        count = logicImpl.getVisibleEntryCount(tdp.blog1.getId(), TestDataPreload.USER_ID);
        Assert.assertNotNull(count);
        Assert.assertEquals(4, count.intValue());

        count = logicImpl.getVisibleEntryCount(tdp.blog2.getId(), TestDataPreload.USER_ID);
        Assert.assertNotNull(count);
        Assert.assertEquals(1, count.intValue());

        count = logicImpl.getVisibleEntryCount(tdp.blog1.getId(), TestDataPreload.MAINT_USER_ID);
        Assert.assertNotNull(count);
        Assert.assertEquals(3, count.intValue());

        count = logicImpl.getVisibleEntryCount(tdp.blog2.getId(), TestDataPreload.MAINT_USER_ID);
        Assert.assertNotNull(count);
        Assert.assertEquals(2, count.intValue());

        count = logicImpl.getVisibleEntryCount(tdp.blog1.getId(), TestDataPreload.INVALID_USER_ID);
        Assert.assertNotNull(count);
        Assert.assertEquals(1, count.intValue());
    }

    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.EntryLogicImpl#getEntryById(java.lang.Long)}.
     */
    @Test
    public void testGetEntryById() {
        BlogWowEntry entry = null;

        // test getting valid entry by id
        entry = logicImpl.getEntryById(tdp.entry1_b1.getId(), TestDataPreload.LOCATION1_ID);
        Assert.assertNotNull(entry);
        Assert.assertEquals(tdp.entry1_b1, entry);

        // test get entry from anywhere works if owner
        entry = logicImpl.getEntryById(tdp.entry2_b1.getId(), TestDataPreload.LOCATION2_ID);
        Assert.assertNotNull(entry);
        Assert.assertEquals(tdp.entry2_b1, entry);

        // test get entry by invalid id returns null
        entry = logicImpl.getEntryById("thingummy", TestDataPreload.LOCATION1_ID);
        Assert.assertNull(entry);

        // test get entry with perms fails (USER_ID)
        try {
            entry = logicImpl.getEntryById(tdp.entry6_b2.getId(), TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

        // test get entry from wrong location fails
        try {
            entry = logicImpl.getEntryById(tdp.entry6_b2.getId(), TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

        // change current user
        logicStub.currentUserId = TestDataPreload.ACCESS_USER_ID;

        // test get entry in group
        entry = logicImpl.getEntryById(tdp.entry2_b1.getId(), TestDataPreload.LOCATION1_ID);
        Assert.assertNotNull(entry);
        Assert.assertEquals(tdp.entry2_b1, entry);

        // test get entry out of group fails
        try {
            entry = logicImpl.getEntryById(tdp.entry2_b1.getId(), TestDataPreload.LOCATION2_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

        // test get private entry fails
        try {
            entry = logicImpl.getEntryById(tdp.entry3_b1.getId(), TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

    }

    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.EntryLogicImpl#removeEntry(java.lang.Long)}.
     */
    @Test
    public void testRemoveEntry() {
        BlogWowEntry entry = null;

        // change current user
        logicStub.currentUserId = TestDataPreload.ACCESS_USER_ID;

        // try to remove stuff you cannot remove
        try {
            logicImpl.removeEntry(tdp.entry1_b1.getId(), TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

        // revert current user
        logicStub.setDefaults();

        logicImpl.removeEntry(tdp.entry1_b1.getId(), TestDataPreload.LOCATION1_ID);
        entry = logicImpl.getEntryById(tdp.entry1_b1.getId(), TestDataPreload.LOCATION1_ID);
        Assert.assertNull(entry);

        // try to remove stuff you cannot remove
        try {
            logicImpl.removeEntry(tdp.entry5_b2.getId(), TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

        logicStub.currentUserId = TestDataPreload.MAINT_USER_ID;

        logicImpl.removeEntry(tdp.entry5_b2.getId(), TestDataPreload.LOCATION1_ID);
        entry = logicImpl.getEntryById(tdp.entry5_b2.getId(), TestDataPreload.LOCATION1_ID);
        Assert.assertNull(entry);

        // try to remove stuff you cannot remove
        try {
            logicImpl.removeEntry(tdp.entry7_b3.getId(), TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

    }

    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.EntryLogicImpl#saveEntry(org.sakaiproject.blogwow.model.BlogWowEntry)}.
     */
    @Test
    public void testSaveEntry() {
        BlogWowEntry entry = null;

        tdp.entry1_b1.setText("new text");
        logicImpl.saveEntry(tdp.entry1_b1, TestDataPreload.LOCATION1_ID);
        entry = logicImpl.getEntryById(tdp.entry1_b1.getId(), TestDataPreload.LOCATION1_ID);
        Assert.assertNotNull(entry);
        Assert.assertEquals("new text", entry.getText());

        // try to save stuff you cannot save
        try {
            logicImpl.saveEntry(tdp.entry7_b3, TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

        // change current user
        logicStub.currentUserId = TestDataPreload.ACCESS_USER_ID;

        // try to save stuff you cannot save
        try {
            logicImpl.saveEntry(tdp.entry1_b1, TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

    }

}
