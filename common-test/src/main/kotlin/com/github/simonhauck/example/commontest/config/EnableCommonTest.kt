package com.github.simonhauck.example.commontest.config

import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(CommonTestConfiguration::class)
annotation class EnableCommonTest
