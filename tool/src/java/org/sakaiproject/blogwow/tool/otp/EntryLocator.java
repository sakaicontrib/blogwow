/**
 * Copyright (c) 2003-2010 The Apereo Foundation
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
package org.sakaiproject.blogwow.tool.otp;

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.blogwow.constants.BlogConstants;
import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;

import uk.org.ponder.beanutil.WriteableBeanLocator;
import uk.org.ponder.messageutil.TargettedMessage;
import uk.org.ponder.messageutil.TargettedMessageList;

public class EntryLocator implements WriteableBeanLocator {

    public static final String NEW_PREFIX = "new ";
    public static final String NEW_1 = NEW_PREFIX + "1";

    private ExternalLogic externalLogic;
    private EntryLogic entryLogic;
    private BlogLogic blogLogic;
    private TargettedMessageList messages;

    private Map<String, BlogWowEntry> delivered = new HashMap<String, BlogWowEntry>();

	public void setMessages(TargettedMessageList messages) {
		this.messages = messages;
	}


    public Object locateBean(String name) {
        String locationId = externalLogic.getCurrentLocationId();
        String currentUserId = externalLogic.getCurrentUserId();
        BlogWowEntry togo = delivered.get(name);
        if (togo == null) {
            if (name.startsWith(NEW_PREFIX)) {
                // create the new object
                BlogWowBlog blog = blogLogic.makeBlogByLocationAndUser(locationId, currentUserId);
                if (blog == null) {
                    throw new IllegalStateException("Could not get blog for the current user ("+currentUserId+") and location ("+locationId+")");
                }
                togo = new BlogWowEntry(blog, currentUserId, null, null, externalLogic.getEntryViewableSetting(), null);
            } else {
                togo = entryLogic.getEntryById(name, locationId);
            }
            delivered.put(name, togo);
        }
        return togo;
    }

    public String publishAll() {
    	for (String key : delivered.keySet()) {
            BlogWowEntry entry = delivered.get(key);
            //if (key.startsWith(NEW_PREFIX)) {
            //    // could do stuff here
            //}
			if (entry.getPrivacySetting() == null) {
				messages.addMessage(new TargettedMessage("blogwow.noprivacy",null,TargettedMessage.SEVERITY_ERROR));
				return "error";
			}
			// fix BW-104 - if an entry is still set to "private" set it to the default view instead
			if (entry.getPrivacySetting().equals(BlogConstants.PRIVACY_PRIVATE)) {
				entry.setPrivacySetting(externalLogic.getEntryViewableSetting());
			}
            if (entry.getTitle() == null || "".equals(entry.getTitle().trim()) || entry.getTitle().length() == 0 ) {
            	messages.addMessage(new TargettedMessage("blogwow.nottitle",null,TargettedMessage.SEVERITY_ERROR));

            	return "error";
            }

            //we need to check that there is some text
            String check = externalLogic.cleanupUserStrings(entry.getText());
            if (check == null || "".equals(check) || check.length() == 0 ) {
              messages.addMessage(new TargettedMessage("blogwow.notext",null,TargettedMessage.SEVERITY_ERROR));
              return "error";
            }

            entryLogic.saveEntry(entry, externalLogic.getCurrentLocationId());
        }
        return "published";
    }

    public String saveAll() {
        for (BlogWowEntry entry : delivered.values()) {
	    if (entry.getTitle() == null || "".equals(entry.getTitle().trim()) || entry.getTitle().length() == 0 ) {
              messages.addMessage(new TargettedMessage("blogwow.nottitle",null,TargettedMessage.SEVERITY_ERROR));

              return "error";
            }

            //we need to check that there is some text
            String check = externalLogic.cleanupUserStrings(entry.getText());
            if (check == null || "".equals(check) || check.length() == 0 ) {
              messages.addMessage(new TargettedMessage("blogwow.notext",null,TargettedMessage.SEVERITY_ERROR));
              return "error";
            }
            entry.setPrivacySetting(BlogConstants.PRIVACY_PRIVATE);
            entryLogic.saveEntry(entry, externalLogic.getCurrentLocationId());
        }
        return "saved";
    }

    public String removeAll() {
    	for (BlogWowEntry entry : delivered.values()) {
            entryLogic.removeEntry(entry.getId(), externalLogic.getCurrentLocationId());
        }
        return "removed";
    }

    public boolean remove(String beanname) {
        entryLogic.removeEntry(beanname, externalLogic.getCurrentLocationId());
        delivered.remove(beanname);
        return true;
    }

    public void set(String beanname, Object toset) {
        throw new UnsupportedOperationException("Not implemented");
    }



    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    public void setEntryLogic(EntryLogic entryLogic) {
        this.entryLogic = entryLogic;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

}
