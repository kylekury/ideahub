package com.ideahub.model;

import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * An IdeaPart is exactly as described, it is a single part of an idea. It's effectively generic, but the IdeaPartType will contain metadata which modifies how
 * it's rendered to the user on the client.
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
    private Long id;

    // I think we need to reference just the id here instead of the object
    // as we'd run into a double-binding issue in a previous project
    @Column(name = "user_id", nullable = false)
    private long userId;

    // I think we need to reference just the id here instead of the object
    // as we'd run into a double-binding issue in a previous project
    @Column(name = "idea_id", nullable = false)
    private long ideaId;

    // We can leave this just as a reference because we're keeping the types as a in-memory cache
    @Column(name = "idea_part_type_id", nullable = false)
    private int ideaPartTypeId;

    // @Column(name = "idea_part_type_id", nullable = false)
    // private int ideaPartTypeId;

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
