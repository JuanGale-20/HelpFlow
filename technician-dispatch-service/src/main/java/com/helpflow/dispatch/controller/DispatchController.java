package com.helpflow.dispatch.controller;

import com.helpflow.dispatch.dto.DispatchRequest;
import com.helpflow.dispatch.dto.DispatchResponse;
import com.helpflow.dispatch.service.DispatchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping("/technician-dispatch")
    public DispatchResponse dispatch(@Valid @RequestBody DispatchRequest request) {
        return dispatchService.dispatch(request);
    }
}