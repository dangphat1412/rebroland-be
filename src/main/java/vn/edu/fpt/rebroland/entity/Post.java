package vn.edu.fpt.rebroland.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private int postId;
    @Column(length = 200)
    private String title;
    @Column(length = 2000)
    private String description;
    private float area;
    private boolean certification;
    private Date startDate;
    private Date transactionStartDate;
    private Date transactionEndDate;
    private Long price;
    @Column(length = 1000)
    private String additionalDescription;
    private String contactName;
    private String contactPhone;
    private String contactAddress;
    private String contactEmail;
    private String ward;
    private String district;
    private String province;
    private String address;

    private String thumbnail;

    private Integer originalPost;

    private boolean allowDerivative;

    private long spendMoney;

    private Date blockDate;

    private boolean block;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyType propertyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direction_id")
    private Direction direction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private UnitPrice unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "longevity_id")
    private Longevity longevity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Report> reports = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Contact> contacts = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Image> images = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Coordinate> coordinates = new HashSet<>();

//    public Post(int postId, String title, String description, float area, Date startDate, Long price, String ward, String district, String province, String address) {
//        this.postId = postId;
//        this.title = title;
//        this.description = description;
//        this.area = area;
//        this.startDate = startDate;
//        this.price = price;
//        this.ward = ward;
//        this.district = district;
//        this.province = province;
//        this.address = address;
//    }
}
