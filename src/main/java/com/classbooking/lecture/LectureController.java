package com.classbooking.lecture;

import com.classbooking.lecture.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;

    /**
     * 강의 생성
     */
    @PostMapping
    public ResponseEntity<LectureCreResponse> createLecture(
            @RequestHeader Long memberId,
            @Valid @RequestBody LectureCreRequest request) {
        LectureCreResponse response = lectureService.createLecture(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 강의 목록 조회 (상태 필터 가능)
     */
    @GetMapping("/list")
    public ResponseEntity<Page<LectureListResponse>> getLectures(
            @RequestParam(required = false) LectureStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<LectureListResponse> response = lectureService.getLectures(status, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * 강의 상세 조회
     */
    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureDetailResponse> getLectureDetail(
            @RequestHeader Long memberId,
            @PathVariable Long lectureId) {
        LectureDetailResponse response = lectureService.getLectureDetail(memberId, lectureId);
        return ResponseEntity.ok(response);
    }
}
