package com.shanjupay.common.valid;

import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * valitaded 绑定错误结果方法
 */
@Slf4j
public class ValidUtils {

    public static void validParams(BindingResult errorResult) {
        if (errorResult.hasErrors()) {
            FieldError fieldError = errorResult.getFieldError();
            if(fieldError!=null){
                String defaultMessage = fieldError.getDefaultMessage();
                log.error("参数问题：{}", defaultMessage);
                throw new BussinessException(defaultMessage,CommonErrorCode.E_100101.getCode());
            }

            //参数校验 返回错误集合
            /*
            List<ObjectError> list = errorResult.getAllErrors();
            if (list.size() > 0) {
                ObjectError objectError = list.get(0);
                log.error("参数问题：{}", objectError.getDefaultMessage());
                throw new BussinessException(objectError.getDefaultMessage());
            }
            */
        }
    }
    /**
     * 在service进行属性校验
     *
     * @param object 校验的对象
     */
    public static void checkFieldsInService(Object object) {
        //String validMessage = "";
        StringBuilder s = new StringBuilder();
        //初始化检查器。
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(false)
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        //检查object
        Set<ConstraintViolation<Object>> set = validator.validate(object);
        //循环set，获取检查结果
        for (ConstraintViolation<Object> voset : set) {
            //validMessage = validMessage + voset.getMessage() + ";";
            s.append( voset.getMessage()).append(";");
        }
//        if (!StringUtils.isEmpty(validMessage)) {
//            //抛出业务异常
//            throw new BussinessException(validMessage);
//        }
        if (!StringUtils.isEmpty(s.toString())) {
            //抛出业务异常
            throw new BussinessException(s.toString());
        }
    }
}
