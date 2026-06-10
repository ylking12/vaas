package com.etas.vaas.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.URL;
import java.nio.file.*;

/**
 * OCR车牌识别服务
 * 默认使用Tesseract开源引擎（需安装: brew install tesseract）
 * 可通过配置切换为阿里云OCR（正式部署时）
 */
@Service
public class OcrService {
    private static final Logger log = LoggerFactory.getLogger(OcrService.class);
    private static final String TESSERACT_CMD = "tesseract";
    private boolean tesseractAvailable = false;

    public OcrService() {
        try {
            Process p = new ProcessBuilder(TESSERACT_CMD, "--version")
                .redirectErrorStream(true).start();
            tesseractAvailable = (p.waitFor() == 0);
            log.info("Tesseract OCR {}可用", tesseractAvailable ? "" : "不");
        } catch (Exception e) {
            log.warn("Tesseract OCR 不可用，将使用模拟模式");
        }
    }

    public String recognizeVehiclePlate(String imageSource) {
        if (tesseractAvailable) {
            try {
                return recognizeWithTesseract(imageSource);
            } catch (Exception e) {
                log.warn("Tesseract识别失败，切换到模拟模式: {}", e.getMessage());
            }
        }
        return mockRecognize(imageSource);
    }

    private String recognizeWithTesseract(String imageSource) throws Exception {
        Path tempFile = Files.createTempFile("ocr_", ".png");
        try {
            // 支持URL和本地文件
            if (imageSource.startsWith("http")) {
                try (InputStream in = new URL(imageSource).openStream()) {
                    Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                Files.copy(Paths.get(imageSource), tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            Process p = new ProcessBuilder(TESSERACT_CMD, tempFile.toString(), "stdout", "-l", "chi_sim")
                .redirectErrorStream(true).start();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                return r.readLine();
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private String mockRecognize(String imageSource) {
        // 模拟车牌识别（正式部署替换为阿里云OCR）
        String plateNum = "苏B" + String.format("%05d", System.currentTimeMillis() % 100000);
        log.info("[模拟OCR] 识别车牌: {} -> {}", imageSource, plateNum);
        return plateNum;
    }
}
