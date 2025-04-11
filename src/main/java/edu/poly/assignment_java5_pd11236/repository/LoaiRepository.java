package edu.poly.assignment_java5_pd11236.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.poly.assignment_java5_pd11236.model.Loai;
import java.util.Optional;


public interface LoaiRepository extends JpaRepository<Loai, Integer>{
	Optional<Loai> findByTenLoai(String tenLoai);
}
