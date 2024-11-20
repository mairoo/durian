package kr.co.pincoin.api.app.member.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.pincoin.api.domain.auth.model.user.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyUserResponse extends UserResponse {
    @JsonProperty("email")
    private final String email;

    @JsonProperty("firstName")
    private final String firstName;

    @JsonProperty("lastName")
    private final String lastName;

    @JsonProperty("dateJoined")
    private final LocalDateTime dateJoined;

    @JsonProperty("lastLogin")
    private final LocalDateTime lastLogin;

    protected MyUserResponse(User user) {
        super(user);

        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.dateJoined = user.getDateJoined();
        this.lastLogin = user.getLastLogin();
    }

    public static MyUserResponse from(User user) {
        return new MyUserResponse(user);
    }
}
