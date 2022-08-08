package vn.edu.fpt.rebroland.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
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

    @Column(name = "user_cared_id")
    private Integer userCaredId;

    private Date startDate;

    @Column(length = 200)
    private String summarize;

    private boolean status;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "post_cares",
            joinColumns = @JoinColumn(name = "care_id", referencedColumnName = "care_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    )
    private Set<Post> posts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "userCare", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCareDetail> userCareDetails = new HashSet<>();
}