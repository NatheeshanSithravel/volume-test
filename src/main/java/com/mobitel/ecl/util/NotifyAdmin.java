package com.mobitel.ecl.util;

import com.mobitel.ecl.dto.ChannelInfoRequestDto;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class NotifyAdmin {
    public int sendEmail(String toEmailAddress, String content, Logger logger, Connection conIfx) {
        String sql = "";
        int x = -1;

        PreparedStatement pstmt = null;
        try {

            String subject = "eChannelling location mismatch";

            sql = "INSERT INTO email VALUES(0,'ecl@mobitel.lk','" + toEmailAddress
                    + "','" + subject + "',?,'N',NULL,'P','ECL',today,current,NULL,NULL,0)";
            pstmt = conIfx.prepareStatement(sql);
            pstmt.setString(1, content);

            x = pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("NotifyAdmin.sendEmail | query:" + sql + " | Error:" + e + "\n");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                logger.error("NotifyAdmin.sendEmail Con close Error" + ex);
            }
        }
        logger.info("NotifyAdmin.sendEmail | query:" + sql + " | status:" + x + " \n");
        return x;
    }

    public int sendSMS(String mobileNo, Logger logger, String msg, Connection conIfx) {
        int status = -1;
        Statement statement = null;

        String query = "INSERT INTO sms_notify(ref_no,sender_mob,receip_mob,message,req_by,req_date,"
                + "req_time,sms_stat,exe_date,exe_at,retry_stat) VALUES(0,'ECL','" + mobileNo + "','" + msg
                + "','ECL',today,current,'P',NULL,NULL,NULL)";

        try {
            statement = conIfx.createStatement();
            statement.execute("SET LOCK MODE TO WAIT");
            status = statement.executeUpdate(query);

        } catch (SQLException ex) {
            logger.error(ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                logger.error("NotifyAdmin.sendSMS con close error" + ex);
            }
        }
        logger.info("NotifyAdmin.sendSMS | mobile:" + mobileNo + " | msg:" + msg + " | status:" + status);
        return status;
    }

    public String[] getContactList(String contactType, Logger logger) {
        Properties eclProperties = PropertyLoader.getECLProperties(logger);
        String[] contactList = eclProperties.getProperty("ecl.admin." + contactType).trim().split("\\|");
        return contactList;
    }

    public void notifyAdmins(Connection conIfx, Logger logger, ChannelInfoRequestDto infoEntity, String mobisLocation) {
        String emailContent = "Hi,\n\nPlease note that eChannelling ref no: " + infoEntity.getReferenceNo()
                + " is failed due to location mismatch.\n\tTransaction amount: Rs " + infoEntity.getTotalTransactionAmount()
                + "\n\teChannelling location: " + infoEntity.getBranchCode() + "\n\tMobis location: " + mobisLocation
                + "\n\tUser ID: " + infoEntity.getUserId()+"\n\nThank you.";

        String smsContent = "eChannelling location mismatch. Ref no: " + infoEntity.getReferenceNo() + ". Transaction amount: Rs "
                + infoEntity.getTotalTransactionAmount() + ". eChannelling location: " + infoEntity.getBranchCode()
                + ". Mobis location: " + mobisLocation+ ". User ID: " + infoEntity.getUserId();

        String[] emailList = getContactList("emailList", logger);
        for (String emailAdd : emailList) {
            sendEmail(emailAdd, emailContent, logger, conIfx);
        }

        String[] mobileList = getContactList("mobileList", logger);
        for (String mobile : mobileList) {
            sendSMS(mobile, logger, smsContent, conIfx);
        }
    }
}
