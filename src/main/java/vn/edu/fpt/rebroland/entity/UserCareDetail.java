package vn.edu.fpt.rebroland.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_care_details")
public class UserCareDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id", nullable = false)
    private int detailId;

    @Column(nullable = false)
    private Date dateCreate;

    @Column(nullable = false)
    private Date appointmentTime;

    @Column(nullable = false)
    private Float alertTime;

    @Column(length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    private Type type;

    private boolean status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_id", nullable = false)
    private UserCare userCare;
}
