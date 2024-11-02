#pragma once

#include <stdbool.h>

/* make this header C++ friendly. */
#ifdef     __cplusplus
extern "C" {
#endif  

#ifdef   __GNUC__
# define UNUSED(x) UNUSED_ ## x __attribute__((__unused__))
#else
# define UNUSED(X) UNUSED_ ## x
#endif

extern volatile bool prophet_initialized;

// make this header C++ friendly.
#ifdef     __cplusplus
}
#endif    //__cplusplus
