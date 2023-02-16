package com.example.demooutlookmail.outllookmail.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailModel {
    private String to;
    private String from;
    private String subject;
    private Object mailBody;
}


