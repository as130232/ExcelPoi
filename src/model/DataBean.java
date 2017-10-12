package model;

import annotations.ColumnName;
import comm.ColumnNameConstants;
import comm.StatusConstants;


public class DataBean {
	
	@ColumnName(value = ColumnNameConstants.TASKNAME)
	private String taskName;
	
	@ColumnName(value = ColumnNameConstants.STARTTIME)
	private String startTime;
	
	@ColumnName(value = ColumnNameConstants.FINISHTIME)
	private String finishTime;
	
//	@ColumnName(value = ColumnNameConstants.SURPLUSWORKTIME)
//	private String surplusWorkTime;
	
	@ColumnName(value = ColumnNameConstants.FINISHPERCENTAGE)
	private String finishPercentage;
	
	@ColumnName(value = ColumnNameConstants.STATUS)
	private StatusConstants status;
	
//	@ColumnName(value = ColumnNameConstants.FRONTTASK)
//	private String frontTask;
	
	@ColumnName(value = ColumnNameConstants.RESOURCESNAME)
	private String resourcesName;
	
	@ColumnName(value = ColumnNameConstants.REASONBEHIND)
	private String reasonBehind;
	
	@ColumnName(value = ColumnNameConstants.REMARK)
	private String remark;
	
	
	public DataBean(){}
	
	
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

//	public String getSurplusWorkTime() {
//		return surplusWorkTime;
//	}
//
//	public void setSurplusWorkTime(String surplusWorkTime) {
//		this.surplusWorkTime = surplusWorkTime;
//	}

	public String getFinishPercentage() {
		return finishPercentage;
	}

	public void setFinishPercentage(String finishPercentage) {
		this.finishPercentage = finishPercentage;
	}

	public StatusConstants getStatus() {
		return status;
	}

	public void setStatus(StatusConstants status) {
		this.status = status;
	}

//	public String getFrontTask() {
//		return frontTask;
//	}
//
//	public void setFrontTask(String frontTask) {
//		this.frontTask = frontTask;
//	}

	public String getResourcesName() {
		return resourcesName;
	}

	public void setResourcesName(String resourcesName) {
		this.resourcesName = resourcesName;
	}

	public String getReasonBehind() {
		return reasonBehind;
	}

	public void setReasonBehind(String reasonBehind) {
		this.reasonBehind = reasonBehind;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
