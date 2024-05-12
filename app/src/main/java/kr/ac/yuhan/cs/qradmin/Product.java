package kr.ac.yuhan.cs.qradmin;

import com.google.firebase.firestore.PropertyName;

// 여기서 카테고리는 준비완료
public class Product {
    private int productCode;
    private String productName;
    private String image; // 상품 이미지 또는 3D 파일 데이터
    private int price; // 가격
    private int stock; // 재고량
    private String category;//카테고리

    // 생성자
    public Product(int productCode, String productName, String category, String image, int price, int stock) {
        this.productCode = productCode;
        this.productName = productName;
        this.category = category;
        this.image = image;
        this.price = price;
        this.stock = stock;
    }

    // 기본 생성자 추가
    public Product() {
        // Firestore 역직렬화를 위해 필요 파이어베이스에서 읽은 정보를 임시로 담아두는 역할을 함
    }

    public void setproductCode(int productCode) {
        this.productCode = productCode;
    }

    public void setName(String name) {
        this.productName = productName;
    }

    public void setImage1(String image1) {
        this.image = image;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setcategory(String category) {this.category = category;}

    // 게터 메서드
    public int getproductCode() {
        return productCode;
    }

    public String getproductName() {
        return productName;
    }

    public String getImage() {
        return image;
    } // 메서드 이름 변경

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
    @PropertyName("category") // Firestore 필드 이름과 일치하도록 설정
    public String getCategory() {
        return category;
    }
}
