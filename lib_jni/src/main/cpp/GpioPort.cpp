#include <fcntl.h>
#include <termios.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string>

#include "include/GpioPort.h"

using namespace std;

GpioPort::GpioPort() {}

GpioPort::GpioPort(const char *port, int len) {
    string portStr = port;
    GpioPort::port = portBuffer.append(portStr).c_str();
    GpioPort::portLen = len + 1;
    GpioPort::gpioPath = gpioPathPrefix.append(portStr).c_str();
    GpioPort::directPath = directPathBuffer.append(gpioPath).append(directSubPath).c_str();
    GpioPort::valuePath = valuePathBuffer.append(gpioPath).append(valueSubPath).c_str();
    LOGD("GpioPort>> Create GpioPort! port: %s, gpioPath: %s, directPath: %s, valuePath: %s", port,
         gpioPath, directPath, valuePath);
}

int GpioPort::openPort() {
    if (0 == access(gpioPath, F_OK)) {
        //已经打开
        LOGD("GpioPort>> open %s port success file %s already open!", port, gpioPath);
        isClose = false;
        return TRUE;
    }
    int fd = open(exportPath, O_WRONLY);
    if (fd < 0) {
        LOGE("GpioPort>> Error to open %s file to export %s!", exportPath, port);
        isClose = true;
        return FALSE;
    }
    char buffer[portLen];
    int len = snprintf(buffer, portLen, "%s", port);
    if (write(fd, buffer, len) < 0) {
        LOGE("GpioPort>> Fail to export %s port to file %s!", port, exportPath);
        close(fd);
        return FALSE;
    }
    close(fd);
    isClose = false;
    LOGD("GpioPort>> open %s port in file %s success, portLen %d!", port, exportPath, portLen);
    return TRUE;
}

int GpioPort::closePort() {
    int fd = open(unExportPath, O_WRONLY);
    if (fd < 0) {
        LOGE("GpioPort>> Error to open %s file to unexport %s!", unExportPath, port);
        return FALSE;
    }
    char buffer[portLen];
    int len = snprintf(buffer, portLen, "%s", port);
    if (write(fd, buffer, len) < 0) {
        LOGE("GpioPort>> Fail to unExport %s port to file %s!", port, unExportPath);
        close(fd);
        return FALSE;
    }
    close(fd);
    isClose = true;
    LOGD("GpioPort>> close %s in file %s success!", port, unExportPath);
    return TRUE;
}

int GpioPort::readStatus() {
    if (isClose) return FALSE;

    char value_str[3];
    int fd;
    fd = open(valuePath, O_RDONLY);
    if (fd < 0) {
        LOGE("GpioPort>> failed to open %s for reading status!", valuePath);
        return -1;
    }

    if (read(fd, value_str, 3) < 0) {
        LOGE("GpioPort>> failed to read status to %s file!", valuePath);
        close(fd);
        return -1;
    }

    close(fd);
    int value = atoi(value_str);
    return (value);
}

int GpioPort::writeStatus(int value) {
    if (isClose) return FALSE;

    static const char values_str[] = "01";
    int fd;
    fd = open(valuePath, O_WRONLY);
    if (fd < 0) {
        LOGE("GpioPort>> failed to open %s for writing status '%d'!", valuePath, value);
        return FALSE;
    }

    if (write(fd, &values_str[value > 0 ? 1 : 0], 1) < 0) {
        LOGE("GpioPort>> failed to write status '%d' to %s file!", value, valuePath);
        close(fd);
        return FALSE;
    }
    close(fd);
    LOGD("GpioPort>> writeStatus %s port, status %d to %s success!", port, value, valuePath);
    return TRUE;
}

int GpioPort::getDirection() {
    if (isClose) return FALSE;

    char value_str[3];
    int fd;
    fd = open(directPath, O_RDONLY);
    if (fd < 0) {
        LOGE("GpioPort>> failed to open %s for reading direction!", directPath);
        return -1;
    }

    if (read(fd, value_str, 3) < 0) {
        LOGE("GpioPort>> failed to read direction to %s file!", directPath);
        close(fd);
        return -1;
    }

    int value = -1;
    if (strncmp(value_str, "in", 2) == 0) {
        value = 0;
    } else if (strncmp(value_str, "out", 3) == 0) {
        value = 1;
    }
    close(fd);
    LOGD("GpioPort>> GpioPort getDirection %s port, direction %d from %s success!", port, value,
         directPath);
    return value;
}

int GpioPort::setDirection(int direction) {
    if (isClose) return FALSE;

    int fd;
    const char *dir_str = direction == 0 ? "in" : "out";
    fd = open(directPath, O_WRONLY);
    if (fd < 0) {
        LOGE("GpioPort>> failed to open %s for writing direction '%s'!", directPath, dir_str);
        return FALSE;
    }
    if (write(fd, dir_str, direction == 0 ? 2 : 3) < 0) {
        LOGE("GpioPort>> failed to write direction '%s' to %s file!", dir_str, directPath);
        close(fd);
        return FALSE;
    }

    close(fd);
    LOGD("GpioPort>> setDirection %s port, direction %s to %s success!", port, dir_str, directPath);
    return TRUE;
}