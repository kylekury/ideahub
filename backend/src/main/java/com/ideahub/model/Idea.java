package com.ideahub.model;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@DynamicUpdate(true)
@JsonInclude(Include.NON_NULL)
public class Idea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    // I think we need to reference just the id here instead of the object
    // as we'd run into a double-binding issue in a previous project
    @Column(name = "user_id", nullable = false)
    private long userId;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "idea_id") // Which column in the referenced table will
                                  // be joined
    @MapKey(name = "ideaPartTypeId")
    private Map<Integer, IdeaPart> ideaParts;

    @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "idea")
    private Set<IdeaCollaborator> ideaCollaborators;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    private transient String name;
    
    private transient String elevatorPitch;
    
    private transient int votes;
    
    private transient int contributions;
    
    private transient String userName;
}