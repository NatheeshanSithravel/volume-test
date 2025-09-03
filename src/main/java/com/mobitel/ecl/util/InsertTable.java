package com.mobitel.ecl.util;

import com.mobitel.ecl.dto.ChannelInfoRequestDto;
import com.mobitel.ecl.dto.CommonResponseDto;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertTable {
    public CommonResponseDto inserteChannelInfo(ChannelInfoRequestDto infoEntity, String flag, Connection conIfx, Logger logger, String reason, String transactionType) {
        CommonResponseDto cr = new CommonResponseDto();

        int refNo = infoEntity.getReferenceNo();
        String mobileNo = infoEntity.getContactNo().trim();
        String channelDate = infoEntity.getDate().trim();
        String channelTime = infoEntity.getTime().trim();
        float transactionAmount = infoEntity.getTotalTransactionAmount();
        float commision = infoEntity.getMobitelCommission();
        String branchName = infoEntity.getBranchName().trim();
        String branchCode = infoEntity.getBranchCode().trim();
        String userId = infoEntity.getUserId().trim();
        String customerName = infoEntity.getCustomerName().trim();
        String nic = infoEntity.getNic().trim();
        String payType = infoEntity.getPayType().trim();

        Statement statement = null;
        String returnStatus = "Success";
        String statusCode = "5000";

        int x = -1;

        String query = "INSERT INTO ecl_echannel_info(ref_no,contact_no,channel_date,channel_time,transaction_amount,"
                + "mobitel_commission,branch_name,branch_code,user_id,customer_name,nic,added_at,flag,transaction_type,pay_type,reason) VALUES ("
                + refNo + ",'" + mobileNo + "','" + channelDate + "','"+channelTime+"'," + transactionAmount + "," + commision + ",'" + branchName
                + "','" + branchCode + "','" + userId + "','" + customerName + "','" + nic + "',SYSDATE,'"+flag+"','"+transactionType+"','"+payType+"','"+reason+"')";
        logger.info(query);
        try {
//            conIfx = dbc.getIfxConnection();
            statement = conIfx.createStatement();
            statement.execute("SET LOCK MODE TO WAIT");
            x = statement.executeUpdate(query);

        } catch (SQLException ex) {
            logger.error(ex);
            returnStatus = "Failure : Error in SQL";
            statusCode = "1000";
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                logger.error(ex);
                returnStatus = "Failure : Connection Error";
                statusCode = "1000";
            }
        }
        logger.info("insert to ecl_echannel_info | refNo:"+refNo+" | flag:"+flag+" | insertStatus:"+x);
        cr.setRespCode(statusCode);
        cr.setRespDesc(returnStatus);
        return cr;
    }

    public CommonResponseDto insertToOtherPayDetail(ChannelInfoRequestDto infoEntity, Connection conIfx, Logger logger, String transactionType) {
        CommonResponseDto cr = new CommonResponseDto();

        float transactionAmount = infoEntity.getTotalTransactionAmount();
        String branchCode = infoEntity.getBranchCode().trim();
        String payType = infoEntity.getPayType().trim();
        int refNo = infoEntity.getReferenceNo();

        Statement statement = null;
        String returnStatus = "Success";
        String statusCode = "5000";

        int x = -1;

        String query = "INSERT INTO cpsm:other_pay_detail(tran_cat,tran_date,tran_time,tran_location,tran_pay_status,"
                + "tran_pay_type,tran_amount,tran_id) VALUES ('ECH',today,current,'" + branchCode + "','"+transactionType+"','"
                + payType + "'," + transactionAmount + ",'" + refNo + "')";
        logger.info(query);
        try {
//            conIfx = dbc.getIfxConnection();
            statement = conIfx.createStatement();
            statement.execute("SET LOCK MODE TO WAIT");
            x = statement.executeUpdate(query);

        } catch (SQLException ex) {
            logger.error(ex);
            returnStatus = "Failure : Error in SQL";
            statusCode = "1000";
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                logger.error(ex);
                returnStatus = "Failure : Connection Error";
                statusCode = "1000";
            }
        }
        logger.info("insert to cpsm:other_pay_detail | refNo:"+refNo+" | insertStatus:"+x);
        cr.setRespCode(statusCode);
        cr.setRespDesc(returnStatus);
        return cr;
    }
}
