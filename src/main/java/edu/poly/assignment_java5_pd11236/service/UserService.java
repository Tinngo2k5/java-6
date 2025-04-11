package edu.poly.assignment_java5_pd11236.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import edu.poly.assignment_java5_pd11236.model.GioHang;

import edu.poly.assignment_java5_pd11236.model.Users;
import edu.poly.assignment_java5_pd11236.repository.GioHangChiTietRepository;
import edu.poly.assignment_java5_pd11236.repository.GioHangRepository;
import edu.poly.assignment_java5_pd11236.repository.HoaDonChiTietRepository;
import edu.poly.assignment_java5_pd11236.repository.HoaDonRepository;
import edu.poly.assignment_java5_pd11236.repository.UsersRepository;
import edu.poly.assignment_java5_pd11236.utils.ImageUtils;

@Service
public class UserService {
	@Autowired
	UsersRepository usersRepository;
	@Autowired
	private HttpSession session;
	@Autowired
	GioHangRepository gioHangRepository;
	@Autowired
	GioHangChiTietRepository gioHangChiTietRepository;
	@Autowired
	HoaDonChiTietRepository hoaDonChiTietRepository;
	@Autowired
	HoaDonRepository hoaDonRepository;

	@Autowired
	private EmailService emailService;

	// Số điện thoại
	public boolean checkPhoneNumberExists(String phoneNumber) {
		return usersRepository.findBySdt(phoneNumber).isPresent();
	}

	public void sendPasswordToEmail(String phoneNumber) throws MessagingException {
        Optional<Users> userOptional = usersRepository.findBySdt(phoneNumber);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            String email = user.getIdUser();
            String password = user.getMatkhau(); // Lấy mật khẩu hiện tại

            String subject = "Lấy lại mật khẩu của bạn";
            String message = "Mật khẩu hiện tại của bạn là: " + password;

            emailService.sendEmail(email, subject, message);
        }
    }
	// email
	public String forgotPassword(String username) {
        Optional<Users> userOptional = usersRepository.findBySdt(username);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            String email = user.getIdUser();
            String password = user.getMatkhau(); // Lấy mật khẩu hiện tại (hoặc reset)

            // Gửi email
            try {
                emailService.sendEmail(email, "Reset Password",
                        "Mật khẩu của bạn là: <b>" + password + "</b>");
                return "Mật khẩu đã được gửi về email của bạn.";
            } catch (Exception e) {
                return "Lỗi khi gửi email: " + e.getMessage();
            }

        } else {
            return "Không tìm thấy tài khoản!";
        }
    }

	public Users register(Users user) {
		Optional<Users> existingUserById = usersRepository.findById(user.getIdUser());
		if (existingUserById.isPresent()) {
			throw new IllegalArgumentException("Email người dùng đã tồn tại!");
		}

		Optional<Users> existingUserByPhone = usersRepository.findBySdt(user.getSdt());
		if (existingUserByPhone.isPresent()) {
			throw new IllegalArgumentException("Số điện thoại đã được sử dụng!");
		}
		user.setVaitro(false);
		usersRepository.save(user);

		GioHang gioHang = new GioHang();
		gioHang.setUsers(user);
		gioHangRepository.save(gioHang);

		return user;
	}

	public Users updateUser(String id, Users updatedUser) {
		Optional<Users> optionalUser = usersRepository.findById(id);
		if (optionalUser.isPresent()) {
			Users user = optionalUser.get();
			user.setHoten(updatedUser.getHoten());
			user.setSdt(updatedUser.getSdt());
			user.setHinh(updatedUser.getHinh());
			user.setVaitro(updatedUser.isVaitro());

			return usersRepository.save(user);
		} else {
			throw new RuntimeException("Không tìm thấy người dùng!");
		}
	}

	public Users changePassword(String id, String newPassword) {
		Optional<Users> optionalUser = usersRepository.findById(id);
		if (optionalUser.isPresent()) {
			Users user = optionalUser.get();
			user.setMatkhau(newPassword);
			session.setAttribute("currentUser", usersRepository.save(user));
			return user;
		} else {
			throw new RuntimeException("Không tìm thấy người dùng!");
		}
	}

	public Users updateProfile(String id, Users updatedUser, MultipartFile image) {
		Optional<Users> optionalUser = usersRepository.findById(id);
		if (optionalUser.isPresent()) {
			Users user = optionalUser.get();
			if (image.getSize() > 0) {
				if (Objects.nonNull(user.getHinh()) || user.getHinh() == "") {
					ImageUtils.delete(user.getHinh());
				}
				user.setHinh(ImageUtils.upload(image)
						.orElseThrow(() -> new RuntimeException("Có lỗi xảy ra trong quá trình tải ảnh")));
			}
			user.setHoten(updatedUser.getHoten());
			user.setSdt(updatedUser.getSdt());
			session.setAttribute("currentUser", usersRepository.save(user));
			return user;
		} else {
			throw new RuntimeException("Không tìm thấy người dùng!");
		}
	}

	public Users login(Users users) {
		Optional<Users> userOptional = usersRepository.findById(users.getIdUser());
		if (userOptional.isEmpty()) {
			throw new IllegalArgumentException("Tài khoản không tồn tại!");
		}

		Users user = userOptional.get();
		if (!user.getMatkhau().equals(users.getMatkhau())) {
			throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác!");
		}
		session.setAttribute("currentUser", user);
		return user;
	}

	public void logout() {
		session.invalidate();
	}

	public Page<Users> getAllUsers(int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit);
		return usersRepository.findAll(pageable);
	}

	public Users getUserById(String email) {
		return usersRepository.findById(email)
				.orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại!"));
	}

	public void deleteUser(String id) {
		if (!usersRepository.existsById(id)) {
			throw new RuntimeException("Người dùng không tồn tại!");
		}
		hoaDonChiTietRepository.deleteByUserId(id);
		hoaDonRepository.deleteByUserId(id);
		gioHangChiTietRepository.deleteByUserId(id);
		gioHangRepository.deleteByUserId(id);
		usersRepository.deleteById(id);
	}
	
}
