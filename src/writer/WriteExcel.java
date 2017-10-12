package writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import annotations.ColumnName;
import comm.ColumnNameConstants;
import model.DataBean;

public class WriteExcel {
	
	public static void outputExcel(ArrayList<DataBean> dataBeanList){
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String dateStr = dateFormat.format(date);
		System.out.println(dateStr);
		
		String path = "D:\\" + dateStr + "週報_.xlsx";
		//String path = dateStr + "週報_.xlsx";
		File excelFile = new File(path);
		if(!excelFile.exists()){
			createExcel(excelFile, dataBeanList);
		}else{
			System.out.println("The file is exist.");
		}
		
	}
	
	private static void createExcel(File excelFile,ArrayList<DataBean> dataBeanList) {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		Font font = workbook.createFont();
		font.setColor(HSSFColor.BLACK.index);	//顏色
		font.setFontName("新細明體");				//設定字體
		font.setFontHeightInPoints((short) 12);	//設定字體大小
		XSSFCellStyle styleFont = workbook.createCellStyle();//設定樣板
		styleFont.setFont(font); 				//設定字體
		
		
		XSSFSheet sheet = workbook.createSheet("sheet");	//產生分頁
		XSSFRow rowTitle = sheet.createRow(0);
		
		//存放欄位
		ArrayList<String> rowTitileList = new ArrayList<>();
		
		//取出annotation的value 即欄位名稱並儲存到row(0)中
		Class dataBeanClass = DataBean.class;
		Annotation[] annotations = null;
		int index = 0;
		for(Field field : dataBeanClass.getDeclaredFields()){
			annotations = field.getDeclaredAnnotations();
			String columnName = (((ColumnName)annotations[0]).value().getColumnName()).toString();;
			System.out.println("欄位名稱:" + columnName);
			rowTitle.createCell(index).setCellValue(columnName);
			rowTitileList.add(columnName);
			
			Cell cellFinishPercentage = rowTitle.createCell(index);	//建立儲存格
			cellFinishPercentage.setCellStyle(styleFont);			//套用格式
			cellFinishPercentage.setCellValue(columnName);			//設定內容
			
			index++;
		}
		
		String taskName = null, startTime = null, finishTime = null, finishPercentage  = null, 
			   status = null, resourcesName = null, reasonBehind = null, remark = null;
		//資料筆數
		for(int i = 0; i < dataBeanList.size(); i++){
			XSSFRow rowContent = sheet.createRow(i + 1); // 建立儲存格
			
			DataBean dataBean = dataBeanList.get(i);
			
			//String taskName = formateTaskName(dataBean);
//			startTime = dataBean.getStartTime();
//			finishTime = dataBean.getFinishTime();
			//String surplusWorkTime = dataBean.getSurplusWorkTime();
//			finishPercentage = dataBean.getFinishPercentage();
//			status = "";
//			try{
//				status = dataBean.getStatus().getStatusName();
//			}catch(NullPointerException e){}
			//String frontTask = dataBean.getFrontTask();
//			resourcesName = dataBean.getResourcesName();
//			reasonBehind = dataBean.getReasonBehind();
//			remark = dataBean.getRemark();
			
			//欄位筆數
			for(int k = 0; k < rowTitileList.size(); k++){
				String columnName = rowTitileList.get(k);
				//設定樣式
				Cell cell = rowContent.createCell(k);
				XSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setFont(font);
				//自動調整欄位寬度
				sheet.autoSizeColumn(k);
				
				
				//找到對應的欄位並塞值，例如任務名稱 --> XXX
				if((ColumnNameConstants.TASKNAME).getColumnName().equals(columnName)){
					//必須先取名稱判斷，若是"專案名稱:"開頭必須把該Bean後面的值塞空值，讓頁面不顯示
					taskName = formateTaskName(dataBean, cellStyle, rowContent);
					cell.setCellValue(taskName);
				}	
				else if((ColumnNameConstants.STARTTIME).getColumnName().equals(columnName)){
					startTime = dataBean.getStartTime();
					cell.setCellValue(startTime);
				}
				else if((ColumnNameConstants.FINISHTIME).getColumnName().equals(columnName)){
					finishTime = dataBean.getFinishTime();
					cell.setCellValue(finishTime);
				}
				//取消剩餘工時
//				else if((ColumnNameConstants.SURPLUSWORKTIME).getColumnName().equals(columnName)){
//					cell.setCellValue(surplusWorkTime);
//				}
				else if((ColumnNameConstants.FINISHPERCENTAGE).getColumnName().equals(columnName)){
					//設定工時完成百分 靠右對齊
					finishPercentage = dataBean.getFinishPercentage();
					cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
					cell.setCellValue(finishPercentage);
				}
				else if((ColumnNameConstants.STATUS).getColumnName().equals(columnName)){
					status = "";
					Short statusColor = HSSFColor.WHITE.index;
					try{
						status = dataBean.getStatus().getStatusName();
						statusColor = dataBean.getStatus().getColor();
					}catch(NullPointerException e){}
					//設定狀態顏色及置中
					cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  //設置可填充儲存格底色
					cellStyle.setFillForegroundColor(statusColor);
					cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);  
					cell.setCellValue(status);
				}
				//取消前置任務
//				else if((ColumnNameConstants.FRONTTASK).getColumnName().equals(columnName)){
//					cell.setCellValue(frontTask);
//				}
				else if((ColumnNameConstants.RESOURCESNAME).getColumnName().equals(columnName)){
					resourcesName = dataBean.getResourcesName();
					cell.setCellValue(resourcesName);
				}
				else if((ColumnNameConstants.REASONBEHIND).getColumnName().equals(columnName)){
					reasonBehind = dataBean.getReasonBehind();
					cell.setCellValue(reasonBehind);
				}
				else if((ColumnNameConstants.REMARK).getColumnName().equals(columnName)){
					remark = dataBean.getRemark();
					cell.setCellValue(remark);
				}
				cell.setCellStyle(cellStyle);
			}
			
			//輸出結果
			System.out.println("taskName:  " + taskName);
			System.out.println("startTime:  " + startTime);
			System.out.println("finishTime:  " + finishTime);
			//System.out.println("surplusWorkTime:  " + surplusWorkTime);
			System.out.println("finishPercentage:  " + finishPercentage);
			System.out.println("status:  " + status);
			//System.out.println("frontTask:  " + frontTask);
			System.out.println("resourcesName:  " + resourcesName);
			System.out.println("reasonBehind:  " + reasonBehind);
			System.out.println("remark:  " + remark);
		}
		
		System.out.println("--- Output success! ---");
		//輸出Excel
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(excelFile);
			workbook.write(fileOut);
			fileOut.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	private static String formateTaskName(DataBean dataBean, XSSFCellStyle cellStyle, XSSFRow rowContent) {
		String taskName = dataBean.getTaskName();
		String frontFourWordsByTaskName = null;
		try{
			frontFourWordsByTaskName = taskName.substring(0, 5);
		}catch(StringIndexOutOfBoundsException e){
			//System.out.println("字數未超過5個");
			frontFourWordsByTaskName = taskName;
		}
		
		if("專案名稱:".equals(frontFourWordsByTaskName)){
			taskName = taskName.substring(6);//5是空格，從6開始
			dataBean.setFinishPercentage("");
			dataBean.setFinishTime("");
			//dataBean.setFrontTask("");
			dataBean.setReasonBehind("");
			dataBean.setRemark("");
			dataBean.setResourcesName("");
			dataBean.setStartTime("");
			dataBean.setStatus(null);
			//dataBean.setSurplusWorkTime("");
			//專案名稱後的儲存格清空
		   for(int i = 1; i < 10; i++){
		    rowContent.createCell(i);
		   }
		}else{
			//增加縮排
			//taskName = "　" + taskName;
			taskName = taskName;
			short indention = (short) 1;
			cellStyle.setIndention(indention);
		}
		return taskName;
	}

	
	public static void main(String [] args){
//		double programElecdia = 110.8;
//		programElecdia = programElecdia * 10;
//		//取得打孔直徑*10後的個位數
//		String programElecdiaStr = String.valueOf(programElecdia);
//		
//		String digitsOfProgramElecdiaStr = programElecdiaStr.split("\\.")[0];
//		digitsOfProgramElecdiaStr = digitsOfProgramElecdiaStr.substring(digitsOfProgramElecdiaStr.length() - 1);
//		System.out.println("aaaa:" + digitsOfProgramElecdiaStr);
//		System.out.println(digitsOfProgramElecdiaStr);
		
		
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date createDate = null,lastEstimateEndTimeDate = null;
		Date actualStartTime = null,actualEndTime = null;
		Date estimateStartTime = null,estimateEndTime = null,startTime = null,endTime = null;
		Integer totalWorkTime = 300,estimateTime = 5;
		
		String createDateStr = "2016-12-15 12:30:31.633";
		String lastEstimateEndTimeStr = "";
		String startTimeStr = "2016-12-10 12:00:00.633";
		String endTimeStr = "2016-12-23 21:30:00.633";
		try {
			createDate = sdf.parse(createDateStr);
			startTime = sdf.parse(startTimeStr);
			endTime = sdf.parse(endTimeStr);
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		
		WorkTimeInfo workTimeInfo = new WorkTimeInfo();
//		workTimeInfo.setActualEndTime(actualEndTime);
//		workTimeInfo.setActualStartTime(actualStartTime);
//		workTimeInfo.setEstimateEndTime(estimateEndTime);
//		workTimeInfo.setEstimateStartTime(estimateStartTime);
		workTimeInfo.setStartTime(startTime);
		workTimeInfo.setEndTime(endTime);
		workTimeInfo.setEstimateTime(estimateTime);
		workTimeInfo.setTotalWorkTime(totalWorkTime);
		
		//第一次執行，故預估的開始時間 = 該製程工段建立的時間
		lastEstimateEndTimeDate = lastEstimateEndTimeDate == null? createDate:lastEstimateEndTimeDate;	
		
		List<List<WorkTimeInfo>> workTimeInfoList = verifyTimeInScopeOfStandard(workTimeInfo, lastEstimateEndTimeDate);

		for(List<WorkTimeInfo> estimateTimeAndActualList: workTimeInfoList){

			for(WorkTimeInfo estimateTimeAndActual:estimateTimeAndActualList){
				System.out.println("********分隔線*******");
				System.out.println("預估工時:" + estimateTimeAndActual.getEstimateTime());
				System.out.println("開始時間:" + estimateTimeAndActual.getStartTime());
				System.out.println("結束時間:" + estimateTimeAndActual.getEndTime());
				System.out.println("實際總工時:" + estimateTimeAndActual.getTotalWorkTime());
				
				System.out.println("*******************");
			}
			System.out.println("---------------------------------------------");
		}
	
	
	}
	
	private static Date lastTime;
	private static int ONWORK = 8;
	private static int OFFWORK = 20;
	
	
	private static List<List<WorkTimeInfo>> verifyTimeInScopeOfStandard(WorkTimeInfo workTimeInfo, Date lastEstimateEndTime) {
		
		List<List<WorkTimeInfo>> workTimeInfoList = new ArrayList<>();
		
		List<WorkTimeInfo> estimateTimeInfoList = new ArrayList<>();
		List<WorkTimeInfo> actualTimeInfoList = new ArrayList<>();
		WorkTimeInfo estimateTimeInfo = new WorkTimeInfo();
		WorkTimeInfo actualTimeInfo = new WorkTimeInfo();
		
		/*
		 * 處理預估時間
		 */
		Integer estimateTimeTemplate = 1;
		//預估工時時間，單位為小時，注意得轉為毫秒計算
		Integer estimateTime = workTimeInfo.getEstimateTime();
		
		//預估的開始時間 = 延續上次的結束時間
		Date estimateStartTime = lastEstimateEndTime;
		 
		//預估結束時間 = 預估開始時間 + 預估工時時間
		Long estimateEndTimeMillisecond = DateToMillisecond(estimateStartTime) + (estimateTime * 3600000);
		Date estimateEndTime = MillisecondToDate(estimateEndTimeMillisecond);
		
		//將這次結束時間存在lastEstimateEndTime以利於下次的預估開始時間使用
		lastEstimateEndTime = estimateEndTime;
		
		Map<String, Date> estimateTimeAcrossMap = acrossOffWorkAndHolidays(estimateStartTime, estimateEndTime, estimateTimeTemplate);
		estimateTimeInfoList = getAllCuttingSectionTimeRange(estimateTime, estimateTimeAcrossMap, null, estimateTimeTemplate);
		
		//若為空值，表示沒有橫跨下班或假日，沿用舊值
		if(estimateTimeInfoList.isEmpty()){
			estimateTimeInfo.setEstimateTime(estimateTime);
			estimateTimeInfo.setStartTime(estimateStartTime);
			estimateTimeInfo.setEndTime(estimateEndTime);
			estimateTimeInfoList.add(estimateTimeInfo);
		}

		
		/*
		 * 處理實際時間
		 */
		Integer acutalTimeTemplate = 2;
		//實際加工時間(扣掉暫停時間，這裡不會用到)
		Integer totalWorkTime = workTimeInfo.getTotalWorkTime();
		
		//實際開始時間
		Date actualStartTime = workTimeInfo.getStartTime();
		
		//實際結束時間
		Date actualEndTime = workTimeInfo.getEndTime();
		
		//判斷input的開始時間、結束時間，是否有橫跨上班或假日，若有的話將分割成currentStartTime、currentEndTime跟nextStartTime、nextEndTime
		Map<String, Date> actualTimeAcrossMap = acrossOffWorkAndHolidays(actualStartTime, actualEndTime, acutalTimeTemplate);
		
		//取得總共有幾次橫跨下班和假日
		actualTimeInfoList = getAllCuttingSectionTimeRange(null, actualTimeAcrossMap, totalWorkTime, acutalTimeTemplate);
		
		//若為空值，表示沒有橫跨下班或假日，沿用舊值
		if(actualTimeInfoList.isEmpty()){
			actualTimeInfo.setTotalWorkTime(totalWorkTime);
			actualTimeInfo.setStartTime(actualStartTime);
			actualTimeInfo.setEndTime(actualEndTime);
			actualTimeInfoList.add(actualTimeInfo);
		}
		
		workTimeInfoList.add(estimateTimeInfoList);
		workTimeInfoList.add(actualTimeInfoList);
		
		return workTimeInfoList;
	}
	
	//取得所有 預估或實際 跨過下班和假日的總切割時間範圍
	private static List<WorkTimeInfo> getAllCuttingSectionTimeRange(Integer estimateTime, Map<String, Date> sourceMap, Integer totalWorkTime, Integer isEstimateOrAcutalTemplate){
		List<WorkTimeInfo> results = new ArrayList<>();
		if(!sourceMap.isEmpty()){
			
			//持續判斷，若有下次時間(nextStartTime、nextEndTime)，就在判斷一次是否有橫跨下班或假日
			while ( sourceMap.containsKey("nextStartTime") && sourceMap.containsKey("nextEndTime") ){
				WorkTimeInfo workTimeInfo = new WorkTimeInfo();
				
				Date currentStartTime = sourceMap.get("currentStartTime");
				Date currentEndTime = sourceMap.get("currentEndTime");
				workTimeInfo.setEstimateTime(estimateTime);
				workTimeInfo.setTotalWorkTime(totalWorkTime);
				workTimeInfo.setStartTime(currentStartTime);
				workTimeInfo.setEndTime(currentEndTime);
				// 把檢驗合格的當前開始/結束時間 放到list中
				results.add(workTimeInfo);
				
				Date nextStartTime = sourceMap.get("nextStartTime");
				Date nextEndTime = sourceMap.get("nextEndTime");
				// 下次的開始/結束時間 有可能仍跨過下班或假日時間，必須再判斷一次
				sourceMap = acrossOffWorkAndHolidays(nextStartTime, nextEndTime, isEstimateOrAcutalTemplate);
				
				//當回傳空值，表示已無橫跨下班或假日，記得將最後一次也儲存
				if(sourceMap.isEmpty()){
					WorkTimeInfo finalWorkTimeInfo = new WorkTimeInfo();
					finalWorkTimeInfo.setEstimateTime(estimateTime);
					finalWorkTimeInfo.setTotalWorkTime(totalWorkTime);
					finalWorkTimeInfo.setStartTime(nextStartTime);
					finalWorkTimeInfo.setEndTime(nextEndTime);
					results.add(finalWorkTimeInfo);
				}
			}
		}
		return results;
	}
	
	
	private static Map<String, Date> acrossOffWorkAndHolidays(Date startTimeDate, Date endTimeDate, Integer isEstimateOrAcutalTemplate){
		
		
		Map<String, Date> acrossOffWorkAndHolidaysMap = new HashMap<>();
		
		Long endTimeMillisecond = DateToMillisecond(endTimeDate);
		
		
		//設定上下班時間，上下班時間，超過都要扣除掉，單位(小時) 轉毫秒
		Long onWork = (long) ((8) * 3600000);
		Long offWork = (long) ((20) * 3600000);
		Long saturdayAndSunday = (long) (2 * 86400000);
		
		//存放所有六日和國定假日、公司休假，未來將新增
		//List<Date> holidays = getHolidays();		
		
		
		//由傳入日期(開始時間)來取得當天下班時間	
		Calendar cal = Calendar.getInstance(); 
		Date nowDate = cal.getTime();
		Long nowDateTime = DateToMillisecond(nowDate);
		cal.setTime(startTimeDate);
		//設定為24小時制，Calendar.HOUR is strictly for 12 hours.
		cal.set(Calendar.HOUR_OF_DAY,  0 );                 //把當前時間小時變成0，注意這裡雖設為0，但是設定中午12點整，若需要凌晨12點需再扣掉12小時
		cal.set(Calendar.MINUTE,  0 );                      //把當前時間分鐘變成0
		cal.set(Calendar.SECOND,  0 );                      //把當前時間秒數變成0
		cal.set(Calendar.MILLISECOND,  0 );                 //把當前時間毫秒變成0
		
		//開始時間的為星期幾
		int dayOfWeekByStartTime = cal.get(Calendar.DAY_OF_WEEK) - 1;
		
		//開始時間的凌晨12點整
		Date startTimeOfMidnightDate = cal.getTime();
		Long startTimeOfMidnightMillisecond = DateToMillisecond(startTimeOfMidnightDate);
		//取得當天下班時間(單位毫秒)，設定基準為凌晨12點再加上 到下班的時間，得當天 下班時間
		Long currentDayOffWorkMillisecond = startTimeOfMidnightMillisecond + offWork;
		Date currentDayOffWorkDate = MillisecondToDate(currentDayOffWorkMillisecond);
		
		//由傳入日期(開始時間)來取得當周五下班時間，用於計算橫跨假日(六、日)
		cal.setFirstDayOfWeek(Calendar.MONDAY);             //設定星期的第一天是星期一，注意 沒有設定，誤差會差一個禮拜
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);		//把日期變成本週的星期五

		Long currentFridayOffWorkMillisecond = DateToMillisecond(cal.getTime()) + offWork;
		Date currentFridayOffWorkDate = MillisecondToDate(currentFridayOffWorkMillisecond);
		
		
		//為剩餘時間 ，判斷此任務的結束時間是否有超過下班時間，剩餘時間 > 0 表示有橫跨，若 < 0 表示沒橫跨
		Long surplusWorkTimeAcrossOffWork = endTimeMillisecond - currentDayOffWorkMillisecond;
		//為剩餘時間 ，判斷此任務的結束時間是否有超過週五下班時間，剩餘時間 > 0 表示有橫跨，若 < 0 表示沒橫跨
		Long surplusWorkTimeAcrossHolidays = endTimeMillisecond - currentFridayOffWorkMillisecond;
		
		
		
		//超過下班時間
		if(surplusWorkTimeAcrossOffWork > 0){
			
			//當天開始時間一樣
			acrossOffWorkAndHolidaysMap.put("currentStartTime", startTimeDate);
			//當天結束時間，為當天下班時間(因為橫跨了)
			acrossOffWorkAndHolidaysMap.put("currentEndTime", currentDayOffWorkDate);
			
			//延後時間 = 下班到今夜12點整 加上 凌晨12點整到隔日上班時間
			Long postponeTime = (24 * 3600000 - offWork) + onWork;
			
			//下次開始時間 = 當天下班時間 + 延後時間 (單位毫秒)，即為隔日
			Long nextStartTimeMillisecond = currentDayOffWorkMillisecond + postponeTime;
			//下次結束時間 = 下次開始時間 + 上一次超過下班時間相減的剩餘時間
			Long nextEndTimeMillisecond = nextStartTimeMillisecond + surplusWorkTimeAcrossOffWork;
			
			//若當前開始時間為星期五，超過週五下班含假日時間，延後時間、下次時間另外取值
			if(dayOfWeekByStartTime == 5 ){
				//延後時間 = 週五下班到週六12點整 加上 六日兩天 再加上 週日凌晨12點整到周一上班時間
				postponeTime = (24 * 3600000 - offWork) + saturdayAndSunday + onWork;
				//跨假日的下次開始時間 = 當天下班時間 + 延後時間 (單位毫秒)，即為下周一
				nextStartTimeMillisecond = currentDayOffWorkMillisecond + postponeTime;
				
				//跨假日的下次結束時間 = 下次開始時間 + 超過假日時間的剩餘時間
				nextEndTimeMillisecond = nextStartTimeMillisecond + surplusWorkTimeAcrossHolidays;
				
			}
			
			Date nextStartTimeDate = MillisecondToDate(nextStartTimeMillisecond);
			Date nextEndTimeDate = MillisecondToDate(nextEndTimeMillisecond);
			
			acrossOffWorkAndHolidaysMap.put("nextStartTime", nextStartTimeDate) ;
			acrossOffWorkAndHolidaysMap.put("nextEndTime", nextEndTimeDate) ;
			
			//實際時間不需要加上延後時間，只要將開始到結束的時間篩選掉上下班及跨假日即可，因此下一次結束時間都是input的結束時間
			if(isEstimateOrAcutalTemplate == 2){
				acrossOffWorkAndHolidaysMap.put("nextEndTime", endTimeDate) ;
			}
			
		}
		
		return acrossOffWorkAndHolidaysMap;
	}
	
	//此判斷是用於實際加工時間
	private static Map<String, Date> acrossOffWorkAndHolidaysByActualTime(Date startTimeDate, Date endTimeDate){
		Map<String, Date> acrossOffWorkAndHolidaysByActualTimeMap = new HashMap<>();
		
		//設定上下班時間，上下班時間，超過都要扣除掉，單位(小時) 轉毫秒
		Long onWork = (long) ((8) * 3600000);
		Long offWork = (long) ((20) * 3600000);
		Long saturdayAndSunday = (long) (2 * 86400000);
		
		//存放所有六日和國定假日、公司休假，未來將新增
		//List<Date> holidays = getHolidays();		
		
		
		return acrossOffWorkAndHolidaysByActualTimeMap;
	}
	
	private static Long DateToMillisecond(Date date){
		Long millisecond = null;
		millisecond = date.getTime();
		return millisecond;
	}
	
	private static Date MillisecondToDate(Long millisecond){

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Date date = null;
		date = new Date(millisecond);
		
		//String dateStr = sdf.format(date);
		
		return date;
	}
	
	
	
//new 	
//	private static Map<String, Date> acrossOffWorkAndHolidays(Date startTimeDate, Date endTimeDate){
//		
//		
//		Map<String, Date> acrossOffWorkAndHolidaysMap = new HashMap<>();
//		
//		Long endTimeMillisecond = DateToMillisecond(endTimeDate);
//		
//		
//		//設定上下班時間，上下班時間，超過都要扣除掉，單位(小時) 轉毫秒
//		Long onWork = (long) ((8) * 3600000);
//		Long offWork = (long) ((20) * 3600000);
//		Long saturdayAndSunday = (long) (2 * 86400000);
//		
//		//存放所有六日和國定假日、公司休假，未來將新增
//		//List<Date> holidays = getHolidays();		
//		
//		
//		//由傳入日期(開始時間)來取得當天下班時間	
//		Calendar cal = Calendar.getInstance(); 
//		Date nowDate = cal.getTime();
//		Long nowDateTime = DateToMillisecond(nowDate);
//		cal.setTime(startTimeDate);
//		//設定為24小時制，Calendar.HOUR is strictly for 12 hours.
//		cal.set(Calendar.HOUR_OF_DAY,  0 );                 //把當前時間小時變成0，注意這裡雖設為0，但是設定中午12點整，若需要凌晨12點需再扣掉12小時
//		cal.set(Calendar.MINUTE,  0 );                      //把當前時間分鐘變成0
//		cal.set(Calendar.SECOND,  0 );                      //把當前時間秒數變成0
//		cal.set(Calendar.MILLISECOND,  0 );                 //把當前時間毫秒變成0
//		
//		//開始時間的凌晨12點整
//		Date startTimeOfMidnightDate = cal.getTime();
//		Long startTimeOfMidnightMillisecond = DateToMillisecond(startTimeOfMidnightDate);
//		//取得當天下班時間(單位毫秒)，設定基準為中午12點整，需再扣掉12小時為凌晨12點再加上 到下班的時間，得當天 下班時間
//		Long currentDayOffWorkMillisecond = startTimeOfMidnightMillisecond + offWork;
//		Date currentDayOffWorkDate = MillisecondToDate(currentDayOffWorkMillisecond);
//		
//		//由傳入日期(開始時間)來取得當周五下班時間，用於計算橫跨假日(六、日)
//		cal.setFirstDayOfWeek(Calendar.MONDAY);             //設定星期的第一天是星期一，注意 沒有設定，誤差會差一個禮拜
//		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);		//把日期變成本週的星期五
//
//		Long currentFridayOffWorkMillisecond = DateToMillisecond(cal.getTime()) + offWork;
//		Date currentFridayOffWorkDate = MillisecondToDate(currentFridayOffWorkMillisecond);
//		
//		
//		//為剩餘時間 ，判斷此任務的結束時間是否有超過下班時間，剩餘時間 > 0 表示有橫跨，若 < 0 表示沒橫跨
//		Long surplusWorkTimeAcrossOffWork = endTimeMillisecond - currentDayOffWorkMillisecond;
//		//為剩餘時間 ，判斷此任務的結束時間是否有超過週五下班時間，剩餘時間 > 0 表示有橫跨，若 < 0 表示沒橫跨
//		Long surplusWorkTimeAcrossHolidays = endTimeMillisecond - currentFridayOffWorkMillisecond;
//		
//		
//		//超過下班時間
//		if(surplusWorkTimeAcrossOffWork > 0){
//			
//			//當天開始時間一樣
//			acrossOffWorkAndHolidaysMap.put("currentStartTime", startTimeDate);
//			//當天結束時間，為當天下班時間(因為橫跨了)
//			acrossOffWorkAndHolidaysMap.put("currentEndTime", MillisecondToDate(currentDayOffWorkMillisecond));
//			
//			//延後時間 = 下班到今夜12點整 加上 凌晨12點整到隔日上班時間
//			Long postponeTime = (24 * 3600000 - offWork) + onWork;
//			
//			//下次開始時間 = 當天下班時間 + 延後時間 (單位毫秒)，即為隔日
//			Long nextStartTimeMillisecond = currentDayOffWorkMillisecond + postponeTime;
//			
//			//下次結束時間 = 下次開始時間 + 上一次超過下班時間相減的剩餘時間
//			Long nextEndTimeMillisecond = nextStartTimeMillisecond + surplusWorkTimeAcrossOffWork;
//			
//			//超過週五下班含假日時間，延後時間、下次時間另外取值
//			if(surplusWorkTimeAcrossHolidays > 0){
//				//延後時間 = 週五下班到週六12點整 加上 六日兩天 再加上 週日凌晨12點整到周一上班時間
//				postponeTime = (24 * 3600000 - offWork) + saturdayAndSunday + onWork;
//				//跨假日的下次開始時間 = 當天下班時間 + 延後時間 (單位毫秒)，即為下周一
//				nextStartTimeMillisecond = currentDayOffWorkMillisecond + postponeTime;
//				
//				//跨假日的下次結束時間 = 下次開始時間 + 超過假日時間的剩餘時間
//				nextEndTimeMillisecond = nextStartTimeMillisecond + surplusWorkTimeAcrossHolidays;
//			}
//			
//			acrossOffWorkAndHolidaysMap.put("nextStartTime", MillisecondToDate(nextStartTimeMillisecond)) ;
//			acrossOffWorkAndHolidaysMap.put("nextEndTime", MillisecondToDate(nextEndTimeMillisecond)) ;
//			
//		}
//		
//		return acrossOffWorkAndHolidaysMap;
//	}
	
	
	
//	舊版 用於預估
//	private static Map<String, Date> acrossOffWorkAndHolidays(Date startTimeDate, Date endTimeDate){
//		
//		
//		Map<String, Date> acrossOffWorkAndHolidaysMap = new HashMap<>();
//		
//		Long endTimeMillisecond = DateToMillisecond(endTimeDate);
//		
//		
//		//設定上下班時間，上下班時間，超過都要扣除掉，單位(小時) 轉毫秒
//		Long onWork = (long) ((8) * 3600000);
//		Long offWork = (long) ((20) * 3600000);
//		Long saturdayAndSunday = (long) (2 * 86400000);
//		
//		//存放所有六日和國定假日、公司休假，未來將新增
//		//List<Date> holidays = getHolidays();		
//		
//		
//		//由傳入日期(開始時間)來取得當天下班時間	
//		Calendar cal = Calendar.getInstance(); 
//		Date nowDate = cal.getTime();
//		Long nowDateTime = DateToMillisecond(nowDate);
//		cal.setTime(startTimeDate);
//		//設定為24小時制，Calendar.HOUR is strictly for 12 hours.
//		cal.set(Calendar.HOUR_OF_DAY,  0 );                 //把當前時間小時變成0，注意這裡雖設為0，但是設定中午12點整，若需要凌晨12點需再扣掉12小時
//		cal.set(Calendar.MINUTE,  0 );                      //把當前時間分鐘變成0
//		cal.set(Calendar.SECOND,  0 );                      //把當前時間秒數變成0
//		cal.set(Calendar.MILLISECOND,  0 );                 //把當前時間毫秒變成0
//		
//		//開始時間的凌晨12點整
//		Date startTimeOfMidnightDate = cal.getTime();
////		System.out.println("開始時間::" + startTimeDate);
////		System.out.println("結束時間:" + endTimeDate);
////		System.out.println("開始時間的凌晨12點整:" + startTimeOfMidnightDate);
//
//		Long startTimeOfMidnightMillisecond = DateToMillisecond(startTimeOfMidnightDate);
//		//取得當天下班時間(單位毫秒)，設定基準為中午12點整，需再扣掉12小時為凌晨12點再加上 到下班的時間，得當天 下班時間
//		Long currentDayOffWorkMillisecond = startTimeOfMidnightMillisecond + offWork;
//		Date currentDayOffWorkMillisecondDate = MillisecondToDate(currentDayOffWorkMillisecond);
//		
////		System.out.println("開始時間當天的下班時間:" + currentDayOffWorkMillisecondDate);
//		//由傳入日期(開始時間)來取得當周五下班時間，用於計算橫跨假日(六、日)
//		cal.setFirstDayOfWeek(Calendar.MONDAY);             //設定星期的第一天是星期一，注意 沒有設定，誤差會差一個禮拜
//		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);		//把日期變成本週的星期五
//
//		Long currentFridayOffWorkMillisecond = DateToMillisecond(cal.getTime()) + offWork;
//		Date currentFridayOffWorkMillisecondDate = MillisecondToDate(currentFridayOffWorkMillisecond);
//		
//		
//		//為剩餘時間 ，判斷此任務的結束時間是否有超過下班時間，剩餘時間 > 0 表示有橫跨，若 < 0表示沒橫跨
//		Long surplusWorkTimeAcrossOffWork = endTimeMillisecond - currentDayOffWorkMillisecond;
//		//為剩餘時間 ，判斷此任務的結束時間是否有超過週五下班時間，剩餘時間 > 0 表示有橫跨，若 < 0表示沒橫跨
//		Long surplusWorkTimeAcrossHolidays = endTimeMillisecond - currentFridayOffWorkMillisecond;
//		
//		
//		//超過下班時間
//		if(surplusWorkTimeAcrossOffWork > 0){
//			
//			//當天開始時間一樣
//			acrossOffWorkAndHolidaysMap.put("currentStartTime", startTimeDate);
//			//當天結束時間，為當天下班時間(因為橫跨了)
//			acrossOffWorkAndHolidaysMap.put("currentEndTime", MillisecondToDate(currentDayOffWorkMillisecond));
//			
//			//延後時間 = 下班到今夜12點整 加上 凌晨12點整到隔日上班時間
//			Long postponeTime = (24 * 3600000 - offWork) + onWork;
//			
//			//下次開始時間 = 當天下班時間 + 延後時間 (單位毫秒)，即為隔日
//			Long nextStartTimeMillisecond = currentDayOffWorkMillisecond + postponeTime;
//			
//			//下次結束時間 = 下次開始時間 + 上一次超過下班時間相減的剩餘時間
//			Long nextEndTimeMillisecond = nextStartTimeMillisecond + surplusWorkTimeAcrossOffWork;
//			
//			//超過週五下班含假日時間，延後時間、下次時間另外取值
//			if(surplusWorkTimeAcrossHolidays > 0){
//				//延後時間 = 週五下班到週六12點整 加上 六日兩天 再加上 週日凌晨12點整到周一上班時間
//				postponeTime = (24 * 3600000 - offWork) + saturdayAndSunday + onWork;
//				//跨假日的下次開始時間 = 當天下班時間 + 延後時間 (單位毫秒)，即為下周一
//				nextStartTimeMillisecond = currentDayOffWorkMillisecond + postponeTime;
//				
//				//跨假日的下次結束時間 = 下次開始時間 + 超過假日時間的剩餘時間
//				nextEndTimeMillisecond = nextStartTimeMillisecond + surplusWorkTimeAcrossHolidays;
//			}
//			
//			acrossOffWorkAndHolidaysMap.put("nextStartTime", MillisecondToDate(nextStartTimeMillisecond)) ;
//			acrossOffWorkAndHolidaysMap.put("nextEndTime", MillisecondToDate(nextEndTimeMillisecond)) ;
//			
//		}
//		
//		return acrossOffWorkAndHolidaysMap;
//	}
}
