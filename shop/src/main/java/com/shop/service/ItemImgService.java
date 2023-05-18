package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {
    /*
     * 상품 이미지를 업로드하고, 상품 이미지 정보를 저장하는 클래스
     */
    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes()); // 사용자가 상품의 이미지를 등록했다면, 저장할 경로, 파일의 이름, 파일을 파일의 바이트 배열을 파일 업로드 파라미터로 uploadFile() 메서드를 호출한다. 호출 결과 로컬에 저장된 파일의 이름을 imgName 변수에 저장한다.
            imgUrl = "/images/item/" + imgName; // 저장한 상품 이미지를 불러올 경로를 설정한다. 외부 리소스를 불러오는 urlPatterns로 WebMvcConfig 클래스에서 '/images/**'를 설정했고, application.properties에서 설정한 uploadPath 경로인 'C:/shop/' 아래 item 폴더에 이미지를 저장하기 때문에, 상품 이미지를 불러오는 경로로 '/images/item'을 붙여준다.
        }

        // 상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        /*
         * imgName : 실제 로컬에 저장된 상품 이미지 파일의 이름
         * oriImgName : 업로드했던 상품 이미지 파일의 원래 이름
         * imgUrl : 업로드 결과 로컬에 저장된 상품 이미지 파일을 불러오는 경로
         */

        itemImgRepository.save(itemImg); // 입력받은 상품 이미지 정보를 저장한다.
    }
}
