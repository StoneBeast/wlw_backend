package at.along.com.handler;

import at.along.com.exception.BusinessException;
import at.along.com.utils.ResponseUtils;
import at.along.com.utils.Result;
import at.along.com.utils.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    /**
     * 业务异常处理
     * @param e
     * @return ErrorInfo
     */
    @ExceptionHandler({BusinessException.class})
    public Result businessExceptionHandler(HttpServletRequest request, BusinessException e)
            throws BusinessException {
        log.error("BusinessException异常：{}", e.getMessage());
        return ResponseUtils.failResult(ResultCode.fail, e.getMessage(), null);
    }

    /**
     * 业务异常处理
     * @param e
     * @return ErrorInfo
     */
//    @ExceptionHandler({AccessDeniedException.class})
//    public ResponseEntity<Object> BusinessExceptionHandler(HttpServletRequest request, AccessDeniedException e) throws BusinessException {
//        return ResponseEntity.of(401, e.getMessage(), null);
//    }

    /**
     * 只要抛出该类型异常,则由此方法处理
     * @return
     * @throws Exception
     */
//    @ExceptionHandler(value = Exception.class)
//    public ResponseEntity<Object> handleException(HttpServletRequest request, Exception e) throws Exception {
//        log.error(e.getMessage(), e);
//        return ResponseEntity.of(ResponseCode.SERVER_INTERNAL_ERROR, e.getMessage(), null);
//    }
}
