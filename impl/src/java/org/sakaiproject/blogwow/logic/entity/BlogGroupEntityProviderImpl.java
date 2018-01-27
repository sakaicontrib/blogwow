package org.sakaiproject.blogwow.logic.entity;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.entitybroker.EntityBroker;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;

public class BlogGroupEntityProviderImpl  implements BlogGroupEntityProvider, CoreEntityProvider, AutoRegisterEntityProvider {

	private BlogLogic blogLogic;
    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    private EntityBroker entityBroker;
    public void setEntityBroker(EntityBroker entityBroker) {
        this.entityBroker = entityBroker;
    }


    /*
     * (non-Javadoc)
     * @see org.sakaiproject.entitybroker.entityprovider.EntityProvider#getEntityPrefix()
     */
    public String getEntityPrefix() {
        return ENTITY_PREFIX;
    }

    /*
     * (non-Javadoc)
     * @see org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider#entityExists(java.lang.String)
     */
    public boolean entityExists(String id) {
        // entity is real if there are any blogs in this location (id should be an entity ref)
        String locationId = "/site/" + id;
        if (entityBroker.entityExists(locationId)) {
            // entity container is real, check for number of blogs
            if (blogLogic.getAllVisibleBlogs(locationId, null, false, 0, 1).size() > 0) {
                // there is at least one visible blog here
                return true;
            }
        }
        return false;
    }
    
}
