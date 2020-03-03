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

package com.shootoff.camera

import com.github.sarxos.webcam.Webcam
import com.github.sarxos.webcam.WebcamCompositeDriver
import com.github.sarxos.webcam.ds.buildin.WebcamDefaultDriver
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver
import com.shootoff.camera.cameratypes.Camera
import com.shootoff.camera.cameratypes.IpCamera
import com.shootoff.camera.cameratypes.OptiTrackCamera
import com.shootoff.camera.cameratypes.SarxosCaptureCamera
import org.apache.commons.lang3.SystemUtils
import org.opencv.videoio.VideoCapture

object CameraFactory {

    init {
        Webcam.setDriver(DefaultCompositeDriver())
    }

    private const val MAXIMUM_CAMERAS_NUMBER = 5

    val cameras: List<Camera> by lazy {
        initCameras()
    }

    val defaultCamera: Camera? by lazy {
        if(cameras.isEmpty()) {
            null
        } else {
            cameras[0]
        }
    }

    private fun initCameras(): List<Camera> {
        val cameras = mutableListOf<Camera>()

        if(SystemUtils.IS_OS_MAC_OSX) {
            cameras.addAll(resolveMacOsCameras())
        } else {
            cameras.addAll(resolveCamerasForOtherPlatforms())
        }

        cameras.addAll(resolveAdditionalCameras())

        return cameras
    }

    private fun resolveMacOsCameras(): List<Camera> {
        // IP Cameras are not supported for OS X because issue in webcam-capture
        return resolveAvailableCamerasIndexes().map { it ->
            SarxosCaptureCamera("Camera $it", it)
        }
    }

    private fun resolveCamerasForOtherPlatforms(): List<Camera> {
        return Webcam.getWebcams().map { it ->
            if(it is IpCamDevice) {
                IpCamera(it)
            } else {
                SarxosCaptureCamera(it.name)
            }
        }
    }

    private fun resolveAdditionalCameras(): List<Camera> {
        val result = mutableListOf<Camera>()

        if(OptiTrackCamera.tryInit()) {
            result.add(OptiTrackCamera())
        }

        return result
    }

    private fun resolveAvailableCamerasIndexes(): List<Int> {
        val indexes = mutableListOf<Int>()
        for (i in 0 until MAXIMUM_CAMERAS_NUMBER) {
            val capture = VideoCapture(i)
            if (capture.isOpened) {
                indexes.add(i)
                capture.release()
            }
        }
        return indexes
    }

    private class DefaultCompositeDriver: WebcamCompositeDriver(WebcamDefaultDriver(), IpCamDriver())
}