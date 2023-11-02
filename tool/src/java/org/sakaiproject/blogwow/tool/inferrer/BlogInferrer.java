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

import org.sakaiproject.blogwow.logic.entity.BlogEntityProvider;
import org.sakaiproject.blogwow.tool.params.BlogParams;
import org.sakaiproject.blogwow.tool.producers.BlogViewProducer;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.rsf.entitybroker.EntityViewParamsInferrer;

import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * Sends the incoming entity URL to the correct location,
 * handles blogs, ref id is blog id
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogInferrer implements EntityViewParamsInferrer {

   public String[] getHandledPrefixes() {
      return new String[] {BlogEntityProvider.ENTITY_PREFIX};
   }

   public ViewParameters inferDefaultViewParameters(String reference) {
      EntityReference ref = new EntityReference(reference);
      return new BlogParams(BlogViewProducer.VIEW_ID, ref.getId(), null, false);
   }

}
