// This file is part of OpenCV project.
// It is subject to the license terms in the LICENSE file found in the top-level directory
// of this distribution and at http://opencv.org/license.html.
//
// Copyright (C) 2018 Intel Corporation


#ifndef OPENCV_GAPI_CPU_CORE_API_HPP
#define OPENCV_GAPI_CPU_CORE_API_HPP

#include <opencv2/gapi/gkernel.hpp> // GKernelPackage
#include <opencv2/gapi/own/exports.hpp> // GAPI_EXPORTS

namespace cv {
    namespace gapi {
        namespace core {
            namespace cpu {

                GAPI_EXPORTS_W cv::GKernelPackage

                kernels();

            } // namespace cpu
        } // namespace core
    } // namespace gapi
} // namespace cv


#endif // OPENCV_GAPI_CPU_CORE_API_HPP
