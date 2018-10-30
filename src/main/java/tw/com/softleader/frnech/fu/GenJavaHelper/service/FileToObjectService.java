package tw.com.softleader.frnech.fu.GenJavaHelper.service;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import tw.com.softleader.frnech.fu.GenJavaHelper.enums.YesNo;
import tw.com.softleader.frnech.fu.GenJavaHelper.model.ColumnDetail;
import tw.com.softleader.frnech.fu.GenJavaHelper.model.SettingFromOds;
import tw.com.softleader.frnech.fu.GenJavaHelper.model.TableDetail;

/**
 * @author French.Fu
 * target : to read file And make obj
 */
@Service
public class FileToObjectService {
	
	private final String ABOUT_SOME_SETTING = "ABOUT_SOME_SETTING";
	private final String ABOUT_DBTYPE_CLASS_MAPPING = "ABOUT_DBTYPE_CLASS_MAPPING";
	private final List<String> skipBookmark = Lists.newArrayList(ABOUT_SOME_SETTING,ABOUT_DBTYPE_CLASS_MAPPING);
	
	public SettingFromOds scanOdtToSettingObj(InputStream inputStrem) throws Exception {
		
		SettingFromOds resultObj  =  new SettingFromOds();
		boolean hasDefaultSetting = false;
		SpreadsheetDocument data = SpreadsheetDocument.loadDocument(inputStrem);
		
		List<Table> tableList = data.getTableList();
		for(Table odtTable : tableList)
			{
			
				int runCounter = odtTable.getRowCount();
				if (ABOUT_SOME_SETTING.equals(odtTable.getTableName())) {
					hasDefaultSetting = true;
					Map<Integer,String> columnsAttributesMap = makeColumnsAttributesMap(odtTable.getRowByIndex(0));
					for(int i = 1 ; i < runCounter ; i++   ) 
						{
						
							Row row = odtTable.getRowByIndex(i);
							int cellCounter = row.getCellCount();
							for(int k = 0 ; k < cellCounter ; k++   ) 
								{
								
									String beanName = columnsAttributesMap.get(k);
									Cell cell = row.getCellByIndex(k);
									String strValue = cell.getStringValue();
									if("makeInterFaceService".equals(beanName)) {
										if("Y".equalsIgnoreCase(strValue)) {
											resultObj.setMakeInterFaceService(YesNo.valueOf("Y"));
										}else {
											resultObj.setMakeInterFaceService(YesNo.valueOf("N"));
										}
									}else {
										BeanUtils.setProperty(resultObj, beanName, strValue);	
									}				
									
								}
								
						}

				} else if(ABOUT_DBTYPE_CLASS_MAPPING.equals(odtTable.getTableName())){

					
					for( int i = 1 ; i < runCounter ; i++   ) 
						{
							
							Row row = odtTable.getRowByIndex(i);
							Cell cellDbtype= row.getCellByIndex(0);
							Cell cellClass= row.getCellByIndex(1);
							String dbtype = cellDbtype.getStringValue();
							String classPath = cellClass.getStringValue();
							resultObj.getDbTypeClassMapping().put(dbtype, classPath);
							
						}
				}
				
			}
		
		System.out.println(resultObj.getDbTypeClassMapping());
		System.out.println(resultObj.getDbTypeClassMapping());
	
		if(hasDefaultSetting) {
			return resultObj;
		}else {
			throw new Exception(" ODS FILE NOT HAVE BOOK MARK ABOUT SETTING DETAIL  ");
		}
		
	}
	
	
	public List<TableDetail> scanOdtToTableDetails (InputStream inputStream) throws Exception {
		
		List<TableDetail> resultModelTableDetails;
		resultModelTableDetails = Lists.newArrayList();
		
		SpreadsheetDocument data = SpreadsheetDocument.loadDocument(inputStream);
		
		List<Table> tableList = data.getTableList();
		for(Table table : tableList) 
			{
				if(!skipBookmark.contains(table.getTableName())) {
					resultModelTableDetails.add(scanOdtTableToTableDetail(table));
				}
				
			}
		
		return resultModelTableDetails;
	}
	
	
	
	public TableDetail scanOdtTableToTableDetail(Table odtTable) throws IllegalAccessException, InvocationTargetException {
		
		
		TableDetail resultModelTableDetail;
		resultModelTableDetail = new TableDetail();
		
		String TABLE_NAME =  odtTable.getTableName();
		Map<Integer,String> columnsAttributesMap = makeColumnsAttributesMap(odtTable.getRowByIndex(0));
		String TABLE_LOCAL_NAME =  odtTable.getRowByIndex(1).getCellByIndex(0).getStringValue();
		
		int runCounter = odtTable.getRowCount();
		for(int i = 1 ;i < runCounter ; i++   ) 
			{
			
				ColumnDetail columnDetail = new ColumnDetail();
				Row row = odtTable.getRowByIndex(i);
				
				int cellCounter = row.getCellCount();
				for(int k = 1 ;k < cellCounter ; k++   ) 
					{
					
						String beanName = columnsAttributesMap.get(k);
						Cell cell = row.getCellByIndex(k);
						String strValue = cell.getStringValue();
						BeanUtils.setProperty(columnDetail, beanName, strValue);	
						
					}
				
				resultModelTableDetail.getColumnDetails().add(columnDetail);
				
			}
    	
    	resultModelTableDetail.setTableName(TABLE_NAME);
    	resultModelTableDetail.setTableLocalName(TABLE_LOCAL_NAME);

    	
    	
		return resultModelTableDetail;
	}
	
	
	
	private Map<Integer, String> makeColumnsAttributesMap(Row rowByIndex) {
		
		Map<Integer, String> resultMap;
		resultMap = Maps.newHashMap();
		
		int cellCounter = rowByIndex.getCellCount();		
		for(int i = 0 ;i < cellCounter ; i++   ) 
			{
				resultMap.put(i, rowByIndex.getCellByIndex(i).getStringValue());	
			}
		
		return resultMap;
	}

	
	
	
}
