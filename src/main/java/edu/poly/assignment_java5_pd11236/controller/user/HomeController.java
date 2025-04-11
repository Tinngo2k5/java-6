package edu.poly.assignment_java5_pd11236.controller.user;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import edu.poly.assignment_java5_pd11236.model.HoaDon;
import edu.poly.assignment_java5_pd11236.model.SanPham;
import edu.poly.assignment_java5_pd11236.model.Users;
import edu.poly.assignment_java5_pd11236.repository.HoaDonChiTietRepository;
import edu.poly.assignment_java5_pd11236.service.HoaDonService;
import edu.poly.assignment_java5_pd11236.service.LoaiService;
import edu.poly.assignment_java5_pd11236.service.SanPhamService;
import edu.poly.assignment_java5_pd11236.service.UserService;

@Controller
public class HomeController {
	@Autowired
	UserService userService;

	@Autowired
	LoaiService loaiService;

	@Autowired
	SanPhamService sanPhamService;

	@Autowired
	HoaDonChiTietRepository hoaDonChiTietService;

	@Autowired
	HttpSession session;

	@Autowired
	HoaDonService hoaDonService;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		model.addAttribute("sanphams", sanPhamService.getAllSanPham(0, 12));
		model.addAttribute("listSPGiamGia", sanPhamService.getAllSanPhamGiamGia(0, 12));

	  	Pageable top12 = PageRequest.of(0, 12);
    	model.addAttribute("listSPBanChay", hoaDonChiTietService.findTopSellingProducts(top12));
		//model.addAttribute("listSPBanChay", hoaDonChiTietService.findBySoLuongBanGreaterThan(0, 12));

		
		return "user/home";
	}

	@GetMapping("/loai-all")
	public String loaiAll(Model model) {
		model.addAttribute("loais", loaiService.getAllLoai(0, 100));
		return "user/categoryAll";
	}

	@GetMapping("/signin")
	public String signin(@ModelAttribute("user") Users user, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser != null) {
			return "redirect:/";
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/signin";
	}

@PostMapping("/signin")
public String login(@ModelAttribute("user") Users user, Model model) {
    try {
        Users authenticatedUser = userService.login(user); // Giả sử hàm login trả về đối tượng Users
        session.setAttribute("currentUser", authenticatedUser); // Lưu user vào session
        return "redirect:/";
    } catch (Exception e) {
        model.addAttribute("errorMessage", e.getMessage());
        return "user/signin";
    }
}


	@GetMapping("/signup")
	public String signup(@ModelAttribute("user") Users user, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser != null) {
			return "redirect:/";
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/signup";
	}

	@GetMapping("/signout")
	public String signout() {
		userService.logout();
		return "redirect:/";
	}

	@PostMapping("/signup")
	public String regiter(@ModelAttribute("user") Users user, Model model) {
		try {
			userService.register(user);
			model.addAttribute("successMessage", "Tạo tài khoản thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "user/signup";
	}

	@ResponseBody
	@GetMapping("/image/{filename:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable(name = "filename") String filename) {
		File file = new File("D:\\Java5\\img\\" + filename);
		if (!file.exists()) {
			throw new RuntimeException("File không tồn tại!");
		}

		UrlResource resource;
		try {
			resource = new UrlResource(file.toURI());
		} catch (MalformedURLException ex) {
			throw new RuntimeException("File không tồn tại!");
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
				.body(resource);
	}

	@GetMapping("/sanpham")
	public String sanpham(@RequestParam(name = "id") String id, Model model) {
		try {
			SanPham sanPham = sanPhamService.getSanPhamById(Integer.valueOf(id));
			model.addAttribute("sanpham", sanPham);
			model.addAttribute("sanphams", sanPhamService.getSanPhamByIdLoai(sanPham.getLoai().getIdLoai(), 0, 4)
					.filter(item -> item.getIdSanpham() != sanPham.getIdSanpham()));
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("luotMua", sanPhamService.getLuotMuaById(sanPham.getIdSanpham()));
		
			return "user/productDetail";
		} catch (Exception e) {
			return "redirect:/";
		}
	}

	@GetMapping("/order")
	public String order(@RequestParam(defaultValue = "0", name = "page") int page, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null) {
			return "redirect:/";
		}
		Page<HoaDon> hoaDonPage = hoaDonService.getAllHoaDonByIdUser(currentUser.getIdUser(), page, 4);
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		model.addAttribute("orders", hoaDonPage.getContent()); // Danh sách user
		model.addAttribute("currentPage", page); // Trang hiện tại
		model.addAttribute("totalPages", hoaDonPage.getTotalPages());
		return "user/orderView";
	}

	@GetMapping("/orderDetail")
	public String orderDetail(@RequestParam(name = "id") String id, Model model) {
		try {
			Users currentUser = (Users) session.getAttribute("currentUser");
			if (currentUser == null) {
				return "redirect:/";
			}
			AtomicReference<Double> tempPrice = new AtomicReference<>(0.0);
			List<SanPham> listSanPham = new ArrayList<>();
			HoaDon hoaDon = hoaDonService.getHoaDonById(Integer.valueOf(id));
			hoaDon.getHoaDonChiTiets().forEach(item -> {
				SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
				double price = (item.getGiamgia() == 0) ? sanPham.getGia() * item.getSoluong()
						: (sanPham.getGia() * (100 - item.getGiamgia()) / 100) * item.getSoluong();
				tempPrice.updateAndGet(v -> v + price);
				sanPham.setSoluong(item.getSoluong());
				sanPham.setGiamgia(item.getGiamgia());
				listSanPham.add(sanPham);
			});
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("tempPrice", tempPrice.get());
			model.addAttribute("listSanPham", listSanPham);
			model.addAttribute("deliveryPrice", hoaDon.getGiaohang().equals("Giao hàng nhanh") ? 50000 : 20000);
			model.addAttribute("order", hoaDon);
			return "user/orderDetailView";
		} catch (Exception e) {
			return "redirect:/";
		}
	}

	@PostMapping("/orderDetail")
	public String huyOrderDetail(@RequestParam(name = "id") String id, Model model) {
		try {
			Users currentUser = (Users) session.getAttribute("currentUser");
			if (currentUser == null) {
				return "redirect:/";
			}
			hoaDonService.cancelOrder(Integer.valueOf(id));
			return "redirect:/order";
		} catch (Exception e) {
			return "redirect:/";
		}
	}

	@GetMapping("/setting")
	public String setting(Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null) {
			return "redirect:/";
		}
		model.addAttribute("user", userService.getUserById(currentUser.getIdUser()));
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/settingView";
	}

	@PostMapping("/setting")
	public String setting(@RequestParam(name = "image", required = false) MultipartFile image,
			@ModelAttribute("user") Users updateUser, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		try {
			if (currentUser == null) {
				return "redirect:/";
			}
			model.addAttribute("user", userService.updateProfile(currentUser.getIdUser(), updateUser, image));
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("successMessage", "Cập nhật tài khoản thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "user/settingView";
	}

	@GetMapping("/changePassword")
	public String changePassword(Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null) {
			return "redirect:/";
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/changePasswordView";
	}

	@PostMapping("/changePassword")
	public String changePassword(@RequestParam(name = "currentPassword") String currentPassword,
			@RequestParam(name = "newPassword") String newPassword,
			@RequestParam(name = "newPasswordAgain") String newPasswordAgain, Model model) {
		Users currentUser = (Users) session.getAttribute("currentUser");
		if (currentUser == null) {
			return "redirect:/";
		}
		boolean currentPasswordEqualsUserPassword = currentPassword.equals(currentUser.getMatkhau());
		boolean newPasswordEqualsNewPasswordAgain = newPassword.equals(newPasswordAgain);
		if (currentPasswordEqualsUserPassword && newPasswordEqualsNewPasswordAgain) {
			userService.changePassword(currentUser.getIdUser(), newPassword);
			model.addAttribute("successMessage", "Đổi mật khẩu thành công!");
		} else {
			model.addAttribute("currentPassword", currentPassword);
			model.addAttribute("newPassword", newPassword);
			model.addAttribute("newPasswordAgain", newPasswordAgain);
			model.addAttribute("errorMessage", "Mật khẩu hoặc mật khẩu nhập lại không khớp!");
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/changePasswordView";
	}

	@GetMapping("/forgotPassword")
    public String showForgotPasswordForm() {
        return "user/forgotPassword"; // Trả về trang HTML nhập số điện thoại
    }

    @PostMapping("/forgotPassword")
    public String processForgotPassword(@RequestParam("sdt") String phoneNumber, Model model) {
        boolean isUserExist = userService.checkPhoneNumberExists(phoneNumber);

        if (!isUserExist) {
            model.addAttribute("errorMessage", "Số điện thoại không tồn tại trong hệ thống!");
            return "user/forgotPassword";
        }

        // Nếu tồn tại, gửi email chứa mật khẩu hiện tại
        try {
			userService.sendPasswordToEmail(phoneNumber);
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Lỗi khi gửi email: " + e.getMessage());
			return "user/forgotPassword";
		}
        model.addAttribute("successMessage", "Mật khẩu đã được gửi qua email của bạn!");
        
        return "user/forgotPassword";
    }
}
