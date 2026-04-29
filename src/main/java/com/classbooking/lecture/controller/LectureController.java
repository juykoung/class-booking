package com.classbooking.lecture.controller;

import com.classbooking.lecture.dto.LectureCreRequest;
import com.classbooking.lecture.dto.LectureCreResponse;
import com.classbooking.lecture.dto.LectureDetailResponse;
import com.classbooking.lecture.dto.LectureListResponse;
import com.classbooking.lecture.dto.LectureStatus;
import com.classbooking.lecture.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lecture")
@RequiredArgsConstructor
@Tag(name = "Lecture", description = "강의 생성, 조회, 오픈, 마감 API")
public class LectureController {
    private final LectureService lectureService;

    @Operation(summary = "강의 생성", description = "강사가 새 강의를 생성합니다.")
    @PostMapping
    public ResponseEntity<LectureCreResponse> createLecture(
            @Parameter(description = "요청 회원 ID", example = "1")
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody LectureCreRequest request
    ) {
        LectureCreResponse response = lectureService.createLecture(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "강의 목록 조회", description = "강의 목록을 조회합니다. 상태 필터는 선택값입니다.")
    @GetMapping("/list")
    public ResponseEntity<Page<LectureListResponse>> getLectures(
            @Parameter(description = "강의 상태 필터", example = "OPEN")
            @RequestParam(required = false) LectureStatus status,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<LectureListResponse> response = lectureService.getLectures(status, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "강의 상세 조회", description = "강의 상세를 조회합니다.")
    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureDetailResponse> getLectureDetail(
            @Parameter(description = "강의 ID", example = "1")
            @PathVariable Long lectureId
    ) {
        LectureDetailResponse response = lectureService.getLectureDetail(lectureId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "강의 오픈", description = "강사가 본인 강의를 OPEN 상태로 변경합니다.")
    @PatchMapping("/{lectureId}/open")
    public ResponseEntity<Void> openLecture(
            @Parameter(description = "강사 회원 ID", example = "1")
            @RequestHeader("X-Member-Id") Long memberId,
            @Parameter(description = "강의 ID", example = "1")
            @PathVariable Long lectureId
    ) {
        lectureService.openLecture(memberId, lectureId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강의 마감", description = "강사가 본인 강의를 CLOSED 상태로 변경합니다.")
    @PatchMapping("/{lectureId}/close")
    public ResponseEntity<Void> closeLecture(
            @Parameter(description = "강사 회원 ID", example = "1")
            @RequestHeader("X-Member-Id") Long memberId,
            @Parameter(description = "강의 ID", example = "1")
            @PathVariable Long lectureId
    ) {
        lectureService.closeLecture(memberId, lectureId);
        return ResponseEntity.ok().build();
    }
}
