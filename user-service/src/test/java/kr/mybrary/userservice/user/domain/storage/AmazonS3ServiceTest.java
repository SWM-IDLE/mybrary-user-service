package kr.mybrary.userservice.user.domain.storage;

import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Template;
import kr.mybrary.userservice.user.UserTestData;
import kr.mybrary.userservice.user.domain.exception.storage.StorageClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AmazonS3ServiceTest {

    @Mock
    S3Template s3Template;
    @InjectMocks
    AmazonS3Service amazonS3Service;

    private static final String BASE_URL = "https://mybrary-user.s3.ap-northeast-2.amazonaws.com/";
    private static final String RESIZED_BASE_URL = "https://mybrary-user-resized.s3.ap-northeast-2.amazonaws.com/";

    @Test
    @DisplayName("파일을 업로드한다.")
    void putFile() throws Exception {
        // given
        MockMultipartFile multipartFile = UserTestData.createMockMultipartFile();
        given(s3Template.upload(any(), any(), any(), any())).willReturn(null);

        // when
        String url = amazonS3Service.putFile(multipartFile, "path");

        // then
        assertAll(
                () -> assertThat(url).isEqualTo(BASE_URL + "path")
        );

        verify(s3Template).upload(any(), any(), any(), any());
    }

    @Test
    @DisplayName("파일을 업로드할 때 S3Exception이 발생하면 StorageClientException을 던진다.")
    void putFileWithS3Exception() throws Exception {
        // given
        MockMultipartFile multipartFile = UserTestData.createMockMultipartFile();
        given(s3Template.upload(any(), any(), any(), any())).willThrow(S3Exception.class);

        // when then
        assertThatThrownBy(() -> amazonS3Service.putFile(multipartFile, "path"))
                .isInstanceOf(StorageClientException.class)
                .hasFieldOrPropertyWithValue("status", 500)
                .hasFieldOrPropertyWithValue("errorCode", "S-01")
                .hasFieldOrPropertyWithValue("errorMessage", "스토리지 서버와 통신에 실패했습니다.");

        verify(s3Template).upload(any(), any(), any(), any());
    }

    @Test
    @DisplayName("파일 url에서 base url을 제외한 path를 반환한다.")
    void getFilePath() {
        // given
        String url = BASE_URL + "path";

        // when
        String filePath = amazonS3Service.getPathFromUrl(url);

        // then
        assertAll(
                () -> assertThat(filePath).isEqualTo("path")
        );
    }

    @Test
    @DisplayName("요청된 사이즈로 리사이징된 파일이 있는지 확인한다. 없다면 false를 반환한다.")
    void hasResizedFilesFalse() {
        // given
        String path = "path";
        String size = "size";
        given(s3Template.listObjects(any(), any())).willReturn(List.of());

        // when
        boolean hasResizedFiles = amazonS3Service.hasResizedFiles(path, size);

        // then
        assertAll(
                () -> assertThat(hasResizedFiles).isFalse()
        );

        verify(s3Template).listObjects(any(), any());
    }

    @Test
    @DisplayName("요청된 사이즈로 리사이징된 파일이 있는지 확인한다. 있다면 true를 반환한다.")
    void hasResizedFilesTrue() {
        // given
        String path = "path";
        String size = "size";
        given(s3Template.listObjects(any(), any())).willReturn(Arrays.asList(null, null));

        // when
        boolean hasResizedFiles = amazonS3Service.hasResizedFiles(path, size);

        // then
        assertAll(
                () -> assertThat(hasResizedFiles).isTrue()
        );

        verify(s3Template).listObjects(any(), any());
    }

    @Test
    @DisplayName("리사이징된 파일의 url을 반환한다.")
    void getResizedFileUrl() {
        // given
        String url = "https://mybrary-user.s3.ap-northeast-2.amazonaws.com/path";
        String size = "small";

        // when
        String resizedFileUrl = amazonS3Service.getResizedUrl(url, size);

        // then
        assertAll(
                () -> assertThat(resizedFileUrl).isEqualTo(RESIZED_BASE_URL + "small-path")
        );
    }

}