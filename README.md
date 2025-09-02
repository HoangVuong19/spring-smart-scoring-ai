```sql
-- Thêm dữ liệu vào bảng student
INSERT INTO smartscore.student
(class_room_id, id, gender, literature, name)
VALUES
(1, 1, 'male', '5', 'Tuan'),
(1, 2, 'male', '6', 'Vuong'),
(1, 3, 'male', '6', 'Ky'),
(1, 4, 'famale', '5', 'Thao'),
(1, 5, 'female', '8', 'Hien');

-- Thêm dữ liệu vào bảng class_room
INSERT INTO smartscore.class_room
(id, name)
VALUES
(1, '12A1');

-- Thêm dữ liệu vào bảng exam_test
INSERT INTO smartscore.exam_test
(id, barem, question)
VALUES
(
  1,
  '1. Nội dung & chủ đề:
   - Thể loại đa dạng (shounen, shoujo, seinen, josei...).
   - Thông điệp sâu sắc, phản ánh văn hóa, xã hội, lịch sử, giả tưởng.

   2. Nghệ thuật & phong cách vẽ:
      - Nhân vật có biểu cảm phong phú, cường điệu hóa.
      - Bố cục khung truyện, tiết tấu hợp lý, hình ảnh dẫn dắt cảm xúc.
   3. Ảnh hưởng văn hóa & xã hội:
      - Phổ biến trong nước và toàn cầu.
      - Chuyển thể thành anime, game, phim, góp phần quảng bá Nhật Bản.
   4. Giá trị giải trí & giáo dục:
      - Mang lại thư giãn, phù hợp nhiều lứa tuổi.
      - Giáo dục tinh thần kiên trì, tình bạn, trách nhiệm, bài học nhân sinh.
   5. Nhận xét cá nhân & lập luận:
      - Nêu quan điểm riêng, điểm yêu thích.
      - Lập luận có dẫn chứng (One Piece, Naruto, Doraemon, Attack on Titan...).',
  'Bạn có nhận xét gì về manga Nhật Bản?'
);

-- API Endpoints cho AnalysisController

-- POST /analysis
 Body: JSON String (ví dụ):
 {
   "message": "phân tích tỉ lệ sinh viên có điểm văn cao rơi vào lớp nào?",
 }
 Mô tả: phân tích dữ liệu văn bản
 Response: String

-- POST /analysis/grade-answer
 Body: JSON ScoreRequest (ví dụ):
 {
   "studentId": 1,
   "questionId": 1,
   "answer": "Manga Nhật Bản có nhiều thể loại đa dạng, từ phiêu lưu, tình cảm đến giả tưởng, mang lại sự phong phú trong lựa chọn cho độc giả. Đồng thời, nét vẽ sinh động và biểu cảm đặc trưng đã tạo nên phong cách nghệ thuật riêng, khiến manga dễ dàng được nhận diện trên toàn thế giới."
 }
 Response: JSON ScoreResponse {
   className: String,
   StudentName: String,
   score: String,
   feedBack: String
 }

```
