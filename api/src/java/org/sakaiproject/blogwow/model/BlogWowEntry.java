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
 * This is a single blog entry in a blog
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogWowEntry {

    private String id;

    private BlogWowBlog blog;

    private String ownerId; // Sakai userId

    private String title;

    private String text;

    private String privacySetting;

    private Date dateModified;

    private Date dateCreated;

    /**
     * Default constructor
     */
    public BlogWowEntry() {
    }

    /**
     * Minimal constructor
     */
    public BlogWowEntry(BlogWowBlog blog, String ownerId, String title, String text, String privacySetting, Date dateModified) {
        this.blog = blog;
        this.ownerId = ownerId;
        this.title = title;
        this.text = text;
        this.privacySetting = privacySetting;
        this.dateModified = dateModified;
    }

    /**
     * Full constructor
     */
    public BlogWowEntry(BlogWowBlog blog, String ownerId, String title, String text, String privacySetting, Date dateModified,
            Date dateCreated) {
        this.blog = blog;
        this.ownerId = ownerId;
        this.title = title;
        this.text = text;
        this.privacySetting = privacySetting;
        this.dateModified = dateModified;
        this.dateCreated = dateCreated;
    }

    /**
     * Getters and Setters
     */
    public BlogWowBlog getBlog() {
        return blog;
    }

    public void setBlog(BlogWowBlog blog) {
        this.blog = blog;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivacySetting() {
        return privacySetting;
    }

    public void setPrivacySetting(String privacySetting) {
        this.privacySetting = privacySetting;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

}
