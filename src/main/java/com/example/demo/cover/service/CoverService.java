package com.example.demo.cover.service;

import com.example.demo.cover.dto.CoverRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CoverService {

    private final ImageStorageService imageStorageService;
    // private final BookService bookService;  // 나중에 Book 엔티티 업데이트할 때 주입

    /**
     * 표지 생성 + 저장 전체 플로우 담당
     */
    public String createCover(CoverRequest request) throws IOException {

        Long bookId = request.getBookId();

        // 1) (옵션) GPT에 이미지 생성 요청 → base64Image 얻기
        //    지금은 이미 request.base64Image 로 들어왔다고 가정
        String base64Image = request.getBase64Image();

        // 2) 모듈을 통해 실제 파일 저장
        //    폴더 구조: /img/book/{bookId}/cover.png
        String folderName = "book/" + bookId;
        String fileName = "cover.png";

        String imageUrl = imageStorageService.saveBase64Image(folderName, fileName, base64Image);

        // 3) (옵션) Book 엔티티 coverImageUrl 업데이트
        // bookService.updateCoverImage(bookId, imageUrl);

        return imageUrl;
    }
}
