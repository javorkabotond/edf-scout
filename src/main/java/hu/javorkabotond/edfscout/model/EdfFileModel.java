package hu.javorkabotond.edfscout.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data model representing metadata of an EDF file.
 * <p>
 * Contains information about the file's validity, recording details, patient info,
 * number of channels, channel names and types, recording length, annotations, and
 * any parsing errors if the file could not be read correctly.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EdfFileModel {

    /** The name of the EDF file. */
    private String fileName;

    /** Indicates whether the EDF file was successfully parsed and is valid. */
    private boolean valid;

    /** Unique identifier for the recording, typically extracted from the EDF file. */
    private String identifier;

    /** Date and time when the recording started. */
    private LocalDateTime recordingDate;

    /** Name of the patient associated with the recording. */
    private String patientName;

    /** Total number of channels in the recording. */
    private int numberOfChannels;

    /** List of channel names and their types. */
    private List<String> channelNamesAndTypes;

    /** Duration of the recording in seconds. */
    private double recordingLengthSeconds;

    /** Number of annotations present in the EDF file. */
    private int numberOfAnnotations;

    /** Error message if the file could not be parsed; null if the file is valid. */
    private String errorMessage;
}
