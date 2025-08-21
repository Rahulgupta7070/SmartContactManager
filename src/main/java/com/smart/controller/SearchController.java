package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

//restcontroller yis liye use kiya gya hai ki body simepl return kr diya gya hai na ki view return krna hai 
@RestController
public class SearchController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	//Search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		
		User user = this.userRepository.getUserByUserName(principal.getName());
		//List<Contact> contacts=this.contactRepository.findByNameContainingAndUser(query,user);//yadi kewal name se seach krana hai tab 
		
		  // Name ya secondName dono check karenge
	    //List<Contact> contacts = this.contactRepository.findByUserAndNameContainingOrSecondNameContaining(user, query, query);

        // Sirf logged-in user ke contacts search honge (name + secondName me)
        List<Contact> contacts = this.contactRepository.searchContacts(user, query);

		return ResponseEntity.ok(contacts);
	}
}
