#include <fcntl.h>
#include <termios.h>
#include <unistd.h>
#include <string>

#include "include/SerialPort.h"

using namespace std;

SerialPort::SerialPort() {}

SerialPort::SerialPort(const char *path) {
    string pathStr = path;
    SerialPort::path = pathBuffer.append(pathStr).c_str();
    LOGD("SerialPort>> Create SerialPort! path: %s", path);
}

speed_t SerialPort::getBaudrate(int baudrate) {
    switch (baudrate) {
        case 0:
            return B0;
        case 50:
            return B50;
        case 75:
            return B75;
        case 110:
            return B110;
        case 134:
            return B134;
        case 150:
            return B150;
        case 200:
            return B200;
        case 300:
            return B300;
        case 600:
            return B600;
        case 1200:
            return B1200;
        case 1800:
            return B1800;
        case 2400:
            return B2400;
        case 4800:
            return B4800;
        case 9600:
            return B9600;
        case 19200:
            return B19200;
        case 38400:
            return B38400;
        case 57600:
            return B57600;
        case 115200:
            return B115200;
        case 230400:
            return B230400;
        case 460800:
            return B460800;
        case 500000:
            return B500000;
        case 576000:
            return B576000;
        case 921600:
            return B921600;
        case 1000000:
            return B1000000;
        case 1152000:
            return B1152000;
        case 1500000:
            return B1500000;
        case 2000000:
            return B2000000;
        case 2500000:
            return B2500000;
        case 3000000:
            return B3000000;
        case 3500000:
            return B3500000;
        case 4000000:
            return B4000000;
        default:
            return -1;
    }
}

int SerialPort::setSpeed(int fd, int speed) {
    speed_t b_speed;
    struct termios cfg;
    b_speed = getBaudrate(speed);
    if (tcgetattr(fd, &cfg)) {
        LOGE("SerialPort>> tcgetattr invocation method failed!");
        close(fd);
        return FALSE;
    }

    cfmakeraw(&cfg);
    cfsetispeed(&cfg, b_speed);
    cfsetospeed(&cfg, b_speed);

    if (tcsetattr(fd, TCSANOW, &cfg)) {
        LOGE("SerialPort>> tcsetattr invocation method failed!");
        close(fd);
        return FALSE;
    }
    LOGD("SerialPort>> setSpeed %d success", speed);
    return TRUE;
}

int SerialPort::setParity(int fd, int databits, int stopbits, char parity) {
    struct termios options;
    if (tcgetattr(fd, &options) != 0) {
        LOGE("SerialPort>> The method tcgetattr exception!");
        return FALSE;
    }
    options.c_cflag &= ~CSIZE;
    switch (databits)                                           /* Set data bits */
    {
        case 7:
            options.c_cflag |= CS7;
            break;
        case 8:
            options.c_cflag |= CS8;
            break;
        default:
            LOGE("SerialPort>> Unsupported data size!");
            return FALSE;
    }
    switch (parity) {
        case 'n':
        case 'N':
            options.c_cflag &= ~PARENB;                         /* Clear parity enable */
            options.c_iflag &= ~INPCK;                          /* Enable parity checking */
            break;
        case 'o':
        case 'O':
            options.c_cflag |= (PARODD | PARENB);               /* Set odd checking */
            options.c_iflag |= INPCK;                           /* Disnable parity checking */
            break;
        case 'e':
        case 'E':
            options.c_cflag |= PARENB;                          /* Enable parity */
            options.c_cflag &= ~PARODD;                         /* Transformation even checking */
            options.c_iflag |= INPCK;                           /* Disnable parity checking */
            break;
        case 'S':
        case 's':  /*as no parity*/
            options.c_cflag &= ~PARENB;
            options.c_cflag &= ~CSTOPB;
            break;
        default:
            LOGE("SerialPort>> Unsupported parity!");
            return FALSE;
    }
    /* 设置停止位*/
    switch (stopbits) {
        case 1:
            options.c_cflag &= ~CSTOPB;
            break;
        case 2:
            options.c_cflag |= CSTOPB;
            break;
        default:
            LOGE("SerialPort>> Unsupported stop bits!");
            return FALSE;
    }
    /* Set input parity option */
    if (parity != 'n')
        options.c_iflag |= INPCK;
    tcflush(fd, TCIFLUSH);
    options.c_cc[VTIME] = 150;                                  /* Set timeout to 15 seconds */
    options.c_cc[VMIN] = 0;                                     /* Update the options and do it NOW */
    if (tcsetattr(fd, TCSANOW, &options) != 0) {
        LOGE("SerialPort>> The method tcsetattr exception!");
        return FALSE;
    }
    LOGD("SerialPort>> setParity success");
    return TRUE;
}

int SerialPort::openSerialPort(SerialPortConfig config) {
    fd = open(path, O_RDWR);
    if (fd < 0) {
        LOGE("SerialPort>> Error to open %s port file!", path);
        isClose = true;
        return FALSE;
    }

    if (!setSpeed(fd, config.baudrate)) {
        LOGE("SerialPort>> Set Speed Error!");
        return FALSE;
    }
    if (!setParity(fd, config.databits, config.stopbits, config.parity)) {
        LOGE("SerialPort>> Set Parity Error!");
        return FALSE;
    }
    LOGD("SerialPort>> Open Success fd:%d!", fd);
    isClose = false;
    return TRUE;
}

int SerialPort::readData(BYTE *data, int size) {
    if (isClose) return 0;
    int retval = -1;
    for (int i = 0; i < size; i++) {
        data[i] = static_cast<char>(0xFF);
    }
    fd_set rfds;
    FD_ZERO(&rfds);     //清空集合
    FD_SET(fd, &rfds);  //把要检测的句柄fd加入到集合里
    // TODO Async operation. Thread blocking.
    if (FD_ISSET(fd, &rfds)) {
        FD_ZERO(&rfds);
        FD_SET(fd, &rfds);
        struct timeval select_timeval;
        select_timeval.tv_sec = 0;
        select_timeval.tv_usec = 1000;
        retval = select(fd + 1, &rfds, NULL, NULL, &select_timeval);
        if (retval == -1) {
            LOGE("SerialPort>> Select error!");
        } else if (retval) {
            retval = static_cast<int>(read(fd, data, static_cast<size_t>(size)));
            LOGD("SerialPort>> This device has data! retval: %d", retval);
        } else {
            //LOGE("SerialPort>> Select timeout!");
        }
    }
    return retval;
}

int SerialPort::writeData(BYTE *data, int len) {
    int result;
    result = static_cast<int>(write(fd, data, static_cast<size_t>(len)));
    LOGD("SerialPort>> writeData success len: %d, result: %d", len, result);
    return result < 0 ? FALSE : TRUE ;
}

int SerialPort::setMode(int mode){
   struct termios options;
   if(tcgetattr(fd, &options) != 0){
       LOGE("SerialPort>> The method tcgetattr exception!");
       return FALSE;
   }
   if(mode != 0){
       if(mode == 1){
           options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);   //input
           options.c_oflag &= ~OPOST;                            //out put
       }else if(mode == 2){
           options.c_lflag |= (ICANON | ECHO | ECHOE | ISIG);    //input
           options.c_oflag |= OPOST;                             //out put
       }
       if(tcsetattr(fd, TCSANOW, &options) != 0){
           LOGE("SerialPort>> The method tcsetattr exception!");
           return FALSE;
       }
   }
   LOGD("SerialPort>> writeData success mode: %d", mode);
   return TRUE;
}

int SerialPort::closePort() {
    LOGD("SerialPort>> Close port!");
    isClose = true;
    close(fd);
    return TRUE;
}