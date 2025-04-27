package ptit.dblab.app.enumerate;

public enum ErrorCode {
    SUCCESS(1, "Thành công"),
    ERROR(0, "Có lỗi xảy ra"),
    PROCCESSING(3, "Đang xử lý"),
    INVALID_USERNAME_PASSWORD(10001, "Tên người dùng hoặc mật khẩu không hợp lệ"),
    USER_ALREADY_EXISTS(10002, "Người dùng đã tồn tại"),
    EMAIL_ALREADY_EXISTS(10003, "Email đã tồn tại"),
    NOT_MATCH_TYPE_QUERY(10004, "Loại truy vấn không khớp"),
    REQUIRED_EXISTS(10005, "Yêu cầu đã tồn tại"),
    OVERLAP_CONTEST(10006,"Không thể tham gia contest do trùng thời gian với contest khác đã tham gia"),
    CONTEST_CLOSE(10007,"Không thể tham gia contest do đã kết thúc"),
    QUESTIONS_MUST_BE_REQUIRED(10008,"Vui lòng nhập câu hỏi cho contest"),
    USER_EXISTED_IN_CONTEST(10013,"Bạn đã tham gia contest!"),
    TIME_SET_UP_NOT_VALID(10014,"Thời gian kết thúc phải lớn hơn thời gian bắt đầu"),
    USER_NOT_EXIST(10015,"User không tồn tại"),
    CANNOT_SUBMIT_BECAUSE_CONTEST_NOT_OPEN(10016,"Không thể submit do contest không tồn tại hoặc chưa mở!"),
    CONTEST_NOT_PUBLIC(10017,"Contest không tồn tại hoặc không được chia sẻ với bạn"),
    FILE_UPLOAD_EMPTY(10018,"File upload empty!"),
    FILE_UPLOAD_ERROR(10019,"Upload file thất bại!"),
    DISPLAYNAME_MUST_CONTAIN_TABLE_NAME(10020,"Tên hiển thị phải bao gồm tên bảng"),
    SUBMIT_EXIST_EVALUATE(10021,"Submit này đã được đánh giá"),
    MAX_REQUEST_LIMIT_EXCEEDED(10022,"Đã đạt giới hạn request!"),
    //common
    RESOURCE_NOT_FOUND(10009,"Không có dữ liệu"),
    USER_NOT_LOGIN(10010,"Vui lòng đăng nhập!"),
    CANNOT_CALL_PARTY_SERVICE(10011,"Lỗi khi call tới dịch vụ khác!"),
    PARAM_MUST_BE_REQUIRED(10012,"Thiếu dữ liệu cho trường bắt buộc {}"),
    UN_PERMITION(401,"Không có quyền thực thi!")
    ;
    private final int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

