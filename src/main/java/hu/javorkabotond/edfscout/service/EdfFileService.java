package hu.javorkabotond.edfscout.service;

import hu.javorkabotond.edfscout.model.EdfFileModel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import hu.javorkabotond.edfscout.utils.EDFException;
import hu.javorkabotond.edfscout.utils.EDFreader;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling EDF files.
 * <p>
 * This service is responsible for reading EDF files from a specified data directory,
 * parsing them using {@link EDFreader}, and returning metadata as {@link EdfFileModel} objects.
 * </p>
 */
@Service
public class EdfFileService {

    /**
     * The directory where EDF files are stored.
     * Defaults to /tmp/edf if not specified in the application properties.
     */
    @Value("${DATA_DIRECTORY}")
    private String DATA_DIRECTORY;

    /**
     * Retrieves all EDF files from the data directory.
     *
     * @return a list of {@link EdfFileModel} objects representing each EDF file's metadata
     * @throws IllegalStateException if the directory does not exist, is not accessible, or cannot be read
     */
    public List<EdfFileModel> getEdfFiles() {
        File folder = new File(DATA_DIRECTORY);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalStateException(
                    "The specified data directory does not exist or is not a directory: " + DATA_DIRECTORY
            );
        }

        File[] files = folder.listFiles();
        if (files == null) {
            throw new IllegalStateException(
                    "Cannot read files from directory: " + DATA_DIRECTORY + ". " +
                            "It may not exist or is not accessible."
            );
        }

        return Arrays.stream(files)
                .filter(file -> file.getName().toLowerCase().endsWith(".edf"))
                .map(this::parseEdf)
                .collect(Collectors.toList());
    }

    /**
     * Parses a single EDF file and extracts its metadata.
     *
     * @param file the EDF file to parse
     * @return an {@link EdfFileModel} containing metadata about the file;
     *         if parsing fails, a model with valid=false and an error message
     */
    protected EdfFileModel parseEdf(File file) {
        try {
            EDFreader reader = new EDFreader(file.getAbsolutePath());

            List<String> channels = new ArrayList<>();
            for (int i = 0; i < reader.getNumSignals(); i++) {
                channels.add(reader.getSignalLabel(i));
            }

            LocalDateTime recordingDate = LocalDateTime.of(
                    reader.getStartDateYear(),
                    reader.getStartDateMonth(),
                    reader.getStartDateDay(),
                    reader.getStartTimeHour(),
                    reader.getStartTimeMinute(),
                    reader.getStartTimeSecond()
            );

            return EdfFileModel.builder()
                    .fileName(file.getName())
                    .valid(true)
                    .identifier(file.getName())
                    .recordingDate(recordingDate)
                    .patientName(reader.getPatientName())
                    .numberOfChannels(reader.getNumSignals())
                    .channelNamesAndTypes(channels)
                    .recordingLengthSeconds(
                            (double) reader.getFileDuration() / EDFreader.EDFLIB_TIME_DIMENSION
                    )
                    .numberOfAnnotations(reader.annotationslist.size())
                    .build();
        } catch (IOException | EDFException e) {
            return EdfFileModel.builder()
                    .fileName(file.getName())
                    .valid(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}
