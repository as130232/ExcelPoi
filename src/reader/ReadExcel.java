package reader;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import comm.ColumnNameConstants;
import comm.StatusConstants;
import model.DataBean;
import writer.WriteExcel;

public class ReadExcel {
	
	public static DataBean setStatusByTime(DataBean dataBean){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date todayDate = new Date();
		String today = sdf.format(todayDate);
		
		//設定狀態，取出完成百分比、開始時間、完成時間
		String finishPercentage = dataBean.getFinishPercentage();	
		String startTime = dataBean.getStartTime();
		String finishTime = dataBean.getFinishTime();
		Date startTimeDate = null;
		Date finishTimeDate = null;

		Calendar now = Calendar.getInstance(); 
		
		//當周禮拜六的日期
		Date dayOfSaturdayDate = null;
	    Date nextSaturdayDate = null;
	    Date towWeeksFromThisSaturdayDate = null;
	    
	    now.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
	    //當周禮拜五的日期
	    String dayOfSaturday = sdf.format(now.getTime());
	    //System.out.println("當周禮拜六:" + dayOfSaturday);
	    
		try {
			startTimeDate = sdf.parse(startTime);
			finishTimeDate = sdf.parse(finishTime);
			dayOfSaturdayDate = sdf.parse(dayOfSaturday);
			
			now.setTime(dayOfSaturdayDate);
			now.add(Calendar.DATE, 7);
			String nextSaturday = sdf.format(now.getTime());
			//System.out.println("nextFriday: " + nextSaturday);
			nextSaturdayDate = sdf.parse(nextSaturday);
			
			now.setTime(dayOfSaturdayDate);
			now.add(Calendar.DATE, 14);
			String towWeeksFromThisSaturday = sdf.format(now.getTime());
			//System.out.println("towWeeksFromThisFriday: " + towWeeksFromThisSaturday);
			towWeeksFromThisSaturdayDate = sdf.parse(towWeeksFromThisSaturday);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//當進度100%時  --> 任務完成
		if("100%".equals(finishPercentage)){
			dataBean.setStatus(StatusConstants.STATUS_FINISH);
		}
		//當進度0% 且 開始時間小於今天時 --> 尚未開始
		else if("0%".equals(finishPercentage) && startTimeDate.before(todayDate)){
			dataBean.setStatus(StatusConstants.STATUS_UNSTART);
		}
		//有進度 且 開始時間小於今天、完成時間大於今天 --> 按預定時程
		else if(!"0%".equals(finishPercentage) && startTimeDate.before(todayDate) && finishTimeDate.after(todayDate)){
			dataBean.setStatus(StatusConstants.STATUS_ONTARGET);
		}
		//進度未達100% 且 完成時間小於今天  --> 落後
		else if(!"100%".equals(finishPercentage) && finishTimeDate.before(todayDate)){
			dataBean.setStatus(StatusConstants.STATUS_BEHIND);
		}
		//開始任務 在 當周禮拜五+7(下週六)以前  或  包含下禮拜六 --> 本周計畫
		else if(startTimeDate.before(nextSaturdayDate)  || startTimeDate.equals(nextSaturdayDate)){
			dataBean.setStatus(StatusConstants.STATUS_THISWEEKTASK);
		}	
		//開始任務 在 當周禮拜五+14(下下週六)以前  或 包含下下禮拜六 --> 下周計畫
		else if(startTimeDate.before(towWeeksFromThisSaturdayDate) || startTimeDate.equals(towWeeksFromThisSaturdayDate)){
			dataBean.setStatus(StatusConstants.STATUS_NEXTWEEKTASK);
		}
		//開始任務 在 當周禮拜五+14(下下週六) 以後 --> 未來計畫
		else if(startTimeDate.after(towWeeksFromThisSaturdayDate)){
			dataBean.setStatus(StatusConstants.STATUS_FUTURETASK);
		}		
		return dataBean;
	}
	
	public static void main(String [] args){
		try {
			String filePath = "D:\\任務.xlsx";
			//String filePath = "任務.xlsx";
			XSSFWorkbook readWorkbook = new XSSFWorkbook (new FileInputStream(filePath));
			//取得Sheet 可指定sheet的名稱, 參數為sheet名稱
			XSSFSheet readSheet = readWorkbook.getSheetAt(0);
			//取得總列數
			int rowCount = readSheet.getPhysicalNumberOfRows(); 
			System.out.println("總列數:" + rowCount);
			
			int taskNameCol = 0;
			int startTimeCol = 0;
			int finishTimeCol = 0;
			int surplusWorkTimeCol = 0;
			int finishPercentageCol = 0;
			ArrayList<DataBean> dataBeanList = new ArrayList<>();
			
			DataFormatter df = new DataFormatter();
			
			for(int i = 0; i < rowCount; i++) {
				//先取出列
			    XSSFRow row = readSheet.getRow(i);
			    int columnCount = row.getPhysicalNumberOfCells();
			    System.out.println("總欄位:" + columnCount + " ,目前第:" + i + "欄位");
			    
			    for(int j=0; j < columnCount; j++) {
			    	//再取出欄
			    	XSSFCell XCcolumnName = row.getCell(j);
			    	String columnName = XCcolumnName.toString().trim();
			    	
			        System.out.println("columnName:" + columnName);
					//將所需要的欄位欄數存到對應的變數中
			        
					if((ColumnNameConstants.TASKNAME).getColumnName().equals(columnName)){
						taskNameCol = j;
					}
					else if((ColumnNameConstants.STARTTIME).getColumnName().equals(columnName)){
						startTimeCol = j;
					}
					else if((ColumnNameConstants.FINISHTIME).getColumnName().equals(columnName)){
						finishTimeCol = j;
					}
//					else if((ColumnNameConstants.SURPLUSWORKTIME).getColumnName().equals(columnName)){
//						surplusWorkTimeCol = j;
//					}
					else if((ColumnNameConstants.FINISHPERCENTAGE).getColumnName().equals(columnName)){
						finishPercentageCol = j;
					}
			        
			    }
			    System.out.println();
			    
			    //接著開始塞值於DataBean中, 過濾掉欄位名稱
			    if(i != 0){
			    	XSSFCell XCtaskName = row.getCell(taskNameCol);
			    	XSSFCell XCstartTime = row.getCell(startTimeCol);
			    	XSSFCell XCfinishTime = row.getCell(finishTimeCol);
			    	XSSFCell XCsurplusWorkTime = row.getCell(surplusWorkTimeCol);
			    	XSSFCell XCfinishPercentage = row.getCell(finishPercentageCol);
			    	String taskName = df.formatCellValue(XCtaskName);
			    	String startTime = df.formatCellValue(XCstartTime);
			    	String finishTime = df.formatCellValue(XCfinishTime);
			    	String surplusWorkTime = df.formatCellValue(XCsurplusWorkTime);
			    	String finishPercentage = df.formatCellValue(XCfinishPercentage);
			    	
			    	DataBean dataBean = new DataBean();
			    	dataBean.setTaskName(taskName);
			    	dataBean.setStartTime(startTime);
			    	dataBean.setFinishTime(finishTime);
			    	//dataBean.setSurplusWorkTime(surplusWorkTime);
			    	dataBean.setFinishPercentage(finishPercentage);
			    	
					//不需要"規劃視窗:"這個DateBean
					String frontFourWordsByTaskName = "";
					try{
						frontFourWordsByTaskName = taskName.substring(0, 5);
					}catch(StringIndexOutOfBoundsException e){
						//System.out.println("字數未超過5個");
						frontFourWordsByTaskName = taskName;
					}
					if("規劃視窗:".equals(frontFourWordsByTaskName)){
						continue;
					}	
					dataBeanList.add(dataBean);
			    	
			    }
			}
			
			//資料轉換，並加入狀態、前置任務、多個資源名稱、落後原因、備註
			for(int i = 0; i < dataBeanList.size(); i++){
				DataBean dataBean = dataBeanList.get(i);
				dataBean = setStatusByTime(dataBean);
				//dataBean.setFrontTask("");
				dataBean.setResourcesName("");
				dataBean.setReasonBehind("");
				dataBean.setRemark("");
			}
			System.out.println("-------------Output EXCEL--------------");
			//輸出EXCEL
			WriteExcel.outputExcel(dataBeanList);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
