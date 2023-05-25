package com.shop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {
    /*
     * 파일을 업로드하는 메서드와 삭제하는 메서드로 파일을 처리하는 FileService 클래스이다.
     */
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {
        UUID uuid = UUID.randomUUID();
        // uploadPath = "file:///C:/shop/";

        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension; // UUID로 받은 값과 원래 파일의 이름의 확장자를 조합해서 저장될 파일의 이름을 만든다.
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl); // FileOutputStream 클래스는 바이트 단위의 출력을 내보내는 클래스이다. 생성자로 파일이 저장될 위치와 파일의 이름을 넘겨 파일에 쓸 파일 출력 스트림을 만든다.
        fos.write(fileData); // fileData를 파일 출력 스트림에 입력한다.
        fos.close();

        return savedFileName; // 업로드된 파일의 이름을 반환한다.
    }

    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath); // 파일이 저장된 경로를 이용하여 파일 객체를 생성한다.

        if (deleteFile.exists()) { // 해당 파일이 존재하면 파일을 삭제한다.
            deleteFile.delete();
            log.info("파일을 삭제하였습니다");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
