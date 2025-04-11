package edu.poly.assignment_java5_pd11236.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.poly.assignment_java5_pd11236.model.GioHang;
import edu.poly.assignment_java5_pd11236.repository.GioHangRepository;

@Service
public class CartService {
	@Autowired
	GioHangRepository gioHangRepository;

		//kiểm tra login
	public GioHang getCartById(Integer id) {
		return gioHangRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại!"));
	}
}
