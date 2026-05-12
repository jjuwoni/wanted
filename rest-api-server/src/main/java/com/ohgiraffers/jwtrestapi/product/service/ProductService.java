package com.ohgiraffers.jwtrestapi.product.service;

import com.ohgiraffers.jwtrestapi.common.Criteria;
import com.ohgiraffers.jwtrestapi.product.dto.ProductAndCategoryDTO;
import com.ohgiraffers.jwtrestapi.product.dto.ProductDTO;
import com.ohgiraffers.jwtrestapi.product.entity.Product;
import com.ohgiraffers.jwtrestapi.product.entity.ProductAndCategory;
import com.ohgiraffers.jwtrestapi.product.repository.ProductAndCategoryRepository;
import com.ohgiraffers.jwtrestapi.product.repository.ProductRepository;
import com.ohgiraffers.jwtrestapi.util.FileUploadUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Value("${image.image-dir}")
    private String IMAGE_DIR;

    @Value("${image.image-url}")
    private String IMAGE_URL;

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    // Category <-> Product 연관관계 전용 레포짓포리
    private final ProductAndCategoryRepository productAndCategoryRepository;
    /* Entity <-> DTO 객체 변환 관련 라이브러리 */
    private final ModelMapper modelMapper;

    public int selectProductTotal() {

        log.info("[ProductService] selectProductTotal() start!!");

        List<Product> productList = productRepository.findByProductOrderable("Y");

        log.info("[ProductService] selectProductTotal() end!!");

        return productList.size();
    }

    public Object selectProductListWithPaging(Criteria cri) {

        log.info("[ProductService] selectProductListWithPaging() start!!");

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();

        Pageable paging = PageRequest.of(index , count , Sort.by("productCode").descending());

        Page<Product> result = productRepository.findByProductOrderable("Y" , paging);

        List<Product> productList = result.getContent();

        // 이미지 관련 처리 -> 여러분들이 신경쓰지 않아도 됩니다.
        for(int i = 0; i < productList.size(); i++) {
            productList.get(i).setProductImageUrl(IMAGE_URL + productList.get(i).getProductImageUrl());
        }

        log.info("[ProductService] selectProductListWithPaging() end!!");

        // 여기서부터 시작
        return productList.stream().map(product -> modelMapper.map(product , ProductDTO.class)).collect(Collectors.toList());
    }

    public ProductDTO selectProduct(int productCode) {
        log.info("[ProductService] selectProduct() Start");

        // Product vs Optional<Product> 차이 때문
        Product product = productRepository.findById(productCode).get();
        // 이미지 설정
        product.setProductImageUrl(IMAGE_URL + product.getProductImageUrl());

        log.info("[ProductService] selectProduct() End");

        return modelMapper.map(product , ProductDTO.class);
    }

    public List<ProductDTO> selectSearchProductList(String search) {
        log.info("[ProductService] selectSearchProductList() Start");
        log.info("[ProductService] searchValue : {}", search);

        List<Product> productListWithSearchValue = productRepository.findByProductNameContaining(search);
        log.info("[ProductService] productListWithSearchValue : {}", productListWithSearchValue);

        // 이미지 관련 처리
        for (int i = 0; i < productListWithSearchValue.size(); i++) {
            productListWithSearchValue.get(i).setProductImageUrl(IMAGE_URL + productListWithSearchValue.get(i).getProductImageUrl());
        }

        log.info("[ProductService] selectSearchProductList() End");

        return productListWithSearchValue.stream()
                .map(product -> modelMapper.map(product , ProductDTO.class))
                .collect(Collectors.toList());
    }

    public List<ProductDTO> selectProductListAboutMeal() {
        log.info("[ProductService] selectProductListAboutMeal() Start");

        List<Product> productListAboutMeal = productRepository.findByCategoryCode(1);

        // 이미지 처리
        for (int i = 0; i < productListAboutMeal.size(); i++) {
            productListAboutMeal.get(i).setProductImageUrl(IMAGE_URL + productListAboutMeal.get(i).getProductImageUrl());
        }

        log.info("[ProductService] selectProductListAboutMeal() End");

        return productListAboutMeal.stream()
                .map(product -> modelMapper.map(product , ProductDTO.class))
                .collect(Collectors.toList());
    }

    public List<ProductDTO> selectProductListAboutDessert() {
        log.info("[ProductService] selectProductListAboutDessert() Start");

        List<Product> productListAboutDessert = productRepository.findByCategoryCode(2);

        for(int i = 0 ; i < productListAboutDessert.size() ; i++) {
            productListAboutDessert.get(i).setProductImageUrl(IMAGE_URL + productListAboutDessert.get(i).getProductImageUrl());
        }

        log.info("[ProductService] selectProductListAboutDessert() End");

        return productListAboutDessert.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());
    }

    public List<ProductDTO> selectProductListAboutBeverage() {
        log.info("[ProductService] selectProductListAboutBeverage() Start");

        List<Product> productListAboutBeverage = productRepository.findByCategoryCode(3);

        for(int i = 0 ; i < productListAboutBeverage.size() ; i++) {
            productListAboutBeverage.get(i).setProductImageUrl(IMAGE_URL + productListAboutBeverage.get(i).getProductImageUrl());
        }

        log.info("[ProductService] selectProductListAboutBeverage() End");

        return productListAboutBeverage.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());
    }

    public int selectProductTotalForAdmin() {
        log.info("[ProductService] selectProductTotalForAdmin() Start");

        int result = productRepository.findAll().size();

        log.info("[ProductService] selectProductTotalForAdmin() End");

        return result;
    }

    public List<ProductAndCategoryDTO> selectProductListWithPagingForAdmin(Criteria cri) {
        log.info("[ProductService] selectProductListWithPagingForAdmin() Start");

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index , count , Sort.by("productCode").descending());

        Page<ProductAndCategory> result = productAndCategoryRepository.findAll(paging);
        List<ProductAndCategory> productList = result.getContent();

        // 이미지 관련 처리
        for(int i = 0; i < productList.size(); i++) {
            productList.get(i).setProductImageUrl(IMAGE_URL + productList.get(i).getProductImageUrl());
        }

        log.info("[ProductService] selectProductListWithPagingForAdmin() End");

        return productList
                .stream()
                .map(product -> modelMapper.map(product , ProductAndCategoryDTO.class))
                .collect(Collectors.toList());
    }

    public ProductDTO selectProductForAdmin(int productCode) {
        log.info("[ProductService] selectProductForAdmin() Start");

        Product product = productRepository.findById(productCode).get();
        product.setProductImageUrl(IMAGE_URL + product.getProductImageUrl());

        log.info("[ProductService] selectProductForAdmin() End");



        return modelMapper.map(product , ProductDTO.class);
    }

    @Transactional
    public String insertProduct(ProductDTO productDTO , MultipartFile productImage) {
        log.info("[ProductService] insertProduct() Start");
        log.info("[ProductService] productDTO : {}", productDTO);

        String imageName = UUID.randomUUID().toString().replace("-" , "");
        String replaceFileName = null;
        int result = 0;

        try {
            replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR , imageName , productImage);

            // 변환 처리 된 파일 값으로 Set
            productDTO.setProductImageUrl(replaceFileName);

            // 화면에서 전달 받은 DTO 객체를 Entity 로 변경
            Product insertProduct = modelMapper.map(productDTO , Product.class);

            productRepository.save(insertProduct);

            // 정상적으로 예외 없이 마무리 되면 result 를 1로 초기화
            result = 1;
        } catch (IOException e) {
            // 예외 발생 시 파일에 대한 정보 삭제
            FileUploadUtils.deleteFile(IMAGE_DIR , replaceFileName);
            throw new RuntimeException(e);
        }

        log.info("[ProductService] insertProduct() End");

        return (result > 0) ? productDTO.getProductName() + " 상품 등록 성공!!" : "상품 등록 실패";
    }

    @Transactional
    public String updateProduct(ProductDTO productDTO, MultipartFile productImage) {
        log.info("[ProductService] updateProduct() Start");
        log.info("[ProductService] productDTO : {}", productDTO);

        String replaceFileName = null;
        int result = 0;

        try {

            /* 설명. update 할 엔티티 조회 */
            Product product = productRepository.findById(productDTO.getProductCode()).get();
            String oriImage = product.getProductImageUrl();
            log.info("[updateProduct] oriImage : {}", oriImage);

            /* 설명. update를 위한 엔티티 값 수정 */
            /* setter 방식을 사용 (지양하는 코드 참고!!) */
            product.setCategoryCode(productDTO.getCategoryCode());
            product.setProductName(productDTO.getProductName());
            product.setProductPrice(productDTO.getProductPrice());
            product.setProductOrderable(productDTO.getProductOrderable());
            product.setCategoryCode(productDTO.getCategoryCode());
            product.setProductStock(productDTO.getProductStock());
            product.setProductDescription(productDTO.getProductDescription());

            if(productImage != null){
                String imageName = UUID.randomUUID().toString().replace("-", "");
                replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR, imageName, productImage);
                log.info("[updateProduct] InsertFileName : {}", replaceFileName);

                product.setProductImageUrl(replaceFileName);	// 새로운 파일 이름으로 update
                log.info("[updateProduct] deleteImage : {}", oriImage);

                boolean isDelete = FileUploadUtils.deleteFile(IMAGE_DIR, oriImage);
                log.info("[update] isDelete : {}", isDelete);
            } else {

                /* 설명. 이미지 변경 없을 경우 */
                product.setProductImageUrl(oriImage);
            }

            result = 1;
        } catch (IOException e) {
            log.info("[updateProduct] Exception!!");
            FileUploadUtils.deleteFile(IMAGE_DIR, replaceFileName);
            throw new RuntimeException(e);
        }
        log.info("[ProductService] updateProduct End ===================================");
        return (result > 0) ? "상품 업데이트 성공" : "상품 업데이트 실패";
    }
}
