package com.mobitel.ecl.util;

import com.mobitel.ecl.dbConnection.DBConnection;
import com.mobitel.ecl.dto.ChannelInfoRequestDto;
import com.mobitel.ecl.dto.CommonResponseDto;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class Validator {
//    private static final Logger logger = LogManager.getLogger(Validator.class);
    @Autowired
    private DBConnection dbc;

    public CommonResponseDto validateRequestParameters(ChannelInfoRequestDto infoEntity, Logger logger, CommonResponseDto response) {
        String status = "Failure";
        String statusCode = "1000";

        if (infoEntity.getReferenceNo() == 0) {
            status = "Failure: Invalid reference number";
        } else if (infoEntity.getContactNo() == null || infoEntity.getContactNo().trim().equals("")) {
            status = "Failure: Contact number cannot be empty";
        } else if (infoEntity.getDate() == null || infoEntity.getDate().equals("")) {
            status = "Failure: Date cannot be empty";
        } else if (infoEntity.getTime() == null || infoEntity.getTime().trim().equals("")) {
            status = "Failure: Time cannot be empty";
        } else if (infoEntity.getTotalTransactionAmount() == 0) {
            status = "Failure: Invalid total trasaction amount";
        } else if (infoEntity.getMobitelCommission() == 0) {
            status = "Failure: Invalid mobitel commmission";
        } else if (infoEntity.getBranchName() == null || infoEntity.getBranchName().trim().equals("")) {
            status = "Failure: Branch name cannot be empty";
        } else if (infoEntity.getBranchCode() == null || infoEntity.getBranchCode().trim().equals("")) {
            status = "Failure: Branch code cannot be empty";
        } else if (infoEntity.getUserId() == null || infoEntity.getUserId().trim().equals("")) {
            status = "Failure: User id cannot be empty";
        } else if (infoEntity.getCustomerName() == null || infoEntity.getCustomerName().trim().equals("")) {
            status = "Failure: Customer name cannot be empty";
        } else if (infoEntity.getNic() == null || infoEntity.getNic().trim().equals("")) {
            status = "Failure: Nic cannot be empty";
        } else if (infoEntity.getPayType() == null || infoEntity.getPayType().trim().equals("")) {
            status = "Failure: Pay type cannot be empty";
        } else if (infoEntity.getPayType() != null && (!infoEntity.getPayType().trim().equals("CA")) && (!infoEntity.getPayType().trim().equals("CC"))) {
            status = "Failure: Invalid pay type";
        } else {
            String channelDate = infoEntity.getDate().trim();
            String channelTime = infoEntity.getTime().trim();
            String dateArray[] = channelDate.split("-");
            if (dateArray.length != 3 || dateArray[0].length() != 4 || dateArray[1].length() != 2 || dateArray[2].length() != 2) {
                status = "Failure: Date should be of YYYY-MM-DD format";
            } else {
                Date cDate = null;
                Date cTime = null;
                try {
                    cDate = new SimpleDateFormat("yyyy-MM-dd").parse(channelDate);
                    channelDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                    try {
                        cTime = new SimpleDateFormat("HH:mm:ss").parse(channelTime);
                        channelTime = new SimpleDateFormat("HH:mm:ss").format(cTime);
                        status = "Valid Request";
                        statusCode = "5555";
                    } catch (ParseException ex) {
                        status = "Failure: Time should be of hh:mm:ss format";
                        logger.error("Validator.validateRequestParameters | refNo:" + infoEntity.getReferenceNo() + " | time:" + channelTime + " | " + ex);
                    }
                } catch (ParseException ex) {
                    status = "Failure: Date should be of YYYY-MM-DD format";
                    logger.error("Validator.validateRequestParameters | refNo:" + infoEntity.getReferenceNo() + " | date:" + channelDate + " | " + ex);
                }
            }
        }

        response.setRespCode(statusCode);
        response.setRespDesc(status);
        return response;
    }

    public int alreadySubmitted(int refNo, Connection conIfx, Logger logger) {
        Statement st = null;
        int status = -1;

        String query = "SELECT COUNT(*) FROM ecl_echannel_info WHERE flag='S' AND ref_no=" + refNo;

        try {
            st = conIfx.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                status = rs.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error("Validator.alreadySubmitted Error: " + ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                logger.error("Validator.alreadySubmitted statement close Error: " + ex);
            }
        }

        return status;
    }

    public String getAIMSLocation(String userId, Logger logger) {
        //DBConnection dbc = new DBConnection();
        Connection conMySql = null;
        String mobisLocation = null;

        String query = "SELECT location_code FROM employee where emp_number='" + userId + "'";
        logger.info("getAIMSLocation | Query Executing: " + query);

        try {
            conMySql = dbc.openConnection("MySql", logger);
            if (conMySql != null) {
                Statement st = conMySql.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    mobisLocation = rs.getString("location_code");
                    logger.info("getAIMSLocation | Location: " + mobisLocation);
                }
            } else {
                logger.error("getAIMSLocation | Error: Connection is null - MySql");
            }

        } catch (SQLException ex) {
            logger.error("Validator.getAIMSLocation Error:" + ex);
        } finally {
            try {
                if (conMySql != null) {
                    conMySql.close();
                }
            } catch (SQLException ex) {
                logger.error("Validator.getAIMSLocation con close Error:" + ex);
                System.out.println(ex);
            }
        }
        return mobisLocation;
    }

    public int isRequestDateIsToday(int refNo, Connection conIfx, Logger logger) {
        Statement st = null;
        int status = -1;

        String query = "SELECT COUNT(*) FROM ecl_echannel_info WHERE flag='S' AND DATE(added_at)=today AND ref_no=" + refNo;

        try {
            st = conIfx.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                status = rs.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error("Validator.isRequestDateIsToday Error: " + ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                logger.error("Validator.isRequestDateIsToday statement close Error: " + ex);
            }
        }

        return status;
    }

    public String requestInOrOut(float totalAmount, Logger logger) {
        String transactionType = "";
        if (totalAmount > 0) {
            transactionType = "I";
        } else if (totalAmount < 0) {
            transactionType = "O";
        }

        return transactionType;
    }

}
