/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.dev33.satoken.annotation.SaCheckLogin
 *  com.etas.vaas.admin.ApiResponse
 *  com.etas.vaas.admin.config.DbInfoPrinter
 *  com.etas.vaas.admin.controller.AdminController
 *  com.etas.vaas.admin.dto.AddCarMappingRequest
 *  com.etas.vaas.admin.dto.GetCarMappingListRequest
 *  com.etas.vaas.admin.dto.GetCarMappingListResp
 *  com.etas.vaas.admin.dto.GetHeartbeatResp
 *  com.etas.vaas.admin.dto.LicensePlate
 *  com.etas.vaas.admin.dto.ResponseTemplate
 *  com.etas.vaas.admin.dto.UpdateCarMappingRequest
 *  com.etas.vaas.admin.enums.GeneralEnum
 *  com.etas.vaas.admin.service.AdminService
 *  com.etas.vaas.admin.service.OcrService
 *  com.etas.vaas.common.entity.BrandModel
 *  jakarta.annotation.Resource
 *  jakarta.validation.Valid
 *  jakarta.validation.constraints.NotBlank
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.DeleteMapping
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.PutMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 *  org.springframework.web.multipart.MultipartFile
 */
package com.etas.vaas.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.etas.vaas.admin.ApiResponse;
import com.etas.vaas.admin.config.DbInfoPrinter;
import com.etas.vaas.admin.dto.AddCarMappingRequest;
import com.etas.vaas.admin.dto.GetCarMappingListRequest;
import com.etas.vaas.admin.dto.GetCarMappingListResp;
import com.etas.vaas.admin.dto.GetHeartbeatResp;
import com.etas.vaas.admin.dto.LicensePlate;
import com.etas.vaas.admin.dto.ResponseTemplate;
import com.etas.vaas.admin.dto.UpdateCarMappingRequest;
import com.etas.vaas.admin.enums.GeneralEnum;
import com.etas.vaas.admin.service.AdminService;
import com.etas.vaas.admin.service.OcrService;
import com.etas.vaas.common.entity.BrandModel;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@SaCheckLogin
@Valid
@CrossOrigin
@RestController
@RequestMapping(value={"/admin"})
@SuppressWarnings({"unchecked"})
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    @Resource
    private AdminService adminService;
    @Resource
    private DbInfoPrinter dbInfoPrinter;
    @Resource
    private OcrService ocrService;

    @PostMapping(value={"/list"})
    public ResponseTemplate<GetCarMappingListResp> getFleetManagementList(@RequestBody GetCarMappingListRequest request) {
        GetCarMappingListResp response = this.adminService.doGetCarMappingList(request);
        return new ResponseTemplate(Integer.valueOf(202), "good", (Object)response);
    }

    @PutMapping(value={"/update"})
    public ResponseTemplate<Void> updateFleetManagement(@Valid @RequestBody UpdateCarMappingRequest request) {
        boolean result = this.adminService.doUpdateCarMapping(request);
        return result ? new ResponseTemplate((ApiResponse)GeneralEnum.SUCCESS) : new ResponseTemplate((ApiResponse)GeneralEnum.FAILED);
    }

    @DeleteMapping(value={"/delete"})
    public ResponseTemplate<Void> deleteFleetManagement(@NotBlank @RequestParam(value="deviceId") String deviceId) {
        boolean success = this.adminService.doDeleteFleetManagement(deviceId);
        return success ? new ResponseTemplate((ApiResponse)GeneralEnum.SUCCESS) : new ResponseTemplate((ApiResponse)GeneralEnum.FAILED);
    }

    @PostMapping(value={"/add"})
    public ResponseTemplate<Void> addFleetManagement(@Valid @RequestBody AddCarMappingRequest request) {
        boolean result = this.adminService.doAddFleetManagement(request);
        return result ? new ResponseTemplate((ApiResponse)GeneralEnum.SUCCESS) : new ResponseTemplate((ApiResponse)GeneralEnum.FAILED);
    }

    @GetMapping(value={"/db-name"})
    public ResponseTemplate<String> getDbName() {
        return new ResponseTemplate((ApiResponse)GeneralEnum.SUCCESS, (Object)this.dbInfoPrinter.getDbName());
    }

    @GetMapping(value={"/model-option"})
    public ResponseTemplate<List<BrandModel>> getModelOption() {
        List result = this.adminService.doGetAllModel();
        return new ResponseTemplate((ApiResponse)GeneralEnum.SUCCESS, (Object)result);
    }

    @PostMapping(value={"/ocr-plate"})
    public ResponseTemplate<LicensePlate> upload(@RequestParam(value="file") MultipartFile file) {
        try {
            LicensePlate plate = new LicensePlate();
            plate.setPlateString(this.ocrService.recognizeVehiclePlate(file.getOriginalFilename()));
            return new ResponseTemplate((ApiResponse)GeneralEnum.SUCCESS, (Object)plate);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseTemplate((ApiResponse)GeneralEnum.FAILED);
        }
    }

    @GetMapping(value={"/heartbeat"})
    public ResponseTemplate<GetHeartbeatResp> getHeartbeat() {
        GetHeartbeatResp resp = this.adminService.doGetHeartbeatInfo();
        return new ResponseTemplate((ApiResponse)GeneralEnum.SUCCESS, (Object)resp);
    }
}

