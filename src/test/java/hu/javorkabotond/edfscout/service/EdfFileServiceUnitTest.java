package hu.javorkabotond.edfscout.service;

import hu.javorkabotond.edfscout.model.EdfFileModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;


public class EdfFileServiceUnitTest {
    private EdfFileService edfFileService;
    private final List<File> tempDirs = new ArrayList<>();

    @BeforeEach
    void setUp() {
        edfFileService = Mockito.spy(new EdfFileService());
    }

    @AfterEach
    void cleanUp() {
        for (File dir : tempDirs) {
            if (dir.exists()) {
                dir.setReadable(true);
                for (File f : dir.listFiles()) {
                    f.delete();
                }
                dir.delete();
            }
        }
        tempDirs.clear();
    }
    private File createTempDir(String prefix) throws Exception {
        File dir = Files.createTempDirectory(prefix).toFile();
        tempDirs.add(dir);
        ReflectionTestUtils.setField(edfFileService, "DATA_DIRECTORY", dir.getAbsolutePath());
        return dir;
    }


    @Test
    void shouldThrowExceptionWhenDataDirectoryDoesNotExist() {
        ReflectionTestUtils.setField(edfFileService, "DATA_DIRECTORY", "/non/existing/path");

        IllegalStateException exception = assertThrows(IllegalStateException.class, edfFileService::getEdfFiles);
        assertTrue(
                exception.getMessage().contains("does not exist or is not a directory") ||
                        exception.getMessage().contains("Cannot read files from directory"),
                "Exception message: " + exception.getMessage()
        );
    }

    @Test
    void shouldReturnEmptyListWhenDataDirectoryIsEmpty() throws Exception {
        createTempDir("edf-empty-test");
        assertTrue(edfFileService.getEdfFiles().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenListFilesReturnsNull() throws Exception {
        File tempDir = createTempDir("edf-null-test");
        tempDir.setReadable(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, edfFileService::getEdfFiles);
        assertTrue(
                exception.getMessage().contains("Cannot read files from directory") ||
                        exception.getMessage().contains("does not exist or is not a directory"),
                "Exception message: " + exception.getMessage()
        );
    }

    @Test
    void shouldProcessOnlyEdfFilesInDirectory() throws Exception {
        File tempDir = createTempDir("edf-test");

        File edf1 = new File(tempDir, "file1.edf");
        File edf2 = new File(tempDir, "file2.EDF");
        File txt = new File(tempDir, "ignore.txt");

        edf1.createNewFile();
        edf2.createNewFile();
        txt.createNewFile();

        List<EdfFileModel> result = edfFileService.getEdfFiles();

        assertEquals(2, result.size());
        for (EdfFileModel model : result) {
            assertTrue(model.getFileName().endsWith(".edf") || model.getFileName().endsWith(".EDF"));
        }
    }

    @Test
    void shouldMarkModelAsInvalidForCorruptedEdfFile() throws Exception {
        File tempDir = createTempDir("edf-invalid-test");
        File invalidEdf = new File(tempDir, "invalid.edf");
        invalidEdf.createNewFile();

        List<EdfFileModel> result = edfFileService.getEdfFiles();

        assertEquals(1, result.size());
        EdfFileModel model = result.get(0);

        assertEquals("invalid.edf", model.getFileName());
        assertFalse(model.isValid());
        assertNotNull(model.getErrorMessage());
    }

    @Test
    void shouldReturnCorrectModelsForValidEdfFiles() throws Exception {
        File tempDir = createTempDir("edf-detailed-test");
        File edf1 = new File(tempDir, "file1.edf");
        File edf2 = new File(tempDir, "file2.EDF");
        edf1.createNewFile();
        edf2.createNewFile();

        EdfFileModel model1 = EdfFileModel.builder()
                .fileName("file1.edf")
                .valid(true)
                .identifier("REC001")
                .patientName("Alice")
                .recordingDate(LocalDateTime.of(2025, 9, 21, 10, 0, 0))
                .numberOfChannels(2)
                .channelNamesAndTypes(List.of("C3", "C4"))
                .recordingLengthSeconds(1800.0)
                .numberOfAnnotations(3)
                .build();

        EdfFileModel model2 = EdfFileModel.builder()
                .fileName("file2.EDF")
                .valid(true)
                .identifier("REC002")
                .patientName("Bob")
                .recordingDate(LocalDateTime.of(2025, 9, 21, 11, 0, 0))
                .numberOfChannels(3)
                .channelNamesAndTypes(List.of("F3", "F4", "O1"))
                .recordingLengthSeconds(3600.0)
                .numberOfAnnotations(5)
                .build();

        doReturn(model1).when(edfFileService).parseEdf(edf1);
        doReturn(model2).when(edfFileService).parseEdf(edf2);

        List<EdfFileModel> result = edfFileService.getEdfFiles();

        assertEquals(2, result.size(), "Két .edf fájl kell, hogy legyen a listában");

        EdfFileModel r1 = result.stream().filter(m -> m.getFileName().equals("file1.edf")).findFirst().orElse(null);
        assertNotNull(r1);
        assertTrue(r1.isValid());
        assertEquals("REC001", r1.getIdentifier());
        assertEquals("Alice", r1.getPatientName());
        assertEquals(LocalDateTime.of(2025, 9, 21, 10, 0, 0), r1.getRecordingDate());
        assertEquals(2, r1.getNumberOfChannels());
        assertEquals(List.of("C3", "C4"), r1.getChannelNamesAndTypes());
        assertEquals(1800.0, r1.getRecordingLengthSeconds());
        assertEquals(3, r1.getNumberOfAnnotations());

        EdfFileModel r2 = result.stream().filter(m -> m.getFileName().equals("file2.EDF")).findFirst().orElse(null);
        assertNotNull(r2);
        assertTrue(r2.isValid());
        assertEquals("REC002", r2.getIdentifier());
        assertEquals("Bob", r2.getPatientName());
        assertEquals(LocalDateTime.of(2025, 9, 21, 11, 0, 0), r2.getRecordingDate());
        assertEquals(3, r2.getNumberOfChannels());
        assertEquals(List.of("F3", "F4", "O1"), r2.getChannelNamesAndTypes());
        assertEquals(3600.0, r2.getRecordingLengthSeconds());
        assertEquals(5, r2.getNumberOfAnnotations());
    }
}
