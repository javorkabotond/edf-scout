package hu.javorkabotond.edfscout.controller;

import hu.javorkabotond.edfscout.model.EdfFileModel;
import hu.javorkabotond.edfscout.service.EdfFileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing EDF files.
 * <p>
 * This controller provides access to the list of available EDF files.
 * </p>
 */
@RestController
public class EdfFileController {
    private final EdfFileService edfFileService;

    public EdfFileController(EdfFileService edfFileService) {
        this.edfFileService = edfFileService;
    }

    /**
     * Returns a list of all available EDF files.
     *
     * @return a list of {@link EdfFileModel} objects containing metadata of the files
     */
    @GetMapping("/api/files")
    public List<EdfFileModel> getFiles() {
        return edfFileService.getEdfFiles();
    }
}
