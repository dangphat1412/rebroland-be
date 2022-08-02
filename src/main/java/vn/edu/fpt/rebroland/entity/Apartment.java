package vn.edu.fpt.rebroland.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "apartments")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apartment_id", nullable = false)
    private int id;
    private int numberOfBedroom;
    private int numberOfBathroom;
    private Integer floorNumber;
    private String roomNumber;
    private String barcode;
    private String buildingName;
    private String owner;
    private String ownerPhone;
    private int plotNumber;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;


}
