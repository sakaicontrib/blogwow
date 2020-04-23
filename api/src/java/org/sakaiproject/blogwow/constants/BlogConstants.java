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
package org.sakaiproject.blogwow.constants;

/**
 * Stores constants for use throughout the blog services, logic layer, and dao layer Render constants should not be stored here
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogConstants {

    /**
     * visible to owner only
     */
    public final static String PRIVACY_PRIVATE = "private";

    /**
     * visible to owner and group leader (instructor) of group related to blog
     */
    public final static String PRIVACY_GROUP_LEADER = "leader";

    /**
     * visible to group related to blog
     */
    public final static String PRIVACY_GROUP = "group";

    /**
     * visible to anyone
     */
    public final static String PRIVACY_PUBLIC = "public";

}
