package edu.poly.assignment_java5_pd11236.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import edu.poly.assignment_java5_pd11236.model.HoaDon;
import edu.poly.assignment_java5_pd11236.model.KhachHangVipDTO;


public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
	@Modifying
	@Transactional
	@Query("DELETE FROM HoaDon h WHERE h.users.idUser = :idUser")
	void deleteByUserId(@Param("idUser") String idUser);

	Page<HoaDon> findByUsers_idUser(String email, Pageable pageable);

	@Query("SELECT COUNT(h) FROM HoaDon h WHERE h.trangthai = 'received'")
	long countReceivedOrders();

	@Query("SELECT new edu.poly.assignment_java5_pd11236.model.KhachHangVipDTO(" + 
	"u.hoten, SUM(hdct.soluong * hdct.dongia), MIN(h.ngaytao), MAX(h.ngaytao)) " +
	"FROM HoaDon h " +
	"JOIN h.users u " +  
	"JOIN h.hoaDonChiTiets hdct " +
	"GROUP BY u.idUser, u.hoten " +
	"ORDER BY SUM(hdct.soluong * hdct.dongia) DESC")
	List<KhachHangVipDTO> findTop10KhachHangVip(Pageable pageable);


}
