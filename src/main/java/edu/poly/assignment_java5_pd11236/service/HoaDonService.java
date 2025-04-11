package edu.poly.assignment_java5_pd11236.service;

import java.util.Date;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import edu.poly.assignment_java5_pd11236.model.HoaDon;
import edu.poly.assignment_java5_pd11236.model.HoaDonChiTiet;
import edu.poly.assignment_java5_pd11236.model.HoaDonChiTietId;

import edu.poly.assignment_java5_pd11236.model.OrderRequest;
import edu.poly.assignment_java5_pd11236.model.SanPham;
import edu.poly.assignment_java5_pd11236.model.Users;
import edu.poly.assignment_java5_pd11236.model.KhachHangVipDTO;
import edu.poly.assignment_java5_pd11236.repository.HoaDonChiTietRepository;
import edu.poly.assignment_java5_pd11236.repository.HoaDonRepository;
import edu.poly.assignment_java5_pd11236.repository.SanPhamRepository;

@Service
public class HoaDonService {
	@Autowired
	UserService userService;
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	SanPhamRepository sanPhamRepository;
	@Autowired
	HoaDonRepository hoaDonRepository;
	@Autowired
	HoaDonChiTietRepository hoaDonChiTietRepository;
	@Autowired
	GioHangChiTietService gioHangChiTietService;

	public List<KhachHangVipDTO> getTop10KhachHangVip() {
		Pageable top10 = PageRequest.of(0, 10);
		return hoaDonRepository.findTop10KhachHangVip(top10);
	}
	

	// Thêm hóa đơn
	// Kiểm tra số lượng tồn kho của sản phẩm
	// Nếu số lượng tồn kho < số lượng sản phẩm trong giỏ hàng thì thông báo lỗi
	public void add(OrderRequest orderRequest) throws RuntimeException {
		Users users = userService.getUserById(orderRequest.getUserId());
		orderRequest.getOrderItems().forEach(item -> {
			SanPham sanPham = sanPhamService.getSanPhamById(item.getProductId());
			if (item.getQuantity() > sanPham.getSoluong()) {
				throw new RuntimeException("Số lượng tồn kho của sản phẩm " + sanPham.getTenSanpham());
			}
		});

		// Tạo hóa đơn
		// Lưu hóa đơn
		// Cập nhật số lượng tồn kho của sản phẩm
		// Xóa sản phẩm khỏi giỏ hàng

		HoaDon hoaDon = new HoaDon(users, new Date(), "ondelivery", orderRequest.getAddress(),
				// xác định trạng thái giao hàng
				orderRequest.getDeliveryMethod() == 1 ? "Giao hàng tiêu chuẩn" : "Giao hàng nhanh"); 
		hoaDonRepository.save(hoaDon);
	
		orderRequest.getOrderItems().forEach(item -> {
			// tìm sản phẩm cần thêm vào giỏ hàng
			SanPham sanPham = sanPhamService.getSanPhamById(item.getProductId());
			// tạo chi tiết hóa đơn
			HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet(	
					new HoaDonChiTietId(hoaDon.getIdHoadon(), sanPham.getIdSanpham()), hoaDon, sanPham,
					item.getQuantity(), sanPham.getGiamgia(), sanPham.getGia());
			hoaDonChiTietRepository.save(hoaDonChiTiet);

			sanPham.setSoluong(sanPham.getSoluong() - item.getQuantity());
			sanPhamRepository.save(sanPham);

			gioHangChiTietService.delete(orderRequest.getCartId(), item.getProductId());

		});
	}

	public Page<HoaDon> getAllHoaDonByIdUser(String email, int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit, Sort.by("ngaytao").descending());
		return hoaDonRepository.findByUsers_idUser(email, pageable);
	}

	public HoaDon getHoaDonById(Integer id) {
		return hoaDonRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Mã hóa đơn không tồn tại!"));
	}

	public void cancelOrder(Integer id) {
		HoaDon hoaDon = getHoaDonById(id);
		hoaDon.setTrangthai("cancel");
		hoaDonRepository.save(hoaDon);

		hoaDon.getHoaDonChiTiets().forEach(item -> {
			SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
			sanPham.setSoluong(sanPham.getSoluong() + item.getSoluong());
			sanPhamRepository.save(sanPham);
		});
	}

	public Page<HoaDon> getAllHoaDon(int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit, Sort.by("ngaytao").descending());
		return hoaDonRepository.findAll(pageable);
	}

	public String updateHoadon(Integer id, String action) throws IllegalArgumentException {
		HoaDon hoaDon = getHoaDonById(id);
		if ("CONFIRM".equals(action)) {
			hoaDon.setTrangthai("received");
			hoaDonRepository.save(hoaDon);
			return "Đã xác nhận đã giao đơn hàng #" + id + " thành công!";
		} else if ("CANCEL".equals(action)) {
			hoaDon.setTrangthai("cancel");
			hoaDonRepository.save(hoaDon);

			hoaDon.getHoaDonChiTiets().forEach(item -> {
				SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
				sanPham.setSoluong(sanPham.getSoluong() + item.getSoluong());
				sanPhamRepository.save(sanPham);
			});
			return "Đã hủy đơn hàng #" + id + " thành công!";
		} else {
			if (hoaDon.getTrangthai().equals("cancel")) {
				hoaDon.getHoaDonChiTiets().forEach(item -> {
					SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
					sanPham.setSoluong(sanPham.getSoluong() - item.getSoluong());
					sanPhamRepository.save(sanPham);
				});
			}
			hoaDon.setTrangthai("ondelivery");
			hoaDonRepository.save(hoaDon);
			return "Đã đặt lại trạng thái là đang giao hàng cho đơn hàng #" + id + " thành công!";
		}
	}

	public long countReceivedOrders() {
		return hoaDonRepository.countReceivedOrders();
	}
}
