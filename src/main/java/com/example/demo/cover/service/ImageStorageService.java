package com.example.demo.cover.service;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * - Base64 이미지를 받아서 프로젝트 내부에 실제 이미지 파일로 저장하는 역할 전담
 */
@Service
public class ImageStorageService {

    // 정적 리소스 기본 경로 (브라우저에서 /img/** 로 접근 가능)
    private static final String BASE_DIR = "src/main/resources/static/img";

    /**
     * Base64 이미지를 로컬 파일로 저장하고,
     * 정적 리소스 URL(/img/...) 을 반환한다.
     *
     * @param folderName  하위 폴더명 (예: "book/1", "hangaang")
     * @param fileName    저장할 파일명 (예: "cover.png")
     * @param base64Image Base64 인코딩된 이미지 문자열
     * @return  브라우저에서 접근 가능한 URL (예: "/img/book/1/cover.png")
     */
    public String saveBase64Image(String folderName,
                                  String fileName,
                                  String base64Image) throws IOException {

        // 1) data:image/png;base64, 같은 prefix 제거
        String pureBase64 = stripBase64Prefix(base64Image);

        // 2) Base64 → byte[]
        byte[] imageBytes = Base64.getDecoder().decode(pureBase64);

        // 3) 저장할 디렉터리 경로 결정 (예: src/main/resources/static/img/book/1)
        Path dirPath = Paths.get(BASE_DIR, folderName);
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath); // 폴더 없으면 생성
        }

        // 4) 파일 전체 경로 (예: .../static/img/book/1/cover.png)
        Path filePath = dirPath.resolve(fileName);

        // 5) 파일로 쓰기
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(imageBytes);
        }

        // 6) 정적 리소스 기준 URL 반환 (스프링이 /static 아래를 / 로 서빙)
        return "/img/" + folderName + "/" + fileName;
    }

    // "data:image/png;base64,xxxx" → 뒤의 순수 base64만 추출
    private String stripBase64Prefix(String base64Image) {
        int commaIndex = base64Image.indexOf(',');
        if (commaIndex > 0) {
            return base64Image.substring(commaIndex + 1);
        }
        return base64Image;
    }
}
