/******************************************************************************
 * BlogRSSInferrer.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.tool.inferrer;

import org.sakaiproject.blogwow.logic.entity.BlogRssEntityProvider;
import org.sakaiproject.blogwow.tool.params.BlogRssViewParams;
import org.sakaiproject.blogwow.tool.producers.BlogRSSProducer;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.rsf.entitybroker.EntityViewParamsInferrer;

import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * Sends the incoming entity URL to the correct location,
 * handles blogs, ref id is blog id
 * 
 * @author Sakai App Builder -AZ
 */
public class BlogRSSInferrer implements EntityViewParamsInferrer {

   public String[] getHandledPrefixes() {
      return new String[] {BlogRssEntityProvider.ENTITY_PREFIX};
   }

   public ViewParameters inferDefaultViewParameters(String reference) {
      EntityReference ref = new EntityReference(reference);
      return new BlogRssViewParams(BlogRSSProducer.VIEW_ID, ref.getId(), null );
   }

}
