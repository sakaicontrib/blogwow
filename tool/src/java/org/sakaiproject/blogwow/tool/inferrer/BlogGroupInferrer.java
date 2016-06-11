package org.sakaiproject.blogwow.tool.inferrer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.sakaiproject.blogwow.logic.entity.BlogGroupEntityProvider;
import org.sakaiproject.blogwow.tool.params.BlogParams;
import org.sakaiproject.blogwow.tool.producers.BlogViewProducer;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.rsf.entitybroker.EntityViewParamsInferrer;

import uk.org.ponder.rsf.viewstate.ViewParameters;

public class BlogGroupInferrer implements EntityViewParamsInferrer {
	public String[] getHandledPrefixes() {
	      return new String[] {BlogGroupEntityProvider.ENTITY_PREFIX};
	   }

	   public ViewParameters inferDefaultViewParameters(String reference) {
	      String decoded = null;
	      EntityReference ref = new EntityReference(reference);
	      try {
	         decoded = URLDecoder.decode(ref.getId(), "UTF-8");
	      } catch (UnsupportedEncodingException e) {
	         throw new IllegalArgumentException(e);
	      }
	      return new BlogParams(BlogViewProducer.VIEW_ID, true, 
	            "/site/" + decoded);
	   }
}
