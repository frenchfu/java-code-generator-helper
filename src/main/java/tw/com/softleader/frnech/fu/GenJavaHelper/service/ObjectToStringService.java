package tw.com.softleader.frnech.fu.GenJavaHelper.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import tw.com.softleader.frnech.fu.GenJavaHelper.common.utils.BeanHump;
import tw.com.softleader.frnech.fu.GenJavaHelper.common.utils.FrenchFileUtils;
import tw.com.softleader.frnech.fu.GenJavaHelper.common.utils.FrenchMappingValueRuleUtil;
import tw.com.softleader.frnech.fu.GenJavaHelper.common.utils.FrenchTempleteUtils;
import tw.com.softleader.frnech.fu.GenJavaHelper.model.ColumnDetail;
import tw.com.softleader.frnech.fu.GenJavaHelper.model.SettingFromOds;
import tw.com.softleader.frnech.fu.GenJavaHelper.model.TableDetail;

/**
 * @author French.Fu
 * target to gen Java String
 *
 */
@Service
public class ObjectToStringService {

	private final String IS_MODEL = "IS_MODEL";
	private final String IS_VO = "IS_VO";
	private final String IS_VO_SERVICE = "IS_VO_SERVICE";
	private final String IS_MODEL_IDENTITY = "IS_MODEL_IDENTITY";
	private final String IS_IDENTITY_VO = "IS_VO_IDENTITY";
	private final String IS_ENTITY = "IS_ENTITY";
	private final String IS_DAO = "IS_DAO";
	private final String IS_SERVICE = "IS_SERVICE";
	private final String IS_IDENTITY = "IS_IDENTITY";
	private final String TAB ="	";
	private final String NEWLINE = System.lineSeparator();
	
	public static Map<String,String> columnTypeclassMappingMap = Maps.newHashMap();
	public static String TEMPLETE_FOLDER_PATH = new ObjectToStringService().getClass().getResource("/").getPath()+"templete/";
	public static String WEB_CONTROLLER_GET_METHOD_TEMPLETE_FILENAME = "webControllerGetMethodTemplete.txt";
	public static String WEB_CONTROLLER_INSERT_METHOD_TEMPLETE_FILENAME = "webControllerInsertMethodTemplete.txt";
	public static String WEB_CONTROLLER_UPDATE_METHOD_TEMPLETE_FILENAME = "webControllerUpdateMethodTemplete.txt";
	public static String WEB_CONTROLLER_DELETE_METHOD_TEMPLETE_FILENAME = "webControllerDeleteMethodTemplete.txt";
	public static String WEB_STUB_METHOD_TEMPLETE_FILENAME = "stubCrudMethodTemplete.txt";
	public static String CRUD_SERVICE_TEMPLETE_FILENAME = "crudServiceTemplete.txt";
	public static String GATEWAY_SETTING_VO_VALUE_SERVICE_TEMPLETE_FILENAME = "mappingServiceTemplete.txt";

	
	/**
	 *  
	 *  for Entire System CRUD CODE entiyt ~ dao ~ service ~ controlle
	 * @throws IOException 
	 * 
	 */
	public Map<String, String> scanObjListToJavaCodeMap(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) throws IOException {

		//init
		Map<String, String> resultMap = Maps.newHashMap();
		
		for(TableDetail  tableDetail : tableDetailObjList) {

			Map<String, String> loopUnitMap = this.genObjToJavaCodeMap(settingFromOds,tableDetail);
			resultMap.putAll(loopUnitMap);
			
		}
		
		Map<String,String> webControllerCodeMap = this.genWebControllerCodeForAllTable(settingFromOds, tableDetailObjList);
		resultMap.putAll(webControllerCodeMap);
		
		return resultMap;
	
	}
	
	//**produce A controller contain all table save update code
	public Map<String, String> genWebControllerCodeForAllTable(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) {
		
		//init
		Map<String , String > resultMap = Maps.newHashMap();
		StringBuffer codeSb = new StringBuffer();
		String packageStr = "";
		String controlleImportdStr = "";
		String commentStr ="";
		String annotationStr ="";
		String publicClassPartStr="";
		String urlPartStr="";
		String autowiredPartStr="";
		String methodPartStr= "" ;
		String endPartSrt = "";
		
		
		
		//package
		packageStr = "package " + settingFromOds.getPackageToRpc() + ";" + NEWLINE;
		
		//import
		controlleImportdStr = this.getControlleImportStr(settingFromOds, tableDetailObjList);
		
		//comment part
		commentStr = getWebControllerCommentPartStr(settingFromOds, tableDetailObjList);
		
		//annotation part
		annotationStr = new StringBuffer().append("@Slf4j").append(NEWLINE)
				.append("@RestController").append(NEWLINE)
				.append("@RequestMapping(\"").append(settingFromOds.getControllerRequestMapping()).append("\")").append(NEWLINE).toString();
		
		//publicClassPart
		publicClassPartStr = new StringBuffer().append("public class ").append(settingFromOds.getControllerName()).append(" {").append(NEWLINE).toString();
		
		//urlPartStr
		urlPartStr = getWebControllerUrlPartStr(settingFromOds, tableDetailObjList);
		
		//autowired
		autowiredPartStr = getWebControllerAutowiredPartStr(settingFromOds, tableDetailObjList);
		
		//method
		methodPartStr = getWebControllerMethodPartStr(settingFromOds, tableDetailObjList);
		
		//endPart
		endPartSrt = new StringBuffer("}").append(NEWLINE).toString();
		
		
		//combine
		codeSb.append(packageStr).append(NEWLINE);
		codeSb.append(controlleImportdStr).append(NEWLINE);
		codeSb.append(commentStr).append(NEWLINE);
		codeSb.append(annotationStr).append(NEWLINE);
		codeSb.append(publicClassPartStr).append(NEWLINE);//public class
		codeSb.append(urlPartStr).append(NEWLINE);
		codeSb.append(autowiredPartStr).append(NEWLINE);
		codeSb.append(methodPartStr).append(NEWLINE);//combine end
		codeSb.append(endPartSrt).append(NEWLINE);//combine end
		
		//put key & Code Stirng
		resultMap.put(settingFromOds.getPackageToRpc().replace(".", "/") +"/" + settingFromOds.getControllerName() +".java" , codeSb.toString());
		
		//return
		return resultMap;
		
	}

	private String getWebControllerMethodPartStr(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		
		//loop
		tableDetailObjList.forEach(t -> {
			try {
				resultSb.append(getWebControllerGetMethodStrFromTable(t)).append(NEWLINE).append(NEWLINE);
				resultSb.append(getWebControllerInsertMethodStrFromTable(t)).append(NEWLINE).append(NEWLINE);
				resultSb.append(getWebControllerUpdateMethodStrFromTable(t)).append(NEWLINE).append(NEWLINE);
				resultSb.append(getWebControllerDeleteMethodStrFromTable(t)).append(NEWLINE).append(NEWLINE);
			}catch ( Exception e) {
				e.printStackTrace();
			}
		});
		return resultSb.toString();
	}
	
	private String getStubMethodPartStr(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		
		//loop
		tableDetailObjList.forEach(t -> {
			try {
				resultSb.append(getStubMethodPartStrFromTable(t)).append(NEWLINE).append(NEWLINE);
			}catch ( Exception e) {
				e.printStackTrace();
			}
		});
		return resultSb.toString();
	}

	private String getStubMethodPartStrFromTable(TableDetail tableDetail) throws IOException {
		// init
		StringBuffer resultSb = new StringBuffer();

		// define
		String entityClassName = getJavaNameFromTableName2(tableDetail.getTableName(), IS_ENTITY);
		String tableName = tableDetail.getTableName();
		String serviceClassName2 = BeanHump.underlineToCamel2(tableDetail.getTableName().toLowerCase()) + "Service";
		String templete = FrenchFileUtils
				.loadFileToStirng(TEMPLETE_FOLDER_PATH + WEB_STUB_METHOD_TEMPLETE_FILENAME);

		// get map
		Map<String, String> keyValueMap = Maps.newHashMap();
		keyValueMap.put("${entityClassName}", entityClassName);
		keyValueMap.put("${tableName}", tableName);

		resultSb.append(FrenchTempleteUtils.templeteReplaceByKeyValueMapLogic(templete, keyValueMap));

		return resultSb.toString();
	}

	private String getWebControllerDeleteMethodStrFromTable(TableDetail tableDetail) throws IOException {
		// init
		StringBuffer resultSb = new StringBuffer();

		// define
		String entityClassName = getJavaNameFromTableName2(tableDetail.getTableName(), IS_ENTITY);
		String tableName = tableDetail.getTableName();
		String serviceClassName2 = BeanHump.underlineToCamel2(tableDetail.getTableName().toLowerCase()) + "Service";
		String templete = FrenchFileUtils
				.loadFileToStirng(TEMPLETE_FOLDER_PATH + WEB_CONTROLLER_DELETE_METHOD_TEMPLETE_FILENAME);

		// get map
		Map<String, String> keyValueMap = Maps.newHashMap();
		keyValueMap.put("${entityClassName}", entityClassName);
		keyValueMap.put("${tableName}", tableName);
		keyValueMap.put("${serviceClassName2}", serviceClassName2);

		resultSb.append(FrenchTempleteUtils.templeteReplaceByKeyValueMapLogic(templete, keyValueMap));

		return resultSb.toString();
	}

	private String getWebControllerUpdateMethodStrFromTable(TableDetail tableDetail) throws IOException {
		// init
		StringBuffer resultSb = new StringBuffer();

		// define
		String entityClassName = getJavaNameFromTableName2(tableDetail.getTableName(), IS_ENTITY);
		String tableName = tableDetail.getTableName();
		String serviceClassName2 = BeanHump.underlineToCamel2(tableDetail.getTableName().toLowerCase()) + "Service";
		String templete = FrenchFileUtils
				.loadFileToStirng(TEMPLETE_FOLDER_PATH + WEB_CONTROLLER_UPDATE_METHOD_TEMPLETE_FILENAME);

		// get map
		Map<String, String> keyValueMap = Maps.newHashMap();
		keyValueMap.put("${entityClassName}", entityClassName);
		keyValueMap.put("${tableName}", tableName);
		keyValueMap.put("${serviceClassName2}", serviceClassName2);

		resultSb.append(FrenchTempleteUtils.templeteReplaceByKeyValueMapLogic(templete, keyValueMap));

		return resultSb.toString();
	}

	//Web Inert Method
	private String getWebControllerInsertMethodStrFromTable(TableDetail tableDetail) throws IOException {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		
		//define
		String entityClassName = getJavaNameFromTableName2(tableDetail.getTableName(),IS_ENTITY);
		String tableName = tableDetail.getTableName();
		String serviceClassName2 = BeanHump.underlineToCamel2(tableDetail.getTableName().toLowerCase()) + "Service";
		String templete = FrenchFileUtils.loadFileToStirng(TEMPLETE_FOLDER_PATH+WEB_CONTROLLER_INSERT_METHOD_TEMPLETE_FILENAME);
		
		//get map
		Map<String, String> keyValueMap = Maps.newHashMap();
		keyValueMap.put("${entityClassName}", entityClassName);
		keyValueMap.put("${tableName}", tableName);
		keyValueMap.put("${serviceClassName2}", serviceClassName2);
		
		resultSb.append(FrenchTempleteUtils.templeteReplaceByKeyValueMapLogic(templete, keyValueMap));		

		return resultSb.toString();
		
	}
	

	//** WEB GET METHOD */
	private String getWebControllerGetMethodStrFromTable(TableDetail tableDetail) throws IOException {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		
		//define
		String entityClassName  = getJavaNameFromTableName2(tableDetail.getTableName(),IS_ENTITY);
		String tableName = tableDetail.getTableName();
		String serviceClassName2 = BeanHump.underlineToCamel2(tableDetail.getTableName().toLowerCase()) + "Service";
		String templete = FrenchFileUtils.loadFileToStirng(TEMPLETE_FOLDER_PATH+WEB_CONTROLLER_GET_METHOD_TEMPLETE_FILENAME);
		
		//get map
		Map<String, String> keyValueMap = Maps.newHashMap();
		keyValueMap.put("${entityClassName}", entityClassName);
		keyValueMap.put("${tableName}", tableName);
		keyValueMap.put("${serviceClassName2}", serviceClassName2);
		
		resultSb.append(FrenchTempleteUtils.templeteReplaceByKeyValueMapLogic(templete, keyValueMap));		

		return resultSb.toString();
		
	}

	private String getControlleImportStr(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		
		//service
		resultSb.append("import ").append(settingFromOds.getPackageToService()).append(".*;").append(NEWLINE);
		//entity
		resultSb.append("import ").append(settingFromOds.getPackageToEntity()).append(".*;").append(NEWLINE);
		
		//common part
		resultSb.append("import org.springframework.beans.factory.annotation.Autowired;").append(NEWLINE);
		resultSb.append("import org.springframework.http.ResponseEntity;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.PostMapping;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.GetMapping;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.DeleteMapping;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.PutMapping;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.RequestBody;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.RequestMapping;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.RestController;").append(NEWLINE);
		
		//soft leader Part
		resultSb.append("import lombok.extern.slf4j.Slf4j;").append(NEWLINE);
		resultSb.append("import tw.com.softleader.jasmine.commons.http.JasmineResponseStatus;").append(NEWLINE);
		resultSb.append("import tw.com.softleader.web.http.ResponseDetails;").append(NEWLINE);
		resultSb.append("import tw.com.softleader.web.http.Responses;").append(NEWLINE);

		return resultSb.toString();
	}

	/**
	 *  
	 *  for Only produce VO
	 * 
	 */
	public Map<String, String> scanObjListToJavaCodeMapForVo(SettingFromOds settingFromOds,List<TableDetail> tableDetailObjList) {
		//init
		Map<String, String> resultMap = Maps.newHashMap();
		
		//loop
		for(TableDetail  tableDetail : tableDetailObjList) {
			Map<String, String> loopUnitMap = this.genObjToVoJavaCodeMap(settingFromOds,tableDetail);
			resultMap.putAll(loopUnitMap);				
		}	
		
		return resultMap;
	}

	
	
	private Map<String, String> genObjToVoJavaCodeMap(SettingFromOds settingFromOds, TableDetail tableDetail) {
		//init
		Map<String, String> resultMap = Maps.newHashMap();
		resultMap.putAll((this.genJavaVoCodeFromTableOnj(settingFromOds,tableDetail)));
		return resultMap;
	}
	

	private Map<String, String> genObjToGateWayServiceJavaCodeMap(SettingFromOds settingFromOds,TableDetail tableDetail) throws IOException {
		Map<String, String> resultMap = Maps.newHashMap();
		resultMap.putAll((this.genJavaGateServiceCodeFromTableOnj(settingFromOds, tableDetail)));
		return resultMap;
	}

	private Map<String, String> genObjToJavaCodeMap(SettingFromOds settingFromOds, TableDetail tableDetail) throws IOException {
		
		//init
		Map<String, String> resultMap = Maps.newHashMap();
		StringBuffer daoSb = new StringBuffer(); 
		StringBuffer serviceSb = new StringBuffer(); 
		
		//TODO interfaceService
		
		resultMap.putAll((this.genJavaEntityStrCodeFromTableOnj(settingFromOds,tableDetail)));
		daoSb.append(this.genJavaDaoStrCodeFromTableOnj(settingFromOds,tableDetail));
		serviceSb.append(this.genJavaServiceStrCodeFromTableOnj(settingFromOds,tableDetail));
		
		//resultMap.put(settingFromOds.getPackageToEntity() +"." + getJavaNameFromTableName(tableDetail.getTableName(),IS_ENTITY) , entitySb.toString());
		resultMap.put(settingFromOds.getPackageToDao().replace(".", "/") +"/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_DAO) , daoSb.toString());
		resultMap.put(settingFromOds.getPackageToService().replace(".", "/")+"/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_SERVICE) , serviceSb.toString());
		
		return resultMap;
		
	}


	private String genJavaServiceStrCodeFromTableOnj(SettingFromOds settingFromOds, TableDetail tableDetail) throws IOException {
		StringBuffer resultSb = new StringBuffer();
		
		StringBuffer importPartSb = new StringBuffer();
		String className = getJavaNameFromTableName2(tableDetail.getTableName(),IS_ENTITY);
		
		//import part
		importPartSb.append("import org.springframework.beans.factory.annotation.Autowired;").append(NEWLINE);
		importPartSb.append("import org.springframework.stereotype.Service;").append(NEWLINE);
		importPartSb.append("import ").append(settingFromOds.getPackageToDao()).append(".").append(className).append("Dao;").append(NEWLINE);
		importPartSb.append("import ").append(settingFromOds.getPackageToEntity()).append(".").append(className).append(";").append(NEWLINE);
		importPartSb.append("import ").append(settingFromOds.getPackageToEntity()).append(".identity.").append(className).append("Identity;").append(NEWLINE);
		
		//Apend str Logic dao part
		//package start
		resultSb.append("package ").append(settingFromOds.getPackageToService()).append(";").append(NEWLINE);
		resultSb.append(importPartSb.toString());
		resultSb.append(NEWLINE);
		resultSb.append("/**").append(NEWLINE);
		resultSb.append("* @author French.Fu").append(NEWLINE);
		resultSb.append("*/").append(NEWLINE);
		resultSb.append("@Service").append(NEWLINE);
		
		String entityClassName = getJavaNameFromTableName2(tableDetail.getTableName(), IS_ENTITY);
		String tableName = tableDetail.getTableName();
		String serviceClassName2 = BeanHump.underlineToCamel2(tableDetail.getTableName().toLowerCase()) + "Service";
		String templete = FrenchFileUtils
				.loadFileToStirng(TEMPLETE_FOLDER_PATH + CRUD_SERVICE_TEMPLETE_FILENAME);

		// get map
		Map<String, String> keyValueMap = Maps.newHashMap();
		keyValueMap.put("${entityClassName}", entityClassName);
		keyValueMap.put("${tableName}", tableName);
		keyValueMap.put("${serviceClassName2}", serviceClassName2);

		resultSb.append(FrenchTempleteUtils.templeteReplaceByKeyValueMapLogic(templete, keyValueMap));
		
		
		
		return resultSb.toString();
		
	}
	
	private String genJavaDaoStrCodeFromTableOnj(SettingFromOds settingFromOds, TableDetail tableDetail) {
	
		StringBuffer resultSb = new StringBuffer();
		
		StringBuffer importPartSb = new StringBuffer();
		String className = getJavaNameFromTableName2(tableDetail.getTableName(),IS_ENTITY);
		
		//import part
		importPartSb.append("import org.springframework.data.repository.CrudRepository;").append(NEWLINE);
		importPartSb.append("import ").append(settingFromOds.getPackageToEntity()).append(".").append(className).append(";").append(NEWLINE);
		importPartSb.append("import ").append(settingFromOds.getPackageToEntity()).append(".identity.").append(className).append("Identity;").append(NEWLINE);
		
		//Apend str Logic dao part
		//package start
		resultSb.append("package ").append(settingFromOds.getPackageToDao()).append(";").append(NEWLINE);
		resultSb.append(NEWLINE);
		resultSb.append(importPartSb.toString());
		resultSb.append(NEWLINE);
		resultSb.append("/**").append(NEWLINE);
		resultSb.append("* @author French.Fu").append(NEWLINE);
		resultSb.append("*/").append(NEWLINE);
		resultSb.append("public interface ").append(className).append("Dao extends CrudRepository<")
		.append(className).append(",").append(className).append("Identity> {").append(NEWLINE);
		resultSb.append(NEWLINE);
		resultSb.append("}");
		return resultSb.toString();

	}
	
	private Map<String, String> genJavaGateServiceCodeFromTableOnj(SettingFromOds settingFromOds,TableDetail tableDetail) throws IOException {
		// init
		Map<String, String> resultMap = Maps.newHashMap();
		StringBuffer gateWayServiceSb = new StringBuffer();
		
		
		// define
		String voClassName = getJavaNameFromTableName2(tableDetail.getTableName(), IS_VO);
		String settingValuePart = getGateServiceSettingValuePart(tableDetail);
		String identityVoClassName = getJavaNameFromTableName2(tableDetail.getTableName(), IS_IDENTITY_VO);
		String templete = FrenchFileUtils.loadFileToStirng(TEMPLETE_FOLDER_PATH + GATEWAY_SETTING_VO_VALUE_SERVICE_TEMPLETE_FILENAME);

		// get map
		Map<String, String> keyValueMap = Maps.newHashMap();
		keyValueMap.put("${voClassName}", voClassName);
		keyValueMap.put("${settingValuePart}", settingValuePart);
		keyValueMap.put("${identityVoClassName}", identityVoClassName);
		
		gateWayServiceSb.append(FrenchTempleteUtils.templeteReplaceByKeyValueMapLogic(templete, keyValueMap));

		resultMap.put(settingFromOds.getPackageToGateWayService().replace(".", "/") + "/"
				+ getJavaNameFromTableName(tableDetail.getTableName(), IS_VO_SERVICE), gateWayServiceSb.toString());

		return resultMap;
	}
	

	private String getGateServiceSettingValuePart(TableDetail tableDetail) {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		System.out.println("tableDetail.getTableName()"+tableDetail.getTableName());
		for(ColumnDetail columnDetail :  tableDetail.getColumnDetails()) {
			System.out.println(columnDetail.getColumnName());
			String mappingValueUnitStr = FrenchMappingValueRuleUtil.getMappingValueUnitStr(columnDetail);
			resultSb.append(TAB).append(TAB).append(mappingValueUnitStr).append(NEWLINE);
		}		
		return resultSb.toString();
		
	}

	private Map<String,String> genJavaVoCodeFromTableOnj(SettingFromOds settingFromOds, TableDetail tableDetail) {
		//init
		Map<String,String> resultMap = Maps.newHashMap();
		
		StringBuffer voSb = new StringBuffer();
		StringBuffer identitySb = new StringBuffer();
		String importPartStr = "";
		String classInformationPartStr = "";
		String annotationPartStr = "";
		String embeddedIdPartStr = "";
		String className = getJavaNameFromTableName2(tableDetail.getTableName(),IS_ENTITY);
		String identityClassName = className + "IdentityVo";
		
		//partString Logic
		annotationPartStr = getAnnotationPartStrForVo(tableDetail,className);
		importPartStr = getImportPartStrForVo(settingFromOds,tableDetail,className);
		classInformationPartStr = getClassInformationPartStr(tableDetail);
		embeddedIdPartStr = getEmbeddedIdPartStrForVo(tableDetail,identityClassName);
		
		
		//Apend str Logic entity part
		//package
		voSb.append("package ").append(settingFromOds.getPackageToVo()).append(";").append(NEWLINE)
		.append(NEWLINE)
		.append(importPartStr)//import
		.append(NEWLINE)
		.append(classInformationPartStr)//class Information Part
		.append(annotationPartStr);
		voSb.append("public class ").append(className).append("Vo {").append(NEWLINE)
		.append(NEWLINE)
		.append(embeddedIdPartStr)
		.append(NEWLINE);
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			voSb.append(getColumnPartStrForVo(settingFromOds , columnItem));
		}
		//SET METHOD PART FOR VO need loop
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			if(!columnItem.getIsPk()) 
				voSb.append(getColumnVoSetPartStr(settingFromOds , columnItem));
		}		
		voSb.append(NEWLINE);
		voSb.append(" }");//entity part End
		
		
		//Apend str Logic Identity part
		//package
		
		identitySb.append("package ").append(settingFromOds.getPackageToVo()).append(".").append("identity;").append(NEWLINE)
		.append(NEWLINE)
		.append(importPartStr)//import
		.append(NEWLINE)
		.append(annotationPartStr)
		//.append("@Embeddable").append(NEWLINE)
		.append(classInformationPartStr)//class Information Part
		.append("public class ").append(identityClassName).append(" implements Serializable {").append(NEWLINE);
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			identitySb.append(getIdentityPartStrForVo(settingFromOds , columnItem));
		}		
		identitySb.append(NEWLINE);
		identitySb.append(TAB).append("public ").append(identityClassName).append("(){}").append(NEWLINE);//oring constructer
		//SET METHOD PART FOR VO need loop
		//SET METHOD PART FOR VO need loop
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			if(columnItem.getIsPk()) 
				identitySb.append(getColumnVoSetPartStr(settingFromOds , columnItem));
		}
		identitySb.append(NEWLINE);
		identitySb.append(" }");//Identity end
		
		
		resultMap.put(settingFromOds.getPackageToVo().replace(".", "/") +"/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_VO) , voSb.toString());
		resultMap.put(settingFromOds.getPackageToVo().replace(".", "/") +"/identity/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_IDENTITY_VO) , identitySb.toString());
			
			
		return resultMap;
	}
	

	private Map<String,String> genJavaEntityStrCodeFromTableOnj(SettingFromOds settingFromOds, TableDetail tableDetail) {
		
		//init
		Map<String,String> resultMap = Maps.newHashMap();
		
		StringBuffer entitySb = new StringBuffer();
		StringBuffer identitySb = new StringBuffer();
		String importPartStr = "";
		String classInformationPartStr = "";
		String annotationPartStr = "";
		String annotationPartForIdentityStr = "";
		String embeddedIdPartStr = "";
		String className = getJavaNameFromTableName2(tableDetail.getTableName(),IS_ENTITY);
		String identityOverridePartStr ="";
		String constructPartStr ="";
		String constructPartIdentityStr = "";
		String identityClassName = className + "Identity";
	
		
		//partString Logic
		annotationPartStr = getAnnotationPartStr(tableDetail,className,true);
		annotationPartForIdentityStr = getAnnotationPartStr(tableDetail,className,false);
		importPartStr = getImportPartStr(settingFromOds,tableDetail,className);
		classInformationPartStr = getClassInformationPartStr(tableDetail);
		identityOverridePartStr = getIdentityOverridePartStr(tableDetail,identityClassName);
		embeddedIdPartStr = getEmbeddedIdPartStr(tableDetail,identityClassName);
		constructPartStr = getconstructPartStr(tableDetail, className, identityClassName);
		constructPartIdentityStr = getConstructPartIdentityStr(settingFromOds , tableDetail, className, identityClassName);
		
		
		//Apend str Logic entity part
		//package
		entitySb.append("package ").append(settingFromOds.getPackageToEntity()).append(";").append(NEWLINE)
		.append(NEWLINE)
		.append(importPartStr)//import
		.append(NEWLINE)
		.append(annotationPartStr)
		.append(classInformationPartStr);//class Information Part
		entitySb.append("@ApiModel(value = \"").append(className).append("\", description = \"").append(tableDetail.getTableLocalName()).append("\" )").append(NEWLINE);
		entitySb.append("public class ").append(className).append(" {").append(NEWLINE)
		.append(NEWLINE)
		.append(constructPartStr)
		.append(embeddedIdPartStr)
		.append(NEWLINE);
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			entitySb.append(getColumnPartStr(settingFromOds , columnItem));
		}
		
		entitySb.append(" }");//entity part End
		
		
		//Apend str Logic Identity part
		//package
		
		identitySb.append("package ").append(settingFromOds.getPackageToEntity()).append(".").append("identity;").append(NEWLINE)
		.append(NEWLINE)
		.append(importPartStr)//import
		.append(NEWLINE)
		.append(annotationPartForIdentityStr)
		.append("@Embeddable").append(NEWLINE)
		.append("public class ").append(identityClassName).append(" implements Serializable {").append(NEWLINE);
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			identitySb.append(getIdentityPartStr(settingFromOds , columnItem));
		}		
		identitySb.append(NEWLINE);
		identitySb.append(TAB).append("public ").append(identityClassName).append("(){}").append(NEWLINE);//oring constructer
		identitySb.append(identityOverridePartStr);
		identitySb.append(" }");//Identity end
		
		
		resultMap.put(settingFromOds.getPackageToEntity().replace(".", "/") +"/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_ENTITY) , entitySb.toString());
		resultMap.put(settingFromOds.getPackageToEntity().replace(".", "/") +"/identity/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_IDENTITY) , identitySb.toString());


		return resultMap;
	}

	private String getConstructPartIdentityStr(SettingFromOds settingFromOds, TableDetail tableDetail, String className, String identityClassName) {
		//init
		StringBuffer resultSb = new StringBuffer();
		String identityClassNameLowerFirstCharStr = identityClassName.substring(0, 1).toLowerCase() + identityClassName.substring(1);
		String pointStr = "";
		
		resultSb.append(TAB).append("public").append(" ").append(identityClassName).append("(");
		for(ColumnDetail columnItem : tableDetail.getColumnDetails()) {
			if(columnItem.getIsPk()) {
				String classType = getColumnClassType(settingFromOds, columnItem);
				String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
				resultSb.append(classType).append(" ").append(javaColumnName).append(pointStr).append(" ");
				pointStr = ",";
			}
		}	
		resultSb.append(") {").append(NEWLINE);
		for(ColumnDetail columnItem : tableDetail.getColumnDetails()) {
			if(columnItem.getIsPk()) {
				String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
				resultSb.append(TAB).append(TAB).append("this.").append(javaColumnName).append(" = ").append(javaColumnName).append(";").append(NEWLINE);
			}
		}
		resultSb.append(TAB).append("}").append(NEWLINE).append(NEWLINE);

		
		return resultSb.toString();
	}
	

	private String getconstructPartStr(TableDetail tableDetail, String className, String identityClassName) {
		//init
		StringBuffer resultSb = new StringBuffer();
		String identityClassNameLowerFirstCharStr = identityClassName.substring(0, 1).toLowerCase() + identityClassName.substring(1);
		
		resultSb.append(TAB).append("public").append(" ").append(className).append("(").append(identityClassName).append(" ").append(identityClassNameLowerFirstCharStr).append(") {").append(NEWLINE)
		.append(TAB).append(TAB).append("this.").append(identityClassNameLowerFirstCharStr).append(" = ").append(identityClassNameLowerFirstCharStr).append(";").append(NEWLINE)
		.append(TAB).append("}").append(NEWLINE).append(NEWLINE);
		resultSb.append(TAB).append("public").append(" ").append(className).append("() {};").append(NEWLINE).append(NEWLINE);
		
		return resultSb.toString();
	}

	private String getEmbeddedIdPartStr(TableDetail tableDetail, String identityClassName) {
		//init
		String identityClassNameLowerFirstCharStr = identityClassName.substring(0, 1).toLowerCase() + identityClassName.substring(1);
		
		StringBuffer resultSb = new StringBuffer();
		resultSb.append(TAB).append("@EmbeddedId").append(NEWLINE);
		resultSb.append(TAB).append("private ").append(identityClassName).append(" ").append(identityClassNameLowerFirstCharStr).append(";").append(NEWLINE);
		
		return resultSb.toString();
	}

	private String getEmbeddedIdPartStrForVo(TableDetail tableDetail, String identityClassName) {
		//init
		String identityClassNameLowerFirstCharStr = identityClassName.substring(0, 1).toLowerCase() + identityClassName.substring(1);
		
		StringBuffer resultSb = new StringBuffer();
		resultSb.append(TAB).append("private ").append(identityClassName).append(" ").append(identityClassNameLowerFirstCharStr).append(";").append(NEWLINE);
		
		return resultSb.toString();
	}

	private String getColumnPartStr(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		Integer columnLength = StringUtils.isEmpty(columnItem.getLenghth())?getColumnLength(columnItem.getDataType()):new Integer(columnItem.getLenghth());
		String classType = getColumnClassType(settingFromOds, columnItem);
		String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
		String columnDesc = getColumnDescStr(columnItem);
		boolean isJpa = StringUtils.isEmpty(settingFromOds.getActionType()) || "jpa".equals(settingFromOds.getActionType());
		
		if(!columnItem.getIsPk()) {
			
			resultSb.append(TAB).append("/**").append(NEWLINE);
			resultSb.append(columnDesc);
			resultSb.append(TAB).append("*/").append(NEWLINE);
			if(isJpa) {
				if(columnItem.getNotNull().contains("V")) {
					resultSb.append(TAB).append("@NotNull").append(NEWLINE);
					if(columnLength != null) {
						resultSb.append(TAB).append("@Size(min =1, max = ").append(columnLength).append(")").append(NEWLINE);
					}
					
				}

				resultSb.append(TAB).append("@Column(name=\"").append(columnItem.getColumnName())
				.append("\" ");
				if(columnLength != null) {
					resultSb.append(", length=").append(columnLength);
				}
				resultSb.append(", columnDefinition=\"").append(columnItem.getDataType()).append("\"")
				.append(")").append(NEWLINE);
			}
			resultSb.append(TAB).append("private ").append(classType).append(" ").append(javaColumnName).append(";").append(NEWLINE).append(NEWLINE);
			
			
		}
		return resultSb.toString();
	}

	private String getColumnPartStrForVo(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		Integer columnLength = getColumnLength(columnItem.getDataType());
		String classType = getColumnClassType(settingFromOds, columnItem);
		String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
		String columnDesc = getColumnDescStr(columnItem);
		
		if(!columnItem.getIsPk()) {
			
			resultSb.append(TAB).append("/**").append(NEWLINE);
			resultSb.append(columnDesc);
			resultSb.append(TAB).append("*/").append(NEWLINE);
			if(columnItem.getNotNull().contains("V")) {
				resultSb.append(TAB).append("@NotNull").append(NEWLINE);
				if(columnLength != null) {
					resultSb.append(TAB).append("@Size(min =1, max = ").append(columnLength).append(")").append(NEWLINE);
				}
				
			}
			resultSb.append(TAB).append("private ").append(classType).append(" ").append(javaColumnName).append(";").append(NEWLINE).append(NEWLINE);
			
			
		}
		return resultSb.toString();
		
	}
	
	private String getColumnVoSetPartStr(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		//init
		StringBuffer resultSb = new StringBuffer();
		Integer columnLength = getColumnLength(columnItem.getDataType());
		String classType = getColumnClassType(settingFromOds, columnItem);
		String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
		String columnDesc = getColumnDescStr(columnItem);
		String methodName = "set"+ javaColumnName.substring(0, 1).toUpperCase() +javaColumnName.substring(1);
		
			
		resultSb.append(TAB).append("/**").append(NEWLINE);
		resultSb.append(columnDesc);
		if(columnItem.getNotNull().contains("V")) {
			resultSb.append(TAB).append("@NotNull").append(NEWLINE);
			if(columnLength != null) {
				resultSb.append(TAB).append("@Size(min =1, max = ").append(columnLength).append(")").append(NEWLINE);
			}
		}
		resultSb.append(TAB).append("*/").append(NEWLINE);
		resultSb.append(TAB).append("public void ").append(methodName).append("( ")
		.append(classType).append(" ").append(javaColumnName).append(" ) {").append(NEWLINE)
		.append(TAB).append(TAB).append("this.").append(javaColumnName).append(" = ").append(javaColumnName).append(";").append(NEWLINE)
		.append(TAB).append("}").append(NEWLINE);;
				
		return resultSb.toString();
	}
	

	private String getIdentityOverridePartStr(TableDetail tableDetail, String identityClassName) {
		//init
		StringBuffer resultSb = new StringBuffer();
		
		//equals rule
		resultSb.append(TAB).append("@Override");
		resultSb.append(TAB).append("public boolean equals(Object o) {").append(NEWLINE);
		resultSb.append(TAB).append(TAB).append("if (this == o) return true;").append(NEWLINE);
		resultSb.append(TAB).append(TAB).append("if (o == null || getClass() != o.getClass()) return false;").append(NEWLINE);
		resultSb.append(TAB).append(TAB).append(identityClassName).append(" that = (").append(identityClassName).append(") o;").append(NEWLINE)
		.append(NEWLINE);
		//loop for pk 
		List<ColumnDetail> pkcolumnDetails = tableDetail.getColumnDetails().stream().filter(c-> !StringUtils.isEmpty(c.getPk()) && c.getIsPk())
		.collect(Collectors.toList());
		for(int i = 0 ;  i < pkcolumnDetails.size() ; i++) {
			ColumnDetail pkColumn = pkcolumnDetails.get(i);
			String javaColumnName = BeanHump.underlineToCamel2(pkColumn.getColumnName().toLowerCase());
			if(i<pkcolumnDetails.size()-1) {
				resultSb.append(TAB).append(TAB).append("if (").append(javaColumnName).append(" != null ? !").append(javaColumnName)
				.append(".equals(that.").append(javaColumnName).append(") : that.").append(javaColumnName).append(" != null) return false;").append(NEWLINE);;				
			}else {
				resultSb.append(TAB).append(TAB).append("return (").append(javaColumnName).append(" != null ? !").append(javaColumnName)
				.append(".equals(that.").append(javaColumnName).append(") : that.").append(javaColumnName).append(" != null);").append(NEWLINE);
			}
		}
		resultSb.append(TAB).append("}"); //equals end
		
		//hashCode rule
		resultSb.append(NEWLINE);
		resultSb.append(TAB).append("public int hashCode() {").append(NEWLINE);
		
		for(int i = 0 ;  i < pkcolumnDetails.size() ; i++) {
			ColumnDetail pkColumn = pkcolumnDetails.get(i);
			String javaColumnName = BeanHump.underlineToCamel2(pkColumn.getColumnName().toLowerCase());
			if(i==0) {
				resultSb.append(TAB).append(TAB).append("int result = ").append(javaColumnName).append(" != null ? ").append(javaColumnName).append(".hashCode() : 0;").append(NEWLINE);;
			}else {
				resultSb.append(TAB).append(TAB).append("result = 31 * result + (").append(javaColumnName).append(" != null ? ").append(javaColumnName).append(".hashCode() : 0);").append(NEWLINE);;
			}		
		}
		resultSb.append(TAB).append(TAB).append("return result;").append(NEWLINE);;
		resultSb.append(TAB).append("}").append(NEWLINE);;//hashCode end
		
		return resultSb.toString();
	}
	
	
	
	
	private String getIdentityPartStr(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		//init
		StringBuffer resultSb = new StringBuffer();
		Integer columnLength = getColumnLength(columnItem.getDataType());
		String classType = getColumnClassType(settingFromOds, columnItem);
		String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
		String columnDesc = getColumnDescStr(columnItem);
		
		if(columnItem.getIsPk()) {
			
			resultSb.append(TAB).append("/**").append(NEWLINE);
			resultSb.append(columnDesc);
			resultSb.append(TAB).append("*/").append(NEWLINE);
			resultSb.append(TAB).append("@NotNull").append(NEWLINE);
			resultSb.append(TAB).append("@Size(min =1, max = ").append(columnLength).append(")").append(NEWLINE);
			resultSb.append(TAB).append("@Column(name=\"").append(columnItem.getColumnName()).append("\" ");
			resultSb.append(", length=").append(columnLength);
			resultSb.append(", columnDefinition=\"").append(columnItem.getDataType()).append("\"")
			.append(")").append(NEWLINE);
			resultSb.append(TAB).append("private ").append(classType).append(" ").append(javaColumnName).append(";").append(NEWLINE).append(NEWLINE);
			
		}
		
		
		return resultSb.toString();
	}

	private String getIdentityPartStrForVo(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		//init
		StringBuffer resultSb = new StringBuffer();
		Integer columnLength = getColumnLength(columnItem.getDataType());
		String classType = getColumnClassType(settingFromOds, columnItem);
		String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
		String columnDesc = getColumnDescStr(columnItem);
		
		if(columnItem.getIsPk()) {
			
			resultSb.append(TAB).append("/**").append(NEWLINE);
			resultSb.append(columnDesc);
			resultSb.append(TAB).append("*/").append(NEWLINE);
			resultSb.append(TAB).append("@NotNull").append(NEWLINE);
			resultSb.append(TAB).append("@Size(min =1, max = ").append(columnLength).append(")").append(NEWLINE);
			resultSb.append(TAB).append("private ").append(classType).append(" ").append(javaColumnName).append(";").append(NEWLINE).append(NEWLINE);
			
		}
		
		
		return resultSb.toString();
	}
	
	private String getWebControllerUrlPartStr(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		resultSb.append(TAB).append("//URL ").append(NEWLINE);
		
		//loop
		tableDetailObjList.forEach(t->{
			//public static final String CRUD_AO_PLYEDR_PREM = "/ao-plyedr-prem/crud";
			String tableCrudUrlName = getTableCrudUrlName(t.getTableName());
			resultSb.append(TAB).append("public static final String  CRUD_").append(t.getTableName().toUpperCase())
			.append(" = \"/").append(tableCrudUrlName).append("/\";").append(NEWLINE);
		});
		
		return resultSb.toString();
		
	}
	
	private String getStubUrlPartStr(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		resultSb.append(TAB).append("//URL ").append(NEWLINE);
		
		//loop TODO URL DYMNDIC
		tableDetailObjList.forEach(t->{
			//public static final String CRUD_AO_PLYEDR_PREM = "/ao-plyedr-prem/crud";
			String tableCrudUrlName = getTableCrudUrlName(t.getTableName());
			resultSb.append(TAB).append("public static final String  CRUD_").append(t.getTableName().toUpperCase())
			.append(" = \"/integration/finance/crud/").append(tableCrudUrlName).append("/\";").append(NEWLINE);
		});
		
		return resultSb.toString();
		
	}
	
	private String getWebControllerAutowiredPartStr(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) {
		
		// init
		StringBuffer resultSb = new StringBuffer();
		resultSb.append(TAB).append("//Autowired Service").append(NEWLINE);

		// loop
		tableDetailObjList.forEach(t -> {
			String[] tableServiceName = getTableServiceNameArray(t.getTableName());
			resultSb.append(TAB).append("@Autowired ").append(tableServiceName[0]).append(" ").append(tableServiceName[1]).append(";").append(NEWLINE);
		});

		return resultSb.toString();
	}
	

	// [0] is first word upcase , [1] is normal Name;
	private String[] getTableServiceNameArray(String tableName) {
		String[] resultStrArray = new String[2];
		String serviceClassName = BeanHump.underlineToCamel2(tableName.toLowerCase()) + "Service";
		resultStrArray[0] = serviceClassName.substring(0, 1).toUpperCase() + serviceClassName.substring(1);
		resultStrArray[1] = serviceClassName;
		return resultStrArray;
	}

	private String getTableCrudUrlName(String tableName) {
		String resultStr = tableName.replace("_", "-").toLowerCase();
		return resultStr;	
	}

	private String getColumnDescStr(ColumnDetail columnItem) {
		StringBuffer resultSb = new StringBuffer();
		
		if(!StringUtils.isEmpty(columnItem.getColumnName()))
			resultSb.append(TAB).append("*").append("columnName:").append(columnItem.getColumnName().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getColumnLocalName()))
			resultSb.append(TAB).append("*").append("localName:").append(columnItem.getColumnLocalName().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getNotNull()))
			resultSb.append(TAB).append("*").append("notNull:").append(columnItem.getNotNull().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getDesc1()))
			resultSb.append(TAB).append("*").append("desc1:").append(columnItem.getDesc1().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getDesc2()))
			resultSb.append(TAB).append("*").append("desc2:").append(columnItem.getDesc2().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getDesc3()))
			resultSb.append(TAB).append("*").append("desc3:").append(columnItem.getDesc3().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getDesc4()))
			resultSb.append(TAB).append("*").append("desc4:").append(columnItem.getDesc4().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getDefine()))
			resultSb.append(TAB).append("*").append("define:").append(columnItem.getDefine().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getSample()))
			resultSb.append(TAB).append("*").append("sample:").append(columnItem.getSample().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getDefaultValue()))
			resultSb.append(TAB).append("*").append("defaultValue:").append(columnItem.getDefaultValue().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getLenghth())) {
			resultSb.append(TAB).append("*").append("length:").append(columnItem.getLenghth().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		}
		
		return resultSb.toString();
	}



	private String getColumnClassType(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		StringBuffer resultSb = new StringBuffer();
		String type = columnItem.getDataType();

		int index1 = type.indexOf("(");
		if(index1 > 0) {
			type = type.substring(0,index1);
		}

		if(settingFromOds.getDbTypeClassMapping().get(type) !=null ) {
			resultSb.append(settingFromOds.getDbTypeClassMapping().get(type));
		}else if(columnTypeclassMappingMap.get(type) !=null ) {
			resultSb.append(columnTypeclassMappingMap.get(type));
		}else {
			resultSb.append("String");
		}
		
		return resultSb.toString();
	}



	private String getJavaNameFromTableName2(String tableName, String isWhat) {
		StringBuffer resultSb = new StringBuffer();
		String javaName = BeanHump.underlineToCamel3(tableName);
		String appendName = "";
		
		switch (isWhat) {
		case IS_ENTITY:
			appendName = "";
			break;
		case IS_DAO:
			appendName = "Dao";
			break;
		case IS_SERVICE:
			appendName = "Service";
			break;
		case IS_VO:
			appendName = "Vo";
			break;
		case IS_IDENTITY_VO:
			appendName = "IdentityVo";
		default:
			break;
		}
		
		resultSb.append(javaName).append(appendName);
		return resultSb.toString();
	}



	private String getJavaNameFromTableName(String tableName, String isWhat) {
		
		StringBuffer resultSb = new StringBuffer();
		String javaName = BeanHump.underlineToCamel3(tableName);
		String appendName = "";
		
		switch (isWhat) {
		case IS_ENTITY:
			appendName = ".java";
			break;
		case IS_DAO:
			appendName = "Dao.java";
			break;
		case IS_SERVICE:
			appendName = "Service.java";
			break;
		case IS_IDENTITY:
			appendName = "Identity.java";
			break;
		case IS_MODEL:
			appendName = "Model.java";
			break;
		case IS_MODEL_IDENTITY:
			appendName = "ModelIdentity.java";
			break;
		case IS_VO:
			appendName = "Vo.java";
			break;
		case IS_VO_SERVICE:
			appendName = "VoService.java";
			break;
		case IS_IDENTITY_VO:
			appendName = "IdentityVo.java";
			break;			
		default:
			break;
		}
		
		resultSb.append(javaName).append(appendName);
		return resultSb.toString();
		
	}
	
	private Integer getColumnLength(String dataType) {
		Integer result = null;
		int i1 = dataType.indexOf("(");
		int i2 = dataType.indexOf(")");			
		if(i1 > -1 && i2 > i1) {
			String number = dataType.substring(i1+1, i2);
			 try {
		            result = Integer.parseInt(number);
		        } catch (NumberFormatException e){
		          
		        }
		}		
		return result;
	}
	
	

	private String getAnnotationPartStr(TableDetail tableDetail, String className , Boolean isEntity) {
		StringBuffer resultSb = new StringBuffer();
		
		resultSb.append("@SuppressWarnings(\"serial\")").append(NEWLINE);
		resultSb.append("@Getter").append(NEWLINE);
		resultSb.append("@Setter").append(NEWLINE);
		
		resultSb.append("@ToString").append(NEWLINE);
		if(isEntity) {
			resultSb.append("@Table(name = \"").append(tableDetail.getTableName()).append("\")").append(NEWLINE);		
			resultSb.append("@Entity").append(NEWLINE);
		}
		return resultSb.toString();
	}
	
	private String getAnnotationPartStrForVo(TableDetail tableDetail, String className) {
		StringBuffer resultSb = new StringBuffer();
		
		resultSb.append("@SuppressWarnings(\"serial\")").append(NEWLINE);
		resultSb.append("@Getter").append(NEWLINE);
		resultSb.append("@Setter").append(NEWLINE);
		resultSb.append("@ToString").append(NEWLINE);
		
		
		return resultSb.toString();
	}
	
	

	private String getImportPartStr(SettingFromOds settingFromOds, TableDetail tableDetail, String className) {
		
		StringBuffer resultSb = new StringBuffer();
		
		//defaultPart
		resultSb.append("import java.io.Serializable;").append(NEWLINE);
		resultSb.append("import javax.persistence.Embeddable;").append(NEWLINE);
		resultSb.append("import javax.persistence.Column;").append(NEWLINE);
		resultSb.append("import javax.persistence.Id;").append(NEWLINE);
		resultSb.append("import javax.persistence.EmbeddedId;").append(NEWLINE);
		resultSb.append("import javax.persistence.Entity;").append(NEWLINE);
		resultSb.append("import javax.persistence.Table;").append(NEWLINE);
		resultSb.append("import javax.validation.constraints.NotNull;").append(NEWLINE);
		resultSb.append("import javax.validation.constraints.Size;").append(NEWLINE);
		resultSb.append("import io.swagger.annotations.ApiModel;").append(NEWLINE);
		resultSb.append("import lombok.Getter;").append(NEWLINE);
		resultSb.append("import lombok.Setter;").append(NEWLINE);
		resultSb.append("import lombok.ToString;").append(NEWLINE);
		resultSb.append("import java.math.BigDecimal;").append(NEWLINE);
		resultSb.append("import java.time.LocalDate;").append(NEWLINE);
		resultSb.append("import java.time.LocalDateTime;").append(NEWLINE);
		resultSb.append("import java.util.List;").append(NEWLINE);
		resultSb.append("import java.util.Map;").append(NEWLINE);
		
		resultSb.append("import ").append(settingFromOds.getPackageToEntity()).append(".identity.").append(className).append("Identity;").append(NEWLINE);	
		
		return resultSb.toString();
	}
	
	private String getImportPartStrForVo(SettingFromOds settingFromOds, TableDetail tableDetail, String className) {
		
		StringBuffer resultSb = new StringBuffer();
		
		//defaultPart
		resultSb.append("import java.io.Serializable;").append(NEWLINE);
		resultSb.append("import javax.validation.constraints.NotNull;").append(NEWLINE);
		resultSb.append("import javax.validation.constraints.Size;").append(NEWLINE);
		resultSb.append("import lombok.Getter;").append(NEWLINE);
		resultSb.append("import lombok.Setter;").append(NEWLINE);
		resultSb.append("import lombok.ToString;").append(NEWLINE);
		resultSb.append("import java.math.BigDecimal;").append(NEWLINE);
		resultSb.append("import java.time.LocalDate;").append(NEWLINE);
		resultSb.append("import java.time.LocalDateTime;").append(NEWLINE);
		resultSb.append("import java.util.List;").append(NEWLINE);
		resultSb.append("import java.util.Map;").append(NEWLINE);
		
		resultSb.append("import ").append(settingFromOds.getPackageToVo()).append(".identity.").append(className).append("IdentityVo;").append(NEWLINE);	
		
		return resultSb.toString();
	}
	
	private String getImportPartStrForGateWayService(SettingFromOds settingFromOds, TableDetail tableDetail, String className) {
		
		StringBuffer resultSb = new StringBuffer();
		
		//defaultPart
		resultSb.append("import tw.com.softleader.jasmine.integration.bs.IntegrationMappingBs;").append(NEWLINE);
		resultSb.append("import tw.com.softleader.jasmine.integration.bs.IntegrationUtilBs;").append(NEWLINE);		
		resultSb.append("import tw.com.softleader.jasmine.integration.finance.universe.UniverseObj;").append(NEWLINE);		
		resultSb.append("import lombok.extern.slf4j.Slf4j;");
		resultSb.append("import java.io.Serializable;").append(NEWLINE);
		resultSb.append("import org.springframework.beans.factory.annotation.Autowired;").append(NEWLINE);
		resultSb.append("import org.springframework.stereotype.Service;").append(NEWLINE);
		resultSb.append("import ").append(settingFromOds.getPackageToVo()).append(".identity.").append(className).append("IdentityVo;").append(NEWLINE);	
		
		return resultSb.toString();
	}
	
	
	

	private String getClassInformationPartStr(TableDetail tableDetail) {
		
		String AUTHOR = "@author French.Fu";//TODO MOVE TO PROPERTY		
		
		StringBuffer resultSb = new StringBuffer();
		resultSb.append("/** ").append(NEWLINE);
		resultSb.append("*").append(AUTHOR).append("<br/>").append(NEWLINE);
		resultSb.append("* ").append(tableDetail.getTableLocalName()).append("<br/>").append(NEWLINE);
		resultSb.append("*/ ").append(NEWLINE);
		return resultSb.toString();
	}
	
	private String getWebControllerCommentPartStr(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) {
		
		String AUTHOR = "@author French.Fu";//TODO MOVE TO PROPERTY				
		StringBuffer resultSb = new StringBuffer();
		resultSb.append("/** ").append(NEWLINE);
		resultSb.append("*").append(AUTHOR).append("<br/>").append(NEWLINE);
		resultSb.append("*").append("crud controller for the following entity ").append("<br/>").append(NEWLINE);
		tableDetailObjList.forEach(t->{
			String javaEntityName = BeanHump.underlineToCamel2(t.getTableName().toLowerCase());
			resultSb.append("*").append(javaEntityName).append("<br/>").append(NEWLINE);
		});
		resultSb.append("*/ ").append(NEWLINE);
		return resultSb.toString();
		
	}
	
	private String getStubCommentPartStr(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) {
		
		String AUTHOR = "@author French.Fu";//TODO MOVE TO PROPERTY				
		StringBuffer resultSb = new StringBuffer();
		resultSb.append("/** ").append(NEWLINE);
		resultSb.append("*").append(AUTHOR).append("<br/>").append(NEWLINE);
		resultSb.append("*").append("crud stub for the following entity ").append("<br/>").append(NEWLINE);
		tableDetailObjList.forEach(t->{
			String javaEntityName = BeanHump.underlineToCamel2(t.getTableName().toLowerCase());
			resultSb.append("*").append(javaEntityName).append("<br/>").append(NEWLINE);
		});
		resultSb.append("*/ ").append(NEWLINE);
		return resultSb.toString();
		
	}

	public Map<String, String> scanObjListToJavaCodeForGateWay(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) throws IOException {
		//init
		Map<String, String> resultMap = Maps.newHashMap();
		
		//loop For VO
		for(TableDetail  tableDetail : tableDetailObjList) {
			Map<String, String> loopUnitVoMap = this.genObjToVoJavaCodeMap(settingFromOds,tableDetail);
			Map<String, String> loopUnitServiceMap = this.genObjToGateWayServiceJavaCodeMap(settingFromOds,tableDetail);
			resultMap.putAll(loopUnitVoMap);
			resultMap.putAll(loopUnitServiceMap);
		}
		
		resultMap.putAll(this.getStubJavaCodeMap(settingFromOds,tableDetailObjList));
		
		
		return resultMap;
	}

	private Map<String, String> getStubJavaCodeMap(SettingFromOds settingFromOds,List<TableDetail> tableDetailObjList) {
		
		//init
		Map<String , String > resultMap = Maps.newHashMap();
		StringBuffer codeSb = new StringBuffer();
		String packageStr = "";
		String importStr = "";
		String commentStr ="";
		String annotationStr ="";
		String publicClassPartStr="";
		String urlPartStr="";
		String methodPartStr= "" ;
		String endPartSrt = "";
		
		//package
		packageStr = "package " + settingFromOds.getPackageToStub() + ";" + NEWLINE;
		
		//import
		importStr = this.getStubImportStr(settingFromOds, tableDetailObjList);
		
		//comment
		commentStr = this.getStubCommentPartStr(settingFromOds, tableDetailObjList);
		
		//annotationStr
		annotationStr = "@FeignClient(\""+settingFromOds.getRpcName()+"\")";
		
		//publicClassPart
		publicClassPartStr = new StringBuffer().append("public interface ").append(settingFromOds.getStubName()).append(" {").append(NEWLINE).toString();
		
		//url
		urlPartStr = getStubUrlPartStr(settingFromOds, tableDetailObjList);
		
		//methodPartStr part
		methodPartStr = getStubMethodPartStr(settingFromOds, tableDetailObjList);
		
		//end
		endPartSrt = "}";
		
		//combine
		codeSb.append(packageStr).append(NEWLINE);
		codeSb.append(importStr).append(NEWLINE);
		codeSb.append(commentStr).append(NEWLINE);
		codeSb.append(annotationStr).append(NEWLINE);
		codeSb.append(publicClassPartStr).append(NEWLINE);
		codeSb.append(urlPartStr).append(NEWLINE);
		codeSb.append(methodPartStr).append(NEWLINE);
		codeSb.append(endPartSrt).append(NEWLINE);//combine end
		
		//put key & Code Stirng
		resultMap.put(settingFromOds.getPackageToStub().replace(".", "/") +"/" + settingFromOds.getStubName() +".java" , codeSb.toString());
		
		return resultMap;
	}

	private String getStubImportStr(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) {
		//init
		StringBuffer resultSb = new StringBuffer();
		
		//vo
		resultSb.append("import ").append(settingFromOds.getPackageToVo()).append(".*;").append(NEWLINE);
	
		//common part
		resultSb.append("import org.springframework.cloud.netflix.feign.FeignClient;").append(NEWLINE);
		resultSb.append("import org.springframework.beans.factory.annotation.Autowired;").append(NEWLINE);
		resultSb.append("import org.springframework.http.ResponseEntity;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.PostMapping;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.GetMapping;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.DeleteMapping;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.PutMapping;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.RequestBody;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.RequestMapping;").append(NEWLINE);
		resultSb.append("import org.springframework.web.bind.annotation.RestController;").append(NEWLINE);
		
		//soft leader Part
		resultSb.append("import lombok.extern.slf4j.Slf4j;").append(NEWLINE);
		resultSb.append("import tw.com.softleader.jasmine.commons.http.JasmineResponseStatus;").append(NEWLINE);
		resultSb.append("import tw.com.softleader.web.http.ResponseDetails;").append(NEWLINE);
		resultSb.append("import tw.com.softleader.web.http.Responses;").append(NEWLINE);
		
		return resultSb.toString();
	}

}
