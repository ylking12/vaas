/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.controller.vehicle.RearMirrorController
 *  com.etas.vaas.backend.service.vehicle.RearMirrorService
 *  com.etas.vaas.backend.vo.RearMirrorResp
 *  jakarta.validation.constraints.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.http.ResponseEntity
 *  org.springframework.validation.annotation.Validated
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 */
package com.etas.vaas.backend.controller.vehicle;

import com.etas.vaas.backend.service.vehicle.RearMirrorService;
import com.etas.vaas.backend.vo.RearMirrorResp;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@Validated
public class RearMirrorController {
    private static final Logger log = LoggerFactory.getLogger(RearMirrorController.class);
    private final RearMirrorService rearMirrorService;

    @GetMapping(value={"/rear-mirror/get-last24h-event"})
    public ResponseEntity<List<RearMirrorResp>> getRoadEventForMirror(@NotNull @RequestParam(value="minute") int minute) {
        log.debug("Entered getRoadEventForMirror");
        return ResponseEntity.ok(this.rearMirrorService.getEventForRearMirror(minute));
    }

    public RearMirrorController(RearMirrorService rearMirrorService) {
        this.rearMirrorService = rearMirrorService;
    }
}

