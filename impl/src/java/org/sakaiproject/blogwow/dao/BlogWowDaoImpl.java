/******************************************************************************
 * BlogWowDaoImpl.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2006 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.sakaiproject.blogwow.constants.BlogConstants;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.sakaiproject.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementations of any specialized DAO methods from the specialized DAO that allows the developer to extend the functionality of the
 * generic dao package
 * 
 * @author Sakai App Builder -AZ
 */
@Slf4j
public class BlogWowDaoImpl extends HibernateGeneralGenericDao implements BlogWowDao {

  

   public void init() {
      log.debug("init");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.sakaiproject.blogwow.dao.BlogWowDao#getLocationsForBlogsIds(java.lang.Long[])
    */
   @SuppressWarnings("unchecked")
   public List<String> getLocationsForBlogsIds(String[] blogIds) {
      String hql = "select distinct blog.location from BlogWowBlog as blog where blog.id in " + arrayToInString(blogIds)
      + " order by blog.location";
      return  (List<String>)getHibernateTemplate().find(hql);

      // alternate method using detached criteria
      // if (blogIds.length == 0) { return new ArrayList(); }
      // DetachedCriteria dc = DetachedCriteria.forClass(BlogWowBlog.class)
      // .add(Property.forName("id").in(blogIds))
      // .setProjection(Projections.distinct(Projections.property("location")))
      // .addOrder( Order.asc("location") );
      // return getHibernateTemplate().findByCriteria(dc);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.sakaiproject.blogwow.dao.BlogWowDao#getBlogPermEntries(java.lang.Long[], java.lang.String, java.lang.String[],
    *      java.lang.String[], java.lang.String, boolean, int, int)
    */
   public List<BlogWowEntry> getBlogPermEntries(String[] blogIds, String userId, 
         String[] readLocations, String[] readAnyLocations, String sortProperty,
         boolean ascending, int start, int limit) {

      // * rules to determine which entries to get
      // * 1) entry.blog.id is in blogIds AND
      // * 2) (entry.privacy is public OR
      // * 3) entry.blog.owner is userId OR
      // * 4) entry.owner is userId OR
      // * 5) (entry.privacy is group AND entry.blog.location is in readLocations))
      // * 6) (entry.privacy is leader AND entry.blog.location is in readAnyLocations))

      if (sortProperty == null) {
         sortProperty = "dateCreated";
         ascending = false;
      }

      // String hql = "from BlogWowEntry entry where entry.blog.id in " +
      // arrayToInString(blogIds) +
      // " and (entry.privacySetting = '"+BlogConstants.PRIVACY_PUBLIC+"'";
      // if (userId != null) {
      // hql += " or entry.blog.ownerId = '"+userId+"' or entry.ownerId =
      // '"+userId+"'";
      // }
      // if (readLocations != null && readLocations.length > 0) {
      // hql += " or (entry.privacySetting = '"+BlogConstants.PRIVACY_GROUP+"' and
      // " +
      // "entry.blog.location in " + arrayToInString(readLocations) + ")";
      // }
      // if (readAnyLocations != null && readAnyLocations.length > 0) {
      // hql += " or (entry.privacySetting =
      // '"+BlogConstants.PRIVACY_GROUP_LEADER+"' and " +
      // "entry.blog.location in " + arrayToInString(readAnyLocations) + ")";
      // }
      // hql += ")";
      // if (sortProperty != null && ! sortProperty.equals("")) {
      // if (ascending) {
      // hql += " order by " + sortProperty + " asc";
      // } else {
      // hql += " order by " + sortProperty + " desc";
      // }
      // }
      // Query query = getSession().createQuery(hql);
      // query.setFirstResult(start);
      // if (limit > 0) { query.setMaxResults(limit); }
      // return query.list();

      // Hibernate in list queries generate invalid SQL for empty lists,
      // Guard this case explicitly.
      if (blogIds == null || blogIds.length == 0) {
         return new ArrayList<BlogWowEntry>();
      }

      Map<String, Object> params = new HashMap<String, Object>();
      // hibernate 3 syntax
//    String hql = "select entry from BlogWowEntry as entry join entry.blog as eblog with eblog.id in (:blogIds) " + 
//    " where (entry.privacySetting = :privacyPublic";
      String hql = "select entry from BlogWowEntry as entry join entry.blog as eblog where eblog.id in (:blogIds) " + 
      " and (entry.privacySetting = :privacyPublic";
      params.put("privacyPublic", BlogConstants.PRIVACY_PUBLIC);
      params.put("blogIds", blogIds);
      if (userId != null) {
         hql += " or entry.blog.ownerId = :userId or entry.ownerId = :userId";
         params.put("userId", userId);
      }
      if (readLocations != null && readLocations.length > 0) {
         hql += " or (entry.privacySetting = :privacyGroup and " + "entry.blog.location in (:readLocations))";
         params.put("privacyGroup", BlogConstants.PRIVACY_GROUP);
         params.put("readLocations", readLocations);
      }
      if (readAnyLocations != null && readAnyLocations.length > 0) {
         hql += " or (entry.privacySetting = :privacyGroupLeader and " + "entry.blog.location in (:readAnyLocations))";
         params.put("privacyGroupLeader", BlogConstants.PRIVACY_GROUP_LEADER);
         params.put("readAnyLocations", readAnyLocations);
      }
      hql += ")";
      if (sortProperty != null && !sortProperty.equals("")) {
         if (ascending) {
            hql += " order by entry." + sortProperty + " asc";
         } else {
            hql += " order by entry." + sortProperty + " desc";
         }
      }
      Session session = getSessionFactory().getCurrentSession();
      Query<BlogWowEntry> query = session.createQuery(hql);
      query.setFirstResult(start);
      if (limit > 0) {
         query.setMaxResults(limit);
      }
      setParameters(query, params);

      @SuppressWarnings("unchecked")
      List<BlogWowEntry> result = query.list();
      return result;

      // DetachedCriteria entry = DetachedCriteria.forClass(BlogWowEntry.class).
      // createAlias("blog", "ownerblog");
      //        
      // // Start building up the main "OR" branch with the only non-optional
      // // criterion, the privacy setting.
      // Junction disjunction = Expression.disjunction().add(
      // Property.forName("privacySetting").eq(BlogConstants.PRIVACY_PUBLIC));
      //
      // if (userId != null) {
      // disjunction.add(Restrictions.eq("ownerblog.ownerId", userId))
      // .add(Restrictions.eq("ownerId", userId));
      // }
      //
      // if (readLocations != null && readLocations.length > 0) {
      // disjunction.add(
      // Expression.and(
      // Restrictions.eq("privacySetting", BlogConstants.PRIVACY_GROUP),
      // Restrictions.in("ownerblog.location", readLocations)));
      // }
      //        
      // if (readAnyLocations != null && readAnyLocations.length > 0) {
      // disjunction.add(
      // Expression.and(
      // Restrictions.eq("privacySetting", BlogConstants.PRIVACY_GROUP_LEADER),
      // Restrictions.in("ownerblog.location", readAnyLocations)));
      // }
      //
      // // Collect together all the terms onto the root criterion.
      //        
      // DetachedCriteria dc = entry.add(
      // Expression.and(Restrictions.in("ownerblog.id", blogIds), disjunction));
      //
      // if (sortProperty != null && !sortProperty.equals("")) {
      // if (ascending) {
      // dc.addOrder( Order.asc(sortProperty) );
      // } else {
      // dc.addOrder( Order.desc(sortProperty) );
      // }
      // }
      //
      // return getHibernateTemplate().findByCriteria(dc, start, limit);
   }

   public Integer getBlogPermCount(String[] blogIds, String userId,
         String[] readLocations, String[] readAnyLocations) {

      int count = 0;

      if (blogIds != null && blogIds.length > 0)
      {
         StringBuilder hql = new StringBuilder("from BlogWowEntry as entry join entry.blog as eblog where eblog.id in ('");
         hql.append(StringUtil.unsplit(blogIds, "','"));
         hql.append("') and (entry.privacySetting = '");
         hql.append(BlogConstants.PRIVACY_PUBLIC);
         hql.append("'");

         if (userId != null) {
            hql.append(" or entry.blog.ownerId = '");
            hql.append(userId);
            hql.append("' or entry.ownerId = '");
            hql.append(userId);
            hql.append("'");
         }
         if (readLocations != null && readLocations.length > 0) {
            hql.append(" or (entry.privacySetting = '");
            hql.append(BlogConstants.PRIVACY_GROUP);
            hql.append("' and entry.blog.location in ('");
            hql.append(StringUtil.unsplit(readLocations,"','"));
            hql.append("'))");
         }
         if (readAnyLocations != null && readAnyLocations.length > 0) {
            hql.append(" or (entry.privacySetting = '");
            hql.append(BlogConstants.PRIVACY_GROUP_LEADER);
            hql.append("' and entry.blog.location in ('");
            hql.append(StringUtil.unsplit(readAnyLocations,"','"));
            hql.append("'))");
         }
         hql.append(")");

         count = count(hql.toString());
      }

      return count;

   }

   /**
    * Turn an array into a string like "('item1','item2','item3')"
    * 
    * @param array
    * @return
    */
   private String arrayToInString(Object[] array) {
      StringBuffer arrayString = new StringBuffer("('");
      for (int i = 0; i < array.length; i++) {
         if (i > 0)
            arrayString.append("','" + array[i]);
         else
            arrayString.append(array[i]);
      }
      arrayString.append("')");
      return arrayString.toString();
   }

   /*
    * (non-Javadoc)
    *
    * @see org.sakaiproject.blogwow.dao.BlogWowDao#getBlogPermEntries(java.lang.Long[], java.lang.String, java.lang.String[],
    *      java.lang.String[], java.lang.String, boolean, Date, int)
    */
   public List<BlogWowEntry> getBlogPermEntries(String[] blogIds, String userId, 
         String[] readLocations, String[] readAnyLocations, String sortProperty,
         boolean ascending, Date start, int limit) {

      // * rules to determine which entries to get
      // * 1) entry.blog.id is in blogIds AND
      // * 2) (entry.privacy is public OR
      // * 3) entry.blog.owner is userId OR
      // * 4) entry.owner is userId OR
      // * 5) (entry.privacy is group AND entry.blog.location is in readLocations))
      // * 6) (entry.privacy is leader AND entry.blog.location is in readAnyLocations))

      if (sortProperty == null) {
         sortProperty = "dateCreated";
         ascending = false;
      }

      if (blogIds == null || blogIds.length == 0) {
         return new ArrayList<BlogWowEntry>();
      }

      Map<String, Object> params = new HashMap<String, Object>();

      String hql = "select entry from BlogWowEntry as entry join entry.blog as eblog where eblog.id in (:blogIds) " + 
      " and (entry.privacySetting = :privacyPublic";
      params.put("privacyPublic", BlogConstants.PRIVACY_PUBLIC);
      params.put("blogIds", blogIds);
      if (userId != null) {
         hql += " or entry.blog.ownerId = :userId or entry.ownerId = :userId";
         params.put("userId", userId);
      }
      if (readLocations != null && readLocations.length > 0) {
         hql += " or (entry.privacySetting = :privacyGroup and " + "entry.blog.location in (:readLocations))";
         params.put("privacyGroup", BlogConstants.PRIVACY_GROUP);
         params.put("readLocations", readLocations);
      }
      if (readAnyLocations != null && readAnyLocations.length > 0) {
         hql += " or (entry.privacySetting = :privacyGroupLeader and " + "entry.blog.location in (:readAnyLocations))";
         params.put("privacyGroupLeader", BlogConstants.PRIVACY_GROUP_LEADER);
         params.put("readAnyLocations", readAnyLocations);
      }
      hql += ")";
      if (start != null) {
        hql += " and entry.dateCreated >= (:start)";
        params.put("start", start);
      }
      if (sortProperty != null && !sortProperty.equals("")) {
         if (ascending) {
            hql += " order by entry." + sortProperty + " asc";
         } else {
            hql += " order by entry." + sortProperty + " desc";
         }
      }
      
      Session session = getSessionFactory().getCurrentSession();
      Query<BlogWowEntry> query = session.createQuery(hql);

      if (limit > 0) {
         query.setMaxResults(limit);
      }
      setParameters(query, params);

      @SuppressWarnings("unchecked")
      List<BlogWowEntry> result = query.list();
      return result;
   }
}
