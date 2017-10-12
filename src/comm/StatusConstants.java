package comm;

import org.apache.poi.hssf.util.HSSFColor;

public enum StatusConstants{
	
	STATUS_FINISH("完成",HSSFColor.SEA_GREEN.index),				
	STATUS_UNSTART("尚未開始",HSSFColor.RED.index),			
	STATUS_THISWEEKTASK("本週計畫",HSSFColor.GOLD.index),	
	STATUS_NEXTWEEKTASK("下週計畫",HSSFColor.SKY_BLUE.index),		
	STATUS_FUTURETASK("未來計畫",HSSFColor.WHITE.index),		
	STATUS_ONTARGET("按預定時程",HSSFColor.GOLD.index),		
	STATUS_BEHIND("落後",HSSFColor.RED.index);				
	
	private String statusName;
	private short color;
	
	private StatusConstants(String statusName, short color){
		this.statusName = statusName;
		this.color = color;
	}
	
	
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public short getColor() {
		return color;
	}
	public void setColor(short color) {
		this.color = color;
	}
}
