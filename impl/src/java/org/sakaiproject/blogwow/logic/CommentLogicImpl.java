/******************************************************************************
 * CommentLogicImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic;

import java.util.Date;
import java.util.List;

import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.genericdao.api.search.Order;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation
 * 
 * @author Sakai App Builder -AZ
 */
@Slf4j
public class CommentLogicImpl implements CommentLogic {


    private ExternalLogic externalLogic;

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

    private BlogWowDao dao;

    public void setDao(BlogWowDao dao) {
        this.dao = dao;
    }

    private EntryLogic entryLogic;

    public void setEntryLogic(EntryLogic entryLogic) {
        this.entryLogic = entryLogic;
    }

    /*
     * (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.CommentLogic#getCommentById(java.lang.Long, java.lang.String)
     */
    public BlogWowComment getCommentById(String commentId, String locationId) {
        String currentUserId = externalLogic.getCurrentUserId();
        BlogWowComment comment = (BlogWowComment) dao.findById(BlogWowComment.class, commentId);
        //comment may be null
        if (comment == null)
        	return null;
        
        String entryId = comment.getEntry().getId();
        if (entryLogic.getEntryById(entryId, locationId) != null) {
            return comment;
        } else {
            throw new SecurityException("User (" + currentUserId + ") cannot access this comment (" + commentId + ") in this location ("
                    + locationId + ")");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.CommentLogic#removeComment(java.lang.Long, java.lang.String)
     */
    public void removeComment(String commentId, String locationId) {
        String currentUserId = externalLogic.getCurrentUserId();
        BlogWowComment comment = getCommentById(commentId, locationId);
        if (canRemoveComment(commentId, currentUserId)) {
            dao.delete(comment);
            externalLogic.registerEntityEvent("blog.comment.removed", BlogWowComment.class, comment.getId());
        } else {
            throw new SecurityException(currentUserId + " cannot remove this comment (" + commentId + ") in location: " + locationId);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.CommentLogic#saveComment(org.sakaiproject.blogwow.model.BlogWowComment, java.lang.String)
     */
    public void saveComment(BlogWowComment comment, String locationId) {
        String currentUserId = externalLogic.getCurrentUserId();
        comment.setDateCreated(new Date());
        // set the owner to current if not set
        if (comment.getOwnerId() == null) {
            comment.setOwnerId(externalLogic.getCurrentUserId());
        }
        if (comment.getDateModified() == null) {
            comment.setDateModified(new Date());
        }
        // save comment if new only
        if (comment.getId() == null) {
            if (canAddComment(comment.getEntry().getId(), currentUserId)) {
                dao.save(comment);
                log.debug("Saving comment: " + comment.getId() + ":" + comment.getText());
                externalLogic.registerEntityEvent("blog.comment.added", BlogWowComment.class, comment.getId());
            } else {
                throw new SecurityException(currentUserId + " cannot add comment in location: " + locationId);
            }
        } else {
            throw new IllegalStateException("Current comment cannot be saved, comments cannot be changed after they are saved");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.CommentLogic#getComments(java.lang.Long, java.lang.String, boolean, int, int)
     */
    public List<BlogWowComment> getComments(String entryId, String sortProperty, boolean ascending, int start, int limit) {
       BlogWowEntry entry = entryLogic.getEntryById(entryId, null);
       if (entry == null) {
          throw new IllegalArgumentException("entry id is invalid (" + entryId + "), cannot find related entry object");
       }

       if (sortProperty == null) {
          sortProperty = "dateCreated";
          //ascending = false;
       }

       List<BlogWowComment> comments = dao.findBySearch(BlogWowComment.class, new Search(
                new Restriction("entry.id", entryId),
                new Order(sortProperty, ascending),
                start, limit
             ));
       return comments;
    }

    /*
     * (non-Javadoc)
     * @see org.sakaiproject.blogwow.logic.CommentLogic#canRemoveComment(java.lang.Long, java.lang.String)
     */
    public boolean canRemoveComment(String commentId, String userId) {
        log.debug("commentId: " + commentId + ", userId: " + userId);
        if (externalLogic.isUserAdmin(userId)) {
            // the system super user can remove comments
            return true;
        }
        BlogWowComment comment = (BlogWowComment) dao.findById(BlogWowComment.class, commentId);
        String locationId = comment.getEntry().getBlog().getLocation();
        if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_COMMENTS_REMOVE_ANY, locationId)) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sakaiproject.blogwow.logic.CommentLogic#canAddComment(java.lang.Long, java.lang.String)
     */
    public boolean canAddComment(String entryId, String userId) {
        log.debug("entryId: " + entryId + ", userId: " + userId);
        if (externalLogic.isUserAdmin(userId)) {
            // the system super user can write
            return true;
        }

        BlogWowEntry entry = (BlogWowEntry) dao.findById(BlogWowEntry.class, entryId);
        BlogWowBlog blog = entry.getBlog();
        if (blog.getOwnerId().equals(userId) || entry.getOwnerId().equals(userId)) {
            // blog and entry owner can add comments
            return true;
        } else if (externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_COMMENTS_ADD, blog.getLocation())) {
            // users with permission in the specified location can add comments for that location
            return true;
        }
        return false;
    }

}
