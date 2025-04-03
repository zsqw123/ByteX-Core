package com.ss.android.ugc.bytex.common;


import com.android.build.gradle.api.BaseVariant;
import com.android.build.gradle.internal.pipeline.TransformTask;
import com.ss.android.ugc.bytex.common.flow.TransformFlow;
import com.ss.android.ugc.bytex.common.flow.main.MainTransformFlow;
import com.ss.android.ugc.bytex.gradletoolkit.TaskKt;
import com.ss.android.ugc.bytex.transformer.TransformContext;

import org.gradle.api.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IPlugin {

    default String name() {
        return getClass().getSimpleName();
    }

    // plugin是否可用的开关
    default BaseExtension getExtension() {
        return null;
    }

    /**
     * Determine whether your plugin is enable.
     */
    boolean enable(TransformContext transformContext);

    /**
     * 是否用alone模式，即形成单独的transform
     * if alone, it will make your plugin as a single transform.
     */
    boolean alone();


    /**
     * 是否会alone形成单独的transform运行。{@link #alone()}表示配置状态，{@link #isRunningAlone()}表示结果
     * Whether it will run in a single transform alone. {@link #alone()} indicates that plugin wants
     * run alone, {@link #isRunningAlone()} indicates that plugin will run alone.
     */
    boolean isRunningAlone();

    @Nonnull
    default TransformConfiguration transformConfiguration() {
        return TransformConfiguration.DEFAULT;
    }

    /**
     * TransformFlow的概念： 处理全部的构建产物（绝大部分为class文件）的过程为一次TransformFlow
     * 一个插件可以独立使用单独的TransformFlow，也可以搭车到全局的MainTransformFlow
     * TransformFlow: a process to handle the whole class files.
     * Each plugin can custom their own TransformFlow. Plugins can also take a ride to the global MainTransformFlow.
     *
     * @param mainFlow 全局的MainTransformFlow。The global MainTransformFlow
     * @return 当前插件关联的TransformFlow，通常返回mainFlow就可以。如果想让插件通过单独一个TransformFlow处理class文件，
     * 这个方法需要另外return 新的TransformFlow（可以是自定义的TransformFlow，也可以new 一个MainTransformFlow）。
     * If this plugin is related to the global MainTransformFlow, just return `mainFlow` is OK. If you want to handle
     * class files with a single TransformFlow, you should return a new TransformFlow. If so, this new TransformFlow would
     * only contain this plugin.
     */
    @Nonnull
    TransformFlow registerTransformFlow(@Nonnull MainTransformFlow mainFlow, @Nonnull TransformContext transformContext);

    /**
     * @return get the TransformFlow that the current plugin runs on
     */
    @Nullable
    TransformFlow getTransformFlow();


    /**
     * eg：dexBuilder
     * use {@link #hookTask()} and {@link #hookTask(Task)} instead
     */
    @Deprecated
    @Nullable
    default String hookTransformName() {
        return null;
    }

    /**
     * 是否使用hook模式运行插件，true表示使用hook模式
     */
    default boolean hookTask() {
        String hookTransformName = hookTransformName();
        return hookTransformName != null && !hookTransformName.isEmpty();
    }

    /**
     * 是否需要Hook这个task
     *
     * @param task 被hook的Task
     * @return {@link HookType#Before} 该task将被当前插件hook，插件并在task之前处理
     * {@link HookType#After} 该task将被当前插件hook，插件并在task之后处理
     * {@link HookType#None} 插件不hook这个task
     */
    @Nonnull
    default HookType hookTask(@Nonnull Task task) {
        String transformName = hookTransformName();
        if (transformName == null || transformName.isEmpty()) {
            throw new IllegalStateException("Can not find hook name form hookTransformName:" + transformName);
        }
        if (task instanceof TransformTask) {
            if (transformName.equalsIgnoreCase(((TransformTask) task).getTransform().getName())) {
                return HookType.Before;
            }
        } else {
            String taskName = task.getName().toLowerCase();
            //我们要去掉变体的后缀
            BaseVariant baseVariant = TaskKt.getVariant(task);
            if (baseVariant != null) {
                taskName = taskName.replaceAll(baseVariant.getName().toLowerCase(), "");
            }
            if (taskName.equalsIgnoreCase(transformName)) {
                return HookType.Before;
            }
        }
        return HookType.None;
    }

    default boolean shouldSaveCache() {
        return true;
    }

    /**
     * 准备执行的回调
     */
    default void startExecute(TransformContext transformContext) {
    }

    /**
     * 插件执行执行结束后的回调，不管成功和失败都会执行.做一些数据回收处理工作
     */
    default void afterExecute() throws Throwable {
    }

    enum HookType {
        None, Before, After
    }
}
