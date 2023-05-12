package ftn.socialnetwork.model.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String descripiton;

    @Column(nullable = false)
    private Date creationDate;

    @Column(nullable = false)
    private boolean isSuspended;

    @Column
    private String suspendedReason;
}
