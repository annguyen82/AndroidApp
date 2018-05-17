package com.example.pphighend.hotellocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InstructionActivity extends AppCompatActivity {

    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        tvContent = (TextView) findViewById(R.id.content);
        String content = "1. Tìm khách sạn\n\t- Tại thanh tìm kiếm nhập tên khách sạn" +
                "\n\t- Chọn khách sạn được gợi ý\n\t- Địa chỉ tìm kiếm được hiển thị" +
                "\n\n2. Thêm khách sạn\n\t- Mở Navigation bar\n\t- Chọn Thêm khách sạn\n\t" +
                "- Nhập tên khách sạn, địa chỉ\n\t- Chọn địa điểm gợi ý hoặc nhấn enter" +
                "\n\t- Hiển thị khách sạn\n\t- Chọn marker\n\t- Chọn tên khách sạn\n\t- " +
                "Nếu đã tồn tại:\n\t\t+ Thông báo khách sạn đã tồn tại\n\t" +
                "- Ngược lại:\n\t\t+ Hiển thị xác nhận thêm hay không\n\t\t+ Nếu thêm, chọn Đồng ý\n\t\t" +
                "+ Ngược lại, chọn Không\n\n3. Xem chi tiết khách sạn\n\t- Sau khi hiển thị Marker" +
                "\n\t- Chọn Marker\n\t- Chọn tên khách sạn\n\t- Hiển thị thông tin chi tiết khách sạn" +
                "\n\n4. Địa điểm yêu thích\n\t- Mở Navigation bar\n\t- Chọn Địa điểm yêu thích" +
                "\n\t- Hiển thị danh sách địa điểm yêu thích\n\n5. Hướng dẫn sử dụng\n\t" +
                "- Mở Navigation bar\n\t- Chọn Hướng dẫn sử dụng\n\t- Hiển thị nội dung hướng dẫn sử dụng" +
                "\n\n6. Thông tin ứng dụng\n\t- Mở navigation\n\t- Chọn Thông tin ứng dụng" +
                "\n\t- Hiển thị nội dung Thông tin ứng dụng\n\n7. Chia sẽ địa điểm\n\t" +
                "- Mở Navigation bar\n\t- Chọn chia sẽ địa điểm\n\t- Địa điểm hiện tại sẽ được chia sẽ ngay lên Facebook" +
                "\n\n8. Đăng xuất\n\t- Mở Navigation bar\n\t- Đăng xuất\n\t- Quay về màn hình đăng nhập\n\n";
        tvContent.setText(content);
    }
}
