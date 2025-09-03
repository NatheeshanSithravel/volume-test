package com.mobitel.ecl.controller;

import com.mobitel.ecl.dto.ChannelInfoRequestDto;
import com.mobitel.ecl.dto.CommonResponseDto;
import com.mobitel.ecl.service.impl.EclRestServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("")
public class EclRestController {

    private static final Logger logger = LogManager.getLogger(EclRestController.class);

    @Autowired
    private EclRestServiceImpl eclService;

    @PostMapping("/update-ECL-transaction")
    public CommonResponseDto updateECLTransaction(@RequestBody ChannelInfoRequestDto channelInfoRequest) throws SQLException {
        CommonResponseDto response = new CommonResponseDto();
        logger.info("updateECLTransaction | Received Channel info details: " + channelInfoRequest.toString());
        response = eclService.insertInfo(channelInfoRequest, logger);
        logger.info("updateECLTransaction | Response: " + response.toString());
        return response;
    }
}



