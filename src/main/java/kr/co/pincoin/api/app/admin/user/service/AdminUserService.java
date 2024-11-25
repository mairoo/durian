package kr.co.pincoin.api.app.admin.user.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserService {

    //
//- 고객 목록 조회
//- 고객 상세 정보
//- 고객 서류 인증
//- 고객 이메일 인증
//- 고객 문의 목록
//- 고객 문의 상세, 답변하기
//- 이용후기 목록
//- 이용후기 보기
//- 이용후기 작성
//- 이용후기 답변 (관리)
//- SMS 발송 내역
//- SMS 발송


    public void
    createUser() {

    }

    public void
    findAllUsers() {

    }

    public void
    findUserById() {

    }

    public void
    updateUserName() {

    }

    public void
    updateUserEmail() {

    }

    public void
    updateUserPassword() {

    }

    public void
    deleteUser() {

    }
}
