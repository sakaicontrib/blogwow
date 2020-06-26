/******************************************************************************
 * ExternalLogicImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic;

import java.net.URLEncoder;
import java.util.Date;

import org.azeckoski.reflectutils.ReflectUtils;
import org.azeckoski.reflectutils.exceptions.FieldnameNotFoundException;
import org.sakaiproject.authz.api.FunctionManager;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.blogwow.constants.BlogConstants;
import org.sakaiproject.blogwow.logic.entity.BlogEntityProvider;
import org.sakaiproject.blogwow.logic.entity.BlogEntryEntityProvider;
import org.sakaiproject.blogwow.logic.entity.BlogGroupEntityProvider;
import org.sakaiproject.blogwow.logic.entity.BlogGroupRssEntityProvider;
import org.sakaiproject.blogwow.logic.entity.BlogRssEntityProvider;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entitybroker.EntityBroker;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.util.api.FormattedText;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * This is the implementation for logic which is external to our app logic
 * 
 * @author Sakai App Builder -AZ
 */
@Slf4j
public class ExternalLogicImpl implements ExternalLogic {

   @Setter private FormattedText formattedText;
   private FunctionManager functionManager;
   public void setFunctionManager(FunctionManager functionManager) {
      this.functionManager = functionManager;
   }

   private ToolManager toolManager;
   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

   private SecurityService securityService;
   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   private SessionManager sessionManager;
   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   private SiteService siteService;
   public void setSiteService(SiteService siteService) {
      this.siteService = siteService;
   }

   private UserDirectoryService userDirectoryService;
   public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
      this.userDirectoryService = userDirectoryService;
   }

   private EntityBroker entityBroker;
   public void setEntityBroker(EntityBroker entityBroker) {
      this.entityBroker = entityBroker;
   }

// private SakaiPersonManager sakaiPersonManager;
// public void setSakaiPersonManager(SakaiPersonManager spm) {
// this.sakaiPersonManager = spm;
// }

   private String SAKAIPERSON_PREFIX = "profile";
   public void setSAKAIPERSON_PREFIX(String sakaiperson_prefix) {
      SAKAIPERSON_PREFIX = sakaiperson_prefix;
   }

   private ServerConfigurationService serverConfigurationService;
   public void setServerConfigurationService(
         ServerConfigurationService serverConfigurationService) {
      this.serverConfigurationService = serverConfigurationService;
   }

   private static final String ANON_USER_ATTRIBUTE = "AnonUserAttribute";

   //sakai.property key to use the global sakai property rather than the local one 
   private static final String GLOBAL_PROFILE_SETTING = "blogwow.useglobalprofile";

   //sakai.property key to use the global sakai property rather than the local one 
   private static final String ENTRY_VIEWABLE_SETTING = "blogwow.entry.viewable";

   // contruct a reflection utility class
   ReflectUtils reflectUtil = ReflectUtils.getInstance();

   /**
    * Place any code that should run when this class is initialized by spring here
    */
   public void init() {
      log.debug("init");
      // register Sakai permissions for this tool
      functionManager.registerFunction(BLOG_CREATE);
      functionManager.registerFunction(BLOG_ENTRY_WRITE);
      functionManager.registerFunction(BLOG_ENTRY_WRITE_ANY);
      functionManager.registerFunction(BLOG_ENTRY_READ);
      functionManager.registerFunction(BLOG_ENTRY_READ_ANY);
      functionManager.registerFunction(BLOG_COMMENTS_ADD);
      functionManager.registerFunction(BLOG_COMMENTS_REMOVE_ANY);
   }

   public String getCurrentLocationId() {
      try {
         if (toolManager.getCurrentPlacement() == null)
         {
            return NO_LOCATION;
         }
         Site s = siteService.getSite(toolManager.getCurrentPlacement().getContext());
         return s.getReference(); // get the entity reference to the site
      } catch (IdUnusedException e) {
         return NO_LOCATION;
      }
   }

   public String getLocationTitle(String locationId) {
      try {
         // try to get the site object based on the entity reference (which is the evalGroupId)
         Site site = (Site) entityBroker.fetchEntity(locationId);
         return site.getTitle();
      } catch (Exception e) {
         // invalid site reference
         log.debug("Could not get sakai site from evalGroupId:" + locationId);
         return "----------";
      }
   }

   public String getCurrentUserId() {
      String userId = sessionManager.getCurrentSessionUserId();
      if (userId == null) {
         // if no user found then fake like there is one for this session,
         // we do not want to actually create a user though
         Session session = sessionManager.getCurrentSession();
         userId = (String) session.getAttribute(ANON_USER_ATTRIBUTE);
         if (userId == null) {
            userId = ANON_USER_PREFIX + new Date().getTime();
            session.setAttribute(ANON_USER_ATTRIBUTE, userId);
         }
      }
      return userId;
   }

   public String getUserDisplayName(String userId) {
      try {
         User user = userDirectoryService.getUser(userId);
         return user.getDisplayName();
      } catch (UserNotDefinedException ex) {
         log.error("Could not get user from userId: " + userId, ex);
      }
      if (userId.startsWith(ANON_USER_PREFIX)) {
         return "Anonymous User";
      }
      return "----------";
   }

   public boolean isUserAdmin(String userId) {
      return securityService.isSuperUser(userId);
   }

   public boolean isUserSiteAdmin(String userId, String locationId) {
      return securityService.unlock(userId, org.sakaiproject.site.api.SiteService.SECURE_UPDATE_SITE, locationId);
   }

   public boolean isUserAllowedInLocation(String userId, String permission, String locationId) {
      if (securityService.unlock(userId, permission, locationId)) {
         return true;
      }
      return false;
   }

   public String getBlogRssUrl(String blogId) {
      return entityBroker.getEntityURL(
            new EntityReference(BlogRssEntityProvider.ENTITY_PREFIX, blogId).toString());
   }

   public String getBlogLocationRssUrl(String locationId) {
      String encodedlocation;
      try {
         encodedlocation = URLEncoder.encode(locationId, "UTF-8");
      }
      catch (Exception e) {
         throw new IllegalArgumentException(e);
      }
      return entityBroker.getEntityURL(
            new EntityReference(BlogGroupRssEntityProvider.ENTITY_PREFIX, 
                  encodedlocation).toString());
   }
   
   public String getBlogLocationUrl(String locationId) {
	      String encodedlocation;
	      try {
	         encodedlocation = URLEncoder.encode(locationId, "UTF-8");
	      }
	      catch (Exception e) {
	         throw new IllegalArgumentException(e);
	      }
	      return entityBroker.getEntityURL(
	            new EntityReference(BlogGroupEntityProvider.ENTITY_PREFIX, 
	                  encodedlocation).toString());
	   }

   @SuppressWarnings("deprecation")
   public String cleanupUserStrings(String userSubmittedString) {
      if (userSubmittedString == null) {
         // nulls are ok
         return null;
      } else if (userSubmittedString.length() == 0) {
         // empty string is ok
         return "";
      }

      // clean up the string using Sakai text format (should stop XSS)
      // CANNOT CHANGE THIS TO STRINGBUILDER OR 2.4.x and below will fail -AZ
      String cleanup = formattedText.processFormattedText(userSubmittedString, new StringBuffer()).trim();

      return cleanup;
   }

   public String getBlogEntryUrl(String entryId) {
      return entityBroker.getEntityURL(
            new EntityReference(BlogEntryEntityProvider.ENTITY_PREFIX, entryId).toString());
   }

   public String getBlogUrl(String blogId) {
      return entityBroker.getEntityURL(
            new EntityReference(BlogEntityProvider.ENTITY_PREFIX, blogId).toString());
   }

   /**
    * Use the global profile from PersonManager rather than per-blog profiles 
    * 
    * @return true if the global profiles should be used
    */
   public boolean useGlobalProfile()
   {
      // get from serverconfigurationservice
      boolean ret = serverConfigurationService.getBoolean(GLOBAL_PROFILE_SETTING, false);
      log.debug("useGlobalProfile is: " + ret);   
      return ret;
   }

   public void registerEntityEvent(String eventName, Class<?> entityClass, String entityId) {
       String ref = getEntityReference(entityClass, entityId);
       if (ref != null) {
          log.info("Entity event: " + eventName + " for " + ref);
          entityBroker.fireEvent(eventName, ref);
       }
    }

    protected String getEntityReference(Class<?> entityClass, String entityId) {
       String prefix = null;
       // make sure this class is supported and get the prefix
       if (entityClass == BlogWowBlog.class) {
          prefix = BlogEntityProvider.ENTITY_PREFIX;
       } else if (entityClass == BlogWowEntry.class) {
          prefix = BlogEntryEntityProvider.ENTITY_PREFIX;
       } else if (entityClass == BlogWowComment.class) {
           prefix = "blog-comment";
       } else {
          return "blog:" + entityClass.getSimpleName();
       }

       return new EntityReference(prefix, entityId).toString();
    }

   /**
    * Get the default blog entry viewable settings from sakai.properties.
    * 
    * @return The value of the default setting.
    */
   public String getEntryViewableSetting()
   {
	   return serverConfigurationService.getString(ENTRY_VIEWABLE_SETTING, BlogConstants.PRIVACY_PUBLIC);
   }
   
   public String getCurrentUserDisplayName(){
	   User user = userDirectoryService.getCurrentUser();
	   if(user != null){
		   return user.getDisplayName();
	   }

	   return "";
   	}


public String getProfileEntityPrefix() {
	return serverConfigurationService.getString("blogwow.entityPrefix", "profile");
}
}
