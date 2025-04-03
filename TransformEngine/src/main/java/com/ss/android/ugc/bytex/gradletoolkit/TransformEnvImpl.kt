package com.ss.android.ugc.bytex.gradletoolkit

import bytex.core.patch.AGPStub
import com.android.build.api.artifact.BuildArtifactType
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.scope.InternalArtifactType
import com.google.auto.service.AutoService
import org.gradle.api.Project
import java.io.File
import java.util.*

/**
 * Created by tanlehua on 2019-04-29.
 */
@AutoService(TransformEnv::class)
class TransformEnvImpl : TransformEnv {
    private lateinit var project: Project

    override fun setProject(project: Project) {
        this.project = project
    }

    private var invocation: TransformInvocation? = null

    override fun setTransformInvocation(invocation: TransformInvocation) {
        this.invocation = invocation
    }

    private val artifactFinder: AGPStub.ArtifactCollectionFinder by lazy {
        AGPStub.ArtifactCollectionFinder(invocation!!, project)
    }

    override fun getArtifact(artifact: Artifact): Collection<File> {
        if (invocation == null) {
            return Collections.emptyList()
        }
        return with(artifactFinder) {
            when (artifact) {
                Artifact.AAR -> find(InternalArtifactType.AAR)
                Artifact.JAR -> find(InternalArtifactType.FULL_JAR)
                Artifact.PROCESSED_JAR -> find(InternalArtifactType.FULL_JAR)
                Artifact.CLASSES -> find(BuildArtifactType.JAVA_COMPILE_CLASSPATH)
                Artifact.ALL_CLASSES -> find(BuildArtifactType.JAVAC_CLASSES)
                Artifact.APK -> find(InternalArtifactType.APK)
                Artifact.JAVAC -> find(BuildArtifactType.JAVAC_CLASSES)
                Artifact.MERGED_ASSETS -> find(InternalArtifactType.MERGED_ASSETS)
                Artifact.MERGED_RES -> find(InternalArtifactType.MERGED_RES)
                Artifact.MERGED_MANIFESTS -> find(InternalArtifactType.MERGED_MANIFESTS)
                Artifact.PROCESSED_RES -> find(InternalArtifactType.PROCESSED_RES)
                Artifact.SYMBOL_LIST -> find(InternalArtifactType.SYMBOL_LIST)
                Artifact.SYMBOL_LIST_WITH_PACKAGE_NAME -> find(InternalArtifactType.SYMBOL_LIST_WITH_PACKAGE_NAME)
                else -> emptyList()
            }
        }
    }
}