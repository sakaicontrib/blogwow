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
package org.sakaiproject.blogwow.logic.entity;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.entity.BlogRssEntityProvider;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;

/**
 * Implementation of the provider for single blog rss
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogRssEntityProviderImpl implements BlogRssEntityProvider, CoreEntityProvider, AutoRegisterEntityProvider {

    private BlogLogic blogLogic;

    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    /*
     * (non-Javadoc)
     * @see org.sakaiproject.entitybroker.entityprovider.EntityProvider#getEntityPrefix()
     */
    public String getEntityPrefix() {
        return ENTITY_PREFIX;
    }

    /*
     * (non-Javadoc)
     * @see org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider#entityExists(java.lang.String)
     */
    public boolean entityExists(String id) {
        BlogWowBlog blog = blogLogic.getBlogById(id);
        if (blog == null) {
            return false;
        }
        return true;
    }

}
