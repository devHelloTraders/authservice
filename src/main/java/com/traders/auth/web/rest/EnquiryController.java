package com.traders.auth.web.rest;

import com.traders.auth.exception.BadRequestAlertException;
import com.traders.auth.service.EnquiryService;
import com.traders.auth.service.dto.AddEnquiryRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/open/enquiry")
public class EnquiryController {
    private final EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addEnquiry(@RequestBody AddEnquiryRequest enquiry) {
        if(enquiry==null)
            throw new BadRequestAlertException("Enquiry can not be null","Enquiry Service","Enquiry can not be null");
        enquiryService.addEnquiry(enquiry);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }
}
