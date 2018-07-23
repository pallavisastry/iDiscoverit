package com.sjsu.cmpe295B.idiscoverit.main;

import java.io.InputStream;
import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

/**
 * Renderes all requested audios
 * @author pallavi
 *
 */
public class RenderAudiotronsAction extends ActionSupport{ //implements ServletResponseAware{

    private static final long serialVersionUID = -3205277449746266831L;
    private static final String TAG = "RenderAudiotronsAction";
    private static final Logger logger = Logger.getLogger(RenderAudiotronsAction.class);

    private String audiotronNameToRequest;
    private InputStream inputStream;

    public String execute() {
    	
        logger.info(TAG + "==============================================================================================");
        logger.info(TAG + "-----------------------SERVERSIDE RenderAudiotronsAction STARTS----------------------------");

        if(logger.isInfoEnabled()){
            logger.info(TAG + ".....INFORMATION LOG......");
        }else if(logger.isDebugEnabled()){
            logger.debug(TAG + "......DEBUG LOG.......");
        }
        
        String audiotronRequestedFileName = this.getAudiotronNameToRequest();
        logger.info(TAG + "####Information: Requested file name is: " + audiotronRequestedFileName);

        setAudiotronFromDatabase(audiotronRequestedFileName);
        
        return SUCCESS;
    }
    
    /**
     * This method sets all audiotrons in a list with their name equal to the parameter
     * name passed to be streamed to the calling method.
     * @param fileName
     */
    private void setAudiotronFromDatabase(String fileName) {
    	logger.info(TAG + "--------Inside setAudiotronFromDatabase()---------");
    	
    	IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
    	inputStream = daoHandler.getAudiotron(fileName);
    }
    
    public void setAudiotronNameToRequest(String audiotronNameInRequest) {
        this.audiotronNameToRequest = audiotronNameInRequest;
    }

    private String getAudiotronNameToRequest() {
        return audiotronNameToRequest;
    }
    
    /**
     * Returns the list of audiotrons as an inputstream
     * back to the calling method.
     *  
     * @return InputStream inputStream
     */
	public InputStream getInputStream() {
		return inputStream;
	}
}