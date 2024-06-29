package ftn.socialnetwork.indexmodel;

import ftn.socialnetwork.model.entity.GroupAdmin;
import ftn.socialnetwork.model.entity.GroupRequest;
import ftn.socialnetwork.model.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "group_index")
@Setting(settingPath = "/configuration/serbian-analyzer-config.json")
public class GroupIndex {

    @Id
    private Long id;

    @Field(type = FieldType.Text, store = true, name = "title")
    private String name;

    @Field(type = FieldType.Text, store = true, name = "description_sr", analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String descriptionSr;

//    @Field(type = FieldType.Text, store = true, name = "description_en", analyzer = "english", searchAnalyzer = "english")
//    private String descriptionEn;

    @Field(type = FieldType.Date, store = true, name = "creation_date")
    private LocalDate creationDate;

//    private List<Post> posts = new ArrayList<>();

//    private List<GroupAdmin> admins = new ArrayList<>();

//    private List<GroupRequest> groupRequests = new ArrayList<>();

    @Field(type = FieldType.Boolean, store = true, name = "suspended")
    private boolean isSuspended;

    @Field(type = FieldType.Text, store = true, name = "suspended_reason")
    private String suspendedReason;
}
