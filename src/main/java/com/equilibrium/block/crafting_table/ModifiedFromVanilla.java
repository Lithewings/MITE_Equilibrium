package com.equilibrium.block.crafting_table;

import java.lang.annotation.*;


@Documented
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface ModifiedFromVanilla {
    String value() default "";
}