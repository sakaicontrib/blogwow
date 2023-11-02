/**
 * Copyright (c) 2003-2007 The Apereo Foundation
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
/*
 * Created on 10 Jun 2007
 */
package org.sakaiproject.blogwow.tool.params;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

public class SimpleBlogParams extends SimpleViewParameters {

    public String blogid;

    public SimpleBlogParams() {}

    public SimpleBlogParams(String viewid, String blogid) {
        this.viewID = viewid;
        this.blogid = blogid;
    }

}
