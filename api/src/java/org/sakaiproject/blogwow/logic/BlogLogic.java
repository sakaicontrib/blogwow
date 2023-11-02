/**
 * Copyright (c) 2007-2020 The Apereo Foundation
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
package org.sakaiproject.blogwow.logic;

import java.util.List;

import org.sakaiproject.blogwow.model.BlogWowBlog;

/**
 * This is the interface for the blog app Logic
 * 
 * @author Sakai App Builder -AZ
 */
public interface BlogLogic {

    /**
     * Get a blog based on its id (blogs are visible to everyone)
     * 
     * @param blogId
     *            the id of a {@link BlogWowBlog} object
     * @return a blog or null if not found
     */
    public BlogWowBlog getBlogById(String blogId);

    /**
     * Get a blog based on a location and user, if none found then one is created for the user (if they have appropriate permissions)
     * 
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     * @param userId
     *            the internal user id (not username)
     * @return a blog or null if cannot create one and none exists
     */
    public BlogWowBlog makeBlogByLocationAndUser(String locationId, String userId);

    /**
     * Save (Create or Update) a blog (uses the current site)
     * 
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     * @param blog
     *            the BlogWowBlog to create or update
     */
    public void saveBlog(BlogWowBlog blog, String locationId);

    /**
     * This returns a List of blogs for a specified site (all blogs are visible to everyone)
     * 
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     * @param sortProperty
     *            the name of the {@link BlogWowBlog} property to sort on or null to sort by default property (title asc)
     * @param ascending
     *            sort in ascending order, if false then descending (ignored if sortProperty is null)
     * @param start
     *            the entry number to start on (based on current sort rules), first entry is 0
     * @param limit
     *            the maximum number of entries to return, 0 returns as many entries as possible
     * @return a List of {@link BlogWowBlog} objects
     */
    public List<BlogWowBlog> getAllVisibleBlogs(String locationId, String sortProperty, boolean ascending, int start, int limit);

    // PERMISSIONS

    /**
     * Check if a specified user can write this blog in a specified site. If the user
     * is superadmin, this check will always succeed.
     * 
     * @param blog
     *            the {@link BlogWowBlog} object, or <code>null</code> if the check is
     *            that the user can create a new blog in this site.
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     * @param userId
     *            the internal user id (not username)
     * @return true if blog can be modified, false otherwise
     */
    public boolean canWriteBlog(BlogWowBlog blog, String locationId, String userId);

}
