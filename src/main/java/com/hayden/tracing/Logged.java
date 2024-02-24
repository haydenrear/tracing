package com.hayden.tracing;

import org.springframework.stereotype.Indexed;
import java.lang.annotation.*;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logged {

}
