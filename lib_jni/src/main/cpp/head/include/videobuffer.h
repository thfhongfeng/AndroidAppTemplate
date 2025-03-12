#ifndef VIDEO_BUFFER_H
#define VIDEO_BUFFER_H

#include <stdint.h>

//typedef unsigned long long  u_64;

typedef void (*mids_frame_cb)(int, unsigned char *, int, int, int, unsigned long);

int mids_vbuf_init(mids_frame_cb cb);

int mids_vbuf_deinit();

int mids_vbuf_start_frame(int ch);

int mids_vbuf_stop_frame();

#endif
