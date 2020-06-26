/******************************************************************************
 * ExternalLogicStub.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic.stubs;

import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.logic.test.TestDataPreload;

/**
 * Stub class for the external logic impl (for testing)
 * 
 * @author Sakai App Builder -AZ
 */
public class ExternalLogicStub implements ExternalLogic {

    /**
     * represents the current user userId, can be changed to simulate multiple users
     */
    public String currentUserId;

    /**
     * represents the current location, can be changed to simulate multiple locations
     */
    public String currentLocationId;

    /**
     * Reset the current user and location to defaults
     */
    public void setDefaults() {
        currentUserId = TestDataPreload.USER_ID;
        currentLocationId = TestDataPreload.LOCATION1_ID;
    }

    public ExternalLogicStub() {
        setDefaults();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.blogwow.logic.ExternalLogic#getCurrentLocationId()
     */
    public String getCurrentLocationId() {
        return currentLocationId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.blogwow.logic.ExternalLogic#getLocationTitle(java.lang.String)
     */
    public String getLocationTitle(String locationId) {
        if (locationId.equals(TestDataPreload.LOCATION1_ID)) {
            return TestDataPreload.LOCATION1_TITLE;
        } else if (locationId.equals(TestDataPreload.LOCATION2_ID)) {
            return TestDataPreload.LOCATION2_TITLE;
        }
        return "--------";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.blogwow.logic.ExternalLogic#getCurrentUserId()
     */
    public String getCurrentUserId() {
        return currentUserId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.blogwow.logic.ExternalLogic#getUserDisplayName(java.lang.String)
     */
    public String getUserDisplayName(String userId) {
        if (userId.equals(TestDataPreload.USER_ID)) {
            return TestDataPreload.USER_DISPLAY;
        } else if (userId.equals(TestDataPreload.ACCESS_USER_ID)) {
            return TestDataPreload.ACCESS_USER_DISPLAY;
        } else if (userId.equals(TestDataPreload.MAINT_USER_ID)) {
            return TestDataPreload.MAINT_USER_DISPLAY;
        } else if (userId.equals(TestDataPreload.ADMIN_USER_ID)) {
            return TestDataPreload.ADMIN_USER_DISPLAY;
        }
        return "----------";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.blogwow.logic.ExternalLogic#isUserAdmin(java.lang.String)
     */
    public boolean isUserAdmin(String userId) {
        if (userId.equals(TestDataPreload.ADMIN_USER_ID)) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.blogwow.logic.ExternalLogic#isUserAllowedInLocation(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean isUserAllowedInLocation(String userId, String permission, String locationId) {
        if (userId.equals(TestDataPreload.USER_ID)) {
            if (locationId.equals(TestDataPreload.LOCATION1_ID)) {
                if (permission.equals(BLOG_CREATE) || permission.equals(BLOG_ENTRY_WRITE) || permission.equals(BLOG_ENTRY_READ)
                        || permission.equals(BLOG_COMMENTS_ADD)) {
                    return true;
                }
            }
        } else if (userId.equals(TestDataPreload.ACCESS_USER_ID)) {
            if (locationId.equals(TestDataPreload.LOCATION1_ID)) {
                if (permission.equals(BLOG_CREATE) || permission.equals(BLOG_ENTRY_WRITE) || permission.equals(BLOG_ENTRY_READ)
                        || permission.equals(BLOG_COMMENTS_ADD)) {
                    return true;
                }
            }
        } else if (userId.equals(TestDataPreload.MAINT_USER_ID)) {
            if (locationId.equals(TestDataPreload.LOCATION1_ID)) {
                return true; // can do anything in loc 1 (assume they have all permissions checked)
            }
        } else if (userId.equals(TestDataPreload.ADMIN_USER_ID)) {
            // admin can do anything in any context
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.ExternalLogic#getBlogLocationRssUrl(java.lang.String)
     */
    public String getBlogLocationRssUrl(String locationId) {
        return "http://server:8080/direct/bloglocrss/123/";
    }

    /*
     * (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.ExternalLogic#getBlogRssUrl(java.lang.Long)
     */
    public String getBlogRssUrl(String blogId) {
        return "http://server:8080/direct/blogrss/123/";
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.ExternalLogic#cleanupUserStrings(java.lang.String)
     */
    public String cleanupUserStrings(String userSubmittedString) {
        return userSubmittedString;
    }

    public String getBlogEntryUrl(String entryId) {
        return "http://server:8080/direct/blogentry/123/";
    }

    public String getBlogUrl(String blogId) {
        return "http://server:8080/direct/blog/123/";
    }

	public String getProfileText(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean useGlobalProfile() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUserSiteAdmin(String userId, String locationId) {
		// TODO Auto-generated method stub
		return false;
	}

    public void registerEntityEvent(String eventName, Class<?> entityClass,
            String entityId) {
        // Do nothing
    }

	public String getEntryViewableSetting()
	{
	    return null;
	}

	@Override
	public String getCurrentUserDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfileEntityPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBlogLocationUrl(String locationId) {
		// TODO Auto-generated method stub
		return null;
	}
}
