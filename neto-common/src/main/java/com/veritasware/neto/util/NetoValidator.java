package com.veritasware.neto.util;

import com.veritasware.neto.Constants;
import com.veritasware.neto.annotation.NetoNotNull;
import com.veritasware.neto.annotation.NetoNotNullOrNotEmpty;
import com.veritasware.neto.exception.ValidationException;

import java.lang.reflect.Field;

/**
 * Created by chacker on 2016-11-01.
 */
public class NetoValidator {

    public static void validate(Object target) throws IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            validateField(field, target);
        }
    }

    private static void validateField(Field field, Object target) throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        Object value = field.get(target);
        if (field.isAnnotationPresent(NetoNotNull.class)) {
            if (value == null) {
                throw new ValidationException(Constants.StatusCode.PARAMETER_NULL, field.getName() + " is null");
            }
        } else if (field.isAnnotationPresent(NetoNotNullOrNotEmpty.class)) {

            if (value == null) {
                throw new ValidationException(Constants.StatusCode.PARAMETER_NULL, field.getName() + " is null");
            }

            if (value instanceof String && (value == null || ((String) value).isEmpty())) {
                throw new ValidationException(Constants.StatusCode.PARAMETER_NULL_OR_EMPTY, field.getName() + " is null or empty");
            }
        }
    }

}
