#ifndef GPIOPORTHELPER_GPIOPORT_H
#define GPIOPORTHELPER_GPIOPORT_H
#include <sys/ioctl.h>
#include <stdlib.h>
#include <stdio.h>
#include <string>

#include "PublicType.h"
#include "JniLog.h"

using namespace std;

/** Gpio port device class. */
class GpioPort {
private:
    string portBuffer = "";
    string directPathBuffer = "";
    string valuePathBuffer = "";
    const char *port;   // gpio port
    int portLen;
    const char *exportPath = "/sys/class/gpio/export";
    const char *unExportPath = "/sys/class/gpio/unexport";
    string gpioPathPrefix = "/sys/class/gpio/gpio";
    string directSubPath = "/direction";
    string valueSubPath = "/value";
    const char *directPath;
    const char *valuePath;
    const char *gpioPath;
    bool isClose;

public:
    GpioPort();

    GpioPort(const char *port, int len);

    int openPort();

    int closePort();

    int readStatus();

    int writeStatus(int value);

    int getDirection();

    int setDirection(int direction);
};
#endif //GPIOPORTHELPER_GPIOPORT_H
