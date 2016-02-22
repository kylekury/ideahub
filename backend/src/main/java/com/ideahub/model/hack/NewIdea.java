package com.ideahub.model.hack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewIdea {
    private String name;
    private String elevatorPitch;
    private boolean isPrivate;
}
