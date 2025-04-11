package edu.poly.assignment_java5_pd11236.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.poly.assignment_java5_pd11236.model.Users;

public interface UsersRepository extends JpaRepository<Users, String> {
	Optional<Users> findBySdt(String sdt);

	
	

}