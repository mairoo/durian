package kr.co.pincoin.api.app.admin.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.pincoin.api.app.member.user.response.MyUserResponse;
import kr.co.pincoin.api.domain.auth.model.user.User;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminUserResponse extends MyUserResponse {
    @JsonProperty("isSuperuser")
    private final boolean isSuperuser;

    @JsonProperty("isStaff")
    private final boolean isStaff;

    private AdminUserResponse(User user) {
        super(user);
        this.isSuperuser = user.isSuperuser();
        this.isStaff = user.isStaff();
    }

    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(user);
    }
}
