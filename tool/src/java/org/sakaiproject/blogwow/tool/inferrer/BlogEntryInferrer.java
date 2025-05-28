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

import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.entity.BlogEntryEntityProvider;
import org.sakaiproject.blogwow.tool.params.BlogParams;
import org.sakaiproject.blogwow.tool.producers.BlogViewProducer;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.rsf.entitybroker.EntityViewParamsInferrer;
import org.sakaiproject.blogwow.model.BlogWowEntry;


import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * Sends the incoming entity URL to the correct location,
 * handles entries, ref id is entry id
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogEntryInferrer implements EntityViewParamsInferrer {

   private EntryLogic entryLogic;
   public void setEntryLogic(EntryLogic entryLogic) {
      this.entryLogic = entryLogic;
   }


   public String[] getHandledPrefixes() {
      return new String[] {BlogEntryEntityProvider.ENTITY_PREFIX};
   }

   public ViewParameters inferDefaultViewParameters(String reference) {
      EntityReference ref = new EntityReference(reference);
      BlogWowEntry bwe = entryLogic.getEntryById(ref.getId(), null);
      if ( bwe != null ) {
         return new BlogParams(BlogViewProducer.VIEW_ID, bwe.getBlog().getId(), ref.getId(), true);
      } else {
         throw new SecurityException("User does not have access to this entity: " + reference);
      }
   }

}
