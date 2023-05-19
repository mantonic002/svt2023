package ftn.socialnetwork.model.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
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
    private LocalDate creationDate;

    @OneToMany
    private List<Post> posts;

    @Column(nullable = false)
    private boolean isSuspended;

    @Column
    private String suspendedReason;

    public Group(Long id, String name, String descripiton, LocalDate creationDate, boolean isSuspended, String suspendedReason) {
        this.id = id;
        this.name = name;
        this.descripiton = descripiton;
        this.creationDate = creationDate;
        this.isSuspended = isSuspended;
        this.suspendedReason = suspendedReason;
    }

    public Group() {
        this.creationDate = LocalDate.now();
        this.isSuspended = false;
    }
}
