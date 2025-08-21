package com.smart.dao;



import java.util.List;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer> {

	@Query("from Contact as c where c.user.id =:userId ")
	//CurrentPage -page
	//contact per page -5
	public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pagebale);
	
	//ye method yis liye banaya tha jab user  hme home par reccent 4 contact bana tha
	List<Contact> findTop4ByUserOrderByCIdDesc(User user);
	
	
	//search kre liye  findByNameContainingAndUser ye method findByName krega (Containing) mens name yadi ord rkhta hai ya letter and( User) ka use yis liye kiya gya hai ki user apna hi contact search kr ske 
	//public List<Contact> findByNameContainingAndUser(String name,User user); //yadi name se hi search kre tab 
	
	// Name ya secondName match ho aur user same ho  //yadi name se hi search kre taband second name se bhi  
	//List<Contact> findByUserAndNameContainingOrSecondNameContaining(User user, String name, String secondName);
	
	//  Corrected search query: user + (name ya secondName match)
    @Query("SELECT c FROM Contact c WHERE c.user = :user AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.secondName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Contact> searchContacts(@Param("user") User user, @Param("keyword") String keyword);

}
