package vn.edu.fpt.rebroland.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "average_rates")
public class AvgRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;

    private int roleId;

    private float avgRate;

    public AvgRate(int userId, int roleId, float avgRate) {
        this.userId = userId;
        this.roleId = roleId;
        this.avgRate = avgRate;
    }
}
