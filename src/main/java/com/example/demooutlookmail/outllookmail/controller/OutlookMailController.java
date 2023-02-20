package com.example.demooutlookmail.outllookmail.controller;

import com.example.demooutlookmail.outllookmail.Model.CommonResponse;
import com.example.demooutlookmail.outllookmail.Model.MailModel;
import com.example.demooutlookmail.outllookmail.config.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OutlookMailController {

    private final EmailService emailService;


    public OutlookMailController(EmailService emailService) {

        this.emailService = emailService;
    }

//    @PostMapping("/send-mail")
//    public ResponseEntity sendMail(@RequestBody MailModel mailModel) {
//        System.out.println(mailModel.toString());
//
//        try {
//            emailConfig.sendEmail(mailModel.getTo(), mailModel.getSubject(), mailModel.getMailBody());
//            return ResponseEntity.ok("Success");
//        } catch (
//                Exception e) {
//            System.out.println(e);
//            return ResponseEntity.internalServerError().body("Unsuccessful");
//        }
//
//    }


    @PostMapping("/send-mail1")
    public ResponseEntity sendMail1(@RequestPart(value = "file", required = false) MultipartFile file, @RequestPart("mailModel") MailModel mailModel) {
        System.out.println(mailModel.toString());

        System.out.println(file.getContentType());

        try {
            emailService.sendEmail(mailModel.getTo(), mailModel.getSubject(), (String) mailModel.getMailBody(),file);
            return ResponseEntity.ok("Success");
        } catch (
                Exception e) {
            System.out.println(e);
            return ResponseEntity.internalServerError().body("Unsuccessful");
        }

    }


    @GetMapping("/read-mail1")
    public CommonResponse readMail1() {
        CommonResponse commonResponse = new CommonResponse();
        try {

            commonResponse.setData(emailService.readMail());
            commonResponse.setResponseMsg("Success");
            commonResponse.setResponseCode(200);


        } catch (
                Exception e) {
            System.out.println(e);
            commonResponse.setResponseMsg("Failed. Eroor!!!");
            commonResponse.setResponseCode(500);

        }
        return commonResponse;
    }
}
