/**
 * Copyright (c) 2003-2008 The Apereo Foundation
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
package org.sakaiproject.blogwow.tool.producers;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.tool.params.BlogEntryParams;
import org.sakaiproject.blogwow.tool.params.SimpleBlogParams;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIJointContainer;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

public class NavBarRenderer {

    private ExternalLogic externalLogic;
    private BlogLogic blogLogic;

    public void makeNavBar(UIContainer tofill, String divID, String currentViewID) {
        BlogWowBlog blog = blogLogic.makeBlogByLocationAndUser(externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId());

        UIJointContainer joint = new UIJointContainer(tofill, divID, "blog-wow-navigation:", ""+1);

        if (blog != null) {
            UIBranchContainer cell = UIBranchContainer.make(joint, "navigation-cell:", "1");
            UILink.make(cell, "item-icon", "../images/page_white_edit.png");
            if (currentViewID.equals(AddEntryProducer.VIEW_ID)) {
                UIMessage.make(cell, "item-text", "blogwow.navbar.add");
            } else {
                // user cannot create blog so no add entry link
                UIInternalLink.make(cell, "item-link", UIMessage.make("blogwow.navbar.add"), 
                        new BlogEntryParams(AddEntryProducer.VIEW_ID, blog.getId(), null));               
            }
            UIOutput.make(cell, "item-separator");
        }
        UIBranchContainer cell = UIBranchContainer.make(joint, "navigation-cell:", "2");
        UILink.make(cell, "item-icon", "../images/page_white_edit.png");
        if (currentViewID.equals(HomeProducer.VIEW_ID)) {
            UIMessage.make(cell, "item-text", "blogwow.navbar.bloglist");
        } else {
            UIInternalLink.make(cell, "item-link", UIMessage.make("blogwow.navbar.bloglist"), new SimpleViewParameters(HomeProducer.VIEW_ID));
        }

        if (!externalLogic.useGlobalProfile() && blog != null) {
            UIOutput.make(cell, "item-separator");
        	cell = UIBranchContainer.make(joint, "navigation-cell:", "3");
            UILink.make(cell, "item-icon", "../images/cog.png");
            if (currentViewID.equals(MySettingsProducer.VIEW_ID)) {
                UIMessage.make(cell, "item-text", "blogwow.navbar.settings");
            } else {
                UIInternalLink.make(cell, "item-link", 
                        UIMessage.make("blogwow.navbar.settings"), 
                            new SimpleBlogParams(MySettingsProducer.VIEW_ID, blog.getId()));
            }
        }      
        
        if (externalLogic.isUserSiteAdmin(externalLogic.getCurrentUserId(), externalLogic.getCurrentLocationId())) {       
            UIOutput.make(cell, "item-separator");
        	cell = UIBranchContainer.make(joint, "navigation-cell:", "4");
	        UILink.make(cell, "item-icon", "../images/group_gear.png");
	        if (currentViewID.equals(PermissionsProducer.VIEW_ID))
	            UIMessage.make(cell, "item-text", "blogwow.navbar.permissions");
	        else
	            UIInternalLink.make(cell, "item-link", UIMessage.make("blogwow.navbar.permissions"), new SimpleViewParameters(PermissionsProducer.VIEW_ID));  
        }

 
    }

    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

}
