package com.ideahub.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadata for the frontend clients to help describe different idea types.
 *
 * @author kyle
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdeaPartTypeMetadata implements Serializable {
    private static final long serialVersionUID = 7804576875753054455L;
    
    private String nameText;
    
    private String justificationText;
}

