package edu.poly.assignment_java5_pd11236.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.assignment_java5_pd11236.model.SanPham;
import edu.poly.assignment_java5_pd11236.service.LoaiService;
import edu.poly.assignment_java5_pd11236.service.SanPhamService;

@Controller
public class SearchController {
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	LoaiService loaiService;
	private static final int PRODUCTS_PER_PAGE = 12;

	@GetMapping("/search")
	public String search(Model model, @RequestParam(name = "q", required = false) String query,
			@RequestParam(defaultValue = "0", name = "page") int page) {
		if (query != null && !query.trim().isEmpty()) {
			Page<SanPham> sanPhamPage = sanPhamService.searchByName(page, PRODUCTS_PER_PAGE, query);
			model.addAttribute("sanphams", sanPhamPage.getContent());
			model.addAttribute("totalProducts", sanPhamPage.getTotalElements());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", sanPhamPage.getTotalPages());
			model.addAttribute("query", query);
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			return "user/search";
		}
		return "redirect:/";
	}
}

