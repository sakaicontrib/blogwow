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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.tool.params.BlogRssViewParams;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.content.ContentTypeInfoRegistry;
import uk.org.ponder.rsf.content.ContentTypeReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class BlogRSSProducer implements 
ViewComponentProducer, 
ViewParamsReporter,
ContentTypeReporter
{
    public static final String VIEW_ID = "blog_rss";
    public String getViewID() {
        return VIEW_ID;
    }

    private static final SimpleDateFormat rfc822Format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    
    private BlogLogic blogLogic;
    private EntryLogic entryLogic;
    private ExternalLogic externalLogic;

    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
        String currentUserId = externalLogic.getCurrentUserId();

        BlogRssViewParams params = (BlogRssViewParams) viewparams;
        String blogId = params.blogId;
        String locationId = params.locationId;

        List<BlogWowEntry> entries = new ArrayList<BlogWowEntry>();
        // get the entries for a single blog or a group of blogs depending on what is passed in
        if (blogId != null) {
            BlogWowBlog blog = blogLogic.getBlogById( blogId );
            UIOutput.make(tofill, "channel-title", blog.getTitle());
            UIOutput.make(tofill, "channel_link", externalLogic.getBlogUrl(blogId));
            entries = entryLogic.getAllVisibleEntries(blogId, currentUserId, null, true, 0, 10);                        
        } else if (locationId != null) {
            UIOutput.make(tofill, "channel-title", externalLogic.getLocationTitle(locationId));
            // TODO - Add direct linking to site
            //UIOutput.make(tofill, "channel_link", externalLogic.getBlogUrl(locationId));
            List<BlogWowBlog> blogs = blogLogic.getAllVisibleBlogs(locationId, null, true, 0, 10);
            String[] blogIds = new String[blogs.size()];
            for (int i=0; i<blogs.size(); i++) {
                blogIds[i] = (blogs.get(i)).getId();
            }
            entries = entryLogic.getAllVisibleEntries(blogIds, currentUserId, null, true, 0, 10);                        
        }

        Date publishDate = null;
        for (int i = 0; i < entries.size(); i++) {
            BlogWowEntry entry = entries.get(i);
            UIBranchContainer rssitem = UIBranchContainer.make(tofill, "item:", i+"");
            UIOutput.make(rssitem, "item-title", entry.getTitle());
            UIOutput.make(rssitem, "creator", entry.getOwnerId());
            UIOutput.make(rssitem, "item_link", externalLogic.getBlogEntryUrl(entry.getId()));
            if (publishDate == null ||
                    entry.getDateModified().before(publishDate)) {
                publishDate = entry.getDateModified();
            }
            UIOutput.make(rssitem, "item_publish_date", rfc822Format.format(entry.getDateModified()));

            String desc = "<![CDATA[" 
                + ( entry.getText().length() < 200 
                        ? entry.getText().substring(0, entry.getText().length())
                                : entry.getText().substring(0, 200)) 
                                + "]]>";
            String content = "<![CDATA[" + entry.getText() + "]]>";
            UIVerbatim.make(rssitem, "description", desc);
            UIVerbatim.make(rssitem, "content" ,content);
        }

        if (publishDate == null) { publishDate = new Date(); }
        UIOutput.make(tofill, "channel_publish_date", rfc822Format.format(publishDate));
    }

    public ViewParameters getViewParameters() {
        return new BlogRssViewParams();
    }

    public String getContentType() {
        return ContentTypeInfoRegistry.RSS_2;
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
