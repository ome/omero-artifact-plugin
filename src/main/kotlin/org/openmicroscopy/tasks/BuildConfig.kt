package org.openmicroscopy.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.MethodSpec
import javax.lang.model.element.Modifier
import com.squareup.javapoet.JavaFile




class BuildConfig : DefaultTask() {

    val BUILD_CONFIG_NAME = "BuildConfig.java"

    @TaskAction
    fun generate() {
        val main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Void.TYPE)
                .addParameter(Array<String>::class.java, "args")
                .addStatement("\$T.out.println(\$S)", System::class.java, "Hello, JavaPoet!")
                .build()

        val helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build()

        val javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build()

        javaFile.writeTo(System.out)
    }

}