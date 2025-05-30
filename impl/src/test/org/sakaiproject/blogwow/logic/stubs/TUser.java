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
package org.sakaiproject.blogwow.logic.stubs;

import java.util.Date;
import java.util.Stack;

import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.user.api.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test class for the Sakai User object<br/> This has to be here since I cannot create a User object in Sakai for some reason... sure would
 * be nice if I could though -AZ
 * 
 * @author Sakai App Builder -AZ
 */
@SuppressWarnings("unchecked")
public class TUser implements User {
    private String userId;

    private String userEid = "fakeEid";

    private String displayName = "Fake DisplayName";

    /**
     * Construct an empty test user with an id set
     * 
     * @param userId
     *            a id string
     */
    public TUser(String userId) {
        this.userId = userId;
    }

    /**
     * Construct an empty test user with an id and eid set
     * 
     * @param userId
     *            a id string
     * @param userEid
     *            a username string
     */
    public TUser(String userId, String userEid) {
        this.userId = userId;
        this.userEid = userEid;
    }

    /**
     * Construct an empty test user with an id and eid set
     * 
     * @param userId
     *            a id string
     * @param userEid
     *            a username string
     * @param displayName
     *            a user display name
     */
    public TUser(String userId, String userEid, String displayName) {
        this.userId = userId;
        this.userEid = userEid;
        this.displayName = displayName;
    }

    public boolean checkPassword(String pw) {
        // TODO Auto-generated method stub
        return false;
    }

    public User getCreatedBy() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDisplayId() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDisplayId(String context) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDisplayName(String context) {
        return this.displayName;
    }

    public String getEid() {
        return this.userEid;
    }

    public String getEmail() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getFirstName() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLastName() {
        // TODO Auto-generated method stub
        return null;
    }

    public User getModifiedBy() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSortName() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getType() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getId() {
        return userId;
    }

    public ResourceProperties getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getReference() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getReference(String rootProperty) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUrl(String rootProperty) {
        // TODO Auto-generated method stub
        return null;
    }

    public Element toXml(Document doc, Stack stack) {
        // TODO Auto-generated method stub
        return null;
    }

    public int compareTo(Object arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public Time getCreatedTime() {
        // TODO Auto-generated method stub
        return null;
    }

    public Time getModifiedTime() {
        // TODO Auto-generated method stub
        return null;
    }
    public String getUrlEmbeddableId(){
        return this.getDisplayId();
    }

	public Date getCreatedDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getModifiedDate() {
		// TODO Auto-generated method stub
		return null;
	}
}
