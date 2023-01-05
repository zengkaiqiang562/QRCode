package com.deploy.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class DeployPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        println ("hello DeployPlugin in project:${project.name}")

        project.extensions.create("deployExt", DeployExtension::class.java)

        project.tasks.create("deployTask", DeployTask::class.java)
    }
}