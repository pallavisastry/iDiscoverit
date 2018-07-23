package com.sjsu.cmpe295B.idiscoverit.tests;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
//import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Test;

import com.sjsu.cmpe295B.idiscoverit.model.Category;
import com.sjsu.cmpe295B.idiscoverit.model.Media;
import com.sjsu.cmpe295B.idiscoverit.model.User;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;
import com.sjsu.cmpe295B.idiscoverit.utilities.HibernateUtil;

public class IdiscoveritDAOHandlerTest {
	
	private Session session;

	IdiscoveritDAOHandler dao = new IdiscoveritDAOHandler();
	private SessionFactory sf=HibernateUtil.getSessionFactory();
	private Query query;
	
	/*@Test
	public void testAddFeedback(){
		sf.getCurrentSession().beginTransaction();
		dao.addFeedbackToMedia("2.0F", false, "bhagyada lakshmi", "user1");
		sf.getCurrentSession().getTransaction().commit();
		sf.getCurrentSession().close();
	}*/
	
	@Test
	public void getAvgRatingFromFeedback() {
		sf.getCurrentSession().beginTransaction();
		
		dao.addFeedbackToMedia("4.3", false, "bhagyada lakshmi", "Pallavi");
//		String mTitle = "bhagyada lakshmi";
//		query = session.createQuery("Select avg(rating) from feedback where mediaid in(select mediaid from media" +
//				" where audiotron_name = '" + mTitle + "' and is_active <> 1)");
//				System.out.println("query srting >>> "+query.getQueryString());
//				String avg = query.list().get(0).toString();
//				Float avgR = new Float(avg.trim());
//				System.out.println("Average rating before inserting into media >>" + avgR.longValue());
				
				sf.getCurrentSession().getTransaction().commit();
				sf.getCurrentSession().close();
		/*List blah = dao.dummy();
		System.out.println("blah is > "+blah);*/
//		String avgRating = query.list().toString();
//		Float avg = new Float(avgRating.trim());
//		System.out.println("Average rating before inserting into media >>" + avg.longValue());
	}
	
	@Test
	public void getHallOfFameAudiotrons() {
		sf.getCurrentSession().beginTransaction();
		ArrayList<Media> audiotronList = new ArrayList<Media>();
		audiotronList = dao.getHallOfFameAudiotronNames();
		System.out.println("audiotron List is >>>" + audiotronList);
		
		sf.getCurrentSession().getTransaction().commit();
		sf.getCurrentSession().close();
	}

//	@Test
//	public void testDistributeAudiotronsInCategory(){
//		sf.getCurrentSession().beginTransaction();
//		Category c = new Category("Misc");
//		dao.distributeAudiotronsInCategory("temp", c);
//		
//		sf.getCurrentSession().getTransaction().commit();
//		sf.getCurrentSession().close();
//	}
	
//	@Test
//	public void testAddToFavorites(){
//		sf.getCurrentSession().beginTransaction();
//
//		boolean f = false;
//		String mTitle="audiotron1";
//		
//		User u = new User("temp","3d801aa532c1cec3ee82d87a99fdf63f");
//		
//
//		boolean val = dao.addToFavorites("temp", mTitle, true);
//		
//		sf.getCurrentSession().getTransaction().commit();
//		
//		System.out.println("Value of favorite addition is >>> "+val);
//		
//		sf.getCurrentSession().close();
//	}
	
//	@Test
//	public void testGetMyFavoriteAudiotron(){
//		sf.getCurrentSession().beginTransaction();
//		ArrayList list = (ArrayList) dao.getMyFavoriteAudiotrons("temp");
//		System.out.println("all aud list>>>"+(ArrayList)list);
//		sf.getCurrentSession().getTransaction().commit();
//		sf.getCurrentSession().close();
//	}
	
//	@Test
//	public void testGetAllAudiotronsForUser(){
//		sf.getCurrentSession().beginTransaction();
//		ArrayList list = (ArrayList) dao.getAllAudiotronsForUser("temp");
//		System.out.println("all aud list>>>"+(ArrayList)list);
//		sf.getCurrentSession().getTransaction().commit();
//		sf.getCurrentSession().close();
//	}
//	@Test
//	public void testGetHallOfFameAudiotronNames(){
//		sf.getCurrentSession().beginTransaction();
//		List list = dao.getHallOfFameAudiotronNames();
//		sf.getCurrentSession().getTransaction().commit();
//		System.out.println("all cat list>>>"+(ArrayList)list);
//		sf.getCurrentSession().close();
//	}
	
//	@Test
//	public void testGetAllCategories() {
//		
//		sf.getCurrentSession().beginTransaction();
//		
//		List<String> list = dao.getAllCategoriesToTagRecord();
//		
//		sf.getCurrentSession().getTransaction().commit();
//		
//		//Assert.assertNotNull(list);
//		System.out.println("all cat list>>>"+list);
//		sf.getCurrentSession().close();
//	}

//	@Test
//	public void testGetAllAudiotrons() {
//		List list = dao.getAllAudiotrons();
//		
//		Assert.assertNotNull(list);
//		System.out.println("all trons list>>>"+list);
//	}

//	@Test
//	public void testGetAllAudiotronsNamesInCategory() {
//		
//		sf.getCurrentSession().beginTransaction();
//		
//		Category c = new Category("Jokes");
//		
//		List<String> list =new ArrayList<String>(); 
//			list=dao.getAllAudiotronNamesInCategory(c);
//		
//		sf.getCurrentSession().getTransaction().commit();
//		
//		System.out.println("list in test >> "+list);
//		
//		for(Iterator it = list.iterator();it.hasNext();)
//			System.out.println("Test >> "+it.next());
//		
//		Assert.assertNotNull(list);
//		
//		
//		sf.getCurrentSession().close();
//	}
	
	/*@Test
	public void testAddMedia(){
		
		sf.getCurrentSession().beginTransaction();
		File uploadAudiotronFile = new File("C:\\_Anu_\\Aliceinwonderland.3gp");
		byte[] bFile = new byte[3];
		FileInputStream fileInputStream = null;
		fileInputStream = new FileInputStream(uploadAudiotronFile);
		fileInputStream.read(bFile);
		fileInputStream.close();
		

		String res = dao.addMedia(uploadAudiotronFile,"kitteni","3gp",Calendar.getInstance().getTime(),1000,"Kids Stories","user1");
		
		sf.getCurrentSession().getTransaction().commit();
		
		System.out.println("Value of media addition is >>> "+res);
		
		sf.getCurrentSession().close();
	}*/
//
//
//	@Test
//	public void testGetAudiotron() {
//		sf.getCurrentSession().beginTransaction();
//		List<Media> list = dao.getAudiotron("pal2");
//		sf.getCurrentSession().getTransaction().commit();
//		
//		System.out.println("all trons list>>>"+list);
//		sf.getCurrentSession().close();
//	}
	
	/*@Test
	public void testGetNonFeedbackAudiotronsByUser() {
		sf.getCurrentSession().beginTransaction();
		List<String> list = dao.getAudiotronsWithNoFeedbackAndNotFavorited("user1", "Kids Stories");
		sf.getCurrentSession().getTransaction().commit();
		
		System.out.println("all non-feedback, non-favorited audiotrons>>>" + list);
		sf.getCurrentSession().close();
	}*/

}
