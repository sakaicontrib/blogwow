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

import java.util.Properties;

import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Tool;

/**
 * Test class for the Sakai Placement object<br/> This has to be here since I cannot create a Placement object in Sakai for some reason...
 * sure would be nice if I could though -AZ
 * 
 * @author Sakai App Builder -AZ
 */
public class TPlacement implements Placement {

    private String id = "FAKE12345";

    private String context; // a.k.a. siteId

    private String title;

    private Tool tool;

    private String toolId;

    /**
     * Construct a test Placement object with a context (siteId) set
     * 
     * @param context
     *            a String representing a site context (siteId)
     */
    public TPlacement(String context) {
        this.context = context;
    }

    public Properties getConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getContext() {
        return context;
    }

    public String getId() {
        return id;
    }

    public Properties getPlacementConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getTitle() {
        return this.title;
    }

    public Tool getTool() {
        return tool;
    }

    public String getToolId() {
        return toolId;
    }

    public void save() {
        // TODO Auto-generated method stub
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTool(String toolId, Tool tool) {
        this.tool = tool;
        this.toolId = toolId;
    }

}
