package edu.poly.assignment_java5_pd11236.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.poly.assignment_java5_pd11236.model.SanPham;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
	
	Page<SanPham> findByTenSanphamContainingIgnoreCase(String keyword, Pageable pageable);

	Page<SanPham> findByLoai_IdLoai(Integer id, Pageable pageable);

	Page<SanPham> findByGiamgiaGreaterThan(Integer value, Pageable pageable);
	

	// Tính tổng số lượng sản phẩm đã bán theo id sản phẩm
	@Query("SELECT COALESCE(SUM(hdct.soluong), 0) " + "FROM HoaDonChiTiet hdct " + "JOIN hdct.hoaDon hd "
			+ "WHERE hd.trangthai = 'received' " + "AND hdct.sanPham.idSanpham = :idSanpham")
	int tongSoLuongSanPhamDaBanTheoId(@Param("idSanpham") Integer idSanpham);

	// Lấy danh sách sản phẩm theo id loại, giá từ minPrice đến maxPrice,
	// sắp xếp theo bán chạy nhất và mới nhất , giá thấp nhất
	// Để tránh lỗi SQL injection, sử dụng @Param để truyền tham số vào câu truy vấn
	// và sử dụng CASE WHEN để sắp xếp theo nhiều trường hợp
	@Query("SELECT s FROM SanPham s " 
			+ "WHERE (:idLoai IS NULL OR s.loai.idLoai = :idLoai) "
			+ "AND (:minPrice IS NULL OR s.gia >= :minPrice) " + "AND (:maxPrice IS NULL OR s.gia <= :maxPrice) "
			+ "ORDER BY "
			+ "CASE WHEN :sortBy = 'totalBuy-DESC' THEN (SELECT SUM(hdct.soluong) FROM HoaDonChiTiet hdct WHERE hdct.sanPham = s) END DESC, "
			+ "CASE WHEN :sortBy = 'createdAt-DESC' THEN s.ngaytao END DESC, "
			+ "CASE WHEN :sortBy = 'price-ASC' THEN s.gia END ASC")
	Page<SanPham> filterProducts(@Param("idLoai") Integer idLoai, @Param("minPrice") Integer minPrice,	
			@Param("maxPrice") Integer maxPrice, @Param("sortBy") String sortBy, Pageable pageable);

		//Thống kê doanh thu theo loại hàng với cấu trúc thông tin (Tổng doanh thu, Tổnsố lượng, Giá cao nhất, Giá thấp nhât, Giá trung bình)
	//  @Query("SELECT sp.loai.tenLoai, SUM(sp.gia * sp.soLuong) AS tongDoanhThu, " +
    //        "SUM(sp.soLuong) AS tongSoLuong, " +
    //        "MAX(sp.gia) AS giaCaoNhat, " +
    //        "MIN(sp.gia) AS giaThapNhat, " +
    //        "AVG(sp.gia) AS giaTrungBinh " +
    //        "FROM SanPham sp WHERE " +
    //        "(:loaiId IS NULL OR sp.loai.id = :loaiId) AND " +
    //        "(:startDate IS NULL OR sp.ngayTao >= :startDate) AND " +
    //        "(:endDate IS NULL OR sp.ngayTao <= :endDate) " +
    //        "GROUP BY sp.loai.tenLoai")
    // List<Object[]> getThongKeKinhDoanh(@Param("loaiId") Integer loaiId,
    //                                    @Param("startDate") LocalDate startDate,
    //                                    @Param("endDate") LocalDate endDate);
		@Query("SELECT sp.loai.tenLoai, " +
			"SUM(sp.gia * sp.soluong) AS tongDoanhThu, " +
			"SUM(sp.soluong) AS tongSoLuong, " +
			"MAX(sp.gia) AS giaCaoNhat, " +
			"MIN(sp.gia) AS giaThapNhat, " +
			"AVG(sp.gia) AS giaTrungBinh " +
			"FROM SanPham sp GROUP BY sp.loai.tenLoai")
		List<Object[]> getThongKeDoanhThuTheoLoai();
}
