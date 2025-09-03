package com.mobitel.ecl.dto;

import lombok.Data;

@Data
public class ChannelInfoRequestDto {
    private int referenceNo;
    private String contactNo;
    private String date;
    private String time;
    private float totalTransactionAmount;
    private float mobitelCommission;
    private String branchName;
    private String branchCode;
    private String userId;
    private String customerName;
    private String nic;
    private String payType;
}
