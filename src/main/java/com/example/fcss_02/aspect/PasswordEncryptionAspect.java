package com.example.fcss_02.aspect;

import com.example.fcss_02.annotation.CustomEncryption;
import com.example.fcss_02.service.EncryptService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Modifier;
import java.util.Arrays;


@Aspect
@Component
@RequiredArgsConstructor
public class PasswordEncryptionAspect {

    private final EncryptService encryptService;

    @Around("execution(* com.example.fcss_02.controller..*.*(..))")
    public Object passwordEncryptionAspect(ProceedingJoinPoint pjp) throws Throwable {
        Arrays.stream(pjp.getArgs())
                .forEach(this::fieldEncryption);

        return pjp.proceed();
    }

    public void fieldEncryption(Object object) {
        if (ObjectUtils.isEmpty(object)) {
            return;
        }

        FieldUtils.getAllFieldsList(object.getClass())
                .stream()
                .filter(filter -> !(Modifier.isFinal(filter.getModifiers()) && Modifier.isStatic(filter.getModifiers())))
                .forEach(field -> {
                    try {
                        boolean encryptionTarget = field.isAnnotationPresent(CustomEncryption.class);
                        if (!encryptionTarget) {
                            return;
                        }

                        Object encryptionField = FieldUtils.readField(field, object, true);
                        if (!(encryptionField instanceof String)) {
                            return;
                        }

                        String encrypted = encryptService.encrypt((String) encryptionField);
                        FieldUtils.writeField(field, object, encrypted);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}