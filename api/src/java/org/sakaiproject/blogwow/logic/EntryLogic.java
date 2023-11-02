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

import java.util.Date;
import java.util.List;

import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;

/**
 * Logic for handling blog entries
 * 
 * @author Sakai App Builder -AZ
 */
public interface EntryLogic {

    /**
     * Get a blog entry by its unique id
     * 
     * @param entryId
     *            a unique id for a {@link BlogWowEntry}
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     * @return a blog wow entry or null if not found
     */
    public BlogWowEntry getEntryById(String entryId, String locationId);
    
    /**
     * Checks if an entry is real based on an entry id
     * @param entryId a unique id for a {@link BlogWowEntry}
     * @return true if this is an existing entry, false otherwise
     */
    public boolean entryExists(String entryId);

    /**
     * Create or update a blog entry
     * 
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     * @param entry
     *            a blog entry object
     */
    public void saveEntry(BlogWowEntry entry, String locationId);

    /**
     * Remove a blog entry and all associated comments
     * 
     * @param entryId
     *            a unique id for a {@link BlogWowEntry}
     * @param locationId
     *            a unique id which represents the current location of the user (entity reference)
     */
    public void removeEntry(String entryId, String locationId);

    /**
     * Get all blog entries which are visible to a specific user
     * 
     * @param blogId
     *            unique id of a {@link BlogWowBlog}
     * @param userId
     *            the internal user id (not username), if null then return all entries regardless of visibility
     * @param sortProperty
     *            the name of the {@link BlogWowEntry} property to sort on or null to sort by default property (dateCreated desc)
     * @param ascending
     *            sort in ascending order, if false then descending (ignored if sortProperty is null)
     * @param start
     *            the entry number to start on (based on current sort rules), first entry is 0
     * @param limit
     *            the maximum number of entries to return, 0 returns as many entries as possible
     * @return a list of {@link BlogWowEntry} objects
     */
    public List<BlogWowEntry> getAllVisibleEntries(String blogId, String userId, String sortProperty, boolean ascending, int start, int limit);

    /**
     * Quickly get a count of entries visible to a user
     * @param blogId
     * 		unique id of a {@link BlogWowBlog}
     * @param userId
     * 		the internal user id (not username), if null then return all entries regardless of visibility
     * @return
     */
    public Integer getVisibleEntryCount(String blogId, String userId);

    /**
     * Efficiency method which gets all blog entries which are visible to a specific user for an array of blogs
     * 
     * @param blogIds
     *            an array of unique ids of {@link BlogWowBlog}
     * @param userId
     *            the internal user id (not username), if null then return all entries regardless of visibility
     * @param sortProperty
     *            the name of the {@link BlogWowEntry} property to sort on or null to sort by default property (dateCreated desc)
     * @param ascending
     *            sort in ascending order, if false then descending (ignored if sortProperty is null)
     * @param start
     *            the entry number to start on (based on current sort rules), first entry is 0
     * @param limit
     *            the maximum number of entries to return, 0 returns as many entries as possible
     * @return a list of {@link BlogWowEntry} objects
     */
    public List<BlogWowEntry> getAllVisibleEntries(String[] blogIds, String userId, String sortProperty, boolean ascending, int start,
            int limit);

    // PERMISSIONS

    /**
     * Check if an entry can be updated or removed
     * 
     * @param entryId
     *            a unique id for a {@link BlogWowEntry}
     * @param userId
     *            the internal user id (not username)
     * @return true if this entry can be udpated or removed by this user, false otherwise
     */
    public boolean canWriteEntry(String entryId, String userId);

    /**
     * Efficiency method which gets all blog entries which are visible to a specific user for an array of blogs
     *
     * @param blogIds
     *            an array of unique ids of {@link BlogWowBlog}
     * @param userId
     *            the internal user id (not username), if null then return all entries regardless of visibility
     * @param sortProperty
     *            the name of the {@link BlogWowEntry} property to sort on or null to sort by default property (dateCreated desc)
     * @param ascending
     *            sort in ascending order, if false then descending (ignored if sortProperty is null)
     * @param startDate
     *            all returned entries will have been created after this {@link java.util.Date}
     * @param limit
     *            the maximum number of entries to return, 0 returns as many entries as possible
     * @return a list of {@link BlogWowEntry} objects
     */
    public List<BlogWowEntry> getVisibleEntriesCreatedSince (String[] blogIds, String userId, String sortProperty, boolean ascending, Date startDate, int limit);

    /**
     * Get all blog entries which are visible to a specific user
     *
     * @param blogId
     *            unique id of a {@link BlogWowBlog}
     * @param userId
     *            the internal user id (not username), if null then return all entries regardless of visibility
     * @param sortProperty
     *            the name of the {@link BlogWowEntry} property to sort on or null to sort by default property (dateCreated desc)
     * @param ascending
     *            sort in ascending order, if false then descending (ignored if sortProperty is null)
     * @param startDate
     *            all returned entries will have been created after this {@link java.util.Date}
     * @param limit
     *            the maximum number of entries to return, 0 returns as many entries as possible
     * @return a list of {@link BlogWowEntry} objects
     */
    public List<BlogWowEntry> getVisibleEntriesCreatedSince(String blogId, String userId, String sortProperty, boolean ascending, Date startDate, int limit);
}
