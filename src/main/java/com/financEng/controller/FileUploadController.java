package com.financEng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
public class FileUploadController {

    private String filePath="./uploads";

    @GetMapping("/upload")
    public String fileUploadForm(Model model) {
        return "upload";
    }

    // Handling single file upload request
    @PostMapping("/singleFileUpload")
    public String singleFileUpload(@RequestParam("file") MultipartFile file, Model model)
            throws IOException {

        // Save file on system
        if (!file.getOriginalFilename().isEmpty()) {
            BufferedOutputStream outputStream = new BufferedOutputStream(
                    new FileOutputStream(
                            new File(filePath, file.getOriginalFilename())));
            outputStream.write(file.getBytes());
            outputStream.flush();
            outputStream.close();

            model.addAttribute("msg", "File uploaded successfully.");
        } else {
            model.addAttribute("msg", "Please select a valid file..");
        }

        return "upload";
    }
}