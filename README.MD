# Laptop Shop

Ứng dụng web cửa hàng laptop được phát triển như một dự án cá nhân để thực hành xây dựng ứng dụng web với Java và Spring Boot. Dự án tập trung vào nền tảng bán hàng laptop và có thể tiếp tục được mở rộng, tùy chỉnh theo nhu cầu.

> Đây là dự án cá nhân được phát triển và chỉnh sửa dựa trên quá trình học tập. Một số kiến thức và ý tưởng ban đầu được tham khảo từ series Java Spring MVC của **Hỏi Dân IT (Eric)**. Phần ghi nhận này nhằm tôn trọng nguồn tham khảo; các thay đổi và phần phát triển tiếp theo thuộc quá trình làm dự án của mình.

## Công nghệ

- Java 17
- Spring Boot 3.2.2
- Spring MVC, Spring Data JPA, Spring Security
- JSP / JSTL
- MySQL
- Maven

## Yêu cầu

- JDK 17 trở lên
- MySQL
- Git (nếu clone dự án)

## Cài đặt và chạy

1. Clone repository:

   ```bash
   git clone https://github.com/NghiemGiaHuy/Laptop-Shop.git
   cd Laptop-Shop
   ```

2. Tạo database MySQL tên `laptopshop`.

3. Cấu hình kết nối database trong `src/main/resources/application.properties` theo môi trường của bạn. Không dùng hoặc commit mật khẩu mặc định trong môi trường thực tế.

4. Khởi chạy ứng dụng bằng Maven Wrapper:

   ```bash
   # macOS / Linux
   ./mvnw spring-boot:run

   # Windows
   mvnw.cmd spring-boot:run
   ```

5. Mở `http://localhost:8080` trên trình duyệt.

## Cấu hình database

Ứng dụng mặc định kết nối tới MySQL trên `localhost`, database `laptopshop`. Hãy cập nhật URL, username và password trong `application.properties` để phù hợp với cấu hình máy của bạn. Với môi trường triển khai, nên đưa thông tin nhạy cảm vào biến môi trường hoặc cơ chế quản lý secrets thay vì lưu trực tiếp trong mã nguồn.

## Phát triển

Chạy kiểm thử:

```bash
./mvnw test
```

Trên Windows:

```bash
mvnw.cmd test
```

## Ghi nhận

Cảm ơn **Hỏi Dân IT (Eric)** và các tài liệu cộng đồng đã chia sẻ kiến thức hữu ích về Java Spring MVC. Dự án này được sử dụng cho mục đích học tập và phát triển cá nhân.
