package tw.com.softleader.frnech.fu.GenJavaHelper.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;

import ch.qos.logback.core.util.FileUtil;
import tw.com.softleader.frnech.fu.GenJavaHelper.common.utils.FrenchFileUtils;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.core.ZipFile;

/**
 * @author French.Fu
 * target : to return a zip file system incloud all java code
 * 
 */
@Service
public class MapToJavaFileFolderSystemService {

	
	public File scanMapToJavaFileFolderZip(Map<String, String> javaCodeMap) throws IOException, ZipException {
		
		//init
		File resultZipFile = null;
		
		resultZipFile = FrenchFileUtils.createTempDirectory();
		File targetFolder = FrenchFileUtils.createTempDirectory();
		//ex : C:\Users\DEFAUL~1.LAP\AppData\Local\Temp\temp8023567088028469974658847381642100
		String targetPath = targetFolder.getPath();
		
		for(String javaFilepath : javaCodeMap.keySet()) {
			
			String stringfileRealPath = targetPath +"/"+javaFilepath;
			//String stringFolderRealPath = stringfileRealPath.substring(0,stringfileRealPath.lastIndexOf("/")-1);
			//File directory = new File(stringFolderRealPath);
			
			//if (! directory.exists()){
				//FileUtil.createMissingParentDirectories(directory);
			//	directory.mkdir();
			//}
			
			File file = new File(stringfileRealPath);
			if(!file.exists()) {
				FileUtil.createMissingParentDirectories(file);
				file.createNewFile();
			}
		    
			try{
		        FileWriter fw = new FileWriter(file.getPath());
		        BufferedWriter bw = new BufferedWriter(fw);
		        bw.write(javaCodeMap.get(javaFilepath));
		        bw.close();
		        fw.close();
		    } catch (IOException e){
		        e.printStackTrace();
		        System.exit(-1);
		    }
			
		}
		resultZipFile.delete();
		ZipFile zipFile = new ZipFile(resultZipFile);
		ZipParameters parameters = new ZipParameters();
		parameters.setIncludeRootFolder(false);
		zipFile.createZipFileFromFolder(targetFolder, parameters, false, -1);	
		targetFolder.delete();
		
		return resultZipFile;
	}

	
	public static void main (String [] aaaa) throws IOException {
		
		File targetFolder = FrenchFileUtils.createTempDirectory();
		String targetPath = targetFolder.getPath();
		System.out.println(targetPath);
		targetFolder.delete();
		
	}
	
	
}
