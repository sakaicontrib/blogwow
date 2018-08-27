/******************************************************************************
 * BlogLogicImpl.java - created by Sakai App Builder -AZ
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

import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.genericdao.api.search.Order;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;

import lombok.extern.slf4j.Slf4j;

/**
 * This is the implementation of the blog business logic interface
 * 
 * @author Sakai App Builder -AZ
 */
@Slf4j
public class BlogLogicImpl implements BlogLogic {


    private ExternalLogic externalLogic;
    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

    private BlogWowDao dao;
    public void setDao(BlogWowDao dao) {
        this.dao = dao;
    }

    /**
     * Place any code that should run when this class is initialized by spring here
     */
    public void init() {
        log.debug("init");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.blogwow.logic.BlogLogic#getBlogByLocationAndUser(java.lang.String, java.lang.String)
     */
    public BlogWowBlog makeBlogByLocationAndUser(String locationId, String userId) {
        List<BlogWowBlog> l = dao.findBySearch(BlogWowBlog.class, new Search(
                new Restriction[] {
                        new Restriction("location", locationId),
                        new Restriction("ownerId", userId)
                }
        ) );

        BlogWowBlog blog = null;
        if (l.size() <= 0) {
            // no blog found, create a new one
            if (canWriteBlog(null, locationId, userId)) {
                blog = new BlogWowBlog(userId, locationId, null, null, null, new Date(), null);
                saveUserBlog(blog, locationId, userId);
                externalLogic.registerEntityEvent("blog.blog.created", BlogWowBlog.class, blog.getId());
            }
        } else if (l.size() == 1) {
            // found existing blog
            blog = (BlogWowBlog) l.get(0);
            
            // check to see if user display name has changed
            String currentTitle = blog.getTitle();
            String currentUserDisplayName = externalLogic.getUserDisplayName(userId);
            
            if (currentTitle != null && currentUserDisplayName != null && !currentTitle.equals(currentUserDisplayName)) {
            	blog.setTitle(currentUserDisplayName);
            	saveUserBlog(blog, locationId, userId);
            	externalLogic.registerEntityEvent("blog.blog.updated", BlogWowBlog.class, blog.getId());
            }
        } else {
            throw new IllegalStateException("Found more than one blog for user (" + userId + ") in location (" + locationId
                    + "), only one is allowed");
        }
        return blog;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.blogwow.logic.BlogLogic#getAllVisibleBlogs(java.lang.String, java.lang.String, boolean, int, int)
     */
    public List<BlogWowBlog> getAllVisibleBlogs(String locationId, String sortProperty, boolean ascending, int start, int limit) {
        Order order = new Order(sortProperty, ascending);
        if (sortProperty == null) {
            order = new Order("title", true);
        }

        Search search = new Search();
        search.addOrder(order);
        search.setStart(start);
        search.setLimit(limit);
        if (locationId != null) {
            search.addRestriction( new Restriction("location", locationId) );
        }
        List<BlogWowBlog> l = dao.findBySearch(BlogWowBlog.class, search);
        return l;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.blogwow.logic.BlogLogic#getBlogById(java.lang.Long)
     */
    public BlogWowBlog getBlogById(String blogId) {
        return (BlogWowBlog) dao.findById(BlogWowBlog.class, blogId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.blogwow.logic.BlogLogic#saveBlog(org.sakaiproject.blogwow.model.BlogWowBlog, java.lang.String)
     */
    public void saveBlog(BlogWowBlog blog, String locationId) {
        if (blog == null) {
            throw new IllegalArgumentException("Cannot save null blog");
        }
        // set the owner to current if not set
        String currentUserId = externalLogic.getCurrentUserId();
        // this check depends that the id of a non-persistent blog is null
        if (canWriteBlog(blog, locationId, currentUserId)) {
            saveUserBlog(blog, locationId, currentUserId);
            log.info("Saved blog: " + blog.getId() + ":" + blog.getProfile());
            externalLogic.registerEntityEvent("blog.blog.saved", BlogWowBlog.class, blog.getId());
        } else {
            throw new SecurityException("Current user cannot save blog " + blog.getId() + ":" + blog.getTitle()
                    + " because they do not have permission");
        }
    }


    public boolean canWriteBlog(BlogWowBlog blog, String locationId, String userId) {
        boolean allowed = false;
        if (externalLogic.isUserAdmin(userId)) {
            allowed = true;
        } else {
            if (blog != null) {
                if (locationId.equals(blog.getLocation()) && blog.getOwnerId().equals(userId)) {
                    // the location must match with the one in the blog, and the user must also
                    if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_CREATE, locationId)) {
                        // finally the user must have permission in the location
                        allowed = true;
                    }
                }
            } else {
                // no blog to check
                if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_CREATE, locationId)) {
                    // the user must have permission in the location
                    allowed = true;
                }
            }
        }
        return allowed;
    }

    /**
     * Does not do security checks
     * @param blog
     * @param locationId
     * @param userId
     */
    private void saveUserBlog(BlogWowBlog blog, String locationId, String userId) {
        if (blog.getOwnerId() == null) {
            blog.setOwnerId(userId);
        }
        if (blog.getDateCreated() == null) {
            blog.setDateCreated(new Date());
        }
        if (blog.getTitle() == null || "".equals(blog.getTitle())) {
            blog.setTitle( externalLogic.getUserDisplayName(userId) );
        }
        if (blog.getProfile() != null && blog.getProfile().length() > 0) {
            // clean up the blog profile
            blog.setProfile( externalLogic.cleanupUserStrings(blog.getProfile()) );            
        }
        dao.save(blog);
    }

}
