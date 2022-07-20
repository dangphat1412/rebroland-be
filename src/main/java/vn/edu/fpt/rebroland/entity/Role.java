package vn.edu.fpt.rebroland.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role {
    @Id
    private int id;

    private String name;

//    @OneToMany(mappedBy = "role")
//    private Set<UserRole> userRoles;
}
