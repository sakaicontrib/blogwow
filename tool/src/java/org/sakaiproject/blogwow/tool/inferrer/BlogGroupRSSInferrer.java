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
package org.sakaiproject.blogwow.tool.inferrer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.sakaiproject.blogwow.logic.entity.BlogGroupRssEntityProvider;
import org.sakaiproject.blogwow.tool.params.BlogRssViewParams;
import org.sakaiproject.blogwow.tool.producers.BlogRSSProducer;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.rsf.entitybroker.EntityViewParamsInferrer;

import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * Sends the incoming entity URL to the correct location,
 * handles blog groups and ref goes to location
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogGroupRSSInferrer implements EntityViewParamsInferrer {

   public String[] getHandledPrefixes() {
      return new String[] {BlogGroupRssEntityProvider.ENTITY_PREFIX};
   }

   public ViewParameters inferDefaultViewParameters(String reference) {
      String decoded = null;
      EntityReference ref = new EntityReference(reference);
      try {
         decoded = URLDecoder.decode(ref.getId(), "UTF-8");
      } catch (UnsupportedEncodingException e) {
         throw new IllegalArgumentException(e);
      }
      return new BlogRssViewParams(BlogRSSProducer.VIEW_ID, null, 
            "/site/" + decoded);
   }

}
