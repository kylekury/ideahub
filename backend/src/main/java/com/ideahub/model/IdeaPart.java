package com.ideahub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Set;

/**
 * 
 * An IdeaPart is exactly as described, it is a single part of an idea. 
 * It's effectively generic, but the IdeaPartType will contain metadata which
 * modifies how it's rendered to the user on the client.
 * 
 * @author kyle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = { "id" })
@ToString(exclude = { "ideaPartType", "ideaPartSuggestions" })
@Entity
@Table(name = "idea_part")
@DynamicUpdate
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdeaPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @JsonIgnore
    private Long id;
    
    // I think we need to reference just the id here instead of the object
    // as we'd run into a double-binding issue in a previous project
    @Column(name = "user_id", nullable = false)
    private long userId;
    
    // I think we need to reference just the id here instead of the object
    // as we'd run into a double-binding issue in a previous project
    @Column(name = "idea_id", nullable = false)
    private long ideaId;
    
    @ManyToOne
    @JoinColumn(name = "idea_part_type_id")
    private IdeaPartType ideaPartType;
    
//    @Column(name = "idea_part_type_id", nullable = false)
//    private int ideaPartTypeId;
    
    @Column(name = "upvotes", nullable = false)
    private int upvotes;
    
    @Column(name = "downvotes", nullable = false)
    private int downvotes;
    
    @Column(name = "content", nullable = false)
    private String content;
    
    @Column(name = "justification", nullable = false)
    private String justification;
        
    @OneToMany(cascade = { CascadeType.ALL })
    @JoinColumn(name = "idea_part_id") // Which column in the referenced table will be joined
    private Set<IdeaPartSuggestion> ideaPartSuggestions;
}
