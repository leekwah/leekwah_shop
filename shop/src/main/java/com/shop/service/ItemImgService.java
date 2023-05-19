package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

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

    /*
     * 상품 이미지를 수정하는 클래스
     */
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        if (!itemImgFile.isEmpty()) { // 상품 이미지를 수정한 경우 상품 이미지를 업데이트한다.
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId) // 상품 이미지 아이디를 이용하여 기존에 저장했던 상품 이미지 엔티티를 조회한다.
                    .orElseThrow(EntityNotFoundException::new);

            // 기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())) { // 기존에 등록된 상품 이미지 파일이 있을 경우 해당 파일을 삭제한다.
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes()); // 업데이트한 상품 이미지 파일을 업로드한다.
            String imgUrl = "/images/item" + imgName;

            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl); // 변경된 상품 이미지 정보를 세팅해준다. savedItemImg는 영속 상태이기 때문에, 등록과 다르게 변경하는 것만으로 변경 감지 기능이 동작하여 트랜잭션이 끝날 때 update 쿼리가 실행된다. 하지만, 이렇게 되기 위해서 중요한 것은 엔티티가 영속 상태여야한다는 것이다. 그렇지 않으면 itemImgRepository.save() 메서드를 호출해야한다.
        }
    }
}
