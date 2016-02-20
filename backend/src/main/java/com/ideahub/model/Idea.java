package com.ideahub.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Set;

/**
 * 
 * And Idea is formed from several parts, and can be influenced by 
 * creator-chosen collaborators, whom can suggest improvements to the idea.
 * 
 * Links an owner to all of the pieces of an idea.
 *  
 * @author kyle
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = { "id" })
@ToString(exclude = { "ideaParts", "ideaCollaborators" })
@Entity
@Table(name = "idea")
@DynamicUpdate  
public class Idea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;
    
    // I think we need to reference just the id here instead of the object
    // as we'd run into a double-binding issue in a previous project
    @Column(name = "user_id", nullable = false)
    private long userId;
        
    @OneToMany(cascade = { CascadeType.ALL })
    @JoinColumn(name = "idea_id") // Which column in the referenced table will be joined
    private Set<IdeaPart> ideaParts;
    

    @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "idea")
    private Set<IdeaCollaborator> ideaCollaborators;
}
