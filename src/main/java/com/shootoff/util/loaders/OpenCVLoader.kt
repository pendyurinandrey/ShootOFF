package com.shootoff.util.loaders

import org.apache.commons.lang.SystemUtils
import org.slf4j.LoggerFactory

object OpenCVLoader {

    private val logger = LoggerFactory.getLogger(OpenCVLoader::class.java)

    internal const val openCVNativeLibraryName = "opencv_java349"
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