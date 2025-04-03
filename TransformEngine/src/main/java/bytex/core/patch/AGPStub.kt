package bytex.core.patch

import com.android.build.api.artifact.ArtifactType
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.variant.BaseVariantData
import org.gradle.api.Project
import java.io.File


// simple compat for replace bytedance compat.
internal object AGPStub {
    private fun getVariants(project: Project): List<BaseVariant> {
        val androidExtension = project.extensions.findByType(BaseExtension::class.java) ?: return emptyList()
        return when (androidExtension) {
            is AppExtension -> androidExtension.applicationVariants.toList()
            is LibraryExtension -> androidExtension.libraryVariants.toList()
            else -> emptyList()
        }
    }

    private val getVariantDataMethod by lazy {
        BaseVariantImpl::class.java.getMethod("getVariantData")
    }

    fun findVariantScope(project: Project, variantName: String): VariantScope? = kotlin.runCatching {
        val variantImpl = getVariants(project).firstOrNull { it.name == variantName } as? BaseVariantImpl
        val data = getVariantDataMethod.invoke(variantImpl) as? BaseVariantData
        data?.scope
    }.onFailure { it.printStackTrace() }.getOrNull()

    @JvmStatic
    fun getVariant(transformInvocation: TransformInvocation, project: Project): BaseVariant? {
        return getVariants(project).firstOrNull { it.name == transformInvocation.context.variantName }
    }

    class ArtifactCollectionFinder(
        val transformInvocation: TransformInvocation, val project: Project
    ) {
        fun find(artifactType: ArtifactType): Collection<File> {
            val scope = findVariantScope(project, transformInvocation.context.variantName) ?: return emptyList()
            return scope.artifacts.getArtifactFiles(artifactType).files
        }
    }
}
