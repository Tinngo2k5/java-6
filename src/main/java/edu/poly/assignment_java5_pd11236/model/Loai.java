package edu.poly.assignment_java5_pd11236.model;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "LOAI")
public class Loai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_loai", unique = true, nullable = false)
    private Integer idLoai;

    @Column(name = "ten_loai", nullable = false)
    private String tenLoai;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "loai")
    @ToString.Exclude
    private List<SanPham> sanPhams;
}
