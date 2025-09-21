package hu.javorkabotond.edfscout.service;

import hu.javorkabotond.edfscout.model.EdfFileModel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import utils.EDFException;
import utils.EDFreader;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EdfFileService {

    @Value("${DATA_DIRECTORY:/tmp/edf}")
    private String DATA_DIRECTORY;

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

    EdfFileModel parseEdf(File file) {
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
                    .identifier(reader.getRecording())
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
