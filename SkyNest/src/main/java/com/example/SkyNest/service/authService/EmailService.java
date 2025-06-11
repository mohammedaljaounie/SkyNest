package com.example.SkyNest.service.authService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOTP(String toEmail,String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mohammedsamiraljaouny@gmail.com");
        message.setTo(toEmail);
        message.setSubject("SkyNest");
        message.setText("SkyNest\n" +
                "\n" +
                "Email Verification and Account Creation\n" +
                "Hello,\n" +
                "\n" +
                "You have just requested a verification code for\t"+ toEmail+"\tThis unique code allows you to create a SkyNest account without a password.\n" +
                "\n" +otp+"\n\n"+
                "Please note that this code can only be used once and expires after 5 minutes. Do not share this code with anyone else.\n" +
                "\n" +
                "This code is confidential. Please keep it safe from unauthorized individuals.\n" +
                "\n" +
                "By logging in to your account or creating an account, you agree to our Terms and Conditions and Privacy Statement.\n" +
                "\n" +
                "Copyright © 1996–2025 SkyNest. All rights reserved. SkyNest. This email was sent by, Oosterdokskade 163, 1011 DL Amsterdam, The Netherlands.\n" +
                "\n" +
                "Privacy & Cookies | Customer Service");
        javaMailSender.send(message);
    }


    public String generateOtp(){
        int otpL = (int)(Math.random()*9000)+1000;
        return String.valueOf(otpL);
    }






}
