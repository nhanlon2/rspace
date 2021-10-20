package com.researchspace.exercise.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleDetails {
    private String location;
    private String name;
    private String description;
    private String expiryDate;
    private SubSample[] subSamples;

    public void calculateLocation() {
        StringBuilder locationStringBuilder = new StringBuilder();
        for (SubSample subSample : subSamples) {
            for (Container container : subSample.getParentContainers()) {
                generateContainerLocation(container, locationStringBuilder);
            }
        }
        location = locationStringBuilder.toString();
    }

    private void generateContainerLocation(Container container, StringBuilder locationStringBuilder) {
        String containerLocation = null;
        if (container.getParentLocation() != null) {
            containerLocation = " " + container.getName() + " " + container.getDescription() + " " + container.getParentLocation().getId() + " " + container.getParentLocation().getCoordX() + " " + container.getParentLocation().getCoordY();
        } else {
            containerLocation = " " + container.getName() + " " + container.getDescription();
        }
        locationStringBuilder.append(containerLocation);
        for (Container parent : container.getParentContainers()) {
            generateContainerLocation(parent, locationStringBuilder);
        }
    }
}
