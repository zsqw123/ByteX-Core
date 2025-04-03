# ByteX-Core

Remove all not-Core APIs in [ByteX](https://github.com/bytedance/ByteX) and publish to Maven Central instead of
ByteDance private repo.

[Maven Central: ByteX-Core](https://central.sonatype.com/artifact/host.bytedance.byteX/TransformEngine)

![Maven Central](https://img.shields.io/maven-central/v/host.bytedance.byteX/TransformEngine)

Version Alignment:

| ByteX Version | ByteX-Core Version |
|:-------------:|:------------------:|
|     0.3.0     |       0.3.1        |

This repo only provides those 2 artifacts which taken from ByteX:

```groovy
dependencies {
    implementation("host.bytedance.byteX:TransformEngine:<version>")
    implementation("host.bytedance.byteX:PluginConfigProcessor:<version>")
}
```
