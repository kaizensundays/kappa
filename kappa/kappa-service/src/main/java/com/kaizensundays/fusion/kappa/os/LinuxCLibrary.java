package com.kaizensundays.fusion.kappa.os;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Created: Saturday 11/26/2022, 11:44 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
public interface LinuxCLibrary extends Library {

    LinuxCLibrary INSTANCE = Native.load("c", LinuxCLibrary.class);

    int getpid();

}
