package nextstep.subway.auth.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.auth.dto.TokenResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.auth.acceptance.AuthAcceptanceStepTest.로그인_요청;
import static nextstep.subway.member.MemberAcceptanceStepTest.내_정보_조회_요청;
import static nextstep.subway.member.MemberAcceptanceStepTest.회원_생성을_요청;
import static nextstep.subway.member.MemberAcceptanceTest.*;
import static nextstep.subway.member.MemberAcceptanceTest.AGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 *   Feature: 로그인 기능
 *
 *     Background
 *       Given 내 정보가 회원으로 등록되어 있음
 *
 *     Scenario: 로그인을 시도한다.
 *       When 로그인 요청
 *       Then 로그인 됨
 *
 *     Scenario: 잘못된 비밀번호로 로그인을 시도한다.
 *       When 로그인 요청
 *       Then 로그인 실패
 *
 *     Scenario: 등록하지 않은 회원정보로 로그인을 시도한다.
 *       When 로그인 요청
 *       Then 로그인 실패
 */
public class AuthAcceptanceTest extends AcceptanceTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        회원_생성을_요청(EMAIL, PASSWORD, AGE);
    }


    /**
     * Scenario: 로그인을 시도한다.
     * When 로그인 요청
     * Then 로그인 됨
     */
    @DisplayName("Bearer Auth")
    @Test
    void myInfoWithBearerAuth() {
        // when 로그인 요청
        ExtractableResponse<Response> response = 로그인_요청(new TokenRequest(EMAIL, PASSWORD));

        // then 로그인 됨
        로그인_됨(response);
    }

    /**
     * Scenario: 등록하지 않은 회원정보로 로그인을 시도한다.
     * When 로그인 요청
     * Then 로그인 실패
     */
    @DisplayName("등록되지 않은  회원 정보로 로그인 - Bearer Auth 로그인 실패")
    @Test
    void myInfoWithBadBearerAuthNotSignUp() {
        // when 등록되지 않은 회원 정보로 로그인 요청
        ExtractableResponse<Response> response = 로그인_요청(new TokenRequest("yalmung@gmail.com", "no password"));

        // then 로그인 실패
        로그인_실패(response);
    }

    /**
     * Scenario: 잘못된 비밀번호로 로그인을 시도한다.
     * When 로그인 요청
     * Then 로그인 실패
     */
    @DisplayName("비밀번호 오류 - Bearer Auth 유효하지 않은 토큰")
    @Test
    void myInfoWithWrongBearerAuthWithWrongPassword() {
        // when 잘못된 비밀번호로 로그인 요철
        ExtractableResponse<Response> response = 로그인_요청(new TokenRequest(EMAIL, "wrong password"));

        // then 로그인 실패
        로그인_실패(response);
    }

    @DisplayName("Bearer Auth 유효하지 않은 토큰으로 내 정보를 조회하면 나이가 20로 비로그인된다.")
    @Test
    void myInfoWithWrongBearerAuth() {
        ExtractableResponse<Response> response = 내_정보_조회_요청("invalid_access_token");

        내_정보_조회_실패(response);
    }

    public static void 로그인_됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.as(TokenResponse.class).getAccessToken()).isNotBlank()
        );
    }

    private void 로그인_실패(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private void 내_정보_조회_실패(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

}
