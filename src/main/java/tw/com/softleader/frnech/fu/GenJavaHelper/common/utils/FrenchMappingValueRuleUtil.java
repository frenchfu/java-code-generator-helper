package tw.com.softleader.frnech.fu.GenJavaHelper.common.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import tw.com.softleader.frnech.fu.GenJavaHelper.model.ColumnDetail;

public class FrenchMappingValueRuleUtil {
	
	
	public static void main (String[] test ) {
		
		ColumnDetail columnDetail = new ColumnDetail ();
		columnDetail.setPk("UK");
		columnDetail.setColumnName("this_is_my_column");
		columnDetail.setSetValueLogic("utilBs.getFirstTwoChar(QotPolicyRiskPayInfoData.policyNo,\"YYYY\",qotPolicyInsuredData.getLocalName())");//expect univerVo.getQotPolicyRiskPayInfo().getCreditCardCheckNo()
		System.out.println(getMappingValueUnitStr(columnDetail));
		
	}
	
	final static String  EMPTY_STR = "\"\"";
	final static String  NULL = "null";
	final static String  SYSTEM_DAY  = "LocalDateTime.now()";
	final static String  ZERO  = "BigDecimal.ZERO";
	final static String  NULL2  = "";
	final static String  NULL3  = null;
	final static String  UN_SURE  = null;
	
	

	public static String getMappingValueUnitStr(ColumnDetail columnDetail) {		
		StringBuffer resultSb = new StringBuffer();
		String setValueLogic = columnDetail.getSetValueLogic();
		String javaColumnName = BeanHump.underlineToCamel2(columnDetail.getColumnName().toLowerCase());
		String methodName = "set"+ javaColumnName.substring(0, 1).toUpperCase() +javaColumnName.substring(1);
		
		if(columnDetail.getIsPk()) {
			resultSb.append("identityVo.");
		}else {
			resultSb.append("vo.");
		}
		resultSb.append(methodName).append("(");
		
		//main logic
		StringBuffer mainLogicSb =  new StringBuffer(); 
		mainLogicSb.append(isCommonDefaultValue(setValueLogic));
		mainLogicSb.append(isSelfDefaultStr(setValueLogic));
		mainLogicSb.append(isMappingBsMethodRule(setValueLogic));
		mainLogicSb.append(isUtilBsMethodRule(setValueLogic));
		if(StringUtils.isEmpty(mainLogicSb.toString())) {
			mainLogicSb.append(isGetFromUniverseObj(setValueLogic));
		}
		
		resultSb.append(mainLogicSb.toString());
		
		//end
		resultSb.append(");");
		return resultSb.toString();
		//		identityVo.setIpolicy1(policyData.getPolicyNo());
		//      vo.setItakeover("");
	}
	
	
	private static String isGetFromUniverseObj(String setValueLogic) {//dentityVo.setIpolicy1(policyData.getPolicyNo());
		StringBuffer resultSb = new StringBuffer();
		
		List<String> i = Lists.newArrayList(setValueLogic.split("\\."));
		int count = 0;
		for(String s :  i) {
			if(count == 0) {
				resultSb.append("universeObj.get");
				System.out.println(s);
				resultSb.append(s.substring(0,1).toUpperCase()).append(s.substring(1)).append("()");
			}else {
				if(s.length() >3 && s.substring(0,3).equals("get")) {
					resultSb.append(".").append(s);
				}else {
					resultSb.append(".get").append(s.substring(0,1).toUpperCase()).append(s.substring(1)).append("()");
				}
			}
			count++;
		}
		
		
		return resultSb.toString();
	}

	private static String isUtilBsMethodRule(String setValueLogic) {		
		StringBuffer resultSb = new StringBuffer();
		if(setValueLogic.contains("utilBs.")) {//utilBs.sumQotPolicyRiskItemBypolicyRiskId(QotPolicyRiskPayInfo)
			int index = setValueLogic.indexOf("(");
			resultSb.append(setValueLogic.substring(0, index));
			String s = setValueLogic.substring(index+1,setValueLogic.length() - 1 ); //QotPolicyRiskPayInfo
			List<String> i = Lists.newArrayList(s.split(","));
			String pointer = "";
			resultSb.append("(");
			for(String str :  i) {
				String commonDefaultValue = isSelfDefaultStr(str);
				commonDefaultValue += isCommonDefaultValue(str);
				if(commonDefaultValue.length()>0) {
					resultSb.append(pointer).append(commonDefaultValue);
				}else {
					resultSb.append(pointer).append(isGetFromUniverseObj(str));
				}
				pointer = ",";
			}
			resultSb.append(")");
		}
		return resultSb.toString();
	}

	private static String isMappingBsMethodRule(String setValueLogic) {
		StringBuffer resultSb = new StringBuffer();
		if(setValueLogic.contains("mappingBs.")) {//utilBs.sumQotPolicyRiskItemBypolicyRiskId(QotPolicyRiskPayInfo)
			int index = setValueLogic.indexOf("(");
			resultSb.append(setValueLogic.substring(0, index));
			String s = setValueLogic.substring(index+1,setValueLogic.length() - 1 ); //QotPolicyRiskPayInfo
			List<String> i = Lists.newArrayList(s.split(","));
			String pointer = "";
			resultSb.append("(");
			for(String str :  i) {
				String commonDefaultValue = isSelfDefaultStr(str);
				commonDefaultValue += isCommonDefaultValue(str);
				if(commonDefaultValue.length()>0) {
					resultSb.append(pointer).append(commonDefaultValue);
				}else {
					resultSb.append(pointer).append(isGetFromUniverseObj(str));
				}
				pointer = ",";
			}
			resultSb.append(")");
		}
		return resultSb.toString();
	}

	private static String  isCommonDefaultValue(String setValueLogic) {
		StringBuffer resultSb = new StringBuffer();
		switch (setValueLogic) {
		case "ZERO":
			resultSb.append(ZERO);
			break;
		case "EMPTY_STR":
			resultSb.append(EMPTY_STR);
			break;
		case "NULL":
			resultSb.append(NULL);
			break;
		case NULL2:
			resultSb.append(NULL);
			break;
		case "SYSTEM_DAY":
			resultSb.append(SYSTEM_DAY);
			break;
		case "UN_SURE":
			resultSb.append(UN_SURE);
			break;
		case "?":
			resultSb.append(UN_SURE);
			break;
		default:
			if(setValueLogic == null) {
				resultSb.append(NULL);
			}
			break;
		}
		return resultSb.toString();
	}
	
	
	private static String isSelfDefaultStr(String setValueLogic) {		
		StringBuffer resultSb = new StringBuffer();
		//rule 1 如果開頭和結尾都是 " 則取得中間的VALUE當作DEFAULT STR 如果沒有則返回空白  "abc" return "abc"
		if(setValueLogic!=null) {
			if(setValueLogic.length() > 1) {
				if(setValueLogic.substring(0, 1).equals("\"")) {
					if(setValueLogic.substring(setValueLogic.length()-1).equals("\"")) {
						resultSb.append(setValueLogic);
					}
				}
			}
		}
		return resultSb.toString();
	}
	
	
	

}
