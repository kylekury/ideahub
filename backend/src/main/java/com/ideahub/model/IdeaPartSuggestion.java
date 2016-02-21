package com.ideahub.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * An IdeaPartSuggestion is a suggested improvement to an idea part, from a collaborator on an idea.
 * 
 * @author kyle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = { "id" })
@ToString
@Entity
@Table(name = "idea_part_suggestion")
@DynamicUpdate(true)
@JsonInclude(Include.NON_NULL)
public class IdeaPartSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    // I think we need to reference just the id here instead of the object
    // as we'd run into a double-binding issue in a previous project
    @Column(name = "idea_id", nullable = false)
    private long ideaId;
    
    // I think we need to reference just the id here instead of the object
    // as we'd run into a double-binding issue in a previous project
    @Column(name = "idea_part_id", nullable = false)
    private long ideaPartId;

    // I think we need to reference just the id here instead of the object
    // as we'd run into a double-binding issue in a previous project
    @Column(name = "user_id", nullable = false)
    private long userId;
    
    @Column(name = "suggestion", nullable = false)
    private String suggestion;
    
    // Up/downvotes need to be Integer vs. int, so we can omit them from being updated
    
    @Column(name = "upvotes", nullable = false)
    private Integer upvotes;
    
    @Column(name = "downvotes", nullable = false)
    private Integer downvotes;
}
