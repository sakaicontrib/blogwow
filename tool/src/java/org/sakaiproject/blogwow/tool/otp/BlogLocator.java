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
package org.sakaiproject.blogwow.tool.otp;

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;

import uk.org.ponder.beanutil.BeanLocator;

public class BlogLocator implements BeanLocator {

    public static final String NEW_PREFIX = "new ";
    public static String NEW_1 = NEW_PREFIX + "1";

	private BlogLogic blogLogic;
    private ExternalLogic externalLogic;

	private Map<String, BlogWowBlog> delivered = new HashMap<String, BlogWowBlog>();

	public Object locateBean(String name) {
        BlogWowBlog togo = delivered.get(name);
        if (togo == null) {
            if (name.startsWith(NEW_PREFIX)) {
                // create the new object
                String currentUserId = externalLogic.getCurrentUserId();
                togo = new BlogWowBlog(currentUserId, externalLogic.getCurrentLocationId(), null );
            } else {
                togo = blogLogic.getBlogById(name);
            }
            delivered.put(name, togo);
        }
        return togo;
	}

	public String saveAll() {
		for (String key : delivered.keySet()) {
            BlogWowBlog blog = delivered.get(key);
            //if (key.startsWith(NEW_PREFIX)) {
            //    // could do stuff here
            //}
            blogLogic.saveBlog(blog, blog.getLocation());
        }
		return "saved";
	}


	public void setBlogLogic(BlogLogic blogLogic) {
		this.blogLogic = blogLogic;
	}

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

}
