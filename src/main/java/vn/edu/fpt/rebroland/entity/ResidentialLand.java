package vn.edu.fpt.rebroland.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "residential_lands")
public class ResidentialLand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "land_id", nullable = false)
    private int id;
    private String barcode;
    private Integer plotNumber;
    private float frontispiece;
    private String owner;

    private String ownerPhone;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;
}
