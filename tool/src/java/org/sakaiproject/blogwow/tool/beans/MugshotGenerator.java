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
package org.sakaiproject.blogwow.tool.beans;

import java.util.List;

/** Generates a random (but stable) mugshot for a user based on their id - 
 * for visual variety.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class MugshotGenerator {
  private List<String> mugshotImages;
  public void setMugshotImages(List<String> images) {
      this.mugshotImages = images;
  }
  
  public String getMugshotUrl(String userid) {
      int mugshot = Math.abs(userid.hashCode()) % mugshotImages.size();
      return mugshotImages.get(mugshot);
  }
    
}
