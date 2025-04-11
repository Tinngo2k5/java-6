package edu.poly.assignment_java5_pd11236.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import edu.poly.assignment_java5_pd11236.model.CartItemRequest;
import edu.poly.assignment_java5_pd11236.model.GioHang;
import edu.poly.assignment_java5_pd11236.model.GioHangChiTiet;
import edu.poly.assignment_java5_pd11236.model.GioHangChiTietId;
import edu.poly.assignment_java5_pd11236.model.SanPham;
import edu.poly.assignment_java5_pd11236.repository.GioHangChiTietRepository;
import edu.poly.assignment_java5_pd11236.repository.GioHangRepository;

import edu.poly.assignment_java5_pd11236.repository.UsersRepository;

@Service
public class GioHangChiTietService {
	@Autowired
	UsersRepository usersRepository;
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	CartService cartService;
	@Autowired
	GioHangRepository gioHangRepository;
	@Autowired
	GioHangChiTietRepository gioHangChiTietRepository;

	public void add(CartItemRequest itemRequest) throws IllegalArgumentException {
		// tìm giỏ hàng của người dùng
		GioHang gioHang = gioHangRepository.findByUsers_IdUser(itemRequest.getUserId());
		// tìm sản phẩm cần thêm vào giỏ hàng	
		SanPham sanPham = sanPhamService.getSanPhamById(itemRequest.getProductId());
		//kiểm tra san pham co ton tai trong gio hang chua
		Optional<GioHangChiTiet> gioHangChiTietOptional = gioHangChiTietRepository
				.findById(new GioHangChiTietId(gioHang.getIdGiohang(), sanPham.getIdSanpham()));
		//nếu sản phẩm đã tồn tại trong giỏ hàng thì cập nhật số lượng
		//nếu sản phẩm chưa tồn tại thì thêm mới vào giỏ hàng
		if (gioHangChiTietOptional.isPresent()) {
			GioHangChiTiet gioHangChiTiet = gioHangChiTietOptional.get();
			gioHangChiTiet.setSoluong(gioHangChiTiet.getSoluong() + itemRequest.getQuantity());
			gioHangChiTietRepository.save(gioHangChiTiet);
		//nếu số lượng sản phẩm trong giỏ hàng vượt quá số lượng sản phẩm trong kho
		//thì thông báo lỗi
		} else {
			GioHangChiTiet gioHangChiTiet = new GioHangChiTiet(
					new GioHangChiTietId(gioHang.getIdGiohang(), sanPham.getIdSanpham()), gioHang, sanPham,
					itemRequest.getQuantity());
			gioHangChiTietRepository.save(gioHangChiTiet);
		}
	}
	//cập nhật số lượng sản phẩm trong giỏ hàng
	//nếu số lượng sản phẩm = 0 thì xóa sản phẩm khỏi giỏ hàng
	//nếu số lượng sản phẩm > 0 thì cập nhật số lượng sản phẩm
	//nếu số lượng sản phẩm < 0 thì thông báo lỗi
	//nếu sản phẩm không tồn tại trong giỏ hàng thì thông báo lỗi
	public void update(CartItemRequest itemRequest, Integer cartId) throws IllegalArgumentException {
		GioHang gioHang = cartService.getCartById(cartId);
		SanPham sanPham = sanPhamService.getSanPhamById(itemRequest.getProductId());
		GioHangChiTiet gioHangChiTiet = gioHangChiTietRepository
				.findById(new GioHangChiTietId(gioHang.getIdGiohang(), sanPham.getIdSanpham()))
				.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));
		// cập nhập lưu và database
		gioHangChiTiet.setSoluong(itemRequest.getQuantity());
		gioHangChiTietRepository.save(gioHangChiTiet);
	}
	//xóa sản phẩm khỏi giỏ hàng
	//nếu sản phẩm không tồn tại trong giỏ hàng thì thông báo lỗi
	//nếu sản phẩm tồn tại trong giỏ hàng thì xóa sản phẩm khỏi giỏ hàng
	public void delete(Integer cartId, Integer productId) throws IllegalArgumentException {
		GioHang gioHang = cartService.getCartById(cartId);
		SanPham sanPham = sanPhamService.getSanPhamById(productId);
		GioHangChiTiet gioHangChiTiet = gioHangChiTietRepository
				.findById(new GioHangChiTietId(gioHang.getIdGiohang(), sanPham.getIdSanpham()))
				.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));
		gioHangChiTietRepository.delete(gioHangChiTiet);
	}

	public List<GioHangChiTiet> getAllByIdUser(String id) {
		return gioHangChiTietRepository.findByGioHang_Users_IdUser(id);
	}
}
