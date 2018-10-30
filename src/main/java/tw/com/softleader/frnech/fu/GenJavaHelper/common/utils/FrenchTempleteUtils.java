package tw.com.softleader.frnech.fu.GenJavaHelper.common.utils;

import java.util.Map;

public class FrenchTempleteUtils {
	
	
	static public String templeteReplaceByKeyValueMapLogic(String templete , Map<String,String> keyValueMap) {
		
		String resultStr = templete;
		for(String key :  keyValueMap.keySet()) {
			resultStr = resultStr.replace(key, keyValueMap.get(key));
		}	
		return resultStr;
		
	}
	
	

}
