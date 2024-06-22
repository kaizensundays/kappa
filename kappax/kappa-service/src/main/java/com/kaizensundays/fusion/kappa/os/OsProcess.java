package com.kaizensundays.fusion.kappa.os;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

@SuppressWarnings("ALL")
interface OsProcess {

    Map<String, String> getEnvironment();

    void setEnvironmentVariable(String name, String value);

    void copyOutputTo(OutputStream out);

    void setInput(String allInput);

    void setWorkingDirectory(File workingDirectory);

    int executeAsync();

    @SuppressWarnings("EmptyMethod")
    void waitFor();

    @SuppressWarnings("SameReturnValue")
    boolean destroy();

    @SuppressWarnings("SameReturnValue")
    int getExitCode();

    String getStdOut();

    boolean isRunning();

}