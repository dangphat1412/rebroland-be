package vn.edu.fpt.rebroland.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_cares")
public class UserCare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "care_id", nullable = false)
    private int careId;

    @Column(length = 50,nullable = false)
    private String fullName;
    @Column(length = 10,nullable = false)
    private String phone;

    private Date startDate;
    @Column(length = 50)
    private String email;

    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "userCare", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCareDetail> userCareDetails = new HashSet<>();
}
