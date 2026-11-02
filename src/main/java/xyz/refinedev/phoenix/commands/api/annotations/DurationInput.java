package xyz.refinedev.phoenix.commands.api.annotations;

import xyz.refinedev.lib.commands.annotation.parameter.classifier.Classifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Classifier
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationInput {}
