/******************************************************************************
 * EntryLogicImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sakaiproject.blogwow.constants.BlogConstants;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.genericdao.api.search.Order;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation
 * 
 * @author Sakai App Builder -AZ
 */
@Slf4j
public class EntryLogicImpl implements EntryLogic {


   private ExternalLogic externalLogic;
   public void setExternalLogic(ExternalLogic externalLogic) {
      this.externalLogic = externalLogic;
   }

   private BlogWowDao dao;
   public void setDao(BlogWowDao dao) {
      this.dao = dao;
   }


   public List<BlogWowEntry> getAllVisibleEntries(String blogId, String userId, String sortProperty, boolean ascending, int start, int limit) {
      return getAllVisibleEntries(new String[] { blogId }, userId, sortProperty, ascending, start, limit);
   }

   public Integer getVisibleEntryCount(String blogId, String userId){
      Integer count = Integer.valueOf(0);
      if (externalLogic.isUserAdmin(userId)) {
         count = (int) dao.countBySearch(BlogWowEntry.class, new Search("blog.id",blogId));
             //dao.countByProperties(BlogWowEntry.class, new String[] { "blog.id" }, new Object[] { blogId });
      } else {
         List<String> locations = dao.getLocationsForBlogsIds(new String[] { blogId });
         // check current user perms on these locations to form lists of locations related to this users access
         List<String> readLocations = new ArrayList<String>(); // holds the locations where user has read perms
         List<String> readAnyLocations = new ArrayList<String>(); // holds the locations where user has read any perms
         for (String location : locations) {
            if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_READ_ANY, location)) {
               readAnyLocations.add(location);
               readLocations.add(location);
            } else if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_READ, location)) {
               readLocations.add(location);
            }
         }
         String[] readLocsArray = readLocations.toArray(new String[] {});
         String[] readAnyLocsArray = readAnyLocations.toArray(new String[] {});

         count = dao.getBlogPermCount(new String[] { blogId }, userId, readLocsArray, readAnyLocsArray);
      }

      return count;
   }

   @SuppressWarnings("unchecked")
   public List<BlogWowEntry> getAllVisibleEntries(String[] blogIds, String userId, String sortProperty, boolean ascending, int start,
         int limit) {
      Order order = new Order(sortProperty, ascending);
      if (sortProperty == null) {
         order = new Order("dateCreated", false);
      }

      Search search = new Search();
      search.addOrder(order);
      search.setStart(start);
      search.setLimit(limit);
      if (blogIds != null && blogIds.length > 0) {
         search.addRestriction( new Restriction("blog.id", blogIds) );
      }

      List l = new ArrayList();
      if (externalLogic.isUserAdmin(userId)) {
         // get all entries for a set of blogs
         l = dao.findBySearch(BlogWowEntry.class, search);
      } else {
         List<String> locations = dao.getLocationsForBlogsIds(blogIds);
         // check current user perms on these locations to form lists of locations related to this users access
         List<String> readLocations = new ArrayList<String>(); // holds the locations where user has read perms
         List<String> readAnyLocations = new ArrayList<String>(); // holds the locations where user has read any perms
         for (String location : locations) {
            if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_READ_ANY, location)) {
               readAnyLocations.add(location);
               readLocations.add(location);
            } else if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_READ, location)) {
               readLocations.add(location);
            }
         }
         String[] readLocsArray = readLocations.toArray(new String[] {});
         String[] readAnyLocsArray = readAnyLocations.toArray(new String[] {});

         l = dao.getBlogPermEntries(blogIds, userId, readLocsArray, readAnyLocsArray, order.property, order.ascending, start, limit);
      }

      return l;
   }

   /*
    * (non-Javadoc)
    * @see org.sakaiproject.blogwow.logic.EntryLogic#getEntryById(java.lang.Long, java.lang.String)
    */
   public BlogWowEntry getEntryById(String entryId, String locationId) {
      String currentUserId = externalLogic.getCurrentUserId();
      if (locationId == null) {
         locationId = externalLogic.getCurrentLocationId();
         if (locationId == null) {
            locationId = ExternalLogic.NO_LOCATION;
         }
      }
      BlogWowEntry entry = (BlogWowEntry) dao.findById(BlogWowEntry.class, entryId);
      if (entry == null) {
         // entry not found
         return null;
      } else if (entry.getOwnerId().equals(currentUserId)) {
         // owner can access from anywhere
         return entry;
      } else if (BlogConstants.PRIVACY_PUBLIC.equals(entry.getPrivacySetting())) {
         return entry;
      } else if (externalLogic.isUserAdmin(currentUserId)) {
         // the system super user can read anything
         return entry;
      } else {
    	  if (ExternalLogic.NO_LOCATION.equals(locationId)) {
    		  locationId = entry.getBlog().getLocation();
    	  }
    	  
         if (BlogConstants.PRIVACY_GROUP.equals(entry.getPrivacySetting())
               && externalLogic.isUserAllowedInLocation(currentUserId, ExternalLogic.BLOG_ENTRY_READ, locationId)) {
            return entry;
         } else if ((BlogConstants.PRIVACY_GROUP.equals(entry.getPrivacySetting()) || BlogConstants.PRIVACY_GROUP_LEADER.equals(entry
               .getPrivacySetting()))
               && externalLogic.isUserAllowedInLocation(currentUserId, ExternalLogic.BLOG_ENTRY_READ_ANY, locationId)) {
            return entry;
         }
      }
      throw new SecurityException("User (" + currentUserId + ") cannot access this entry (" + entryId + ") in this location ("
            + locationId + ")");
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.blogwow.logic.EntryLogic#removeEntry(java.lang.String, java.lang.String)
    */
   public void removeEntry(String entryId, String locationId) {
      String currentUserId = externalLogic.getCurrentUserId();
      BlogWowEntry entry = getEntryById(entryId, locationId);
      if (canWriteEntry(entryId, currentUserId)) {
         List<BlogWowComment> l = dao.findBySearch(BlogWowComment.class, new Search("entry.id",entryId));
             //dao.findByProperties(BlogWowComment.class, new String[] { "entry.id" }, new Object[] { entryId });
         if (l.size() == 0) {
            dao.delete(entry);
         } else {
            Set<BlogWowComment> comset = new HashSet<BlogWowComment>();
            for (BlogWowComment comment : l) {
               comset.add(comment);
            }
            dao.deleteSet(comset); // remove all comments first
            dao.delete(entry);
         }
      } else {
         throw new SecurityException(currentUserId + " cannot remove this entry (" + entryId + ") in location: " + locationId);
      }
      externalLogic.registerEntityEvent("blog.entry.removed", BlogWowEntry.class, entry.getId());
   }

   /*
    * (non-Javadoc)
    * @see org.sakaiproject.blogwow.logic.EntryLogic#saveEntry(org.sakaiproject.blogwow.model.BlogWowEntry, java.lang.String)
    */
   public void saveEntry(BlogWowEntry entry, String locationId) {
      entry.setDateModified(new Date());
      // set the owner to current if not set
      if (entry.getOwnerId() == null) {
         entry.setOwnerId(externalLogic.getCurrentUserId());
      }
      if (entry.getDateCreated() == null) {
         entry.setDateCreated(new Date());
      }
      if (entry.getText() != null && entry.getText().length() > 0) {
         // clean up the blog entry
         entry.setText( externalLogic.cleanupUserStrings(entry.getText()) );            
      }
      // check if the current user can save or update the existing item
      if (checkWriteEntry(entry, externalLogic.getCurrentUserId())) {
         dao.save(entry);
         log.debug("Saving entry: " + entry.getId() + ":" + entry.getText());
         externalLogic.registerEntityEvent("blog.entry.saved", BlogWowEntry.class, entry.getId());
      } else {
         throw new SecurityException("Current user cannot save entry " + entry.getId() + " because they do not have permission");
      }
   }

   /*
    * (non-Javadoc)
    * @see org.sakaiproject.blogwow.logic.EntryLogic#canWriteEntry(java.lang.Long, java.lang.String)
    */
   public boolean canWriteEntry(String entryId, String userId) {
      BlogWowEntry entry = (BlogWowEntry) dao.findById(BlogWowEntry.class, entryId);
      if (entry == null) {
         throw new IllegalArgumentException("blog entry id is invalid: " + entryId);
      }

      if ( checkWriteEntry(entry, userId) ) {
         return true;
      }
      return false;
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.blogwow.logic.EntryLogic#entryExists(java.lang.String)
    */
   public boolean entryExists(String entryId) {
      BlogWowEntry entry = (BlogWowEntry) dao.findById(BlogWowEntry.class, entryId);
      if (entry != null) {
         return true;
      }
      return false;
   }


   /**
    * @param entry
    * @param userId
    * @return
    */
   private boolean checkWriteEntry(BlogWowEntry entry, String userId) {
      if (externalLogic.isUserAdmin(userId)) {
         // the system super user can write
         return true;
      }

      BlogWowBlog blog = (BlogWowBlog) dao.findById(BlogWowBlog.class, entry.getBlog().getId() );
      if (blog.getOwnerId().equals(userId)
            && externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_WRITE, blog.getLocation())) {
         // blog owner can write
         return true;
      } else if (entry.getOwnerId().equals(userId)
            && externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_WRITE, blog.getLocation())) {
         // entry owner can write
         return true;
      } else if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_WRITE_ANY, blog.getLocation())) {
         // users with permission in the specified location can write for that location
         return true;
      }
      return false;
   }


public List<BlogWowEntry> getVisibleEntriesCreatedSince(String[] blogIds, String userId,
		String sortProperty, boolean ascending, Date startDate, int limit)
{
    Order order = new Order(sortProperty, ascending);
    if (sortProperty == null) {
       order = new Order("dateCreated", false);
    }

    Search search = new Search();
    search.addOrder(order);
    search.setLimit(limit);
    search.setConjunction(true);
    if (blogIds != null && blogIds.length > 0) {
       search.addRestriction( new Restriction("blog.id", blogIds) );
    }

    if (startDate != null) {
        //Restriction doesn't do '>=' so we'll subtract a millisecond
        search.addRestriction(new Restriction("dateCreated", new Date(startDate.getTime()-1), Restriction.GREATER));
    }

    List<BlogWowEntry> l = new ArrayList<BlogWowEntry>();
    if (externalLogic.isUserAdmin(userId)) {
       // get all entries for a set of blogs
       l = dao.findBySearch(BlogWowEntry.class, search);
    } else {
       List<String> locations = dao.getLocationsForBlogsIds(blogIds);
       // check current user perms on these locations to form lists of locations related to this users access
       List<String> readLocations = new ArrayList<String>(); // holds the locations where user has read perms
       List<String> readAnyLocations = new ArrayList<String>(); // holds the locations where user has read any perms
       for (String location : locations) {
          if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_READ_ANY, location)) {
             readAnyLocations.add(location);
             readLocations.add(location);
          } else if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_READ, location)) {
             readLocations.add(location);
          }
       }
       String[] readLocsArray = readLocations.toArray(new String[] {});
       String[] readAnyLocsArray = readAnyLocations.toArray(new String[] {});

       l = dao.getBlogPermEntries(blogIds, userId, readLocsArray, readAnyLocsArray, order.property, order.ascending, startDate, limit);
    }

    return l;
}

public List<BlogWowEntry> getVisibleEntriesCreatedSince(String blogId, String userId,
		String sortProperty, boolean ascending, Date startDate, int limit)
{
	String[] blogIds = {blogId};
	return getVisibleEntriesCreatedSince(blogIds, userId, sortProperty, ascending, startDate, limit);
}


}
