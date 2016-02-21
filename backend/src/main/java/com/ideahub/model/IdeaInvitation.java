package com.ideahub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mifmif.common.regex.Generex;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Date;

/**
 * 
 * Contains a developer-friendly name and metadata for the client for
 * each different idea part type.
 * 
 * @author massanori
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = { "id" })
@ToString
@Entity
@Table(name = "idea_invitation")
@DynamicUpdate
public class IdeaInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @JsonIgnore
    private int id;
    
    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;
    
    @Column(name = "invite_state", nullable = false)
    private boolean inviteState;

    @Column(name = "accepted_state", nullable = false)
    private boolean acceptedState;
    
    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    public static String generateTransactionId() {
        String regex = "[a-z0-9]{8}-([a-z0-9]{4}-){3}[a-z0-9]{12}";
        return new Generex(regex).random();
    }
}
