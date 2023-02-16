package com.example.demooutlookmail.outllookmail.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {
    private String responseMsg;
    private Integer responseCode;
    private List<MailModel> data;
}
