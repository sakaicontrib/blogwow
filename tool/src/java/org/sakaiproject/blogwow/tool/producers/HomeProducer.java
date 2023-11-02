/**
 * Copyright (c) 2003-2012 The Apereo Foundation
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

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.tool.beans.MugshotGenerator;
import org.sakaiproject.blogwow.tool.params.BlogParams;
import org.sakaiproject.blogwow.tool.params.SimpleBlogParams;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class HomeProducer implements ViewComponentProducer, DefaultView {

    public static final String VIEW_ID = "home";
    public String getViewID() {
        return VIEW_ID;
    }

    private NavBarRenderer navBarRenderer;
    private BlogLogic blogLogic;
    private EntryLogic entryLogic;
    private Locale locale;
    private MugshotGenerator mugshotGenerator;
    private ExternalLogic externalLogic;

    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

        String currentUserId = externalLogic.getCurrentUserId();
        String locationId = externalLogic.getCurrentLocationId();

        UIMessage.make(tofill, "page-title", "blogwow.homepage.title");

        navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEW_ID);
        
        UIMessage.make(tofill, "blogger", "blogwow.homepage.nameheader");
        UIMessage.make(tofill, "entries", "blogwow.homepage.entryheader");
        UIMessage.make(tofill, "last-updated", "blogwow.homepage.lastupdated");

        BlogWowBlog myblog = blogLogic.makeBlogByLocationAndUser(locationId, currentUserId );
        if (myblog != null && myblog.getId() != null) {
            UIBranchContainer myBlogInfo = UIBranchContainer.make(tofill, "my-blog-exists:");
            UIInternalLink.make(myBlogInfo, "my-blog-link", 
                    UIMessage.make("blogwow.homepage.userbloglink"), 
                    new SimpleBlogParams(BlogViewProducer.VIEW_ID, myblog.getId()));
            // use a date which is related to the current users locale
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
            DateFormat tf = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);

            List<BlogWowEntry> myentries = entryLogic.getAllVisibleEntries(myblog.getId(), currentUserId, null, true, 0, 1);
            if (myentries.size() > 0) {
                UIMessage.make(myBlogInfo, "last-blogged-date", "blogwow.homepage.userlastblogged", 
                        new Object[] { df.format( myentries.get(0).getDateModified() ),
                        tf.format( myentries.get(0).getDateModified() ) });
            }

            UILink.make(myBlogInfo, "my-rss-link", externalLogic.getBlogRssUrl(myblog.getId()));
        } else {
            UIMessage.make(tofill, "no-my-blog", "blogwow.homepage.noblog");
        }

        UIMessage.make(tofill, "all-blogs-header", "blogwow.homepage.listofblogs");

        // This needs more work due to URL-encoding of path segments
        String siteId = locationId.replace("/site/", "");
        UILink.make(tofill, "viewallblogs-icon", "../images/group.png");
        UIInternalLink.make(tofill, "all-blogs", UIMessage.make("blogwow.homepage.allblogs"), 
                new BlogParams(BlogViewProducer.VIEW_ID, true, locationId));
        UILink.make(tofill, "all-blog-rss", externalLogic.getBlogLocationRssUrl(siteId));

        List<BlogWowBlog> blogs = blogLogic.getAllVisibleBlogs(locationId, null, true, 0, 0);
        UIBranchContainer blogsTable = UIBranchContainer.make(tofill, "blog-list-table:");

        for (int i = 0; i < blogs.size(); i++) {
            BlogWowBlog blog = blogs.get(i);
            Integer entriesCount = entryLogic.getVisibleEntryCount(blog.getId(), currentUserId);

            if (entriesCount > 0) {
	            UIBranchContainer row = UIBranchContainer.make(blogsTable, "row:", i+"");
	            UILink.make(row, "user-icon", mugshotGenerator.getMugshotUrl(blog.getOwnerId()));
	            UIInternalLink.make(row, "blog-title-link", blog.getTitle(), 
	                    new SimpleBlogParams(BlogViewProducer.VIEW_ID, blog.getId()));
	            UIOutput.make(row, "number-of-entries", entriesCount +"");
            	List<BlogWowEntry> entries = entryLogic.getAllVisibleEntries(blog.getId(), currentUserId, null, true, 0, 1);
								try
								{
				        	DateFormat dtf = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
                	UIOutput.make(row, "time-last-updated", dtf.format(entries.get(0).getDateModified()) );
								}catch (IndexOutOfBoundsException e) {
									// This shouldn't happen, our count doesn't agree with the number of entries we get
									UIOutput.make(row, "time-last-updated", "???" );
								}

	            // UIInternalLink.make(row, "rss-link", new BlogParams(BlogRSSProducer.VIEWID, blog.getId().toString()));
	            UILink.make(row, "rss-link", externalLogic.getBlogRssUrl(blog.getId()));
	            
            }
        }

    }

    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    public void setEntryLogic(EntryLogic entryLogic) {
        this.entryLogic = entryLogic;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setMugshotGenerator(MugshotGenerator mugshotGenerator) {
        this.mugshotGenerator = mugshotGenerator;
    }

    public void setNavBarRenderer(NavBarRenderer navBarRenderer) {
        this.navBarRenderer = navBarRenderer;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

}
