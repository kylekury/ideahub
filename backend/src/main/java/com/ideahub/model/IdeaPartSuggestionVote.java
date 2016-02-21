package com.ideahub.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * Represents a user's vote on an idea part suggestion.
 * This is almost redundant, but we've been bitten before by trying to store votes for two things in one table.
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
@Table(name = "idea_part_suggestion_vote")
@DynamicUpdate(true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdeaPartSuggestionVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "idea_id", nullable = false)
    private long ideaId;
    
    @Column(name = "idea_part_id", nullable = false)
    private long ideaPartId;

    @Column(name = "idea_part_suggestion_id", nullable = false)
    private long ideaPartSuggestionId;

    @Column(name = "vote_count", nullable = false)
    private int voteCount;
}
