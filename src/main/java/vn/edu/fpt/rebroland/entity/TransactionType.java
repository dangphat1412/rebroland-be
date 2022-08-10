package vn.edu.fpt.rebroland.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction_types")
public class TransactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    private String name;

    @OneToMany(mappedBy = "transactionType", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transactions> transactions = new HashSet<>();
}
