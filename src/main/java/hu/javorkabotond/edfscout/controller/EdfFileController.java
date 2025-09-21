package hu.javorkabotond.edfscout.controller;

import hu.javorkabotond.edfscout.model.EdfFileModel;
import hu.javorkabotond.edfscout.service.EdfFileService;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EdfFileController {
    private final EdfFileService edfFileService;

    public EdfFileController(EdfFileService edfFileService) {
        this.edfFileService = edfFileService;
    }

    @GetMapping("/api/files")
    public List<EdfFileModel> getFiles() {
        return edfFileService.getEdfFiles();
    }
}
