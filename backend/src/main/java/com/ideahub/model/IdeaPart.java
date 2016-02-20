package com.ideahub.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class IdeaPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @JsonIgnore
    private int id;
    
    // I think we need to reference just the id here instead of the object
    // as we'd run into a double-binding issue in a previous project
    @Column(name = "user_id", nullable = false)
    private int userId;
    
    // I think we need to reference just the id here instead of the object
    // as we'd run into a double-binding issue in a previous project
    @Column(name = "idea_id", nullable = false)
    private int idea_id;
    
    @OneToOne
    private IdeaPartType ideaPartType;
    
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
