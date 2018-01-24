/******************************************************************************
 * BlogWowLogicImplTest.java - created by Sakai App Builder -AZ
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
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.stubs.ExternalLogicStub;
import org.sakaiproject.blogwow.logic.test.TestDataPreload;
import org.sakaiproject.blogwow.model.BlogWowBlog;
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
public class BlogLogicImplTest extends AbstractTransactionalJUnit4SpringContextTests {
    

    protected BlogLogicImpl logicImpl;

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
        	logicImpl = new BlogLogicImpl();
        	logicImpl.setDao(dao);
        	logicImpl.setExternalLogic(logicStub); // use the stub for testing

        	// preload the DB for testing
        	tdp.preloadTestData(dao);
        }
    }

    /**
     * add some tests
     */

    /**
     * Test method for
     * {@link org.sakaiproject.blogwow.logic.impl.BlogLogicImpl#canWriteBlog(org.sakaiproject.blogwow.model.BlogWowBlog, java.lang.String, java.lang.String)}.
     */
    @Test
    public void testCanWriteBlog() {
        Assert.assertTrue(logicImpl.canWriteBlog(tdp.blog1, TestDataPreload.LOCATION1_ID, TestDataPreload.USER_ID));
        Assert.assertTrue(logicImpl.canWriteBlog(tdp.blog2, TestDataPreload.LOCATION1_ID, TestDataPreload.MAINT_USER_ID));
        Assert.assertTrue(logicImpl.canWriteBlog(tdp.blog3, TestDataPreload.LOCATION2_ID, TestDataPreload.ADMIN_USER_ID));

        // make sure we cannot write in other sites
        Assert.assertFalse(logicImpl.canWriteBlog(tdp.blog1, TestDataPreload.LOCATION2_ID, TestDataPreload.USER_ID));
        Assert.assertFalse(logicImpl.canWriteBlog(tdp.blog2, TestDataPreload.LOCATION2_ID, TestDataPreload.MAINT_USER_ID));

        // make sure we cannot write other blogs
        Assert.assertFalse(logicImpl.canWriteBlog(tdp.blog2, TestDataPreload.LOCATION1_ID, TestDataPreload.USER_ID));
        Assert.assertFalse(logicImpl.canWriteBlog(tdp.blog3, TestDataPreload.LOCATION2_ID, TestDataPreload.MAINT_USER_ID));

        // make sure admin can write all of them
        Assert.assertTrue(logicImpl.canWriteBlog(tdp.blog1, TestDataPreload.LOCATION1_ID, TestDataPreload.ADMIN_USER_ID));
        Assert.assertTrue(logicImpl.canWriteBlog(tdp.blog2, TestDataPreload.LOCATION1_ID, TestDataPreload.ADMIN_USER_ID));
        Assert.assertTrue(logicImpl.canWriteBlog(tdp.blog3, TestDataPreload.LOCATION2_ID, TestDataPreload.ADMIN_USER_ID));
    }

    /**
     * Test method for
     * {@link org.sakaiproject.blogwow.logic.impl.BlogLogicImpl#makeBlogByLocationAndUser(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testGetBlogByLocationAndUser() {
        BlogWowBlog blog = null;

        // test getting valid items by id
        blog = logicImpl.makeBlogByLocationAndUser(TestDataPreload.LOCATION1_ID, TestDataPreload.USER_ID);
        Assert.assertNotNull(blog);
        Assert.assertEquals(tdp.blog1, blog);

        blog = logicImpl.makeBlogByLocationAndUser(TestDataPreload.LOCATION1_ID, TestDataPreload.MAINT_USER_ID);
        Assert.assertNotNull(blog);
        Assert.assertEquals(tdp.blog2, blog);

        // test creating a new blog
        blog = logicImpl.makeBlogByLocationAndUser(TestDataPreload.LOCATION1_ID, TestDataPreload.ADMIN_USER_ID);
        Assert.assertNotNull(blog);
        Assert.assertNotNull(blog.getId());
        Assert.assertEquals(TestDataPreload.LOCATION1_ID, blog.getLocation());
        Assert.assertEquals(TestDataPreload.ADMIN_USER_ID, blog.getOwnerId());

        // test cannot create a new blog in location for user
        blog = logicImpl.makeBlogByLocationAndUser(TestDataPreload.LOCATION2_ID, TestDataPreload.USER_ID);
        Assert.assertNull(blog);

        // test fails to return invalid user or location blog
        // TODO - make this part work -AZ
        // blog = logicImpl.getBlogByLocationAndUser(TestDataPreload.INVALID_LOCATION_ID, TestDataPreload.ADMIN_USER_ID);
        // Assert.assertNull(blog);
        //
        // blog = logicImpl.getBlogByLocationAndUser(TestDataPreload.LOCATION2_ID, TestDataPreload.INVALID_USER_ID);
        // Assert.assertNull(blog);

    }

    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.BlogLogicImpl#getAllVisibleBlogs(java.lang.String)}.
     */
    @Test
    public void testGetAllVisibleBlogs() {
        List<BlogWowBlog> l = null;
        
        l = logicImpl.getAllVisibleBlogs(TestDataPreload.LOCATION1_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(2, l.size());
        Assert.assertTrue(l.contains(tdp.blog1));
        Assert.assertTrue(l.contains(tdp.blog2));

        l = logicImpl.getAllVisibleBlogs(TestDataPreload.LOCATION2_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(1, l.size());
        Assert.assertTrue(l.contains(tdp.blog3));

        // test limiting the results
        l = logicImpl.getAllVisibleBlogs(TestDataPreload.LOCATION1_ID, "title", true, 0, 1);
        Assert.assertNotNull(l);
        Assert.assertEquals(1, l.size());
        Assert.assertTrue(l.contains(tdp.blog1));

        l = logicImpl.getAllVisibleBlogs(TestDataPreload.LOCATION1_ID, "title", false, 0, 1);
        Assert.assertNotNull(l);
        Assert.assertEquals(1, l.size());
        Assert.assertTrue(l.contains(tdp.blog2));

        l = logicImpl.getAllVisibleBlogs(TestDataPreload.INVALID_LOCATION_ID, null, false, 0, 0);
        Assert.assertNotNull(l);
        Assert.assertEquals(0, l.size());
    }

    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.BlogLogicImpl#getBlogById(java.lang.Long)}.
     */
    @Test
    public void testGetBlogById() {
        BlogWowBlog blog = null;

        // test getting valid items by id
        blog = logicImpl.getBlogById(tdp.blog1.getId());
        Assert.assertNotNull(blog);
        Assert.assertEquals(tdp.blog1, blog);

        // test get eval by invalid id returns null
        blog = logicImpl.getBlogById("Thingummy");
        Assert.assertNull(blog);
    }

    /**
     * Test method for {@link org.sakaiproject.blogwow.logic.impl.BlogLogicImpl#saveBlog(org.sakaiproject.blogwow.model.BlogWowBlog)}.
     */
    @Test
    public void testSaveBlog() {
        BlogWowBlog blog = new BlogWowBlog(TestDataPreload.USER_ID, TestDataPreload.LOCATION1_ID, "blog");
        logicImpl.saveBlog(blog, TestDataPreload.LOCATION1_ID);
        Assert.assertNotNull(blog.getId());

        // test update
        tdp.blog1.setTitle("my blog");
        tdp.blog1.setProfile("Changed");
        logicImpl.saveBlog(tdp.blog1, TestDataPreload.LOCATION1_ID);

        // test that the perms cause failure when invalid owner
        try {
            logicImpl.saveBlog(tdp.blog1, TestDataPreload.LOCATION2_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

        try {
            logicImpl.saveBlog(tdp.blog2, TestDataPreload.LOCATION1_ID);
            Assert.fail("Should have thrown exception");
        } catch (SecurityException e) {
            Assert.assertNotNull(e);
        }

    }

}
