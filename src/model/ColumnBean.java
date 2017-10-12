package model;

public class ColumnBean {
	private String taskNameCol;
	private String startTimeCol;
	private String finishTimeCol;
	private String surplusWorkTimeCol;
	private String finishPercentageCol;
	
	public ColumnBean(){}
	
	public String getTaskNameCol() {
		return taskNameCol;
	}
	public void setTaskNameCol(String taskNameCol) {
		this.taskNameCol = taskNameCol;
	}
	public String getStartTimeCol() {
		return startTimeCol;
	}
	public void setStartTimeCol(String startTimeCol) {
		this.startTimeCol = startTimeCol;
	}
	public String getFinishTimeCol() {
		return finishTimeCol;
	}
	public void setFinishTimeCol(String finishTimeCol) {
		this.finishTimeCol = finishTimeCol;
	}
	public String getSurplusWorkTimeCol() {
		return surplusWorkTimeCol;
	}
	public void setSurplusWorkTimeCol(String surplusWorkTimeCol) {
		this.surplusWorkTimeCol = surplusWorkTimeCol;
	}
	public String getFinishPercentageCol() {
		return finishPercentageCol;
	}
	public void setFinishPercentageCol(String finishPercentageCol) {
		this.finishPercentageCol = finishPercentageCol;
	}
	
	
}
