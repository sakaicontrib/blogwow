/******************************************************************************
 * BlogWowDaoImplTest.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.dao;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.blogwow.logic.test.TestDataPreload;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import lombok.extern.slf4j.Slf4j;

/**
 * Testing for the specialized DAO methods (do not test the Generic Dao methods)
 * 
 * @author Sakai App Builder -AZ
 */
@DirtiesContext
@ContextConfiguration(locations={
		"/hibernate-test.xml", "/spring-hibernate.xml"})	
@Slf4j
public class BlogWowDaoImplTest extends  AbstractTransactionalJUnit4SpringContextTests{


    BlogWowDao dao;

    private TestDataPreload tdp = new TestDataPreload();

    // run this before each test starts and as part of the transaction
    @Before
    public void onSetUpBeforeTransaction() throws Exception {
        // load the spring created dao class bean from the Spring Application Context
         dao = (BlogWowDao) applicationContext.getBean("org.sakaiproject.blogwow.dao.BlogWowDao");
        if (dao == null) {
            log.error("onSetUpInTransaction: DAO could not be retrieved from spring context");
        } else {

        	// init the class if needed

        	// check the preloaded data

        	// preload data if desired
        	tdp.preloadTestData(dao);
        }
    }

    /**
     * ADD unit tests below here, use testMethod as the name of the unit test, Note that if a method is overloaded you should include the
     * arguments in the test name like so: testMethodClassInt (for method(Class, int);
     */

    /**
     * Test method for {@link org.sakaiproject.blogwow.dao.impl.BlogWowDaoImpl#getLocationsForBlogsIds(java.lang.String[])}.
     */
    @Test
    public void testGetLocationsForBlogsIds() {
        List<String> locs = null;

        locs = dao.getLocationsForBlogsIds(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() });
        Assert.assertNotNull(locs);
        Assert.assertEquals(2, locs.size());
        Assert.assertTrue(locs.contains(TestDataPreload.LOCATION1_ID));
        Assert.assertTrue(locs.contains(TestDataPreload.LOCATION2_ID));

        locs = dao.getLocationsForBlogsIds(new String[] { tdp.blog1.getId() });
        Assert.assertNotNull(locs);
        Assert.assertEquals(1, locs.size());
        Assert.assertTrue(locs.contains(TestDataPreload.LOCATION1_ID));

        locs = dao.getLocationsForBlogsIds(new String[] { tdp.blog3.getId() });
        Assert.assertNotNull(locs);
        Assert.assertEquals(1, locs.size());
        Assert.assertTrue(locs.contains(TestDataPreload.LOCATION2_ID));

        locs = dao.getLocationsForBlogsIds(new String[] {});
        Assert.assertNotNull(locs);
        Assert.assertEquals(0, locs.size());
    }

    /**
     * Test method for
     * {@link org.sakaiproject.blogwow.dao.impl.BlogWowDaoImpl#getBlogPermEntries(String[], String, String[], String[], String, boolean, int, int)}.
     */
    @Test
    public void testGetBlogPermEntries() {
        List<BlogWowEntry> entries = null;

        // get all public entries
        entries = dao.getBlogPermEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() }, null, null, null, null,
                false, 0, 0);
        Assert.assertNotNull(entries);
        Assert.assertEquals(2, entries.size());
        Assert.assertTrue( entries.get(0) instanceof BlogWowEntry );
        Assert.assertTrue(entries.contains(tdp.entry1_b1));
        Assert.assertTrue(entries.contains(tdp.entry5_b2));

        // get only blog 1 public entries
        entries = dao.getBlogPermEntries(new String[] { tdp.blog1.getId() }, null, null, null, null, false, 0, 0);
        Assert.assertNotNull(entries);
        Assert.assertEquals(1, entries.size());
        Assert.assertTrue(entries.contains(tdp.entry1_b1));

        // get all entries for user
        entries = dao.getBlogPermEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() }, TestDataPreload.USER_ID,
                new String[] { TestDataPreload.LOCATION1_ID }, null, null, false, 0, 0);
        Assert.assertNotNull(entries);
        Assert.assertEquals(5, entries.size());
        Assert.assertTrue(entries.contains(tdp.entry1_b1));
        Assert.assertTrue(entries.contains(tdp.entry2_b1));
        Assert.assertTrue(entries.contains(tdp.entry3_b1));
        Assert.assertTrue(entries.contains(tdp.entry4_b1));
        Assert.assertTrue(entries.contains(tdp.entry5_b2));

        // get all entries for maint user
        entries = dao.getBlogPermEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() },
                TestDataPreload.MAINT_USER_ID, new String[] { TestDataPreload.LOCATION1_ID },
                new String[] { TestDataPreload.LOCATION1_ID }, null, false, 0, 0);
        Assert.assertNotNull(entries);
        Assert.assertEquals(5, entries.size());
        Assert.assertTrue(entries.contains(tdp.entry1_b1));
        Assert.assertTrue(entries.contains(tdp.entry2_b1));
        Assert.assertTrue(entries.contains(tdp.entry3_b1));
        Assert.assertTrue(entries.contains(tdp.entry5_b2));
        Assert.assertTrue(entries.contains(tdp.entry6_b2));

        // get all entries for user with limits
        entries = dao.getBlogPermEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() }, TestDataPreload.USER_ID,
                new String[] { TestDataPreload.LOCATION1_ID }, null, null, false, 3, 0);
        Assert.assertNotNull(entries);
        Assert.assertEquals(2, entries.size());
        Assert.assertTrue(entries.contains(tdp.entry2_b1));
        Assert.assertTrue(entries.contains(tdp.entry1_b1));

        entries = dao.getBlogPermEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() }, TestDataPreload.USER_ID,
                new String[] { TestDataPreload.LOCATION1_ID }, null, null, false, 2, 2);
        Assert.assertNotNull(entries);
        Assert.assertEquals(2, entries.size());
        Assert.assertTrue(entries.contains(tdp.entry3_b1));
        Assert.assertTrue(entries.contains(tdp.entry2_b1));

        // get all entries for maint user with Date limit
        entries = dao.getBlogPermEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() },
                TestDataPreload.MAINT_USER_ID, new String[] { TestDataPreload.LOCATION1_ID },
                new String[] { TestDataPreload.LOCATION1_ID }, null, false, new Date(1230786000000L), 0);
        Assert.assertNotNull(entries);
        Assert.assertEquals(4, entries.size());
        Assert.assertTrue(entries.contains(tdp.entry2_b1));
        Assert.assertTrue(entries.contains(tdp.entry3_b1));
        Assert.assertTrue(entries.contains(tdp.entry5_b2));
        Assert.assertTrue(entries.contains(tdp.entry6_b2));

        // get all entries for user with Date limit
        entries = dao.getBlogPermEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() }, TestDataPreload.USER_ID,
                new String[] { TestDataPreload.LOCATION1_ID }, null, null, false, new Date(1230786000000L), 0);
        Assert.assertNotNull(entries);
        Assert.assertEquals(4, entries.size());
        Assert.assertTrue(entries.contains(tdp.entry2_b1));
        Assert.assertTrue(entries.contains(tdp.entry3_b1));
        Assert.assertTrue(entries.contains(tdp.entry4_b1));
        Assert.assertTrue(entries.contains(tdp.entry5_b2));

        entries = dao.getBlogPermEntries(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() }, TestDataPreload.USER_ID,
                new String[] { TestDataPreload.LOCATION1_ID }, null, "dateCreated", true, new Date(1230786000000L), 2);
        Assert.assertNotNull(entries);
        Assert.assertEquals(2, entries.size());
        Assert.assertTrue(entries.contains(tdp.entry2_b1));
        Assert.assertTrue(entries.contains(tdp.entry3_b1));
    }

    @Test
    public void testGetBlogPermCount()
    {
    	// count all public entries
    	int count = (dao.getBlogPermCount(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() }, null, null, null));
    	Assert.assertEquals(2, count);
    	
    	// count only blog 1 public entries
        count = dao.getBlogPermCount(new String[] { tdp.blog1.getId() }, null, null, null);
        Assert.assertEquals(1, count);
        
        // count all entries for user
        count = dao.getBlogPermCount(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() }, TestDataPreload.USER_ID,
                new String[] { TestDataPreload.LOCATION1_ID }, null);
        Assert.assertEquals(5, count);
        
        // get all entries for maint user
        count = dao.getBlogPermCount(new String[] { tdp.blog1.getId(), tdp.blog2.getId(), tdp.blog3.getId() },
                TestDataPreload.MAINT_USER_ID, new String[] { TestDataPreload.LOCATION1_ID },
                new String[] { TestDataPreload.LOCATION1_ID });
        Assert.assertEquals(5, count);
    }

    /**
     * Add anything that supports the unit tests below here
     */
}
