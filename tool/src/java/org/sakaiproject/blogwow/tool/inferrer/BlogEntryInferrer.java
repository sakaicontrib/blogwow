/******************************************************************************
 * BlogEntryInferrer.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

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
