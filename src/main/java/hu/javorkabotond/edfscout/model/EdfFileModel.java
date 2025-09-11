package hu.javorkabotond.edfscout.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EdfFileModel {
    private String fileName;
    private boolean valid;
    private String identifier;
    private LocalDateTime recordingDate;
    private String patientName;
    private int numberOfChannels;
    private List<String> channelNamesAndTypes;
    private double recordingLengthSeconds;
    private int numberOfAnnotations;
    private String errorMessage;
}
