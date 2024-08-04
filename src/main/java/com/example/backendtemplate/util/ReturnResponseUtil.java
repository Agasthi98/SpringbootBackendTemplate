package com.example.backendtemplate.util;


import com.example.backendtemplate.util.constants.LogMessage;
import com.example.backendtemplate.enums.ResponseStatus;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.model.response.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


/**
 * ReturnResponseUtil class is used to return response based on the response code.
 */
@Component
@Slf4j
public class ReturnResponseUtil {
    private ReturnResponseUtil() {
    }

    public static ResponseEntity<DefaultResponse> returnResponse(BaseDetailsResponse<?> commonResponse) {
        if (commonResponse != null) {
            if (commonResponse.getCode().equals(ResponseCodeUtil.SUCCESS_CODE)) {
                log.info(LogMessage.RETURN_RESPONSE_UTIL, LogMessage.SUCCESS_RESPONSE);

                if (commonResponse.getData() == null) {
                    return ResponseEntity.ok(DefaultResponse.success(ResponseCodeUtil.SUCCESS, commonResponse.getMessage()));
                } else {
                    return ResponseEntity.ok(DefaultResponse.success(ResponseCodeUtil.SUCCESS, commonResponse.getMessage(), commonResponse.getData()));
                }
            } else if (commonResponse.getCode().equals(ResponseCodeUtil.INTERNAL_SERVER_ERROR_CODE)) {
                log.info(LogMessage.RETURN_RESPONSE_UTIL, LogMessage.INTERNAL_SERVER_ERROR_RESPONSE);
                return ResponseEntity.internalServerError().body(DefaultResponse.internalServerError(ResponseCodeUtil.INTERNAL_SERVER_ERROR, commonResponse.getMessage()));

            } else {
                log.info(LogMessage.RETURN_RESPONSE_UTIL, LogMessage.FAILED_RESPONSE);
                return ResponseEntity.badRequest().body(DefaultResponse.error(ResponseCodeUtil.FAILED, commonResponse.getMessage()));
            }
        }
        log.info(LogMessage.RETURN_RESPONSE_UTIL, LogMessage.FAILED_RESPONSE);
        return ResponseEntity.badRequest().body(DefaultResponse.error(ResponseCodeUtil.FAILED, ResponseStatus.FAILED.name()));
    }
}
