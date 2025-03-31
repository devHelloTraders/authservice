package com.traders.auth.service;

import com.google.common.base.Strings;
import com.traders.auth.domain.Enquiry;
import com.traders.auth.exception.BadRequestAlertException;
import com.traders.auth.repository.EnquiryRepository;
import com.traders.auth.service.dto.AddEnquiryRequest;
import com.traders.common.utils.DateTimeUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnquiryService {
    private final EnquiryRepository enquiryRepository;

    public EnquiryService(EnquiryRepository enquiryRepository) {
        this.enquiryRepository = enquiryRepository;
    }

    public void addEnquiry(AddEnquiryRequest addEnquiryRequest) {
        if(Strings.isNullOrEmpty(addEnquiryRequest.name()))
            throw new BadRequestAlertException("Name is required","Enquiry Service","Name is required");
        if(Strings.isNullOrEmpty(addEnquiryRequest.contactNo()))
            throw new BadRequestAlertException("Contact No is required","Enquiry Service","Contact No is required");
        if(Strings.isNullOrEmpty(addEnquiryRequest.message()))
            throw new BadRequestAlertException("Message is required","Enquiry Service","Message is required");
        Enquiry enquiry = new Enquiry();
        enquiry.setName(addEnquiryRequest.name());
        enquiry.setContactNo(addEnquiryRequest.contactNo());
        enquiry.setMessage(addEnquiryRequest.message());
        enquiry.setCreatedDatetime(DateTimeUtil.getCurrentDateTime());
        enquiryRepository.save(enquiry);
    }
}
