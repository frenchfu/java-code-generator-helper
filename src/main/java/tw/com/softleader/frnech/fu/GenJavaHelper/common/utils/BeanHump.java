package tw.com.softleader.frnech.fu.GenJavaHelper.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**
 * 驼峰转换
 * @author 胡汉三
 * 2017年1月19日 下午4:42:58
 * CODE FROM  https://blog.csdn.net/hzw2312/article/details/54617733
 * AND DO LITTLE MODIFY FOR FRENCH.FU 
 * 
 */
public class BeanHump {
	
	//转变的依赖字符
	public static final char UNDERLINE='_';
	public static final char MIDDLELINE='-';
	
	/**
	 * 将驼峰转换成"_"(userId:user_id)
	 * @param param
	 * @return
	 */
	public static String camelToUnderline(String param){  
		if (param==null||"".equals(param.trim())){  
			return "";  
		}  
		int len=param.length();  
		StringBuilder sb=new StringBuilder(len);  
		for (int i = 0; i < len; i++) {  
			char c=param.charAt(i);  
			if (Character.isUpperCase(c)){  
				sb.append(UNDERLINE);  
				sb.append(Character.toLowerCase(c));  
			}else{  
				sb.append(c);  
			}  
		}  
		return sb.toString();  
	}
	/**
	 * 将"_"转成驼峰(user_id:userId)
	 * @param param
	 * @return
	 */
	public static String underlineToCamel(String param){  
		if (param==null||"".equals(param.trim())){  
			return "";  
		}  
		int len=param.length();  
		StringBuilder sb=new StringBuilder(len);  
		for (int i = 0; i < len; i++) {  
			char c=param.charAt(i);  
			if (c==UNDERLINE){  
				if (++i<len){  
					sb.append(Character.toUpperCase(param.charAt(i)));  
				}  
			}else{  
				sb.append(c);  
			}  
		}  
		return sb.toString();  
	}
	/**
	 * 将"_"转成驼峰(user_id:userId)
	 * @param param
	 * @return
	 */
	public static String underlineToCamel2(String param){  
		if (param==null||"".equals(param.trim())){  
			return "";  
		}  
		StringBuilder sb=new StringBuilder(param);  
		Matcher mc= Pattern.compile(UNDERLINE+"").matcher(param);  
		int i=0;  
		while (mc.find()){  
			int position=mc.end()-(i++);  
			String.valueOf(Character.toUpperCase(sb.charAt(position)));  
			sb.replace(position-1,position+1,sb.substring(position,position+1).toUpperCase());  
		}  
		return sb.toString();  
	}
	
	/**
	 * 将"_"转成驼峰(USER_ID:UserId)
	 * @param param
	 * @return
	 * by French
	 */
	public static String underlineToCamel3(String param){  
		if (param==null||"".equals(param.trim())){  
			return "";  
		}
		param = param.toLowerCase();
		StringBuilder sb=new StringBuilder(param);  
		Matcher mc= Pattern.compile(UNDERLINE+"").matcher(param);  
		int i=0;  
		while (mc.find()){  
			int position=mc.end()-(i++);  
			String.valueOf(Character.toUpperCase(sb.charAt(position)));  
			sb.replace(position-1,position+1,sb.substring(position,position+1).toUpperCase());  
		}
		param = sb.toString().substring(0,1).toUpperCase() + sb.toString().substring(1);
		return param;  
	} 
	
	/*
	 * 测试
	 */
	public static void main(String[] args) {
		System.out.println(camelToUnderline("userNameAll"));
		System.out.println(underlineToCamel("AO_PLYEDR_PREM"));
		System.out.println(underlineToCamel2("AO_PLYEDR_PREM"));
		System.out.println(underlineToCamel3("AO_PLYEDR_PREM"));
	}
}