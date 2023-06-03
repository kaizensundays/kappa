package com.kaizensundays.fusion.kappa.os;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;


public interface Kernel32 extends com.sun.jna.platform.win32.Kernel32 {
    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary(
            "kernel32", Kernel32.class, W32APIOptions.UNICODE_OPTIONS);
    WinNT.HANDLE CreateJobObject(WinBase.SECURITY_ATTRIBUTES attrs, String name);
    boolean SetInformationJobObject(HANDLE hJob, int JobObjectInfoClass, Pointer lpJobObjectInfo, int cbJobObjectInfoLength);
    boolean AssignProcessToJobObject(HANDLE hJob, HANDLE hProcess);
    boolean TerminateJobObject(HANDLE hJob, long uExitCode);
    int ResumeThread(HANDLE hThread);
    // 0x00000800
    int JOB_OBJECT_LIMIT_BREAKAWAY_OK = 2048;
    // 0x00002000
    int JOB_OBJECT_LIMIT_KILL_ON_JOB_CLOSE = 8192;
    int JobObjectExtendedLimitInformation = 9;

    @FieldOrder({"PerProcessUserTimeLimit", "PerJobUserTimeLimit", "LimitFlags", "MinimumWorkingSetSize", "MaximumWorkingSetSize", "ActiveProcessLimit", "Affinity", "PriorityClass", "SchedulingClass"})
    static class JOBJECT_BASIC_LIMIT_INFORMATION extends Structure {
        public LARGE_INTEGER PerProcessUserTimeLimit;
        public LARGE_INTEGER PerJobUserTimeLimit;
        public int LimitFlags;
        public SIZE_T MinimumWorkingSetSize;
        public SIZE_T MaximumWorkingSetSize;
        public int ActiveProcessLimit;
        public ULONG_PTR Affinity;
        public int PriorityClass;
        public int SchedulingClass;
    }
    @FieldOrder({"ReadOperationCount", "WriteOperationCount", "OtherOperationCount", "ReadTransferCount", "WriteTransferCount", "OtherTransferCount"})
    static class IO_COUNTERS extends Structure {
        public ULONGLONG ReadOperationCount;
        public ULONGLONG WriteOperationCount;
        public ULONGLONG OtherOperationCount;
        public ULONGLONG ReadTransferCount;
        public ULONGLONG WriteTransferCount;
        public ULONGLONG OtherTransferCount;
    }
    @FieldOrder({"BasicLimitInformation", "IoInfo", "ProcessMemoryLimit", "JobMemoryLimit", "PeakProcessMemoryUsed", "PeakJobMemoryUsed"})
    static class JOBJECT_EXTENDED_LIMIT_INFORMATION extends Structure {
        public JOBJECT_EXTENDED_LIMIT_INFORMATION() {}
        public JOBJECT_EXTENDED_LIMIT_INFORMATION(Pointer memory) {
            super(memory);
        }
        public JOBJECT_BASIC_LIMIT_INFORMATION BasicLimitInformation;
        public IO_COUNTERS IoInfo;
        public SIZE_T ProcessMemoryLimit;
        public SIZE_T JobMemoryLimit;
        public SIZE_T PeakProcessMemoryUsed;
        public SIZE_T PeakJobMemoryUsed;
        public static class ByReference extends JOBJECT_EXTENDED_LIMIT_INFORMATION implements Structure.ByReference {
            public ByReference() {}
            public ByReference(Pointer memory) {
                super(memory);
            }
        }
    }
}