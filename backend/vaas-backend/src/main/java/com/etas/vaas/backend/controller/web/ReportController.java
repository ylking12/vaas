/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.excel.EasyExcel
 *  com.alibaba.excel.ExcelWriter
 *  com.alibaba.excel.write.builder.ExcelWriterTableBuilder
 *  com.alibaba.excel.write.handler.WriteHandler
 *  com.alibaba.excel.write.merge.OnceAbsoluteMergeStrategy
 *  com.alibaba.excel.write.metadata.WriteSheet
 *  com.alibaba.excel.write.metadata.WriteTable
 *  com.alibaba.excel.write.metadata.style.WriteCellStyle
 *  com.alibaba.excel.write.metadata.style.WriteFont
 *  com.alibaba.excel.write.style.HorizontalCellStyleStrategy
 *  com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy
 *  com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy
 *  com.etas.vaas.backend.controller.web.ReportController
 *  com.etas.vaas.backend.service.web.DailyReportService
 *  com.etas.vaas.common.dto.report.daily.EventDailyReport
 *  jakarta.annotation.Resource
 *  jakarta.servlet.ServletOutputStream
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.poi.ss.usermodel.BorderStyle
 *  org.apache.poi.ss.usermodel.FillPatternType
 *  org.apache.poi.ss.usermodel.HorizontalAlignment
 *  org.apache.poi.ss.usermodel.IndexedColors
 *  org.apache.poi.ss.usermodel.VerticalAlignment
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.RestController
 */
package com.etas.vaas.backend.controller.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterTableBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.merge.OnceAbsoluteMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.etas.vaas.backend.service.web.DailyReportService;
import com.etas.vaas.common.dto.report.daily.EventDailyReport;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@SuppressWarnings({"unchecked"})
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);
    @Resource
    private DailyReportService dailyReportService;
    private static final HorizontalCellStyleStrategy styleWriteHandler;

    @GetMapping(value={"/report/{reportDate}"})
    public void exportDailyReport(@PathVariable String reportDate, HttpServletResponse response) throws IOException {
        LocalDate date;
        if (reportDate == null) {
            LocalDate today = LocalDate.now();
            date = today.minusDays(1L);
        } else {
            date = LocalDate.parse(reportDate, DateTimeFormatter.ISO_LOCAL_DATE);
        }
        EventDailyReport dailyReport = this.dailyReportService.generateDailyReportData(date, 10);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode("\u4e8b\u4ef6\u65e5\u62a5_" + reportDate, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + fileName + ".xlsx");
        try (ServletOutputStream outputStream = response.getOutputStream();){
            ExcelWriter excelWriter = EasyExcel.write((OutputStream)outputStream).build();
            WriteSheet sheet = EasyExcel.writerSheet((String)"\u65e5\u62a5").build();
            WriteTable summaryTable = ((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)EasyExcel.writerTable((Integer)0).needHead(Boolean.valueOf(true))).registerWriteHandler((WriteHandler)new OnceAbsoluteMergeStrategy(0, 0, 0, 1))).registerWriteHandler((WriteHandler)new SimpleRowHeightStyleStrategy(Short.valueOf((short)40), Short.valueOf((short)25)))).registerWriteHandler((WriteHandler)new SimpleColumnWidthStyleStrategy(Integer.valueOf(30)))).registerWriteHandler((WriteHandler)styleWriteHandler)).head(List.of(List.of("\u4e8b\u4ef6\u65e5\u62a5-2025-07-29")))).build();
            List<List<String>> summaryTitle = List.of(List.of("\u9879\u76ee", "\u6570\u503c"));
            List<List<Object>> summary = List.of(List.of("\u5f53\u65e5\u4e8b\u4ef6\u603b\u6570", Integer.valueOf(538)), List.of("\u4e25\u91cd\u8def\u9762\u98a0\u7c38\u70b9\u6570\u91cf", Integer.valueOf(111)), List.of("\u6d89\u53ca\u9053\u8def\u603b\u6570", Integer.valueOf(200)));
            excelWriter.write(summaryTitle, sheet, summaryTable);
            excelWriter.write(summary, sheet, summaryTable);
            WriteTable timeRangeTable = ((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)EasyExcel.writerTable((Integer)1).needHead(Boolean.valueOf(true))).registerWriteHandler((WriteHandler)new OnceAbsoluteMergeStrategy(5, 5, 0, 1))).registerWriteHandler((WriteHandler)new SimpleRowHeightStyleStrategy(Short.valueOf((short)30), Short.valueOf((short)20)))).registerWriteHandler((WriteHandler)new SimpleColumnWidthStyleStrategy(Integer.valueOf(30)))).registerWriteHandler((WriteHandler)styleWriteHandler)).head(List.of(List.of("\u6309\u65f6\u95f4\u5206\u5e03")))).build();
            List<List<String>> timeRangeTitle = List.of(List.of("\u65f6\u95f4\u6bb5", "\u4e8b\u4ef6\u6570"));
            ArrayList<List<Object>> timeRangeContent = new ArrayList<List<Object>>();
            timeRangeContent.add(List.of("00:00-02:00", Integer.valueOf(12)));
            timeRangeContent.add(List.of("02:00-04:00", Integer.valueOf(5)));
            excelWriter.write(timeRangeTitle, sheet, timeRangeTable);
            excelWriter.write(timeRangeContent, sheet, timeRangeTable);
            WriteTable topRoadTable = ((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)EasyExcel.writerTable((Integer)2).needHead(Boolean.valueOf(true))).registerWriteHandler((WriteHandler)new OnceAbsoluteMergeStrategy(0, 0, 0, 1))).registerWriteHandler((WriteHandler)new SimpleRowHeightStyleStrategy(Short.valueOf((short)30), Short.valueOf((short)20)))).registerWriteHandler((WriteHandler)new SimpleColumnWidthStyleStrategy(Integer.valueOf(30)))).registerWriteHandler((WriteHandler)styleWriteHandler)).build();
            excelWriter.write(List.of(List.of("\u6309\u9053\u8def\u5206\u5e03Top10")), sheet, topRoadTable);
            List<List<String>> topRoadTitle = List.of(List.of("\u8def\u540d", "\u4e8b\u4ef6\u6570"));
            List<List<Object>> topRoadContent = List.of(List.of("\u4eac\u85cf\u9ad8\u901f", Integer.valueOf(99)), List.of("\u5e7f\u6df1\u9ad8\u901f", Integer.valueOf(88)));
            excelWriter.write(topRoadTitle, sheet, topRoadTable);
            excelWriter.write(topRoadContent, sheet, topRoadTable);
            WriteTable bumpLevelTable = ((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)EasyExcel.writerTable((Integer)3).needHead(Boolean.valueOf(true))).registerWriteHandler((WriteHandler)new OnceAbsoluteMergeStrategy(0, 0, 0, 1))).registerWriteHandler((WriteHandler)new SimpleRowHeightStyleStrategy(Short.valueOf((short)30), Short.valueOf((short)20)))).registerWriteHandler((WriteHandler)new SimpleColumnWidthStyleStrategy(Integer.valueOf(30)))).registerWriteHandler((WriteHandler)styleWriteHandler)).build();
            excelWriter.write(List.of(List.of("\u6309\u9053\u8def\u5206\u5e03Top10")), sheet, topRoadTable);
            List<List<String>> bumpLevelTitle = List.of(List.of("\u98a0\u7c38\u4e8b\u4ef6\u7a0b\u5ea6", "\u5360\u6bd4"));
            List<List<Object>> bumpLevelContent = List.of(List.of("\u8f7b\u5ea6\u98a0\u7c38\u70b9", Integer.valueOf(99)), List.of("\u4e2d\u5ea6\u98a0\u7c38\u70b9", Integer.valueOf(88)), List.of("\u91cd\u5ea6\u98a0\u7c38\u70b9", Integer.valueOf(88)));
            excelWriter.write(bumpLevelTitle, sheet, bumpLevelTable);
            excelWriter.write(bumpLevelContent, sheet, bumpLevelTable);
            WriteTable eventTypeTable = ((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)EasyExcel.writerTable((Integer)4).needHead(Boolean.valueOf(true))).registerWriteHandler((WriteHandler)new OnceAbsoluteMergeStrategy(0, 0, 0, 1))).registerWriteHandler((WriteHandler)new SimpleRowHeightStyleStrategy(Short.valueOf((short)30), Short.valueOf((short)20)))).registerWriteHandler((WriteHandler)new SimpleColumnWidthStyleStrategy(Integer.valueOf(30)))).registerWriteHandler((WriteHandler)styleWriteHandler)).build();
            excelWriter.write(List.of(List.of("\u6309\u9053\u8def\u5206\u5e03Top10")), sheet, topRoadTable);
            List<List<String>> eventTypTitle = List.of(List.of("\u4e8b\u4ef6\u7c7b\u578b", "\u5360\u6bd4"));
            List<List<Object>> eventTypContent = List.of(List.of("\u8def\u9762\u98a0\u7c38", Integer.valueOf(99)), List.of("\u8def\u9762\u6e7f\u6ed1", Integer.valueOf(88)), List.of("\u8def\u9762\u79ef\u6c34", Integer.valueOf(88)));
            excelWriter.write(bumpLevelTitle, sheet, bumpLevelTable);
            excelWriter.write(bumpLevelContent, sheet, bumpLevelTable);
            excelWriter.finish();
        }
    }

    public ReportController() {
    }

    static {
        WriteCellStyle headCellStyle = new WriteCellStyle();
        headCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        WriteFont headFont = new WriteFont();
        headFont.setBold(Boolean.valueOf(true));
        headCellStyle.setWriteFont(headFont);
        headCellStyle.setBorderTop(BorderStyle.THIN);
        headCellStyle.setBorderBottom(BorderStyle.THIN);
        headCellStyle.setBorderLeft(BorderStyle.THIN);
        headCellStyle.setBorderRight(BorderStyle.THIN);
        headCellStyle.setFillForegroundColor(Short.valueOf(IndexedColors.LIGHT_GREEN.getIndex()));
        headCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        WriteCellStyle contentCellStyle = new WriteCellStyle();
        contentCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentCellStyle.setBorderTop(BorderStyle.THIN);
        contentCellStyle.setBorderBottom(BorderStyle.THIN);
        contentCellStyle.setBorderLeft(BorderStyle.THIN);
        contentCellStyle.setBorderRight(BorderStyle.THIN);
        styleWriteHandler = new HorizontalCellStyleStrategy(headCellStyle, contentCellStyle);
    }
}

