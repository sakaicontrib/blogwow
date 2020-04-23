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
package org.sakaiproject.blogwow.model;

import java.util.Date;

/**
 * This is a single users blog in a site
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogWowBlog {

    private String id;

    private String ownerId; // Sakai userId

    private String location; // Sakai entity reference

    private String title;

    private String profile;

    private String imageUrl;

    private Date dateCreated;

    private String icon;

    /**
     * Default constructor
     */
    public BlogWowBlog() {
    }

    /**
     * Minimal constructor
     */
    public BlogWowBlog(String ownerId, String location, String title) {
        this.ownerId = ownerId;
        this.location = location;
        this.title = title;
    }

    /**
     * Full constructor
     */
    public BlogWowBlog(String ownerId, String location, String title, String profile, String imageUrl, Date dateCreated, String icon) {
        this.ownerId = ownerId;
        this.location = location;
        this.title = title;
        this.profile = profile;
        this.imageUrl = imageUrl;
        this.dateCreated = dateCreated;
        this.icon = icon;
    }

    /**
     * Getters and Setters
     */

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
