package edu.poly.assignment_java5_pd11236.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import edu.poly.assignment_java5_pd11236.model.GioHangChiTiet;
import edu.poly.assignment_java5_pd11236.model.GioHangChiTietId;

public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, GioHangChiTietId> {
	@Modifying
	@Transactional
	@Query("DELETE FROM GioHangChiTiet g WHERE g.gioHang.users.idUser = :idUser")
	void deleteByUserId(@Param("idUser") String idUser);
	
	List<GioHangChiTiet> findByGioHang_Users_IdUser(String id);
}
