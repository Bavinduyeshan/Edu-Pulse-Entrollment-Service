package com.Edupulse.EntrollmentService.service;

import com.Edupulse.EntrollmentService.model.dto.ClassResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "class-service", url = "${CLASS_SERVICE_URL:class-service-url}")
public interface ClassServiceClient {


    @GetMapping("/classes/{classId}")
    ClassResponse getClassById(@PathVariable("classId") Long classId);
}
