package com.ideahub.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * Contains a developer-friendly name and metadata for the client for
 * each different idea part type.
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
@DynamicUpdate
public class IdeaPartType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @JsonIgnore
    private int id;
    
    @Column(name = "name", nullable = false)    
    private String name;
    
    @Type(type = "com.ideahub.model.userType.IdeaPartTypeMetadataUserType")
    @Column(name = "metadata", nullable = false)
    private String metadata;
}
