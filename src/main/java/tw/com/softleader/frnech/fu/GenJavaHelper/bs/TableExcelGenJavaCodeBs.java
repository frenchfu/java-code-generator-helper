package tw.com.softleader.frnech.fu.GenJavaHelper.bs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.softleader.frnech.fu.GenJavaHelper.model.SettingFromOds;
import tw.com.softleader.frnech.fu.GenJavaHelper.model.TableDetail;
import tw.com.softleader.frnech.fu.GenJavaHelper.service.FileToObjectService;
import tw.com.softleader.frnech.fu.GenJavaHelper.service.MapToJavaFileFolderSystemService;
import tw.com.softleader.frnech.fu.GenJavaHelper.service.ObjectToStringService;

@Service
public class TableExcelGenJavaCodeBs {
	
	@Autowired
	FileToObjectService fileToObjectService;
	
	@Autowired
	ObjectToStringService objectToStringService;
	
	@Autowired
	MapToJavaFileFolderSystemService mapToJavaFileFolderSystemService;
	
	

	private FileToObjectService getfileToObjectService(){
		if(fileToObjectService == null) fileToObjectService = new FileToObjectService();
		return fileToObjectService;
	}
	
	private ObjectToStringService getObjectToStringService(){
		if(objectToStringService == null) objectToStringService = new ObjectToStringService();
		return objectToStringService;
	
	}
	private MapToJavaFileFolderSystemService getMapToJavaFileFolderSystemService(){
		if(mapToJavaFileFolderSystemService == null) mapToJavaFileFolderSystemService = new MapToJavaFileFolderSystemService();
		return mapToJavaFileFolderSystemService;
	}

	public static void main(String[] args) throws Exception {
		//InputStream inputStrem = new FileInputStream("C:\\gitbox\\ZFjasme\\softleader-jasmine-integration-rpc\\docs\\integration\\ahIntegrationGenTable.ods");
		InputStream inputStrem = new FileInputStream("C:\\gitbox\\fbg\\softleader-gardenia-integration-rpc\\docs\\fbgGenTable.ods");
		
		//step 0 TODO init default setting

		//final Logger app = Logger.getLogger("org.odftoolkit.odfdom.pkg.OdfXMLFactory");
		//app.setLevel(Level.WARNING);
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		rootLogger.setLevel(Level.OFF);
		for (Handler h : rootLogger.getHandlers()) {
		    h.setLevel(Level.OFF);
		}
		
		TableExcelGenJavaCodeBs mainBs = new TableExcelGenJavaCodeBs();
		
		try {
		
			SettingFromOds settingFromOds= mainBs.getfileToObjectService().scanOdtToSettingObj(inputStrem);
			inputStrem.close();
			//inputStrem = new FileInputStream("C:\\gitbox\\ZFjasme\\softleader-jasmine-integration-rpc\\docs\\integration\\ahIntegrationGenTable.ods");
			inputStrem = new FileInputStream("C:\\gitbox\\fbg\\softleader-gardenia-integration-rpc\\docs\\fbgGenTable.ods");

			
			List<TableDetail> tableDetailObjList = mainBs.getfileToObjectService().scanOdtToTableDetails(inputStrem);
			
			
			Map<String,String> javaCodeMapForIntegration = mainBs.getObjectToStringService().scanObjListToJavaCodeMap(settingFromOds , tableDetailObjList);
			Map<String,String> javaCodeMapForGateWay = mainBs.getObjectToStringService().scanObjListToJavaCodeForGateWay(settingFromOds , tableDetailObjList);
			//Map<String,String> javaCodeMapForGateWay = mainBs.getObjectToStringService().scanObjListToJavaCodeMapForVo(settingFromOds , tableDetailObjList);
			
			
			
			File resultFile = mainBs.getMapToJavaFileFolderSystemService().scanMapToJavaFileFolderZip(javaCodeMapForIntegration);
			
			InputStream is = null;
		    OutputStream os = null;
		    try {
		    	File aFile = new File("C:/zipFile/zipfileForIntegration.zip");
		    	aFile.createNewFile();
		        is = new FileInputStream(resultFile);
		        os = new FileOutputStream(aFile);
		        byte[] buffer = new byte[1024];
		        int length;
		        while ((length = is.read(buffer)) > 0) {
		            os.write(buffer, 0, length);
		        }
		    } finally {
		        is.close();
		        os.close();
		    }
		    resultFile.delete();
		    
		    //VO
			is = null;
		    os = null;
		    
		    resultFile = mainBs.getMapToJavaFileFolderSystemService().scanMapToJavaFileFolderZip(javaCodeMapForGateWay);
		    try {
		    	File aFile = new File("C:/zipFile/zipfileForGateWay.zip");
		    	aFile.createNewFile();
		        is = new FileInputStream(resultFile);
		        os = new FileOutputStream(aFile);
		        byte[] buffer = new byte[1024];
		        int length;
		        while ((length = is.read(buffer)) > 0) {
		            os.write(buffer, 0, length);
		        }
		    } finally {
		        is.close();
		        os.close();
		    }
		    resultFile.delete();			
			
			
		}catch (Exception e) {
			throw e;
		} finally {
			inputStrem.close();
		}
	}

	
	
	public List<File> genJavaFileFromTableDetails(List<TableDetail> tableDetail){
		
		
		
		return null;
	}

	
}
