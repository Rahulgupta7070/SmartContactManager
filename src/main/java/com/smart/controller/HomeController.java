package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

/*import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;*/

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;




@Controller
public class HomeController {
	
	
	     //jach krne ke liya liya tha  
	
	/*@Autowired
	private UserRepository userRepository;
	
	
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		
		User user = new User();
		user.setName("rahul");
		user.setEmail("krrahul233@gamail.com");
		
		Contact contact =new Contact();
		
		user.getContacts().add(contact);
		
		userRepository.save(user);
		return "Working";
		
	}*/
	
	@Autowired
	private  BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	
	
	
	
	@RequestMapping("/")
	public String Home(Model model) {
		
		model.addAttribute("title","Home-Smart Contect manger");
		
		return "home";
	}

	
	
	
	/*@RequestMapping("/signup")
	public String signup(Model model) {
		
		model.addAttribute("title","Register -Smart Contect manger");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	//handler for register user
	@PostMapping("/do_register")
	public String registerUser(@ModelAttribute("user") User user, @RequestParam(value="agreement",defaultValue="false") boolean agreement,Model model,HttpSession session) {
		
		try {

			if(! agreement) {
				System.out.println("you have not agreed tearm and condition");
				throw new Exception("you have not agreed tearm and condition");
			}
			
			user.setRole("ROLE_USER");
		    user.setEnabled(true);
		    user.setImageUrl("default.png");
		    
		    
			
			System.out.println("Agreement:" + agreement);
	        System.out.print("USER:"+user);
	        
	        User result =this.userRepository.save(user);
	        
	        model.addAttribute("user", new User());
	        
	        session.setAttribute("message",new Message("Successfuly Registered !!" ,"alert-success"));
			//return "signup";
	        return "redirect:/signup";
	        
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message",new Message("Something went wrong!!" + e.getMessage(),"alert-danger"));
//			return "signup";
			  return "redirect:/signup";
		}
		
	}*/
	
	
	
	 @RequestMapping("/signup")
	    public String signup(Model model, HttpSession session) {
	        model.addAttribute("title", "Register - Smart Contact Manager");
	        model.addAttribute("user", new User());

	        // === START change: session message handling ===
	        Object message = session.getAttribute("message");
	        if (message != null) {
	            model.addAttribute("message", message);
	            session.removeAttribute("message");  // remove after adding to model
	        }
	        // === END change ===

	        return "signup";
	    }
	 
	 
	      //login page 
	 
	 @GetMapping("/signin")
	 public String login(Model model) {
	     model.addAttribute("title", "Login - Smart Contact Manager");
	     return "login"; // sirf login page show kare
	 }
	     

	    //handler for register user
	    @PostMapping("/do_register")
	    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
	                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
	                               Model model, HttpSession session) {

	        try {
	            if (!agreement) {
	                System.out.println("you have not agreed term and condition");
	              //  throw new Exception("you have not agreed term and condition");
	                
	                
	                // User data wapas bhejna  change 
	                model.addAttribute("user", user);
	                model.addAttribute("title", "Register - Smart Contact Manager");
	                
	                model.addAttribute("agreement", agreement); // checkbox ka value preserve karna
	                
	                session.setAttribute("message", new Message("You have not agreed to terms and conditions!", "alert-danger"));

	                
	            }
	            
	            
	            
	            //validation chek krne ke liye ye or @Valid  or BindingResult set krna kra upar public register wale me 
	            if(result1.hasErrors()) {
	            	
	            	//System.out.println("ERROER"+result1.toString());
	            	//model.addAttribute("user",user);
	            	
	            	
	               model.addAttribute("title", "Register - Smart Contact Manager");
	               model.addAttribute("agreement", agreement);//chekbox na kene ke bad bhi data 
	                  
	                  
	            	return "signup";
	            	
	            }

	            // save user
	        

	          

	            session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
	            model.addAttribute("user", new User());

	            //end validation

	            user.setRole("ROLE_USER");
	            user.setEnabled(true);
	            user.setImageUrl("default.png");
	            
	            user.setPassword(passwordEncoder.encode(user.getPassword()));

	            User result = this.userRepository.save(user);

	         // === CHANGE ADDED: send email after registration ===
	            String subject = "Welcome to Smart Contact Manager!";

	            // Yaha pe full HTML structure bna do 👇
	            String message = 
	                "<div style='border:1px solid #e2e2e2; padding:20px; font-family:Arial;'>"
	                + "<h2 style='color:#333;'>Welcome, " + user.getName() + " 👋</h2>"
	                + "<p style='font-size:15px; color:#555;'>"
	                + "Thanks for registering with <b>Smart Contact Manager</b>.<br>"
	                + "Your registered email: <b>" + user.getEmail() + "</b><br><br>"
	                + "We are excited to have you on board. Keep your account safe and enjoy our services!"
	                + "</p>"
	                + "<br>"
	                + "<a href='http://localhost:1010/' "
	                + "style='display:inline-block; padding:10px 20px; background:#4CAF50; color:white; text-decoration:none; border-radius:5px;'>"
	                + "Go to Dashboard</a>"
	                + "<br><br><hr>"
	                + "<small style='color:gray;'>This is a system-generated email. Do not reply.</small>"
	                + "</div>";

	            emailService.sendEmail(subject, message, user.getEmail());
	            // === END CHANGE ===
	            
	            

	            model.addAttribute("user", new User());

	            // === START change: use session to set message ===
	            session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
	            // === END change ===

	            return "redirect:/signup";

	        } catch (Exception e) {
	            e.printStackTrace();

	            model.addAttribute("user", user);
	            model.addAttribute("title", "Register - Smart Contact Manager");
	            
	            
	            model.addAttribute("agreement", agreement);//chekbox na kene ke bad bhi data 

	            // === START change: use session to set error message ===
	            session.setAttribute("message", new Message("Something went wrong!! " + e.getMessage(), "alert-danger"));
	            // === END change ===

	            return "redirect:/signup";
	        }
	    }
	    
	    //about page ke liye 

	        @GetMapping("/about")
	        public String aboutPage(Model model) {
	            model.addAttribute("title", "About Smart Contact Manager");
	            return "about"; // ye about.html ko render karega
	        }
	    

}
