package edu.poly.assignment_java5_pd11236.repository;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import edu.poly.assignment_java5_pd11236.model.HoaDonChiTiet;
import edu.poly.assignment_java5_pd11236.model.HoaDonChiTietId;
import edu.poly.assignment_java5_pd11236.model.SanPham;


public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, HoaDonChiTietId>{
	@Modifying
	@Transactional
	@Query("DELETE FROM HoaDonChiTiet h WHERE h.hoaDon.users.idUser = :idUser")
	void deleteByUserId(@Param("idUser") String idUser);

	@Query("SELECT h.sanPham FROM HoaDonChiTiet h GROUP BY h.sanPham ORDER BY SUM(h.soluong) DESC")
    List<SanPham> findTopSellingProducts(Pageable pageable);
	

 

}
