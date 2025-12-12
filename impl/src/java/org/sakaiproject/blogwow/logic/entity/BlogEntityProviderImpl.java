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
package org.sakaiproject.blogwow.logic.entity;

import java.util.List;
import java.util.Map;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.CollectionResolvable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Createable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Inputable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Resolvable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Saveable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Updateable;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.search.Restriction;
import org.sakaiproject.entitybroker.entityprovider.search.Search;

/**
 * Implementation of blog entity provider,
 * provides access to the blog itself
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class BlogEntityProviderImpl implements BlogEntityProvider, CoreEntityProvider, 
      Saveable, Createable, Resolvable, Updateable, 
      CollectionResolvable, Outputable, Inputable, AutoRegisterEntityProvider {

   private BlogLogic blogLogic;
   public void setBlogLogic(BlogLogic blogLogic) {
      this.blogLogic = blogLogic;
   }

   private DeveloperHelperService developerHelperService;
   public void setDeveloperHelperService(DeveloperHelperService developerHelperService) {
      this.developerHelperService = developerHelperService;
   }

   /**
    * Require a logged-in user; entitybroker will otherwise fabricate an anonymous id.
    * We want these endpoints to be auth-only.
    */
   private String requireCurrentUserId() {
      String userRef = developerHelperService.getCurrentUserReference();
      if (userRef == null) {
         throw new SecurityException("Authentication required to access blog entities");
      }
      return developerHelperService.getUserIdFromRef(userRef);
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.entitybroker.entityprovider.EntityProvider#getEntityPrefix()
    */
   public String getEntityPrefix() {
      return ENTITY_PREFIX;
   }

   public boolean entityExists(String id) {
      // entity is real if there are any entries that match this id
      requireCurrentUserId();
      String blogId = id;
      if (blogLogic.getBlogById(blogId) != null) {
         return true;
      }
      return false;
   }

   // Added for compatibility
   public String createEntity(EntityReference ref, Object entity, Map<String, Object> params) {
       requireCurrentUserId();
       return createEntity(ref, entity);
   }

   public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params) {
       requireCurrentUserId();
       updateEntity(ref, entity);
   }

   public String createEntity(EntityReference ref, Object entity) {
      requireCurrentUserId();
      BlogWowBlog incoming = (BlogWowBlog) entity;
      if (incoming.getLocation() == null) {
         throw new IllegalArgumentException("The blog locationId must be set to create a blog, " +
         		"you can get this from the DeveloperHelperService");
      }
      if (incoming.getOwnerId() == null) {
         throw new IllegalArgumentException("The blog ownerId must be set to create a blog");
      }
      BlogWowBlog blog = blogLogic.makeBlogByLocationAndUser(incoming.getLocation(), incoming.getOwnerId());
      // copy over values
      developerHelperService.copyBean(incoming, blog, 0, new String[] {"locationId","ownerId","dateCreated"}, true);
      blogLogic.saveBlog(blog, blog.getLocation());
      return blog.getId();
   }

   public Object getSampleEntity() {
      return new BlogWowBlog();
   }

   public void updateEntity(EntityReference ref, Object entity) {
      String blogId = ref.getId();
      if (blogId == null) {
         throw new IllegalArgumentException("The id must be set when updating a blog");
      }
      BlogWowBlog blog = blogLogic.getBlogById(blogId);
      if (blog == null) {
         throw new IllegalArgumentException("Cannot find a blog to update with this reference: " + ref);
      }
      BlogWowBlog incoming = (BlogWowBlog) entity;
      // copy over values
      developerHelperService.copyBean(incoming, blog, 0, new String[] {"locationId","ownerId","dateCreated"}, true);
      blogLogic.saveBlog(blog, blog.getLocation());
   }

   public Object getEntity(EntityReference ref) {
      requireCurrentUserId();
      String blogId = ref.getId();
      if (blogId == null) {
         return new BlogWowBlog();
      }
      BlogWowBlog blog = blogLogic.getBlogById(blogId);
      if (blog == null) {
         throw new IllegalArgumentException("No blog found with this id: " + blogId);
      }
      return blog;
   }

   public List<?> getEntities(EntityReference ref, Search search) {
      requireCurrentUserId();
      String locationId = developerHelperService.getCurrentLocationReference();
      if (search != null && !search.isEmpty()) {
         Restriction restriction = search.getRestrictionByProperty("location");
         if (restriction != null) {
            locationId = (String) restriction.getSingleValue();
         }
      }
      if (locationId == null) {
         throw new IllegalArgumentException("Must specify a location to list blogs (search param 'location' or tool context)");
      }
      List<BlogWowBlog> blogs = blogLogic.getAllVisibleBlogs(locationId, null, true, 0, 50);
      return blogs;
   }

   public String[] getHandledOutputFormats() {
      return new String[] { Formats.XML, Formats.JSON };
   }

   public String[] getHandledInputFormats() {
      return new String[] { Formats.HTML, Formats.XML, Formats.JSON };
   }

}
