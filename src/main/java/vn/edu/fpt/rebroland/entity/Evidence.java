package vn.edu.fpt.rebroland.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "evidences")
public class Evidence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "image_link")
    private String image;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "detail_id")
    private ReportDetail reportDetail;
}
