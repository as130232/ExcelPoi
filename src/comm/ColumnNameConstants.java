package comm;

import org.apache.poi.hssf.util.HSSFColor;

public enum ColumnNameConstants{
	
	TASKNAME("任務名稱"),				
	STARTTIME("開始時間"),			
	FINISHTIME("完成時間"),	
	//SURPLUSWORKTIME("剩餘工時"),		
	FINISHPERCENTAGE("工時完成百分比"),		
	STATUS("狀態"),		
	//FRONTTASK("前置任務"),				
	RESOURCESNAME("多個資源的名稱"),				
	REASONBEHIND("落後原因"),			
	REMARK("備註");				
	
	private String columnName;
	private ColumnNameConstants(String columnName){
		this.columnName = columnName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
}
