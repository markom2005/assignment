package com.mmarjanovic.assignment.controller;

import com.mmarjanovic.assignment.service.AssignmentService;
import com.mmarjanovic.assignment.service.ImageService;
import com.mmarjanovic.assignment.service.IngestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class SatelliteController {

    @Autowired AssignmentService assignmentService;

    @Autowired IngestService ingestService;

    @Autowired ImageService imageService;

    @RequestMapping(value = "/assignment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAssignment() {
        return ResponseEntity.ok(assignmentService.getAssignments());
    }

    @RequestMapping(value = "/ingest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity ingestText(@RequestParam("text") String text) throws IOException {
        return ResponseEntity.ok(ingestService.ingestMessage(text));
    }

    @RequestMapping(value = "/image/{imageName:.+}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> downloadImage(@PathVariable("imageName") String imageName) throws IOException {
        byte[] imageBytes = imageService.getImageBytes(imageName);

        if (imageBytes == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(imageBytes);
        }
    }
}