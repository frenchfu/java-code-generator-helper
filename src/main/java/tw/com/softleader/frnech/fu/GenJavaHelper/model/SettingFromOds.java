package tw.com.softleader.frnech.fu.GenJavaHelper.model;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tw.com.softleader.frnech.fu.GenJavaHelper.enums.YesNo;

/**
 * 
 * @author French.Fu
 * Some Setting Value Keeper
 *
 */
@Getter
@Setter
@ToString
@Slf4j
public class SettingFromOds {
	
	private String packageToDao;
	private String packageToService;
	private String packageToEntity;
	private String packageToVo;
	private String packageToRpc;
	private String packageToGateWayService;
	private String packageToStub;
	private String controllerRequestMapping;
	private String controllerName;
	private String rpcName;
	private String stubName;
	private YesNo makeInterFaceService;
	private String actionType;//myBatis / jpa
	private Map<String,String> dbTypeClassMapping = Maps.newHashMap();
	
	
}
