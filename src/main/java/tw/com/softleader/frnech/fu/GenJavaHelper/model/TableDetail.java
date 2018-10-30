package tw.com.softleader.frnech.fu.GenJavaHelper.model;

import java.util.List;

import com.google.common.collect.Lists;

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
 * TABLE DETAIL
 */
public class TableDetail {

	private String tableName;
	private String tableLocalName;
	private List<ColumnDetail> columnDetails = Lists.newArrayList();
	
}
