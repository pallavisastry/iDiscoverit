package com.sjsu.cmpe295B.idiscoverit.tableLoaders;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.sjsu.cmpe295B.idiscoverit.model.Category;
import com.sjsu.cmpe295B.idiscoverit.model.FavoriteBean;
import com.sjsu.cmpe295B.idiscoverit.model.FeedbackBean;
import com.sjsu.cmpe295B.idiscoverit.model.Media;
import com.sjsu.cmpe295B.idiscoverit.model.User;
import com.sjsu.cmpe295B.idiscoverit.utilities.HibernateTablesCreator;
import com.sjsu.cmpe295B.idiscoverit.utilities.HibernateUtil;

/**
 * 
 * @author pallavi
 *This class is used to create tables and add new entities in tables
 *
 */
public class EntityLoader {

	private static Logger logger = Logger.getLogger(EntityLoader.class);
	
	public static void main(String[] args) {
		logger.info("******* Entity Loader Started *********");
		EntityLoader loader = new EntityLoader();
		loader.generateTables();
	}
	
	public void generateTables() throws HibernateException{
		HibernateTablesCreator.createTables(User.class);
		HibernateTablesCreator.createTables(Media.class);
		HibernateTablesCreator.createTables(Category.class);
		HibernateTablesCreator.createTables(FeedbackBean.class);
		HibernateTablesCreator.createTables(FavoriteBean.class);
		
		Session session=HibernateUtil.getSessionFactory().openSession();		
		Transaction transaction=session.beginTransaction();
		
		//**Adding Temporary Users To The Table**//
//		User user1 = new User("Pallavi","pallavi");
//		User user2 = new User("Anu","anu");
//		
//		session.save(user1);
//		session.save(user2);
		
		//**Adding Temporary Categories To The Table**//
		Category cat1 = new Category("Kids Stories");
		cat1.setCategoryTag("idsc_KS");
		Category cat2 = new Category("Adult Stories");
		cat2.setCategoryTag("idsc_AS");
		Category cat3 = new Category("Classical Music");
		cat3.setCategoryTag("idsc_CM");
		Category cat4 = new Category("Mythology/Legends");
		cat4.setCategoryTag("idsc_LL");
		Category cat5 = new Category("Jokes");
		cat5.setCategoryTag("idsc_JK");
		Category cat6 = new Category("Colloquials");
		cat6.setCategoryTag("idsc_cqs");
		Category cat7 = new Category("Spirituality");
		cat7.setCategoryTag("idsc_sprlty");
		Category cat8 = new Category("Personal Memoirs");
		cat8.setCategoryTag("idsc_pmrs");
		Category cat9 = new Category("Gyan");
		cat9.setCategoryTag("idsc_gyan");
		Category cat10 = new Category("Educational");
		cat10.setCategoryTag("idsc_edt");
		Category cat11 = new Category("Audio Dictionary");
		cat11.setCategoryTag("idsc_dict");
		Category cat12 = new Category("Rhymes");
		cat12.setCategoryTag("idsc_rhym");
		Category cat13 = new Category("Poems");
		cat13.setCategoryTag("idsc_poems");
		Category cat14=new Category("Miscellaneous");
		cat14.setCategoryTag("idsc_Misc");
		
		session.save(cat1);
		session.save(cat2);
		session.save(cat3);
		session.save(cat4);
		session.save(cat5);
		session.save(cat6);
		session.save(cat7);
		session.save(cat8);
		session.save(cat9);
		session.save(cat10);
		session.save(cat11);
		session.save(cat12);
		session.save(cat13);
		session.save(cat14);
		
		//**Adding Temporary Media files To The Table**//
//		Media media1=new Media(new byte[100],"audiotron1",user1,cat1);//,"audio");
//		Media media2=new Media(new byte[100],"audiotron2",user2,cat2);//"audio");
//		media1.setAudiotronCreationDate(Calendar.getInstance().getTime());
//		media2.setAudiotronCreationDate(Calendar.getInstance().getTime());
//		media1.setAudiotronFormat("3gp");
//		media2.setAudiotronFormat("3gp");
//		media1.setUser(user1);
//		media2.setUser(user2);
//		media1.setCategory(cat1);
//		media2.setCategory(cat3);
//		
//		session.save(media1);
//		session.save(media2);
//		
//		//**Adding Temporary Feedback To The Table**//
//		FeedbackBean fBean1 = new FeedbackBean(2, false,user1,media1);
//		media1.addFeedback(fBean1);
//		FeedbackBean fBean2 = new FeedbackBean(3, false,user2,media1);
//		media1.addFeedback(fBean2);
//		
//		FeedbackBean fBean3 = new FeedbackBean(5, false,user1,media2);
//		media2.addFeedback(fBean3);
//		FeedbackBean fBean4 = new FeedbackBean(1, true,user2,media2);
//		media2.addFeedback(fBean4);
//		
//		session.save(fBean1);
//		session.save(fBean2);
//		session.save(fBean3);
//		session.save(fBean4);
//		
//		FavoriteBean favBean1=new FavoriteBean(user1, media1, true);
//		user1.addFavorite(favBean1);
//		FavoriteBean favBean2=new FavoriteBean(user1, media2, true);
//		user1.addFavorite(favBean2);
//		FavoriteBean favBean3=new FavoriteBean(user2, media1, true);
//		user2.addFavorite(favBean3);
//		
//		session.save(favBean1);
//		session.save(favBean2);
//		session.save(favBean3);
		
		transaction.commit();
		session.close();
	}
}
