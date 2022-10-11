package me.darknet.cli.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    String permission() default "";

    String noPermissionMessage() default "You do not have permission to use this command.";

}
