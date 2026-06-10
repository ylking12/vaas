/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.controller.web.LocationController
 *  com.etas.vaas.backend.service.web.LocationService
 *  com.etas.vaas.backend.vo.OnlineVehicle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.etas.vaas.backend.controller.web;

import com.etas.vaas.backend.service.web.LocationService;
import com.etas.vaas.backend.vo.OnlineVehicle;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class LocationController {
    private static final Logger log = LoggerFactory.getLogger(LocationController.class);
    private final LocationService locationService;

    @GetMapping(value={"location"})
    public ResponseEntity<Map<String, OnlineVehicle>> getVehicleLocation() {
        return ResponseEntity.ok(this.locationService.getOnlineVehicles());
    }

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }
}

