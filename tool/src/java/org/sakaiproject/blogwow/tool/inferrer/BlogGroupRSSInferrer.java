/******************************************************************************
 * BlogGroupRSSInferrer.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

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
