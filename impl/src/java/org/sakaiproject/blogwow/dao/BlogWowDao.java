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
package org.sakaiproject.blogwow.dao;

import java.util.Date;
import java.util.List;

import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.genericdao.api.GeneralGenericDao;

/**
 * This is a specialized DAO that allows the developer to extend the functionality of the generic dao package
 * 
 * @author Sakai App Builder -AZ
 */
public interface BlogWowDao extends GeneralGenericDao {

    /**
     * Get a list of unique locations from an array of blogIds
     * 
     * @param blogIds
     *            an array of unique ids of {@link BlogWowBlog}
     * @return a list of unique {@link String}s which represent locations
     */
    public List<String> getLocationsForBlogsIds(String[] blogIds);

    /**
     * Get blog entries efficiently using sakai permissions
     * 
     * @param blogIds
     *            an array of unique ids of {@link BlogWowBlog} which we will return entries from
     * @param userId
     *            the internal user id (not username), if null then return public entries
     * @param readLocations
     *            an array of locationIds which we will return all privacy {@link BlogConstants#PRIVACY_GROUP} entries for (the user should
     *            have read permissions in these locations)
     * @param readAnyLocations
     *            an array of locationIds which we will return all privacy {@link BlogConstants#PRIVACY_GROUP_LEADER} entries for (the user
     *            should have read all entries access to these locations)
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
    public List<BlogWowEntry> getBlogPermEntries(String[] blogIds, String userId, String[] readLocations, String[] readAnyLocations, String sortProperty,
            boolean ascending, int start, int limit);

    /**
     * 
     * @param blogIds
     *            an array of unique ids of {@link BlogWowBlog} which we will count entries from
     * @param userId
     *            the internal user id (not username), if null then return public entries
     * @param readLocations
     *            an array of locationIds which we will count all privacy {@link BlogConstants#PRIVACY_GROUP} entries for (the user should
     *            have read permissions in these locations)
     * @param readAnyLocations
     *            an array of locationIds which we will count all privacy {@link BlogConstants#PRIVACY_GROUP_LEADER} entries for (the user
     *            should have read all entries access to these locations)
     * @return a count of {@link BlogWowEntry} objects
     */
    public Integer getBlogPermCount(String[] blogIds, String userId, String[] readLocations, String[] readAnyLocations);

    /**
     *
     * @param blogIds an array of unique ids of {@link BlogWowBlog} which we will count entries from
     * @param userId the internal user id (not username), if null then return public entries
     * @param readLocations an array of locationIds which we will count all privacy {@link BlogConstants#PRIVACY_GROUP} entries for (the user should have read permissions in these locations)
     * @param readAnyLocations an array of locationIds which we will count all privacy {@link BlogConstants#PRIVACY_GROUP_LEADER} entries for (the user should have read all entries access to these locations)
     * @param sortProperty the name of the {@link BlogWowEntry} property to sort on or null to sort by default property (dateCreated desc)
     * @param ascending sort in ascending order, if false then descending (ignored if sortProperty is null)
     * @param start the {@link Date} to start on
     * @param limit the maximum number of entries to return, 0 returns as many entries as possible
     * @return a list of {@link BlogWowEntry} objects
     */
    public List<BlogWowEntry> getBlogPermEntries(String[] blogIds, String userId, String[] readLocations, String[] readAnyLocations, String sortProperty, boolean ascending, Date start, int limit);
}
