/**
 * Copyright (c) 2003-2020 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.blogwow.logic.test;

import java.util.Date;

import org.sakaiproject.blogwow.constants.BlogConstants;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.genericdao.api.GenericDao;

/**
 * Contains test data for preloading and test constants
 * @author Sakai App Builder -AZ
 */
public class TestDataPreload {

    /**
     * current user, access level user in LOCATION_ID1
     */
    public final static String USER_ID = "user-11111111";
    public final static String USER_DISPLAY = "Aaron Zeckoski";
    /**
     * access level user in LOCATION1_ID
     */
    public final static String ACCESS_USER_ID = "access-2222222";
    public final static String ACCESS_USER_DISPLAY = "Regular User";
    /**
     * maintain level user in LOCATION1_ID
     */
    public final static String MAINT_USER_ID = "maint-33333333";
    public final static String MAINT_USER_DISPLAY = "Maint User";
    /**
     * super admin user 
     */
    public final static String ADMIN_USER_ID = "admin";
    public final static String ADMIN_USER_DISPLAY = "Administrator";
    /**
     * Invalid user (also can be used to simulate the anonymous user) 
     */
    public final static String INVALID_USER_ID = "invalid-UUUUUU";

    /**
     * current location
     */
    public final static String LOCATION1_ID = "/site/ref-1111111";
    public final static String LOCATION1_TITLE = "Location 1 title";
    public final static String LOCATION2_ID = "/site/ref-22222222";
    public final static String LOCATION2_TITLE = "Location 2 title";
    public final static String INVALID_LOCATION_ID = "invalid-LLLLLLLL";

    // testing data objects here

    public BlogWowBlog blog1 = new BlogWowBlog(USER_ID, LOCATION1_ID, "blog1 title", "blog1 profile", null, new Date(), null);
    public BlogWowBlog blog2 = new BlogWowBlog(MAINT_USER_ID, LOCATION1_ID, "blog2 title", "blog2 profile", null, new Date(), null);
    public BlogWowBlog blog3 = new BlogWowBlog(ADMIN_USER_ID, LOCATION2_ID, "blog3 title", "blog3 profile", null, new Date(), null);

    public BlogWowEntry entry1_b1 = new BlogWowEntry(blog1, USER_ID, "entry 1", "entry text", BlogConstants.PRIVACY_PUBLIC, new Date(), new Date(1229749200000L));
    public BlogWowEntry entry2_b1 = new BlogWowEntry(blog1, USER_ID, "entry 2", "entry text", BlogConstants.PRIVACY_GROUP, new Date(), new Date(1230786000000L));
    public BlogWowEntry entry3_b1 = new BlogWowEntry(blog1, USER_ID, "entry 3", "entry text", BlogConstants.PRIVACY_GROUP_LEADER, new Date(), new Date(1231995600000L));
    public BlogWowEntry entry4_b1 = new BlogWowEntry(blog1, USER_ID, "entry 4", "entry text", BlogConstants.PRIVACY_PRIVATE, new Date(), new Date(1233378000000L));
    public BlogWowEntry entry5_b2 = new BlogWowEntry(blog2, MAINT_USER_ID, "entry 5", "entry text", BlogConstants.PRIVACY_PUBLIC, new Date(), new Date(1233378000000L));
    public BlogWowEntry entry6_b2 = new BlogWowEntry(blog2, MAINT_USER_ID, "entry 6", "entry text", BlogConstants.PRIVACY_PRIVATE, new Date(), new Date(1233464400000L));
    public BlogWowEntry entry7_b3 = new BlogWowEntry(blog3, ADMIN_USER_ID, "entry 7", "entry text", BlogConstants.PRIVACY_PRIVATE, new Date(), new Date(1234242000000L));

    public BlogWowComment comment1_e5_b2 = new BlogWowComment(entry5_b2, USER_ID, "comment 1", new Date(), new Date());
    public BlogWowComment comment2_e2_b1 = new BlogWowComment(entry2_b1, ACCESS_USER_ID, "comment 2", new Date(), new Date());
    public BlogWowComment comment3_e5_b2 = new BlogWowComment(entry5_b2, ACCESS_USER_ID, "comment 3", new Date(), new Date());
    public BlogWowComment comment4_e1_b1 = new BlogWowComment(entry1_b1, MAINT_USER_ID, "comment 4", new Date(), new Date());
    public BlogWowComment comment5_e7_b3 = new BlogWowComment(entry7_b3, ADMIN_USER_ID, "comment 5", new Date(), new Date());

    /**
     * Preload a bunch of test data into the database
     * @param dao a generic dao
     */
    public void preloadTestData(GenericDao dao) {
        dao.save(blog1);
        dao.save(blog2);
        dao.save(blog3);

        dao.save(entry1_b1);
        dao.save(entry2_b1);
        dao.save(entry3_b1);
        dao.save(entry4_b1);
        dao.save(entry5_b2);
        dao.save(entry6_b2);
        dao.save(entry7_b3);

        dao.save(comment1_e5_b2);
        dao.save(comment2_e2_b1);
        dao.save(comment3_e5_b2);
        dao.save(comment4_e1_b1);
        dao.save(comment5_e7_b3);
    }

}
