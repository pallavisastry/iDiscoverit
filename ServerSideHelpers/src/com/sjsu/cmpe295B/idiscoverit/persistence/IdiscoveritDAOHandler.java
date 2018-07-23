package com.sjsu.cmpe295B.idiscoverit.persistence;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.classic.Session;

import com.sjsu.cmpe295B.idiscoverit.model.Category;
import com.sjsu.cmpe295B.idiscoverit.model.FavoriteBean;
import com.sjsu.cmpe295B.idiscoverit.model.FeedbackBean;
import com.sjsu.cmpe295B.idiscoverit.model.Media;
import com.sjsu.cmpe295B.idiscoverit.model.TagBean;
import com.sjsu.cmpe295B.idiscoverit.model.User;
import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.HibernateUtil;

//TODO:1)Check if media exists before adding new one to DB-done
public class IdiscoveritDAOHandler extends HibernateUtil {

	private Session session;
	private Query CheckUser,query;

	private static final Logger logger = Logger.getLogger(IdiscoveritDAOHandler.class);
	private static final String TAG = "IdiscoveritDAOHandler";
	
	/**
	 * This method adds user to DB if it does not already exist
	 * or the username is not already taken
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	public String addUser(String username, String password) {

		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler addUser()---------------");
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();

			boolean isUserNameExists = isUserNameExists(username);
			logger.info("<<<< isUserNameExists in iDisocveritHandlerDaO-addUser().User exists == >> "+isUserNameExists);
			
			if (!isUserNameExists) {
				
				User newUser = new User(username, password);
				session.save(newUser);

				logger.info("***** Exiting addUser successfully from if()!!! ******");
				return Constants.REGISTRATION_SUCCESSFUL;
			}else if(isUserNameExists){
				logger.info("***** Exiting addUser successfully from else if() !!! ******");
				return Constants.USERNAME_EXISTS;
			}else{
				logger.info("***** Exiting addUser successfully from else() !!! ******");
				return Constants.REGISTRATION_FAIL+Constants.LOGIN_FAIL;
			}
		} catch (HibernateException e) {
			logger.error("***** Exiting addUser with Exception!!! ******");
			logger.error("$$$$$$ Hibernate Exception in addUser$$$$$" + TAG	+ " " + e.getMessage());
			return Constants.REGISTRATION_FAIL;
		}
	}

	/**
	 * 
	 * @param uploadAudiotronFile
	 * @param audiotronName
	 * @param audiotronFormat
	 * @param audiotronCreationDate
	 * @param audiotronSize
	 * @param userName
	 * @return
	 * This method inserts media file into the database
	 */
	 public String addMedia(File uploadAudiotronFile, String audiotronName,
	            String audiotronFormat, Date audiotronCreationDate,
	            long audiotronSize,long recTime, String cat, String uName) {

	        logger.info("==============================================================================================");
	        logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler addMedia()---------------");

	        byte[] bFile = null;
	        FileInputStream fileInputStream = null;

	        Media media = null;
	        User u;
	        Category c;

	        List<Category> catList;
	        List<User> userList;
	        List<String> audiotronNameCheck = null;
	        String result = null;

	        try {
	            session = HibernateUtil.getSessionFactory().getCurrentSession();

	            query = session.createQuery("From Category c where c.categoryName='" + cat+ "'");
	            catList = query.list();

	            if (catList.size() > 0)
	                c = catList.get(0);
	            else {
	                logger.info("---###Category does not exist!! Creating temp category");
	                c = new Category("Misc");
	            }

	            query = session.createQuery("From User u where u.userName='"+ uName + "'");
	            userList = query.list();

	            if (userList.size() > 0)
	                u = userList.get(0);
	            else {
	                logger.info("---###User does not exist!!");
	                return Constants.UNREGISTERED_USER;
	            }

	            // To check if an audiotron name already exists by user in the same
	            // category
	            query = session.createQuery("Select distinct m.audiotronName from Media m where m.category.categoryName = '"
	                            + cat+ "' " + "and m.user.userName = '"+ uName+ "'");
	            audiotronNameCheck = query.list();

	            if (audiotronNameCheck.contains(audiotronName)) {
	                logger.info("##### Audiotron name already exists for the category: '"+ cat + "' ");
	                result = Constants.MEDIA_EXISTS;
	            } else {
	                bFile = new byte[(int) uploadAudiotronFile.length()];

	                fileInputStream = new FileInputStream(uploadAudiotronFile);
	                fileInputStream.read(bFile);
	                fileInputStream.close();

	                media = new Media(bFile, audiotronName, u, c);
	                media.setAudiotron(bFile);
	                media.setAudiotronFormat(audiotronFormat);
	                media.setAudiotronCreationDate(audiotronCreationDate);
	                media.setAudiotronSize(audiotronSize);
	                media.setMediaType("audio");
	                media.setTotalRecordingTimeInSeconds(recTime);//#new
	                media.setIsActive(0);

	                session.save(media);

	                logger.info("***** Exiting addMedia: Media saved successfully!!! ******");
	                result = Constants.MEDIA_SAVE_TO_DB_SUCCESS;
	            }

	        } catch (IOException io) {
	            logger.debug("$$$$$$ IO Exception in addMedia() of " + TAG + " " + io.getMessage());
	            return Constants.MEDIA_SAVE_TO_DB_FAIL;
	        } catch (HibernateException hib) {
	            logger.debug("$$$$$$ Hibernate Exception in addMedia() of " + TAG + " " + hib.getMessage());
	            return Constants.MEDIA_SAVE_TO_DB_FAIL;
	        }

	        return result;
	    }
	/**
	 * Add feedback(rating,etc..)to DB
	 * @param fRating
	 * @param flag
	 * @param isLiked
	 * @param mTitle
	 * @param userName
	 * @return
	 */
	public boolean addFeedbackToMedia(String fRating,boolean flag,String mTitle,String userName){ 
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE addFeedbackToMedia()---------------");
		
		List<Media> mediaList= new ArrayList();
		List<User> userList=new ArrayList();
		List<FeedbackBean> tempList=new ArrayList();
		
		Media mBean = null;
		User u = null;
		FeedbackBean fBean=null;
		
		try{
			session=HibernateUtil.getSessionFactory().getCurrentSession();
			//System.out.println("is session open>>"+session.isOpen());
			
			//Query nQuery;
			query = session.createQuery("From Media m where m.audiotronName='"+mTitle+"' and m.isActive <> 1"); //GET user like this
			mediaList=query.list();
			System.out.println("query srting >>> "+query.getQueryString());
			System.out.println("Media list >>> "+mediaList);
			
			if(mediaList.size()>0){
				mBean = mediaList.get(0);
			}else{
				return false;
			}
			
			//#Remove below comment once user is added to the app!!!!
			query=session.createQuery("From User u where u.userName='"+userName+"'");
			userList=query.list();
			System.out.println("User list >>> "+userList.size()+ " "+userList);
			
			if(userList.size()>0){
				u = userList.get(0);
			}else{
				logger.info("---###User does not exist!!");
                return false;
			}

			//#new:This if loop will prevent duplicates from being added!!
			query=session.createQuery("From FeedbackBean f where f.user in (:userList) and f.media in (:mediaList)");
			query.setParameterList("userList", userList);
			query.setParameterList("mediaList", mediaList);
			tempList=query.list();
			logger.info(">>>templist>>>> "+tempList);
			
			if(tempList.size()>0){
				logger.info("Feedback bean exists..Updating the same!! ");
				fBean = tempList.get(0);
				logger.info("Fbean user >>>"+fBean.getUser()+" media>>"+fBean.getMedia()+" fid>>"+fBean.getFeedbackId());
				logger.info("Prevus rating>>>"+fBean.getRating());
				fBean.setFlag(flag);
				logger.info("flag value >>> "+fBean.isFlag());
				Float r = new Float(fRating.trim());
				fBean.setRating(r.longValue());
				logger.info("RAting value given by user >>"+fRating);
				logger.info("current rating being set >>>"+fBean.getRating());
				
				session.saveOrUpdate(fBean);
			}
			else{
				logger.info("**---Creating new feedback bean---***");
				logger.info("RAting value given by user >>"+fRating);				
				Float r =new Float(fRating.trim());
				logger.info("float to long >>>"+r.longValue());
				fBean= new FeedbackBean(r.longValue(), flag, u, mBean); //Long.parseLong(fRating.trim()
				session.save(fBean);
			}
			
			//calculate the rating average
			query = session.createSQLQuery("Select avg(rating) from feedback where mediaId in(select mediaId from media" +
			" where audiotron_name = '" + mTitle + "' and is_active <> 1)");
			String avg = query.list().get(0).toString();
			Float avgR = new Float(avg.trim());
			mBean.setAverageRating(avgR.longValue());
			logger.info("Average rating for media >>" + mBean.getAverageRating());
			session.saveOrUpdate(mBean);
			
			logger.info("********** Feedback added/exited successfully!! *********");
			return true;

		}catch(NumberFormatException nfe) {
			logger.info("********** Number Format EXCEPtion !! *********"+nfe);
			return false;
		}
		catch (HibernateException he) {
			logger.error("********** Feedback added/exited with EXCEPtion !! *********");
			logger.debug("$$$$$$ Hibernate Exception in addFeedbackToMedia() of "	+ TAG + " " + he.getMessage());
			return false;//#### send string or check each false ....
		}
	}
	
	public List dummy() {
		SQLQuery query1;
		List ls = new ArrayList();
		try{
			session=HibernateUtil.getSessionFactory().getCurrentSession();

		query1 = session.createSQLQuery("SELECT avg(f.rating) from feedback f where f.mediaId = 2");
		 ls= query1.list();
		System.out.println("ls -"+ls);
		}catch (HibernateException he) {
			logger.error("********** with EXCEPtion !! *********");
			
		}//logger.info("Feedback list >>" + query1.list());
		return ls;
	}
	
	public boolean addToFavorites(String username,String mTitle,boolean isFavorite){
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE addToFavorites ()---------------");
		
		List<Media> mediaList;
		List<User> userList;
		List<FavoriteBean> tempList;
		
		FavoriteBean favBean = null;
		Media mBean = null;
		User u = null;
		try{
			session=HibernateUtil.getSessionFactory().getCurrentSession();
			
			query = session.createQuery("From Media m where m.audiotronName='"+mTitle+"' and m.isActive <> 1"); 
			mediaList=query.list();
			System.out.println("Media list >>> "+mediaList);
			
			if(mediaList.size()>0){
				mBean = mediaList.get(0);
			}else{
				return false;
			}
			
			query=session.createQuery("From User u where u.userName='"+username+"'");
			userList=query.list();
			System.out.println("User list >>> "+userList.size()+ " "+userList);
			
			if(userList.size()>0){
				u = userList.get(0);
			}else{
				return false;//u = new User("temp1","temp1"); //####REMOVEEEEEEEEE!!!!!
			}
			//#new:This will prevent duplicates from being added!!
			query=session.createQuery("From FavoriteBean f where f.user in (:userList) and f.media in (:mediaList)");
			query.setParameterList("userList",userList);
			query.setParameterList("mediaList",mediaList);
			tempList=query.list();
			logger.info(">>>templist>>>> "+tempList);
			
			if(tempList.size()>0)
			{
				favBean = tempList.get(0);
				if(isFavorite==true){
					favBean.setFavorite(isFavorite);
					//favBean.setMedia(mBean);
					//favBean.setUser(u);
					session.saveOrUpdate(favBean);
					logger.info("********** Favorite Updated/exited successfully!! *********");
					return true;
				}else{
					logger.info("********** Favorite Exists;not added!! *********");
					return false;
				}
				
			}else{
				if(isFavorite==true){
					favBean = new FavoriteBean(u, mBean, isFavorite);
					session.save(favBean);
					logger.info("********** Favorite added/exited successfully!! *********");
					return true;
				}else{
					logger.info("********** Favorite Exists;not added!! *********");
					return false;
				}
			}

		}catch (HibernateException he) {
			logger.error("********** Favorite added/exited with EXCEPtion !! *********");
			logger.debug("$$$$$$ Hibernate Exception in addToFavorites() of "	+ TAG + " " + he.getMessage());
			return false;
		}
	}
	public boolean addTagToAudiotron(String username,String mTitle,String tagName){
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler addTagToAudiotron()---------------");
		
		TagBean tBean;
		Media mBean;
		User u;
		
		List<Media> mediaList;
		List<User> userList;
		List<TagBean> tempTagList;
		
		try{
			session=HibernateUtil.getSessionFactory().getCurrentSession();
			
			query = session.createQuery("From Media m where m.audiotronName='"+mTitle+"' and m.isActive <> 1"); 
			mediaList=query.list();
			System.out.println("Media list >>> "+mediaList);
			
			if(mediaList.size()>0){
				mBean = mediaList.get(0);
			}else{
				return false;
			}
			
			query=session.createQuery("From User u where u.userName='"+username+"'");
			userList=query.list();
			System.out.println("User list >>> "+userList.size()+ " "+userList);
			if(userList.size()>0){
				u = userList.get(0);
			}else{
				return false;//u = new User("temp1","temp1"); //####REMOVEEEEEEEEE!!!!!
			}
			System.out.println("user list tagging >> "+u);
			
			//#Checking for duplicate tags for the same media
			query = session.createQuery("From TagBean t where t.media in (:mList) and t.tagName='"+tagName+"'");// and t.user in (:uList)
			query.setParameterList("mList", mediaList);
			//query.setParameterList("uList", userList);
			tempTagList=query.list();
			System.out.println("Query string >>> "+query.getQueryString());
			System.out.println("temp tag list size >>> "+tempTagList.size());
			
			if(tempTagList.size()>0){
				logger.info("####Information:"+ mTitle +"media is already tagged by the same name: "+username);
				return false;
			}else{
				tBean = new TagBean(tagName, u, mBean);
				logger.info("####Information: "+ mTitle +" media is tagged successfully by user: "+username+" !!!");
				session.save(tBean);
				return true;
			}
			
		}catch (HibernateException he) {
			logger.error("********** Tag added/exited with EXCEPtion !! *********");
			logger.debug("$$$$$$ Hibernate Exception in addTagToAudiotron() of "	+ TAG + " " + he.getMessage());
			return false;
		}
	}
	/**
	 * This method authenticates the user against DB and returns
	 * value true/false. 
	 * @param uName
	 * @param hashedPassword
	 * @return
	 */
	public boolean isAuthenticated(String uName, String hashedPassword){
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler isAuthenticated()---------------");
		logger.info("hashedpassword >>> "+hashedPassword+" and user name>>> "+uName);
		
		int checkCount;

		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			
			CheckUser = session.createQuery("From User u where u.userName=:username and u.password=:password");
			CheckUser.setParameter("username", uName);
			CheckUser.setParameter("password", hashedPassword);
			
			List uList = CheckUser.list();
			logger.info("-----u list size >>> "+uList.size()+" ulist is empty ?? >> "+uList.isEmpty());
			
			//for(Iterator<User> iter = uList.iterator();iter.hasNext();)
				//System.out.println("user >>>> "+iter.next());
			
			checkCount = uList.size();
			
			if (checkCount > 0) {
				logger.info("***** Exiting isAuthenticated successfully from if()!!! ******");
				return true;//Constants.LOGIN_SUCCESS;
			}else{
				logger.info("***** Exiting isAuthenticated successfully from else()!!! ******");
				return false;//Constants.LOGIN_FAIL;
			}
		}catch (HibernateException e) {
			logger.error("***** Exiting isAuthenticated with Exception!!! ******");
			logger.error("$$$$$$ Hibernate Exception in isAuthenticated()..$$$$$" + TAG	+ " " + e.getMessage());
			return false;//Constants.LOGIN_FAIL;
		}
	}
	/**
	 * 
	 * @param username
	 * @return
	 * This method verifies if a username already exists
	 */
	public boolean isUserNameExists(String username) {
		
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler isUserNameExists()---------------");

		List<User> userList;
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			query = session.createQuery("From User u where u.userName = '"	+ username + "'");

			userList = query.list();
			logger.info("<<<< Existence of user >>> "+userList.size());
			
			if(userList.size()>0){ //#user name exists
				logger.info("***** Exiting isUserNameExists successfully from if()!!! ******");
				return true;
			}
			else{ 
				logger.info("***** Exiting isUserNameExists successfully from else()!!! ******");
				return false;
			}

		} catch (HibernateException e) {
			logger.error("***** Exiting isUserNameExists with EXception!!! ******");
			logger.error("$$$$$$ Hibernate Exception in isUserNameExists$$$$$"+ TAG + " " + e.getMessage());
			return false;
		}
	}
	/**
	 ** All categories
	 * @return
	 */
	public List<String> getAllCategoriesFromDB() {

		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getAllCategoriesFromDB()---------------");

		List<String> categoryList = null;

		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();

			query = session
					.createQuery("Select c.categoryName From Category c");
			query.setCacheable(true);
			categoryList = query.list();
			
			for(Iterator it = categoryList.iterator();it.hasNext();)
				System.out.println("cat-names >>"+it.next());

		} catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in getAllCategories() of "
					+ TAG + " " + he.getMessage());
		}
		logger.info("******* Exiting getAllCategoriesFromDB successfully!!!! **********");
     return categoryList;
	}
	/**
	 * Method that returns a list of categories form DB
	 * in which media files exists. This method should only be
	 * used while retrieving category values to display to 
	 * listening audience and not recording audience.
	 * @return
	 */
//	public List<Category> getAllCategories(){
//		logger.info("==============================================================================================");
//		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getAllCategories()---------------");
//		
//		List<Category> categoryList,finalCategoryList = null;
//		try {
//			session=HibernateUtil.getSessionFactory().getCurrentSession();
//
//			query = session.createQuery("From Category c");
//			query.setCacheable(true);
//			categoryList= query.list();
//			
//			query = session.createQuery("select distinct m.category from Media m where" +
//					" m.category in (:categoryList)");
//			query.setParameterList("categoryList", categoryList);
//			query.setCacheable(true);
//			
//			logger.info("query string >>> "+query.getQueryString());
//			finalCategoryList=query.list();
//			
//			for(Iterator<Category> it =finalCategoryList.iterator();it.hasNext();)
//				logger.info(" query o/p >>> "+it.next().getCategoryId());
//			
//			
//		}catch (HibernateException he) {
//			logger.debug("$$$$$$ Hibernate Exception in getAllCategories() of "	+ TAG + " " + he.getMessage());
//		}
////		for (Iterator<Category> iter = categoryList.iterator(); iter.hasNext();)
////			System.out.println("xxxxxxxCategory List xxxxxxxx"
////					+ iter.next().getCategoryName());
//
//		//return categoryList;
//		logger.info("***** Exiting getAllCategories!!! ******");
//		return finalCategoryList;
//
//	}

	/**
	 * @author pallavi  Method to get hold of all audio files from
	 *         database
	 * */
	public List<Media> getAllAudiotrons() {
		
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getAllAudiotrons()---------------");
		
		List<Media> mediaList = null;
		
		try {
			session=HibernateUtil.getSessionFactory().getCurrentSession();
			
			query = session.createQuery("From Media m where m.isActive <> 1");
			query.setCacheable(true);
			mediaList = query.list();

		} catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in getAllAudiotrons() of "+ TAG + " " + he.getMessage());
		}
		for (Iterator<Media> iter = mediaList.iterator(); iter.hasNext();)
			System.out.println("xxxxxxxMedia List xxxxxxxx"	+ iter.next().getAudiotronName());

		logger.info("***** Exiting getAllAudiotrons!!! ******");
		return mediaList;
	}
	
	/**
	 * This method retrieves all the audiotrons that belong to
	 * a particular category.
	 * The first query is used to fetch all the records of Category table.
	 * Then the list of records is filtered to obtain particular category object which is the 
	 * same as param name.
	 * Then this category object is compared with media object's associated category object to
	 * get the media types in a particular category  
	 * @param c
	 * @return
	 */
	public List<String> getAllAudiotronNamesInCategory(Category c){

		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getAllAudiotronsInCategory(CAtegory cat)---------------");
		
		session=HibernateUtil.getSessionFactory().getCurrentSession();
		
		//List<Category> tempCatList = null;
		List<String> mediaList = null,tempMediaList = null;
		String hql0,hql1,hql2;
		List<Category> catlist = new ArrayList<Category>();
		
		try {
			logger.info("---->Inside try");
			logger.info("session>>>"+session.isOpen());
			
//			hql0 = "From Category c";
//			query = session.createQuery(hql0);
//			query.setCacheable(true);
//			logger.info("query string>>>>"+query.getQueryString());
//			tempCatList=query.list();
//			
//			for(int i=0;i<tempCatList.size();i++){
//				if(tempCatList.get(i).getCategoryName().equals(c.getCategoryName())){
//					logger.info("##### starting loop>>>"+tempCatList.get(i).getCategoryName()+":"+tempCatList.get(i).getCategoryId());
//					catlist.add((tempCatList.get(i)));
//				}
//			}
//			for(Category o:catlist){
//				System.out.println("CAtList vals >>"+o);
//			}
			
			hql1 = "select m.audiotronName from Media m where m.category ='"+c+"' and m.isActive <> 1";//in (:catList)";
			query = session.createQuery(hql1);
			//query.setParameterList("catList", catlist);
			query.setCacheable(true);
			
			tempMediaList=query.list();
			
			hql2 = "From FeedbackBean fb where fb.media in (:tempMediaList) ordery by fb.rating desc";
			query = session.createQuery(hql2);
			query.setParameterList("tempMediaList", tempMediaList);
			query.setCacheable(true);
			
			mediaList=query.list();
			
		for(Iterator<String> it = mediaList.iterator();it.hasNext();){
			String tronName = 	it.next();
			System.out.println("mediaList>>>"+tronName);//.getAudiotronName());//+" category is "+it.next().getCategory().getCategoryName());
		}

		} catch (HibernateException he) {
		logger.debug("$$$$$$ Hibernate Exception in getAllAudiotrons(Category cat) of "
				+ TAG + " " + he.getMessage());
		}
		logger.info("***** Exiting getAllAudsInCategory()!!! ******");
		return mediaList;
	}
	
	public List<String> getRookieTrons(String category) {
        logger.info("==============================================================================================");
        logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getRookieTrons---------------");

        List<String> nonFeedbackNonFavoritedList = null;

        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();

            query = session.createQuery("Select distinct m.audiotronName from Media m where (m.mediaId not in (Select fb.media.mediaId "
                            + "from FeedbackBean fb) AND m.mediaId not in (Select favB.media.mediaId from FavoriteBean favB)) and "
                            + "m.category.categoryName = '" + category + "' and m.isActive <> 1 order by m.audiotronCreationDate desc");

            nonFeedbackNonFavoritedList = query.list();

        } catch (HibernateException he) {
            logger.debug("$$$$$$ Hibernate Exception in getRookieTrons() of "
                    + TAG + " " + he.getMessage());
        }
        logger.info("******* Exiting getRookieTrons() successfully!!!! **********");
        return nonFeedbackNonFavoritedList;
    }

	 public ArrayList<String> distributeAudiotronsInCategory(String username,
	            Category c) {
	        logger.info("==============================================================================================");
	        logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler distributeAudiotronsInCategory(String username, Category c)---------------");

	        String hql0, hql1, hql2, hql3, hql4;

	        List<Media> mList;
	        List<Media> interimMediaList = new ArrayList<Media>();
	        List<User> userList;
	        List<FavoriteBean> favList;
	        List<FavoriteBean> favListOfAll;
	        List<FeedbackBean> fdbList;
	        List<String> finalAudiotronsNamesList = new ArrayList<String>();

	        User uBean;
	        try {
	            session = HibernateUtil.getSessionFactory().getCurrentSession();

	            // Get user bean using username
	            hql0 = "From User u where u.userName='" + username + "'";
	            query = session.createQuery(hql0);
	            userList = query.list();
	            if (userList.size() > 0) {
	                uBean = userList.get(0);// Got user bean
	                logger.info("User Bean from user name >>>" + uBean);
	            }

	            // Get Media beans for category(all media in that category is
	            // selected)
	            hql1 = "From Media m where m.category.categoryName='"
	                    + c.getCategoryName() + "' and m.isActive <> 1 order by m.totalHitCount desc";
	            query = session.createQuery(hql1);
	            mList = query.list(); // Got all media objects in a list
	            logger.info("Media Bean List >>> " + mList.size());
	            for (Media m : mList)
	                logger.info("###Information media list >>> "
	                        + m.getAudiotronName() + " : " + m.getMediaId());

	            // Get favoriteBean data of a user; This gives all favorites of user
	            // in the chosen category
	            hql2 = "From FavoriteBean fb where fb.user in (:uList) and fb.media in (:mList)";
	            query = session.createQuery(hql2);
	            query.setParameterList("uList", userList);
	            query.setParameterList("mList", mList);
	            favList = query.list();
	            if (favList.size() > 0) { // If user has favorites-select user
	                                        // favorite-media within the list of
	                                        // media of a particular category.
	                for (FavoriteBean f : favList) {
	                    logger.info("###Infomration: Favorite Bean list of user >>>> "
	                            + f);
	                    Media m = f.getMedia();
	                    logger.info("####Infor: Media bean in favorites >>>" + m);
	                    interimMediaList.add(m);// .getAudiotronName()); //#User
	                                            // favorites added to the list
	                }
	            } /*else {
	                interimMediaList.addAll(mList); // User has no favorites-First
	                                                // add all media in the selected
	                                                // category
	            }*/

	            // Get favorited-media of all users in the selected category
	            hql3 = "From FavoriteBean fb where fb.media in (:mList)";
	            query = session.createQuery(hql3);
	            query.setParameterList("mList", mList);
	            favListOfAll = query.list();
	            if (favListOfAll.size() > 0) {
	                for (FavoriteBean f : favListOfAll) {
	                    logger.info("###Infomration: Favorite Bean list of all users in the selected category >>>> "
	                            + f);
	                    interimMediaList.add(f.getMedia());
	                }
	            }// If medias in a particular category are not favorites at all,then
	                // do not sort/get according to other's favorites;move on to
	                // next query

	            // Get feedback relating of all media objects in the category
	            // selected through mList,sorted in descending order
	            hql4 = "From FeedbackBean fdb where fdb.media in (:mList) order by fdb.rating desc";
	            query = session.createQuery(hql4);
	            query.setParameterList("mList", mList);
	            fdbList = query.list();

	            if (fdbList.size() > 0) {
	                for (FeedbackBean fdb : fdbList) {
	                    logger.info("######Information: Feedback Bean list for all media in the selected category desc >>> "
	                            + fdb);
	                    interimMediaList.add(fdb.getMedia());// .getAudiotronName());
	                }
	            }// If medias in a particular category has no feedback at all,then
	                // do not consider rating value;move on.
	        } catch (HibernateException he) {
	            logger.debug("$$$$$$ Hibernate Exception in distributeAudiotronsInCategory() of "
	                    + TAG + " " + he.getMessage());
	        }
	        for (Media m : interimMediaList)
	            finalAudiotronsNamesList.add(m.getAudiotronName()); 

	        logger.info("###FInal customized list for user >>>> "+ finalAudiotronsNamesList);

	        // Sorting the final list to remove duplicates from the
	        // list:!!!!Currently its sorting according to name!!!!CHECK!!!
	        HashSet<String> hs = new HashSet<String>();
	        hs.addAll(finalAudiotronsNamesList);
	        finalAudiotronsNamesList.clear();
	        finalAudiotronsNamesList.addAll(hs);

	        logger.info("###FInal customized&sorted list for user >>>> "
	                + finalAudiotronsNamesList);
	        return (ArrayList<String>) finalAudiotronsNamesList;

	    }
	 /**
	 * This method returns an audiotron
	 * with a particular name
	 * @param audiotronName
	 * @return ----->WORKS!!!//#using file instead of inputstream
	 */
//	public File getAudiotron(String audiotronName) {
//		logger.info("==============================================================================================");
//		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getAudiotron(name)---------------");
//		ArrayList<Media> audiotronList = new ArrayList<Media>();
//		File audioFile = new File("/Users/pallavi/Eclipse_Workspace/Cmpe295B/ServerSideHelpers/WebContent/tempFiles/tron.3gp");
//		if(audioFile.exists())
//			try {
//				audioFile.createNewFile();
//			} catch (IOException e1) {
//				logger.debug("$$$$$$ IOException Exception in getAudiotron()-creating new file of "
//						+ TAG + " " + e1.getMessage());
//			}
//		logger.info("is new file created >>>> "+audioFile.exists());
//		try {
//			session=HibernateUtil.getSessionFactory().getCurrentSession();
//			query = session.createQuery("From Media m where m.audiotronName = '"+ audiotronName + "'");
//			audiotronList =  (ArrayList<Media>) query.list();
//			
//			logger.info("audio >>> "+audiotronList.get(0).getAudiotronName());
//			logger.info("audio size >>>"+audiotronList.size());
//			
//			if(audiotronList.size()>0){
//				Media mBean = audiotronList.get(0);
//				logger.info("media bean in dao >>> "+mBean);
//				logger.info("media bean's audio length >>> "+mBean.getAudiotron().length);
//				
//				FileOutputStream output = new FileOutputStream(audioFile);
//				IOUtils.write(mBean.getAudiotron(), output);output.flush();output.close();
//				
//				logger.info("#### IO utility to file >>>"+audioFile.length());
//				
//				int count = mBean.getTotalHitCount();
//				mBean.setTotalHitCount(count+1);
//				
//				session.saveOrUpdate(mBean);
//				logger.info("media bean >>> "+mBean);
//			}
//			
//		}catch (HibernateException he) {
//			logger.debug("$$$$$$ Hibernate Exception in getAudiotron() of "
//			+ TAG + " " + he.getMessage());
//		} catch (FileNotFoundException e) {
//			logger.debug("$$$$$$ FileNotFound Exception in getAudiotron() of "
//					+ TAG + " " + e.getMessage());
//		} catch (IOException e) {
//			logger.debug("$$$$$$ IOException Exception in getAudiotron()-IOUtils.write() of "
//					+ TAG + " " + e.getMessage());
//		}
//		return audioFile;
//	}
	 /**
		 * This method returns an audiotron
		 * with a particular name
		 * @param audiotronName
		 * return InputStream
		 * *
		 */
	 public InputStream getAudiotron(String audiotronName) {
			logger.info("==============================================================================================");
			logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getAudiotron(name)---------------");
			ArrayList<Media> audiotronList = new ArrayList<Media>();
			InputStream inputStream = null;
			
			try {
				session=HibernateUtil.getSessionFactory().getCurrentSession();
				query = session.createQuery("From Media m where m.audiotronName = '"+ audiotronName + "'");
				audiotronList =  (ArrayList<Media>) query.list();
				
				logger.info("audio >>> "+audiotronList.get(0).getAudiotronName());
				logger.info("audio size >>>"+audiotronList.size());
				
				if(audiotronList.size()>0){
					Media mBean = audiotronList.get(0);
					logger.info("media bean in dao >>> "+mBean);
					logger.info("media bean's audio length >>> "+mBean.getAudiotron().length);
					
					inputStream = new ByteArrayInputStream(mBean.getAudiotron());
					
					int count = mBean.getTotalHitCount();
					mBean.setTotalHitCount(count+1);
					
					session.saveOrUpdate(mBean);
					logger.info("media bean >>> "+mBean);
				}
				
			}catch (HibernateException he) {
				logger.debug("$$$$$$ Hibernate Exception in getAudiotron() of "
				+ TAG + " " + he.getMessage());
			} 
			return inputStream;
		}
	 
	/**
	 * getHallOfFameAudiotronNames() returns media file names
	 * as a list of strings with highest rating in descending order
	 * @return
	 */
//	public List<String> getHallOfFameAudiotronNames(){
//		logger.info("==============================================================================================");
//		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getHallOfFameAudiotrons()---------------");
//		
//		List<String> audiotronNamesList = null;
//		List<Media> mList=null;
//		try{
//			session=HibernateUtil.getSessionFactory().getCurrentSession();
//			
//			query = session.createQuery("From Media m");
//			query.setCacheable(true);
//			mList = query.list();
//			
//			query = session.createQuery("select distinct f.media.audiotronName From FeedbackBean f where f.media in (:mList) order by f.rating desc");
//			query.setParameterList("mList", mList);
//			query.setCacheable(true);
//
//			audiotronNamesList = query.list();
//			
//			for(Iterator it = audiotronNamesList.iterator();it.hasNext();)
//				logger.info("trons >>>>"+it.next());
//			
//		}catch (HibernateException he) {
//			logger.debug("$$$$$$ Hibernate Exception in getHallOfFameAudiotronNames() of "+ TAG + " " + he.getMessage());
//		}
//		logger.info("******* Exiting getHallOfFameAudiotronNames successfully!!!! **********");
//		return audiotronNamesList;
//	}
	public ArrayList<Media> getHallOfFameAudiotronNames(){
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getHallOfFameAudiotrons()---------------");
		
		ArrayList<Media> audiotronList = new ArrayList<Media>();
		try{
			session=HibernateUtil.getSessionFactory().getCurrentSession();
			
			query = session.createQuery("From Media m where m.isActive <> 1 and m.averageRating > 0 order by m.averageRating desc");
			query.setCacheable(true);
			audiotronList = (ArrayList<Media>) query.list();
			
			for(Iterator<Media> it = audiotronList.iterator();it.hasNext();){
				logger.info("Audiotron list >>>> " + it.next());
			}
			
		}catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in getHallOfFameAudiotronNames() of "+ TAG + " " + he.getMessage());
		}
		logger.info("******* Exiting getHallOfFameAudiotronNames successfully!!!! **********");
		System.out.println("media list <<< "+audiotronList);
		return audiotronList;
	}
	/**
	 * getMyFavoriteAudiotrons() gets all audiotrons that 
	 * were marked 'favorite' by a user
	 * @param username
	 * @return
	 */
	public List<String> getMyFavoriteAudiotrons(String username){
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getMyFavoriteAudiotrons()---------------");
		
		List<String> audiotronNamesList = null;
		List<User> uList=null;
		
		try{
			session=HibernateUtil.getSessionFactory().getCurrentSession();
			
			query = session.createQuery("From User u where u.userName='"+username+"'");
			uList = query.list();
			
			query = session.createQuery("select fv.media.audiotronName From FavoriteBean fv where fv.user in(:uList)" +
			" and fv.media.isActive <> 1");
			
			query.setParameterList("uList",uList);
			
			audiotronNamesList=query.list();
			
			for(Iterator it = audiotronNamesList.iterator();it.hasNext();)
				logger.info("trons >>>>"+it.next());
		}catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in getMyFavoriteAudiotrons() of "+ TAG + " " + he.getMessage());
		}
		logger.info("******* Exiting getMyFavoriteAudiotrons successfully!!!! **********");
		return audiotronNamesList;
	}
	
	public ArrayList<String> getSearchResults(String queryTag){
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getSearchResults()---------------");
		ArrayList<String> audiotronNamesList = new ArrayList<String>();
		List<String> taggedMediaList=new ArrayList<String>();
		List<String> tempMediaList = new ArrayList<String>();
		
		try{
			session=HibernateUtil.getSessionFactory().getCurrentSession();
			
			query = session.createQuery("select tb.media.audiotronName From TagBean tb where tb.tagName like '%"+queryTag+"%' and tb.media.isActive <> 1");
			taggedMediaList =  query.list();
//			System.out.println("tagged search list >> "+taggedMediaList);
			
			if(taggedMediaList.size()>0){
				audiotronNamesList.addAll(taggedMediaList); //Adding all media files with tag to the list.
			}
			query = session.createQuery("select m.audiotronName From Media m where m.audiotronName like '%"+queryTag+"%' and m.isActive <> 1");
			tempMediaList=query.list();
//			System.out.println("query string >> "+query.getQueryString());
//			System.out.println("temp search list >> "+tempMediaList);
			
			if(tempMediaList.size()>0){
				audiotronNamesList.addAll(tempMediaList); //Adding all media files with name=queryTag to the list.
			}
//			System.out.println("Before sortgin search list >> "+audiotronNamesList);
			
			HashSet<String> hs = new HashSet<String>();
		    hs.addAll(audiotronNamesList);
		    audiotronNamesList.clear();
		    audiotronNamesList.addAll(hs);
		    System.out.println("ARRRAYYYY list search result after sorting >>> "+audiotronNamesList);
		}catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in getSearchResults() of "+ TAG + " " + he.getMessage());
		}
		return audiotronNamesList;
	}
	///-------------------------------------------------Methods for Web App CRUD Operations:ANU---------------------------------------------------------//
	
	/**
	 * This method retrieves all the audiotrons that belong to
	 * a particular user.
	 * The first query is used to fetch all the records in the User table.
	 * Then the list of records is filtered to obtain particular user object which is the 
	 * same as param name.
	 * Then this user object is compared with media object's associated user object to
	 * get the audiotron names recorded by a particular user
	 * @param user
	 * @return
	 */
	public List<Media> getAllAudiotronsForUser(String username) {
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getAllAudiotronsForUser(String username)---------------");
		
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		List<Media> audiotronList = null;
		String hql0, hql1;
		List<User> userlist = new ArrayList<User>();
		
		try {
			logger.info("---->Inside try");
			logger.info("session>>>"+session.isOpen());
			
			hql0 = "From User u where u.userName = '" + username +"'";
			query = session.createQuery(hql0);
			logger.info("query string>>>>" + query.getQueryString());
			userlist = query.list();
			
			/*for(User u : userlist){
				System.out.println("UserList vals >>" + u);
			}*/
			
			hql1 = "from Media m where m.user in (:userList) and m.isActive <> 1 order by m.audiotronCreationDate desc";
			query = session.createQuery(hql1);
			query.setParameterList("userList", userlist);
			query.setCacheable(true);
			logger.info("query string>>>>" + query.getQueryString());
			
			audiotronList = query.list();
			
		} catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in getAllAudiotronsForUser(String username) of "
				+ TAG + " " + he.getMessage());
		}
		
		return audiotronList;
	}
	
	/**
	 * This method deletes an audiotron/s that belong to
	 * a particular user.
	 * @param user
	 * @return
	 */
	public void deleteAudiotronForUser(String username, long mediaId) {
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler deleteAllAudiotronsForUser(Long mediaId, String username)---------------");
		
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		try {
			logger.info("---->Inside try");
			logger.info("session>>>" + session.isOpen());

			Media media = (Media) session.get(Media.class, mediaId);
			if(media.getUser().getUserName().equals(username)) {
				session.delete(media);
			}
			
		} catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in deleteAudiotronForUser(Long mediaId, String username) of "
				+ TAG + " " + he.getMessage());
		}
	}
	
	public List<Media> getlatestAudiotronRecordedByUser(String username) {
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getlatestAudiotronRecordedByUser(String username)---------------");
		
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		List<Media> audiotronList = null;
		String hql0, hql1;
		List<User> userlist = new ArrayList<User>();
		
		try {
			logger.info("---->Inside try");
			logger.info("session>>>"+session.isOpen());
			
			hql0 = "From User u where u.userName = '" + username +"'";
			query = session.createQuery(hql0);
			logger.info("query string>>>>" + query.getQueryString());
			userlist = query.list();
			
			hql1 = "from Media m where m.audiotronCreationDate = " + 
					" (select max(m.audiotronCreationDate) from Media m) " +
					"and m.user in (:userList)";
			query = session.createQuery(hql1);
			query.setParameterList("userList", userlist);
			logger.info("query string>>>>" + query.getQueryString());
			
			audiotronList = query.list();

		} catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in getlatestAudiotronRecordedByUser(String username) of "
				+ TAG + " " + he.getMessage());
		}
		
		return audiotronList;
	}
	
	/**
	 * This method retrieves the audiotrons rated by a particular user.
	 * The first query is used to fetch all the records in the User table.
	 * Then the list of records is filtered to obtain the list of audiotrons recorded by the user.
	 * Then this media list is further filtered based on whether a rating was provided
	 * by the user for the media.
	 * @param user
	 * @return
	 */
	public List<FeedbackBean> getAudiotronsRatedByUser(String username) {
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getAudiotronsRatedByUser(String username)---------------");
		
		session = HibernateUtil.getSessionFactory().getCurrentSession();

		String hql0, hql1, hql2;
		List<User> userlist = new ArrayList<User>();
		List<Media> medialist = new ArrayList<Media>();
		List<FeedbackBean> feedbacklist = new ArrayList<FeedbackBean>();
		
		try {
			logger.info("---->Inside try");
			logger.info("session>>>" + session.isOpen());
			
			hql0 = "From User u where u.userName = '" + username +"'";
			query = session.createQuery(hql0);
			logger.info("query string>>>>" + query.getQueryString());
			userlist = query.list();
			
			/*for(User u : userlist){
				System.out.println("UserList vals >>" + u);
			}*/
			
			hql1 = "From Media m where m.user in (:userList) and m.isActive <> 1";
			query = session.createQuery(hql1);
			query.setParameterList("userList", userlist);
			query.setCacheable(true);
			logger.info("query string>>>>" + query.getQueryString());
			medialist = query.list();
			
			/*for(Media m : medialist){
				System.out.println("MediaList vals >>" + m);
			}*/
			
			hql2 = "From FeedbackBean f where f.media in (:mediaList) and f.rating <> 0";
			query = session.createQuery(hql2);
			query.setParameterList("mediaList", medialist);
			query.setCacheable(true);
			logger.info("query string>>>>" + query.getQueryString());
			feedbacklist = query.list();
			
			/*for(FeedbackBean f : feedbacklist){
				System.out.println("feedbackList vals >>" + f);
			}*/

		} catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in getAudiotronsRatedByUser(String username) of "
				+ TAG + " " + he.getMessage());
		}
		
		return feedbacklist;
	}
	
	/**
	 * This method updates an audiotron name that belongs to
	 * a particular user.
	 * @param user
	 * @return
	 */
	public void updateAudiotronNameForUser(long mediaId, String username, String audiotronName) {
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler updateAudiotronNameForUser(Long mediaId, String username)---------------");
		
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		try {
			logger.info("---->Inside try");
			logger.info("session>>>" + session.isOpen());
			
			Media media = (Media) session.get(Media.class, mediaId);
			media.setAudiotronName(audiotronName);
			session.saveOrUpdate(audiotronName, media);

		} catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in updateAudiotronNameForUser(long mediaId, String username, String audiotronName) of "
				+ TAG + " " + he.getMessage());
		}
	}
	
	public List<FavoriteBean> getAudiotronsAsFavoritesByUser(String username) {
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler getAudiotronsAsFavoritesByUser(String username)---------------");

		List<FavoriteBean> audiotronsList = null;
		List<User> uList = null;

		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();

			query = session.createQuery("From User u where u.userName='"
					+ username + "'");
			uList = query.list();

			query = session
					.createQuery("From FavoriteBean fv where fv.user in(:uList) and fv.media.isActive <> 1");
			query.setParameterList("uList", uList);
			query.setCacheable(true);

			audiotronsList = query.list();

			/*
			 * for(Iterator it = audiotronsList.iterator();it.hasNext();)
			 * logger.info("trons >>>>"+it.next());
			 */
		} catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in getAudiotronsAsFavoritesByUser() of "
					+ TAG + " " + he.getMessage());
		}
		logger.info("******* Exiting getAudiotronsAsFavoritesByUser() successfully!!!! **********");
		return audiotronsList;
	}
	
	public void unmarkAnAudiotronAsFavoriteForUser(String username,
			long favoritesId) {
		logger.info("==============================================================================================");
		logger.info("-------------SERVERSIDE IdiscoveritDAOHAndler unmarkAnAudiotronAsFavoriteForUser(String username, long favoritesId)---------------");

		session = HibernateUtil.getSessionFactory().getCurrentSession();

		try {
			logger.info("---->Inside try");
			logger.info("session>>>" + session.isOpen());

			FavoriteBean fb = (FavoriteBean) session.get(FavoriteBean.class,
					favoritesId);
			if (fb.getUser().getUserName().equals(username)) {
				session.delete(fb);
			}

		} catch (HibernateException he) {
			logger.debug("$$$$$$ Hibernate Exception in unmarkAnAudiotronAsFavoriteForUser(String username, long favoritesId) of "
					+ TAG + " " + he.getMessage());
		}
	}
	
}