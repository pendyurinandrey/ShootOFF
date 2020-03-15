/*
 * ShootOFF - Software for Laser Dry Fire Training
 * Copyright (C) 2020 pendyurinandrey
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.shootoff.util.loaders

import org.apache.commons.lang.SystemUtils
import org.slf4j.LoggerFactory

object OpenCVLoader {

    private val logger = LoggerFactory.getLogger(OpenCVLoader::class.java)

    internal const val openCVNativeLibraryName = "opencv_java420"
    private var isInitialized = false

    fun loadSharedLibs() {
        if (!isInitialized) {
            try {
                // Try to find OpenCV native lib in 'java.library.path' system property
                System.loadLibrary(openCVNativeLibraryName)
                logger.info("Loaded existing OpenCV library %s from 'java.library.path'".format(openCVNativeLibraryName))
            } catch (ex: UnsatisfiedLinkError) {
                logger.info(
                        ("Unable to load OpenCV native lib in 'java.library.path'. " +
                                "Loading failed with '%s'. Trying to fallback...").format(ex.message)
                )
                val loader = resolveLoader()
                loader.load()
            } finally {
                isInitialized = true
            }
        }
    }

    private fun resolveLoader(): AbstractOpenCVLoader {
        if (SystemUtils.IS_OS_WINDOWS) {
            return WindowsOpenCVLoader()
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            return MacOsCVLoader()
        } else {
            throw UnsupportedPlatformException("Current OS is not supported")
        }
    }
}

private abstract class AbstractOpenCVLoader {
    protected val nativeLibsDirName = "native/opencv"
    abstract fun load()

}

private class WindowsOpenCVLoader : AbstractOpenCVLoader() {
    private val nativeLibsWindowsDirName = "windows"
    private val windowsNativeLibsExtension = "dll"

    override fun load() {
        val libPath = "/%s/%s/%s/%s.%s".format(
                nativeLibsDirName,
                nativeLibsWindowsDirName,
                NativeUtils.Arch.getCurrentArch().archName,
                OpenCVLoader.openCVNativeLibraryName,
                windowsNativeLibsExtension
        )

        NativeUtils.loadLibraryFromJar(libPath)
    }

}

private class MacOsCVLoader : AbstractOpenCVLoader() {
    private val nativeLibsOsxDirName = "osx"
    private val openCVNativeLibName = "libopencv_java420.dylib"

    override fun load() {
        val libPath = "/%s/%s/%s".format(
                nativeLibsDirName,
                nativeLibsOsxDirName,
                openCVNativeLibName
        )
        NativeUtils.loadLibraryFromJar(libPath)
    }

}

