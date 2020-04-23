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

import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;

/**
 * Logic for handling blog comments
 * 
 * @author Sakai App Builder -AZ
 */
public interface CommentLogic {

    /**
     * Get a blog comment by its unique id
     * 
     * @param commentId
     *            a unique id for a {@link BlogWowComment}
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     * @return a blog comment or null if not found
     */
    public BlogWowComment getCommentById(String commentId, String locationId);

    /**
     * Create a comment if the current user is allowed to and it is new (changing existing comments is not allowed)
     * 
     * @param comment
     *            a blog comment
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     */
    public void saveComment(BlogWowComment comment, String locationId);

    /**
     * Remove a blog comment if the current user is allowed to
     * 
     * @see #canRemoveComment(Long, String)
     * @param commentId
     *            a unique id for a {@link BlogWowComment}
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     */
    public void removeComment(String commentId, String locationId);

    /**
     * Gets the comments for an entry (only readable if the entry is readable)
     * 
     * @param entryId
     *            a unique id for a {@link BlogWowEntry}
     * @param sortProperty
     *            the name of the {@link BlogWowEntry} property to sort on or null to sort by default property (dateCreated desc)
     * @param ascending
     *            sort in ascending order, if false then descending (ignored if sortProperty is null)
     * @param start
     *            the entry number to start on (based on current sort rules), first entry is 0
     * @param limit
     *            the maximum number of entries to return, 0 returns as many entries as possible
     * @return a list of {@link BlogWowComment} objects
     */
    public List<BlogWowComment> getComments(String entryId, String sortProperty, boolean ascending, int start, int limit);

    // PERMISSIONS

    /**
     * Check if a user can remove a blog comment, only super admins and users with the remove any permission are allowed
     * 
     * @param commentId
     *            a unique id for a {@link BlogWowComment}
     * @param userId
     *            the internal user id (not username)
     * @return true if the user can remove this comment, false otherwise
     */
    public boolean canRemoveComment(String commentId, String userId);

    /**
     * Check if a user can add a comment to a blog, owners can always add comments to their blogs and anyone with the add comment permission
     * 
     * @param entryId
     *            a unique id for a {@link BlogWowEntry}
     * @param userId
     *            the internal user id (not username)
     * @return true if they can add a comment, false otherwise
     */
    public boolean canAddComment(String entryId, String userId);

}
