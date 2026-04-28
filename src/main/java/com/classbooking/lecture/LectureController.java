package com.classbooking.lecture;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;

    @PostMapping
    public ResponseEntity<LectureCreResponse> createLecture(
            @RequestHeader Long memberId,
            @Valid @RequestBody LectureRequest request) {
        LectureCreResponse response = lectureService.createLecture(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
