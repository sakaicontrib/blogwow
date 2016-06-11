/******************************************************************************
 * BlogInferrer.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

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
