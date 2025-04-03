package com.ss.android.ugc.bytex.common.graph;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Objects;

public abstract class MemberEntity implements Jsonable {
    public static final transient int ACCESS_UNKNOWN = -1;
    protected int access;
    protected String className;
    protected String name;
    protected String desc;
    protected String signature;

    public MemberEntity(int access, String className, String name, String desc, String signature) {
        this.access = access;
        this.className = className;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
    }

    public MemberEntity(int access, String className, String name, String desc) {
        this(access, className, name, desc, null);
    }

    public abstract MemberType type();

    public int access() {
        return access;
    }

    public String className() {
        return className;
    }

    public String name() {
        return name;
    }

    public String desc() {
        return desc;
    }

    public String signature() {
        return signature;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberEntity that = (MemberEntity) o;
        return Objects.equals(className, that.className) &&
                Objects.equals(name, that.name) &&
                Objects.equals(desc, that.desc) &&
                Objects.equals(type(), that.type());
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, name, desc);
    }

    @Override
    public String toString() {
        return "MemberEntity{" +
                "access=" + access +
                ", className='" + className + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    @Override
    public void read(JsonReader jsonReader, Gson gson) throws IOException {
        throw new UnsupportedOperationException("Jsonable Not Supported");
    }

    @Override
    public void write(JsonWriter jsonWriter, Gson gson) throws IOException {
        throw new UnsupportedOperationException("Jsonable Not Supported");
    }
}
