package com.smart.controller;

import java.io.File; // ✅ Correct File import
import java.nio.file.Files; // ✅ For file copy
import java.nio.file.Path;  // ✅ Correct Path import
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ContactRepository contactRepository;

    //  Common Data har request me model me add karne ke liye
    @ModelAttribute
    public void addCommanData(Model model, Principal principal) {
        String userName = principal.getName(); // Logged-in user ka username
        System.out.println("USERNAME: " + userName);

        // DB se user object fetch
        User user = userRepository.getUserByUserName(userName);
        System.out.println("USER: " + user);

        model.addAttribute("user", user);
    }

    // ✅ Dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
    	
    	
        model.addAttribute("title", "User dashboard");
        
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        // Total contacts
        int totalContacts = user.getContacts().size();
        model.addAttribute("totalContacts", totalContacts);

        // Last added contact
        Contact lastContact = null;
        if (!user.getContacts().isEmpty()) {
            lastContact = user.getContacts()
                              .get(user.getContacts().size() - 1); // last contact
        }
        model.addAttribute("lastContact", lastContact);
        
        // ✅ Get last 4 recent contacts (sorted by id)
        List<Contact> recentContacts = user.getContacts()
                                           .stream()
                                           .sorted((c1, c2) -> Integer.compare(c2.getcId(), c1.getcId())) // desc order
                                           .limit(4) // only 4
                                           .toList();

        model.addAttribute("recentContacts", recentContacts);
        
        
        
        
        return "normal/user_dashboard";
    }

    // ✅ Add contact form open karne ka handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model, Principal principal) {
        model.addAttribute("title", "Add contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    // ✅ Contact form process karne ka handler
    @PostMapping("/process-contact")
    public String processConstant(
            @ModelAttribute Contact contact,
            @RequestParam("profileImage") MultipartFile file,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            
            
            // ✅ yaha date set karo
            contact.setCreatedAt(LocalDateTime.now());


            // ✅ File handling
            if (file.isEmpty()) {
                contact.setImage("default.png"); // Default image set
            } else {
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/Image").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            // ✅ Contact ko current user ke saath link karna
            contact.setUser(user);
            user.getContacts().add(contact);

            // ✅ User object save karna (cascade se contact bhi save ho jayega)
            this.userRepository.save(user);

            // ✅ message box ko show krna chate hai jab add contact ho jaye tab
            redirectAttributes.addFlashAttribute("message",
                    new Message("Your contact is added !! Add more...", "success"));

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message",
                    new Message("Something went wrong !! Try again..", "danger"));
        }

        // ✅ Redirect to form page
        return "redirect:/user/add-contact";
    }
    
    
    //Show contact
    @GetMapping("/show-contact/{page}")
    public String showContect(@PathVariable("page") Integer page, Model m,Principal principal) {
    	
    	m.addAttribute("title","Show user contacts");
    	
    	//contact ki list ko bheji jani 
    	
    	String userName=principal.getName();//mtlb email hi user ka userName
    	User user =this.userRepository.getUserByUserName(userName);
    	
    	PageRequest pageable =PageRequest.of(page, 4);
    	Page<Contact> contacts =this.contactRepository.findContactsByUser(user.getId(),pageable);//uspar contact kirepository bana kr dalia hai 

    	m.addAttribute("contacts",contacts);
    	m.addAttribute("currentPage",page);
    	m.addAttribute("totalPages" ,contacts.getTotalPages());
    	
    	
    	return "normal/show_contacts";
    }
   



  

  //Showing particular contact details
  @RequestMapping("/{cId}/contact")
  public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal) {
  	
  	System.out.println("CID" +cId);
  	
  	Optional<Contact> contactOptional =this.contactRepository.findById(cId);
  	Contact contact = contactOptional.get();
  	
  	//
  	String userName =principal.getName();
  	User user =this.userRepository.getUserByUserName(userName);
  	 // ✅ yaha date set karo
    
  	
  	if(user.getId()==contact.getUser().getId()) {
  		model.addAttribute("contact" ,contact);
  		model.addAttribute("title" , contact.getName());
  		
  	}
  	
  	return "normal/contact_detail";
  }



     //Delete contact handler
  /*  @GetMapping("/delete/{cid}")
   public String deleteContect(@PathVariable("cid") Integer cId,Model model,HttpSession session) {
	
	
	
	Contact contact =this.contactRepository.findById(cId).get();
	
	contact.setUser(null);
	
	this.contactRepository.delete(contact);
	
	session.setAttribute("message", new Message("Contact delete successfull...","success"));

	return "redirect:/user/show-contact/0";
   }*/
  
//Delete contact handler

  @GetMapping("/delete/{cid}")
  public String deleteContact(@PathVariable("cid") Integer cId,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
      try {
          String userName = principal.getName();
          User user = this.userRepository.getUserByUserName(userName);

          Contact contact = this.contactRepository.findById(cId)
                  .orElseThrow(() -> new RuntimeException("Contact not found"));

          // Ownership check
          if (user != null && contact.getUser() != null 
                  && user.getId() == contact.getUser().getId()) {
   
              this.contactRepository.delete(contact);// list se remove
              this.userRepository.save(user);  // user ko save karo
              redirectAttributes.addFlashAttribute("message", 
                  new Message("Contact deleted successfully!", "success"));
          } else {
              redirectAttributes.addFlashAttribute("message", 
                  new Message("You are not allowed to delete this contact!", "danger"));
          }
         
      } catch (Exception e) {
          redirectAttributes.addFlashAttribute("message", 
              new Message("Something went wrong while deleting!", "danger"));
      }

      return "redirect:/user/show-contact/0";
  }

  //open update form handler
  @PostMapping("/update-contact/{cid}")
  public String updateForm(@PathVariable("cid") Integer cid,Model m) {
	  
	  m.addAttribute("title","Update Contact");
	  
	  Contact contact =this.contactRepository.findById(cid).get();
	  
	  m.addAttribute("contact" , contact);
	  
	  return "normal/update_form";
  }
  
  
  //upadate contact handler
  @RequestMapping(value="/process-update",method=RequestMethod.POST)
  public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model m,  RedirectAttributes redirectAttributes,Principal principal) {
	  
	  try {
		  
		  //old contact details
		  Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();
		  
		  if(!file.isEmpty()) {
			  
			  //delete old photo
			  File deleteFile = new ClassPathResource("static/Image").getFile();
			  File file1=new File(deleteFile,oldcontactDetail.getImage());
			  file1.delete();
			  
			  //update new photo
			 
              File saveFile = new ClassPathResource("static/Image").getFile();
              Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
              Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			  
              contact.setImage(file.getOriginalFilename());
            
		  }else {
			  contact.setImage(oldcontactDetail.getImage());
			  
		  }
		  
		  User user =this.userRepository.getUserByUserName(principal.getName());
		  contact.setUser(user);
		  this.contactRepository.save(contact);
		  
		  //message jayega 
		  redirectAttributes.addFlashAttribute("message", 
                  new Message("Your contact has been updated successfully!", "success"));
          
		  
		  
	  }catch(Exception e) {
		  e.printStackTrace();
		  redirectAttributes.addFlashAttribute("message", 
		            new Message("Something went wrong while updating contact!", "danger"));
		  
	  }
	  //return "redirect:/user/"+contact.getcId()+"/contact";
	 return "redirect:/user/"+contact.getcId()+"/contact";//sir ka likha huaa 
	 // return "redirect:/user/show-contact/0";
  }
  
  
  
  //user profile handler
//  @GetMapping("/profile")
//  public String userProfile(Model model) {
//	  return "normal/profile";
//  }
  
  @GetMapping("/profile")
  public String userProfile(Model model, Principal principal) {
      User user = userRepository.getUserByUserName(principal.getName());
      model.addAttribute("user", user);
      
      int totalContacts = user.getContacts().size();
      model.addAttribute("totalContacts", totalContacts);
      
      Contact lastContact = null;
      if(!user.getContacts().isEmpty()) {
          lastContact = user.getContacts().get(user.getContacts().size()-1);
      }
      model.addAttribute("lastContact", lastContact);
      
      return "normal/profile";
  }

  
  @PostMapping("/update-profile")
  public String updateProfile(@RequestParam(value = "profileImage", required = false) MultipartFile file,
                              @RequestParam(value = "about", required = false) String about,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
      try {
          User user = userRepository.getUserByUserName(principal.getName());

          // ✅ Update About if provided
          if (about != null && !about.trim().isEmpty()) {
              user.setAbout(about);
          }

          // ✅ Update Profile Photo if provided
          if (file != null && !file.isEmpty()) {
              if (user.getImageUrl() != null && !user.getImageUrl().equals("default.png")) {
                  File oldFile = new ClassPathResource("static/Image").getFile();
                  File fileToDelete = new File(oldFile, user.getImageUrl());
                  fileToDelete.delete();
              }

              File saveDir = new ClassPathResource("static/Image").getFile();
              Path path = Paths.get(saveDir.getAbsolutePath() + File.separator + file.getOriginalFilename());
              Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

              user.setImageUrl(file.getOriginalFilename());
          }

          userRepository.save(user);
          redirectAttributes.addFlashAttribute("message", new Message("Profile updated successfully!", "success"));

      } catch (Exception e) {
          e.printStackTrace();
          redirectAttributes.addFlashAttribute("message", new Message("Something went wrong!", "danger"));
      }

      return "redirect:/user/profile";
  }
  
  
  //      Open settings handler 
  @GetMapping("/settings")
  public String openSetting() {
	  
	  return "normal/settings";
  }

  @PostMapping("/change-password")
  public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal ,RedirectAttributes redirectAttributes) {
	  
	  
	  //System.out.println("OLD PASSWORLD"+oldPassword);
	 // System.out.println("New PASSWORLD"+newPassword);
	  
	  String userName=principal.getName();
	  User currentUser=this.userRepository.getUserByUserName(userName);
	  //System.out.println(currentUser.getPassword());
	  
	  if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {//password match krega to if chalega
		  
		  //change the password
		  
		  currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));//password ko encode me change kr ke set kr rhe hai currentUser me 
		  this.userRepository.save(currentUser);//yaha userRepository me save kr de rhe hai 
		  redirectAttributes.addFlashAttribute("message", new Message("Your password is  successfully!", "success"));
		  
		  
	  }else {
		  
		  redirectAttributes.addFlashAttribute("message", new Message("Please enter correct password", "danger"));
		  return"redirect:/user/settings";//usi page pe redirect hoga 
	  }
	  
	  
	  
	  return"redirect:/user/index";
  }
  
  
  
}
