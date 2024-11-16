package org.testprojects.task_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testprojects.task_service.service.ApiService;

@Slf4j
@RestController
public class ApiController {

    private final ApiService apiService;

    @Autowired
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping(value = "/api/fetch-api-data")
    public ResponseEntity<?> fetchApiData() {
        apiService.fetchAndLogData();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
