package ftn.socialnetwork.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate;

    @ElementCollection
    @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "images")
    private List<String> imagePaths;
}
