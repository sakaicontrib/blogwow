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
package org.sakaiproject.blogwow.tool.params;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

/**
 * View parameters which are used for RSS feeds
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogRssViewParams extends SimpleViewParameters {

    public String blogId;
    public String locationId;

    public BlogRssViewParams() {
    }

    /**
     * Used for RSS for a single blog feed
     * @param viewid
     * @param blogId
     */
    public BlogRssViewParams(String viewid, String blogId, String locationId) {
        this.viewID = viewid;
        if (blogId != null && locationId != null) {
            throw new IllegalArgumentException("At most one of blogId and locationId can be set");
        }
        this.blogId = blogId;
        this.locationId = locationId;
    }

}
