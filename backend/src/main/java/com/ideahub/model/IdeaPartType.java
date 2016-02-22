package com.ideahub.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

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
@Table(name = "idea_part_type")
@DynamicUpdate
public class IdeaPartType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "allow_multiple", nullable = false)
    private boolean allowMultiple;

    @Type(type = "com.ideahub.model.userType.IdeaPartTypeMetadataUserType")
    @Column(name = "metadata", nullable = false)
    private IdeaPartTypeMetadata metadata;
}
