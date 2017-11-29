package com.codinggyd.excel.core.exportexcel.service;

import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.codinggyd.excel.constant.ExcelConst;
import com.codinggyd.excel.core.exportexcel.inter.IExcelExport;
import com.codinggyd.excel.exception.ExcelException;

/**
 * 
 * <pre>
 * 类名:  XLSExporter.java
 * 包名:  com.codinggyd.excel.core.exportexcel.service
 * 描述:  xls格式的excel通用导出类
 * 
 * 作者:  guoyd
 * 日期:  2017年11月28日
 *
 * Copyright @ 2017 Corpration Name
 * </pre>
 */
public class XLSExporter extends CommonExporter implements IExcelExport{

	
	@Override
	public <T> Workbook export(Class<?> clazz, List<T> data) throws ExcelException{
		
		if (null == data || null == clazz) {
			return null;
		}
		
		Workbook workbook = null;
		try {
			long start = System.currentTimeMillis();
			//1.初始化解析规则变量
			super.parseConfig(clazz);
			//2.开始写入excel
			workbook = new HSSFWorkbook();
			if (data.size() < ExcelConst.EXCEL_XLS_MAX_ROW_NUM) {
				Sheet sheet = createSheet(workbook);
				//2.1 创建标题行
				createTitleRow(sheet);
				//2.2 创建内容行
				createContentRow(sheet, data);
			} else {
 				int size = data.size();
				int sta = 0;
				int end = 0;
				int i = 0;
				while(size>(sta=end)){
					end=sta+ExcelConst.EXCEL_XLS_MAX_ROW_NUM;
					if(end>size){
						end=size;
					}
					Sheet sheet = createSheet(workbook,sheetConfig.sheetName()+i);
					//2.1 创建标题行
					createTitleRow(sheet);
					//2.2 创建内容行
					createContentRow(sheet, data.subList(sta, end));
					
					i++;//用来生成sheet名称,sheet名称不允许重复
				}
				
			}
			System.out.println("导出数据量"+data.size()+",xls耗时:"+(System.currentTimeMillis()-start)+"ms");
 		} catch (Exception e) {
			throw new ExcelException(e.getMessage());
 		}
 		return workbook;
 	}

	@Override
	public <T> void export(Class<?> clazz, List<T> data, OutputStream outputStream) throws ExcelException{
		Workbook workbook = null;
		try {
			workbook = export(clazz, data);
			workbook.write(outputStream);
			
		} catch (Exception e) {
			throw new ExcelException(e.getMessage());
		} finally {
			try {
				
				if (null != outputStream) {
					outputStream.close();
				}
				
				if (null != workbook) {
 					workbook.close();
				}
			} catch (Exception e){
				throw new ExcelException(e.getMessage());
			}
		}
		
	}

}
