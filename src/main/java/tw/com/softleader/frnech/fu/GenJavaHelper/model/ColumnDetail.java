package tw.com.softleader.frnech.fu.GenJavaHelper.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Getter
@Setter
@ToString
@Slf4j
/**
 * @author French.Fu
 * COLUMN DETAIL
 */
public class ColumnDetail {

	private String tableName;
	private String no;
	private String columnName;
	private String columnLocalName;
	private String dataType;
	private String pk;
	private String defaultValue;
	private String notNull;
	private String sample;
	private String define;
	private String desc1;
	private String desc2;
	private String desc3;
	private String desc4;
	private String setValueLogic;
	private String lenghth;
	
	public boolean getIsPk() {
		String pkVar = pk;
		if(pkVar == null)pkVar ="";
		return pkVar.contains("PK") || pkVar.contains("UK");
	}

}
