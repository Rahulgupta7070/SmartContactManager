package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class forgotController {

    Random random = new Random(); // No fixed seed

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping("/forgot")
    public String openEmail() {
        return "forgot_email_form";
    }

    @RequestMapping("/sent-otp")
    public String sentOTP(@RequestParam("email") String email, RedirectAttributes redirectAttributes, HttpSession session) {

        int otp = 100000 + random.nextInt(900000); // 6-digit OTP

        // Email content
        String subject = "OTP From SCM";
        String message = "<div style='border:1px solid #e2e2e2; padding:20px; font-family:Arial, sans-serif; color:#333;'>"
                + "<h2 style='color:#2a9df4;'>Smart Contact Manager</h2>"
                + "<p>Hello,</p>"
                + "<p>Your One-Time Password (OTP) is:</p>"
                + "<h1 style='color:#ff5722;'>" + otp + "</h1>"
                + "<p>Please do not share this OTP with anyone.</p>"
                + "<p>Thank you,<br/>Smart Contact Manager Team</p>"
                + "</div>";

        boolean flag = this.emailService.sendEmail(subject, message, email);

        if(flag) {
            session.setAttribute("myotp", otp);
            session.setAttribute("email", email);

            // Store message in session
            session.setAttribute("message", new Message("OTP sent successfully! Check your email.", "success"));

            return "verify_otp"; // direct render page
        } else {
            session.setAttribute("message", new Message("Failed to send OTP. Check your email ID.", "danger"));
            return "forgot_email_form";
        }
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {

        int myOtp = (int) session.getAttribute("myotp");
        String email = (String) session.getAttribute("email");

        if(myOtp == otp) {
            User user = this.userRepository.getUserByUserName(email);
            if(user == null) {
            	
            	 session.setAttribute("message", new Message("User does not exist with this email!", "danger"));
                
                return "forgot_email_form";
            }
            return "password_change_form"; // OTP correct, go to password change
        } else {
        	 session.setAttribute("message", new Message("You have entered wrong OTP", "danger"));
           
            return "verify_otp";
        }
    }
    
    //change password
    @PostMapping("change-password")
    public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session, RedirectAttributes redirectAttributes ) {
    	
    	String email=(String)session.getAttribute("email");
    	User user =this.userRepository.getUserByUserName(email);
    	user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
    	this.userRepository.save(user);
    	
    	return "redirect:/signin?change=password changed successfully..";
    }
    
}
