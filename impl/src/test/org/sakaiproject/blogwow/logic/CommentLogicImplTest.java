/******************************************************************************
 * CommentLogicImplTest.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic;

import java.util.List;

import org.junit.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.stubs.ExternalLogicStub;
import org.sakaiproject.blogwow.logic.test.TestDataPreload;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


/**
 * Testing the Logic implementation methods
 * 
 * @author Sakai App Builder -AZ
 */
public class CommentLogicImplTest extends AbstractJUnit4SpringContextTests {

    private static Log log = LogFactory.getLog(BlogLogicImplTest.class);

    protected CommentLogicImpl logicImpl;

    private TestDataPreload tdp = new TestDataPreload();

    private ExternalLogicStub logicStub = new ExternalLogicStub();

    protected String[] getConfigLocations() {
        // point to the needed spring config files, must be on the classpath
        // (add component/src/webapp/WEB-INF to the build path in Eclipse),
        // they also need to be referenced in the project.xml file
        return new String[] { "hibernate-test.xml", "spring-hibernate.xml" };
    }

    // run this before each test starts
    protected void onSetUpBeforeTransaction() throws Exception {
    }

    // run this before each test starts and as part of the transaction
    protected void onSetUpInTransaction() {
        // load the spring created dao class bean from the Spring Application Context
        BlogWowDao dao = (BlogWowDao) applicationContext.getBean("org.sakaiproject.blogwow.dao.BlogWowDao");
        if (dao == null) {
            log.error("onSetUpInTransaction: DAO could not be retrieved from spring context");
        } else {

        	// init the class if needed
        	EntryLogicImpl entryLogic = new EntryLogicImpl();
        	entryLogic.setExternalLogic(logicStub);
        	entryLogic.setDao(dao);

        	// create and setup the object to be tested
        	logicImpl = new CommentLogicImpl();
        	logicImpl.setDao(dao);
        	logicImpl.setExternalLogic(logicStub); // use the stub for testing
        	logicImpl.setEntryLogic(entryLogic);

        	// preload the DB for testing
        	tdp.preloadTestData(dao);
        }
    }

    /**
     * add some tests
     */

    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.CommentLogicImpl#getCommentById(java.lang.Long, java.lang.String)}.
     */
    public void testGetCommentById() {
        BlogWowComment comment = null;
        
        comment = logicImpl.getCommentById(tdp.comment1_e5_b2.getId(), TestDataPreload.LOCATION1_ID);
        Assert.assertNotNull(comment);
        Assert.assertEquals(tdp.comment1_e5_b2, comment);

        comment = logicImpl.getCommentById(tdp.comment3_e5_b2.getId(), TestDataPreload.LOCATION1_ID);
        Assert.assertNotNull(comment);
        Assert.assertEquals(tdp.comment3_e5_b2, comment);

        comment = logicImpl.getCommentById(tdp.comment2_e2_b1.getId(), TestDataPreload.LOCATION1_ID);
        Assert.assertNotNull(comment);
        Assert.assertEquals(tdp.comment2_e2_b1, comment);

        // cannot get to comments you have no access to
        try {
            comment = logicImpl.getCommentById(tdp.comment5_e7_b3.getId(), TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

        //getting a comment that doesn't exist should return null and not throw an NPE
        Assert.assertNull(logicImpl.getCommentById("no_such_id", TestDataPreload.LOCATION1_ID));
        
    }

    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.CommentLogicImpl#removeComment(java.lang.Long, java.lang.String)}.
     */
    public void testRemoveComment() {

        try {
            logicImpl.removeComment(tdp.comment2_e2_b1.getId(), TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

        try {
            logicImpl.removeComment(tdp.comment1_e5_b2.getId(), TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

        logicStub.currentUserId = TestDataPreload.ADMIN_USER_ID;

        logicImpl.removeComment(tdp.comment3_e5_b2.getId(), TestDataPreload.LOCATION1_ID);

    }

    /**
     * Test method for
     * {@link org.sakaiproject.blogwow.logic.impl.CommentLogicImpl#saveComment(org.sakaiproject.blogwow.model.BlogWowComment, java.lang.String)}.
     */
    public void testSaveComment() {

        // cannot save existing comment
        try {
            logicImpl.saveComment(tdp.comment1_e5_b2, TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (IllegalStateException e) {
            Assert.assertNotNull(e);
        }

        try {
            logicImpl.saveComment(new BlogWowComment(tdp.entry7_b3, TestDataPreload.USER_ID, "comment thing", null),
                    TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

        // commenting on your own blog
        logicImpl.saveComment(new BlogWowComment(tdp.entry1_b1, TestDataPreload.USER_ID, "comment thing", null),
                TestDataPreload.LOCATION1_ID);

        // commenting on someone elses blog
        logicImpl.saveComment(new BlogWowComment(tdp.entry1_b1, TestDataPreload.ACCESS_USER_ID, "comment thing", null),
                TestDataPreload.LOCATION1_ID);

    }

    /**
     * Test method for
     * {@link org.sakaiproject.blogwow.logic.impl.CommentLogicImpl#getComments(java.lang.Long, java.lang.String, boolean, int, int)}.
     */
    public void testGetComments() {
        List<BlogWowComment> l = null;

        l = logicImpl.getComments(tdp.entry5_b2.getId(), null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(2, l.size());
        Assert.assertTrue(l.contains(tdp.comment1_e5_b2));
        Assert.assertTrue(!l.contains(tdp.comment2_e2_b1));
        Assert.assertTrue(l.contains(tdp.comment3_e5_b2));
        Assert.assertTrue(!l.contains(tdp.comment4_e1_b1));
        Assert.assertTrue(!l.contains(tdp.comment5_e7_b3));

        l = logicImpl.getComments(tdp.entry1_b1.getId(), null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(1, l.size());
        Assert.assertTrue(!l.contains(tdp.comment1_e5_b2));
        Assert.assertTrue(!l.contains(tdp.comment2_e2_b1));
        Assert.assertTrue(!l.contains(tdp.comment3_e5_b2));
        Assert.assertTrue(l.contains(tdp.comment4_e1_b1));
        Assert.assertTrue(!l.contains(tdp.comment5_e7_b3));

        l = logicImpl.getComments(tdp.entry3_b1.getId(), null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(0, l.size());

        try {
            logicImpl.getComments(tdp.entry7_b3.getId(), null, false, 0, 0);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

    }

    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.CommentLogicImpl#canRemoveComment(java.lang.Long, java.lang.String)}.
     */
    public void testCanRemoveComment() {
    	Assert.assertFalse(logicImpl.canRemoveComment(tdp.comment1_e5_b2.getId(), TestDataPreload.USER_ID));
    	Assert.assertFalse(logicImpl.canRemoveComment(tdp.comment2_e2_b1.getId(), TestDataPreload.USER_ID));
    	Assert.assertFalse(logicImpl.canRemoveComment(tdp.comment3_e5_b2.getId(), TestDataPreload.USER_ID));
    	Assert.assertFalse(logicImpl.canRemoveComment(tdp.comment4_e1_b1.getId(), TestDataPreload.USER_ID));
    	Assert.assertFalse(logicImpl.canRemoveComment(tdp.comment5_e7_b3.getId(), TestDataPreload.USER_ID));

    	Assert.assertTrue(logicImpl.canRemoveComment(tdp.comment1_e5_b2.getId(), TestDataPreload.MAINT_USER_ID));
    	Assert.assertTrue(logicImpl.canRemoveComment(tdp.comment2_e2_b1.getId(), TestDataPreload.MAINT_USER_ID));
    	Assert.assertTrue(logicImpl.canRemoveComment(tdp.comment3_e5_b2.getId(), TestDataPreload.MAINT_USER_ID));
    	Assert.assertTrue(logicImpl.canRemoveComment(tdp.comment4_e1_b1.getId(), TestDataPreload.MAINT_USER_ID));
    	Assert.assertFalse(logicImpl.canRemoveComment(tdp.comment5_e7_b3.getId(), TestDataPreload.MAINT_USER_ID));

    	Assert.assertTrue(logicImpl.canRemoveComment(tdp.comment1_e5_b2.getId(), TestDataPreload.ADMIN_USER_ID));
    	Assert.assertTrue(logicImpl.canRemoveComment(tdp.comment2_e2_b1.getId(), TestDataPreload.ADMIN_USER_ID));
    	Assert.assertTrue(logicImpl.canRemoveComment(tdp.comment3_e5_b2.getId(), TestDataPreload.ADMIN_USER_ID));
    	Assert.assertTrue(logicImpl.canRemoveComment(tdp.comment4_e1_b1.getId(), TestDataPreload.ADMIN_USER_ID));
    	Assert.assertTrue(logicImpl.canRemoveComment(tdp.comment5_e7_b3.getId(), TestDataPreload.ADMIN_USER_ID));

    	Assert.assertFalse(logicImpl.canRemoveComment(tdp.comment1_e5_b2.getId(), TestDataPreload.INVALID_USER_ID));
    	Assert.assertFalse(logicImpl.canRemoveComment(tdp.comment2_e2_b1.getId(), TestDataPreload.INVALID_USER_ID));
    	Assert.assertFalse(logicImpl.canRemoveComment(tdp.comment3_e5_b2.getId(), TestDataPreload.INVALID_USER_ID));
    	Assert.assertFalse(logicImpl.canRemoveComment(tdp.comment4_e1_b1.getId(), TestDataPreload.INVALID_USER_ID));
    	Assert.assertFalse(logicImpl.canRemoveComment(tdp.comment5_e7_b3.getId(), TestDataPreload.INVALID_USER_ID));
    }

    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.CommentLogicImpl#canAddComment(java.lang.Long, java.lang.String)}.
     */
    public void testCanAddComment() {
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry1_b1.getId(), TestDataPreload.USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry2_b1.getId(), TestDataPreload.USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry3_b1.getId(), TestDataPreload.USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry4_b1.getId(), TestDataPreload.USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry5_b2.getId(), TestDataPreload.USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry6_b2.getId(), TestDataPreload.USER_ID));
    	Assert.assertFalse(logicImpl.canAddComment(tdp.entry7_b3.getId(), TestDataPreload.USER_ID));

    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry1_b1.getId(), TestDataPreload.MAINT_USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry2_b1.getId(), TestDataPreload.MAINT_USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry3_b1.getId(), TestDataPreload.MAINT_USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry4_b1.getId(), TestDataPreload.MAINT_USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry5_b2.getId(), TestDataPreload.MAINT_USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry6_b2.getId(), TestDataPreload.MAINT_USER_ID));
    	Assert.assertFalse(logicImpl.canAddComment(tdp.entry7_b3.getId(), TestDataPreload.MAINT_USER_ID));

    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry1_b1.getId(), TestDataPreload.ADMIN_USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry2_b1.getId(), TestDataPreload.ADMIN_USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry3_b1.getId(), TestDataPreload.ADMIN_USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry4_b1.getId(), TestDataPreload.ADMIN_USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry5_b2.getId(), TestDataPreload.ADMIN_USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry6_b2.getId(), TestDataPreload.ADMIN_USER_ID));
    	Assert.assertTrue(logicImpl.canAddComment(tdp.entry7_b3.getId(), TestDataPreload.ADMIN_USER_ID));

    	Assert.assertFalse(logicImpl.canAddComment(tdp.entry1_b1.getId(), TestDataPreload.INVALID_USER_ID));
    	Assert.assertFalse(logicImpl.canAddComment(tdp.entry2_b1.getId(), TestDataPreload.INVALID_USER_ID));
    	Assert.assertFalse(logicImpl.canAddComment(tdp.entry3_b1.getId(), TestDataPreload.INVALID_USER_ID));
    	Assert.assertFalse(logicImpl.canAddComment(tdp.entry4_b1.getId(), TestDataPreload.INVALID_USER_ID));
    	Assert.assertFalse(logicImpl.canAddComment(tdp.entry5_b2.getId(), TestDataPreload.INVALID_USER_ID));
    	Assert.assertFalse(logicImpl.canAddComment(tdp.entry6_b2.getId(), TestDataPreload.INVALID_USER_ID));
    	Assert.assertFalse(logicImpl.canAddComment(tdp.entry7_b3.getId(), TestDataPreload.INVALID_USER_ID));
    }

}
