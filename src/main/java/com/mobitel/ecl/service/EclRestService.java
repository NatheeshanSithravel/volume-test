package com.mobitel.ecl.service;

import com.mobitel.ecl.dto.ChannelInfoRequestDto;
import com.mobitel.ecl.dto.CommonResponseDto;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public interface EclRestService {
    CommonResponseDto insertInfo(ChannelInfoRequestDto channelInfoRequest, Logger logger) throws SQLException;
}
