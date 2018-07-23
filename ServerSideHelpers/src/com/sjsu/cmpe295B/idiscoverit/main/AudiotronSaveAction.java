package com.sjsu.cmpe295B.idiscoverit.main;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.MimetypesFileTypeMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;
import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;

//TODO:1) Support getting list of files and saving it to DB !!!!!-->done
//TODO:2) USE DB checks instead of folder-file checks in renameUploadedFileNameToUserGivenName() !!!!!-->done...
//TODO:3) USE database access/path instead of local path used to store the renamed files:mFilePath !!!!->done...

/**
 * 
 * @author pallavi
 *This action class takes the file,its name directly from the request and 
 *saves it into DB
 */
public class AudiotronSaveAction extends ActionSupport implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4055130460905140275L;
	private static final Logger logger = Logger.getLogger(AudiotronSaveAction.class);
	private static final String TAG = "AudiotronSaveAction";
	
	private File audiotron;
	private String audiotronFileName;
	private String userName;
	private String category;
	private String audiotronSaveMsg; 
	private String recordingTime;
	private Map session;
	

	public String execute() {
		
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE AudiotronSaveAction STARTS----------------------------");
		
		session = this.getSession();
		
		// @Anu
		String audiotronFormat = "";
		Date audiotronCreationDate = Calendar.getInstance().getTime();
		long audiotronSize;

		File uploadedTron = this.getAudiotron();

		logger.info(TAG + "#####Information: uploaded audio-tron is a file >>>> "	+ uploadedTron.isFile());
		logger.info(TAG + "#####Information: uploaded audio-tron temp name >>>> "	+ uploadedTron.getName());

		String uploadedTronName = this.getAudiotronFileName();

		logger.info(TAG + "####Information: uploaded audio-tron actual name >>>> "+ uploadedTronName);
		logger.info(TAG + "####Information: uploaded audio-tron type/stream >>>> "+ new MimetypesFileTypeMap().getContentType(uploadedTron));

		String userName = this.getUserName();

		audiotronFormat = getAudiotronFileFormat(uploadedTronName); // @Anu

		audiotronSize = audiotron.length();
		long totalRecordingTime = 0;
		try{
			totalRecordingTime = Long.valueOf(this.getRecordingTime());
		logger.info(TAG + "####Information: REcording Timeeee==== "+totalRecordingTime);
		}catch(NumberFormatException e){
			logger.info("$$$$$$$$$ NumberFormatException convering string to long-recording time"+e);
		}
		String insertResult = insertIntoDatabase(audiotron, uploadedTronName,
				audiotronFormat, audiotronCreationDate, audiotronSize,totalRecordingTime,category,userName); // @Anu

		if (insertResult.equals(Constants.MEDIA_SAVE_TO_DB_SUCCESS)) {
			logger.info(TAG + "####Information: -----> File Insert Successful in execute of----------> AudiotronSAveAction---------->");
			logger.info(TAG + "####Information: -----> File DELeTEd   =: "+ audiotron.getName() + " =: " + audiotron.delete());
			setAudiotronSaveMsg("saveSuccess");
			return SUCCESS;
		}else if(insertResult.equals(Constants.UNREGISTERED_USER)){
			logger.info(TAG + "####Information: -----> File Insert FAIL in execute of----------> AudiotronSAveAction---------->");
			logger.info(TAG + "####Unregistered USER!!!Cannot save audiotrons");
			setAudiotronSaveMsg("noUser");
		}else if(insertResult.equals(Constants.MEDIA_EXISTS)){
			logger.info(TAG + "####Information: -----> File Insert FAIL in execute of----------> AudiotronSAveAction---------->");
			logger.info(TAG + "####Media name exisits.Please enter another!");
			setAudiotronSaveMsg("mediaNameExists");
			return SUCCESS;
		}else {
			logger.info(TAG + "####Information: -----> File Insert FAIL in execute of----------> AudiotronSAveAction---------->");
			 setAudiotronSaveMsg("saveError");
	         return SUCCESS;
		}
		return SUCCESS;
	}

	/**
	 * //@Anu This method returns the extension/format of the Audiotron file.
	 */
	private String getAudiotronFileFormat(String audiotron) {
		logger.info(TAG + "--------Inside getAudiotronFileFormat()---------");

		String regex = "(?<=\\.).*$";
		String audiotronFormat = "no match";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(audiotron);

		if (matcher.find()) {
			audiotronFormat = matcher.group();
		}

		logger.info(TAG + "Format of " + audiotron + " is: " + audiotronFormat);

		return audiotronFormat;
	}

	/**
	 * @Anu: audioFile and audioName are parameters sent to the Controller
	 *       class, SaveAction, to execute the logic for inserting media
	 *       information into the Database.
	 */
	private String insertIntoDatabase(File audiotronFile, String audiotronName,
			String audiotronFormat, Date audiotronCreationDate,
			long audiotronSize,long recordTime,String categoryName, String userName) {
	
		logger.info(TAG + "--------Inside insertIntoDatabase()---------");

		IdiscoveritDAOHandler audiotronDBhandler = new IdiscoveritDAOHandler();

		String prunedAudiotronName = FilenameUtils.removeExtension(audiotronName);

		String addingResult = audiotronDBhandler.addMedia(audiotronFile,
				prunedAudiotronName, audiotronFormat, audiotronCreationDate,
				audiotronSize,recordTime,category,userName);

		logger.info(TAG + "? IS Audiotron added succesfully >>> '"+ addingResult + "' ########");

		return addingResult;
	}
	public Map getSession() {
		return session;
	}

	public void setSession(Map session) {
		this.session = session;
	}
	public void setCategory(String category){
	     this.category=category;
	}
	public String getRecordingTime() {
		return recordingTime;
	}

	public void setRecordingTime(String recordingTime) {
		this.recordingTime = recordingTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setAudiotron(File audiotron) {
		this.audiotron = audiotron;
	}

	public File getAudiotron() {
		return this.audiotron;
	}

	public void setAudiotronFileName(String audiotronFileName) {
		this.audiotronFileName = audiotronFileName;
	}

	public String getAudiotronFileName() {
		return audiotronFileName;
	}

	public String getAudiotronSaveMsg() {
		return audiotronSaveMsg;
	}

	public void setAudiotronSaveMsg(String audiotronSaveMsg) {
		this.audiotronSaveMsg = audiotronSaveMsg;
	}
}
