package com.mobitel.ecl.service.impl;

import com.mobitel.ecl.dbConnection.DBConnection;
import com.mobitel.ecl.dto.ChannelInfoRequestDto;
import com.mobitel.ecl.dto.CommonResponseDto;
import com.mobitel.ecl.service.EclRestService;
import com.mobitel.ecl.util.InsertTable;
import com.mobitel.ecl.util.NotifyAdmin;
import com.mobitel.ecl.util.Validator;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

@Service
public class EclRestServiceImpl implements EclRestService {

    @Autowired
    private DBConnection dbc;

    @Autowired
    private Validator validator;

    @Override
    public CommonResponseDto insertInfo(ChannelInfoRequestDto channelInfoRequest, Logger logger) throws SQLException {
        CommonResponseDto response = new CommonResponseDto();
//        Validator validator = new Validator();
        response.setRespCode("1000");
        response.setRespDesc("Failure");

        logger.info("insertInfo | Request : " + channelInfoRequest.getReferenceNo() + " | " + channelInfoRequest.getContactNo() + " | " + channelInfoRequest.getDate() + " | "
                + channelInfoRequest.getTime() + " | " + channelInfoRequest.getTotalTransactionAmount() + " | " + channelInfoRequest.getMobitelCommission()
                + " | " + channelInfoRequest.getBranchName() + " | " + channelInfoRequest.getBranchCode() + " | " + channelInfoRequest.getUserId() + " | "
                + channelInfoRequest.getCustomerName() + " | " + channelInfoRequest.getNic() + " | " + channelInfoRequest.getPayType());

        response = validator.validateRequestParameters(channelInfoRequest, logger, response);
        if (response.getRespCode().equals("5555")) {
//            DBConnection dbc = new DBConnection();
            Connection conIfx = dbc.openConnection("Ifx", logger);
            if (conIfx != null) {
                InsertTable it = new InsertTable();
                String transactionType = validator.requestInOrOut(channelInfoRequest.getTotalTransactionAmount(), logger);
                float mobiComission = channelInfoRequest.getMobitelCommission();
                channelInfoRequest.setMobitelCommission(Math.abs(mobiComission));
                if (transactionType.equals("I")) {
                    if (validator.alreadySubmitted(channelInfoRequest.getReferenceNo(), conIfx, logger) == 0) {
                        String aimsLocation = validator.getAIMSLocation(channelInfoRequest.getUserId().trim(), logger);
                        logger.info("AIMS Location : " + aimsLocation);
                        if (aimsLocation == null || aimsLocation.trim().equals("") || (!aimsLocation.trim().equalsIgnoreCase(channelInfoRequest.getBranchCode().trim()))) {
                            logger.info("aimsLocation is null or empty or not equal to eclBranch : " + aimsLocation + " | " + channelInfoRequest.getBranchCode().trim());
                            response.setRespCode("1000");
                            response.setRespDesc("Failure: Location mismatch.");

                            logger.info("Location mismatch | refNo:" + channelInfoRequest.getReferenceNo() + " | eclBranch:" + channelInfoRequest.getBranchCode() + " | aimsLocation:" + aimsLocation);

                            NotifyAdmin na = new NotifyAdmin();
                            it.inserteChannelInfo(channelInfoRequest, "F", conIfx, logger, "Mismatch:" + aimsLocation, transactionType);
                        } else {
                            //insert to wasantha aiya's table
                            response = it.insertToOtherPayDetail(channelInfoRequest, conIfx, logger, transactionType);
                            if (response.getRespCode().equals("5000")) {
                                response.setRespCode("0000");
                                response.setRespDesc("Success");
                                it.inserteChannelInfo(channelInfoRequest, "S", conIfx, logger, "", transactionType);
                            } else {
                                response.setRespCode("1001");
                                response.setRespDesc("System error. Please try again");
                                it.inserteChannelInfo(channelInfoRequest, "F", conIfx, logger, "Insert fail", transactionType);
                            }
                        }
                    } else {
                        response.setRespCode("1000");
                        response.setRespDesc("Failure: This reference no is already submitted");
                    }
                } else if (transactionType.equals("O")) {
                    int requestDateStatus = validator.isRequestDateIsToday(channelInfoRequest.getReferenceNo(), conIfx, logger);
                    if (requestDateStatus == 1) {
                        float totalAmount = channelInfoRequest.getTotalTransactionAmount();
                        channelInfoRequest.setTotalTransactionAmount(Math.abs(totalAmount));
                        //insert to wasantha aiya's table
                        response = it.insertToOtherPayDetail(channelInfoRequest, conIfx, logger, transactionType);
                        if (response.getRespCode().equals("5000")) {
                            response.setRespCode("0000");
                            response.setRespDesc("Success");
                            it.inserteChannelInfo(channelInfoRequest, "S", conIfx, logger, "", transactionType);
                        } else {
                            response.setRespCode("1001");
                            response.setRespDesc("System error. Please try again");
                            it.inserteChannelInfo(channelInfoRequest, "F", conIfx, logger, "Insert fail", transactionType);
                        }
                    } else {
                        response.setRespCode("1000");
                        response.setRespDesc("Failure: This reference is not done today");
                    }
                }
                try {
                    conIfx.close();
                } catch (SQLException ex) {
                    logger.error("Ifx con close error: " + ex);
                }
            } else {
                response.setRespCode("1000");
                response.setRespDesc("Database connection issue occurred");
                logger.info("insertInfo | Database connection is null - Informix");
            }
        }

        return response;
    }
}
