/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.excel.EasyExcel
 *  com.alibaba.excel.ExcelWriter
 *  com.alibaba.excel.write.builder.ExcelWriterBuilder
 *  com.alibaba.excel.write.handler.WriteHandler
 *  com.alibaba.excel.write.metadata.WriteSheet
 *  com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy
 *  com.etas.vaas.backend.controller.web.ExportController
 *  com.etas.vaas.backend.dto.export.EventExcelTableDTO
 *  com.etas.vaas.backend.service.web.ExportService
 *  jakarta.annotation.Resource
 *  jakarta.servlet.ServletOutputStream
 *  jakarta.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.etas.vaas.backend.controller.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.etas.vaas.backend.dto.export.EventExcelTableDTO;
import com.etas.vaas.backend.service.web.ExportService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class ExportController {
    private static final Logger log = LoggerFactory.getLogger(ExportController.class);
    @Resource
    private ExportService exportService;

    @GetMapping(value={"/export/daily"})
    public void exportAsExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode("\u57ce\u5e02\u7ea7\u9053\u8def\u72b6\u6001\u611f\u77e5\u548c\u9884\u8b66\u7cfb\u7edf-\u6587\u4ef6\u5bfc\u51fa", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + fileName + ".xlsx");
        List eventToExcel = this.exportService.getEventByTimeRange();
        try (ServletOutputStream outputStream = response.getOutputStream();){
            ExcelWriter excelWriter = ((ExcelWriterBuilder)EasyExcel.write((OutputStream)outputStream, EventExcelTableDTO.class).registerWriteHandler((WriteHandler)new SimpleColumnWidthStyleStrategy(Integer.valueOf(20)))).build();
            WriteSheet sheet = EasyExcel.writerSheet((String)"sheet1").build();
            excelWriter.write((Collection)eventToExcel, sheet);
            excelWriter.finish();
        }
    }

    public ExportController() {
    }
}

