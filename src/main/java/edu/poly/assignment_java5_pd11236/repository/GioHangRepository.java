package edu.poly.assignment_java5_pd11236.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import edu.poly.assignment_java5_pd11236.model.GioHang;

public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
	@Modifying
	@Transactional
	@Query("DELETE FROM GioHang g WHERE g.users.idUser = :idUser")
	void deleteByUserId(@Param("idUser") String idUser);

	GioHang findByUsers_IdUser(String idUser);
}
