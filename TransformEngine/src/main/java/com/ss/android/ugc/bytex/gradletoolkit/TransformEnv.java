package com.ss.android.ugc.bytex.gradletoolkit;

import com.android.build.api.transform.TransformInvocation;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by tanlehua on 2019-04-29.
 */
public interface TransformEnv extends GradleEnv {
    void setProject(@NotNull Project project);
    void setTransformInvocation(TransformInvocation invocation);
}
