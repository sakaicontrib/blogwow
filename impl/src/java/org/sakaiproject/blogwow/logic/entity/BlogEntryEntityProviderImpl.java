/******************************************************************************
 * BlogEntryEntityProviderImpl.java - created by aaronz on Jun 21, 2007
 * 
 * Copyright (c) 2007 Centre for Academic Research in Educational Technologies
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 * Contributors:
 * Aaron Zeckoski (aaronz@vt.edu) - primary
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.search.Restriction;
import org.sakaiproject.entitybroker.entityprovider.search.Search;

/**
 * entity provider for blog entries
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class BlogEntryEntityProviderImpl implements BlogEntryEntityProvider, CoreEntityProvider, RESTful, AutoRegisterEntityProvider {

   private BlogLogic blogLogic;
   public void setBlogLogic(BlogLogic blogLogic) {
      this.blogLogic = blogLogic;
   }

   private EntryLogic entryLogic;
   public void setEntryLogic(EntryLogic entryLogic) {
      this.entryLogic = entryLogic;
   }

   private DeveloperHelperService developerHelperService;
   public void setDeveloperHelperService(DeveloperHelperService developerHelperService) {
      this.developerHelperService = developerHelperService;
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.entitybroker.entityprovider.EntityProvider#getEntityPrefix()
    */
   public String getEntityPrefix() {
      return ENTITY_PREFIX;
   }

   public boolean entityExists(String id) {
      // entity is real if there are any entries that match this id
      String entryId = id;
      if (entryLogic.entryExists(entryId)) {
         return true;
      }
      return false;
   }

   public String createEntity(EntityReference ref, Object entity) {
      BlogWowEntry incoming = (BlogWowEntry) entity;
      if (incoming.getBlog() == null || incoming.getBlog().getId() == null) {
         throw new IllegalArgumentException("The blog.id must be set in order to create an entry");
      }
      if (incoming.getTitle() == null || incoming.getText() == null || incoming.getPrivacySetting() == null) {
         throw new IllegalArgumentException("The title, text, and privacySetting fields are required when creating an entry");
      }
      String userId = developerHelperService.getUserIdFromRef(developerHelperService.getCurrentUserReference());
      BlogWowBlog blog = blogLogic.getBlogById(incoming.getBlog().getId());
      BlogWowEntry entry = new BlogWowEntry(blog, userId, incoming.getTitle(), incoming.getText(), incoming.getPrivacySetting(), new Date());
      entryLogic.saveEntry(entry, null);
      return entry.getId();
   }

   public Object getSampleEntity() {
      return new BlogWowEntry();
   }

   // Added for compatibility
   public String createEntity(EntityReference ref, Object entity, Map<String, Object> params) {
       return createEntity(ref, entity);
   }

   public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params) {
       updateEntity(ref, entity);
   }

   public void deleteEntity(EntityReference ref, Map<String, Object> params) {
       deleteEntity(ref);
   }

   public void updateEntity(EntityReference ref, Object entity) {
      String entryId = ref.getId();
      if (entryId == null) {
         throw new IllegalArgumentException("The id must be set when updating a blog entry");
      }
      BlogWowEntry entry = entryLogic.getEntryById(entryId, null);
      if (entry == null) {
         throw new IllegalArgumentException("Cannot find a blog entry to update with this reference: " + ref);
      }
      String userId = developerHelperService.getUserIdFromRef(developerHelperService.getCurrentUserReference());
      if (!entryLogic.canWriteEntry(entryId, userId)) {
         throw new SecurityException("User ("+userId+") cannot update entry: " + ref);
      }
      BlogWowEntry incoming = (BlogWowEntry) entity;
      // copy over values
      developerHelperService.copyBean(incoming, entry, 0, new String[] {"id","blog","ownerId","dateCreated"}, true);
      entryLogic.saveEntry(entry, null);
   }

   public Object getEntity(EntityReference ref) {
      String entryId = ref.getId();
      if (entryId == null) {
         return new BlogWowEntry();
      }
      BlogWowEntry entry = entryLogic.getEntryById(entryId, null);
      if (entry == null) {
         throw new IllegalArgumentException("No blog entry found with this id: " + entryId);
      }
      return entry;
   }

   public void deleteEntity(EntityReference ref) {
      String entryId = ref.getId();
      if (!entryLogic.entryExists(entryId)) {
         throw new IllegalArgumentException("Cannot find a blog entry to delete with this reference: " + ref);
      }
      entryLogic.removeEntry(entryId, null);
   }

   public List<?> getEntities(EntityReference ref, Search search) {
      if (search == null || search.isEmpty()) {
         throw new IllegalArgumentException("Must specify at least one blog id in the 'blogIds' param to get entries from");
      }
      String[] blogIds = null;
      Restriction idRestriction = search.getRestrictionByProperty("blogIds");
      if (idRestriction == null || idRestriction.getArrayValue() == null) {
         idRestriction = search.getRestrictionByProperty("blogId");
         if (idRestriction == null || idRestriction.getSingleValue() == null) {
            throw new IllegalArgumentException("Must specify at least one blog id in the 'blogIds' param to get entries from");
         }
         blogIds = new String[] { (String) idRestriction.getSingleValue() };
      } else {
         blogIds = (String[]) idRestriction.getArrayValue();
      }

      List<BlogWowEntry> entries = null;
      String userId = developerHelperService.getUserIdFromRef(developerHelperService.getCurrentUserReference());

      Restriction dateRestriction = search.getRestrictionByProperty("date");
      if (dateRestriction != null) {
        try {
          Date startDate = new Date(Long.parseLong(dateRestriction.getStringValue()));
          entries = entryLogic.getVisibleEntriesCreatedSince(blogIds, userId, null, true, startDate, 0);
        } catch(NumberFormatException e){
          throw new IllegalArgumentException("Date should be a number of seconds since the epoch", e);
        }
      } else {
        entries = entryLogic.getAllVisibleEntries(blogIds, userId, null, true, 0, 0);
      }
      return entries;
   }

   public String[] getHandledOutputFormats() {
      return new String[] { Formats.XML, Formats.JSON };
   }

   public String[] getHandledInputFormats() {
      return new String[] { Formats.HTML, Formats.XML, Formats.JSON };
   }
}
