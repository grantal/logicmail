/*-
 * Copyright (c) 2006, Derek Konigsberg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the project nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.logicprobe.LogicMail.util;

/**
 * This file is a modified version of the one covered by the comments below.
 * The modifications involved changes in the public interface, general formatting,
 * and size reduction.
 * While the RIM API includes an MD5Digest class, it requires a digitally signed
 * binary to be usable.  Since requiring code signing would impose an undesirable
 * restriction on this project, this alternative implementation is provided.
 */

/**
 * Fast implementation of RSA's MD5 hash generator in Java JDK Beta-2 or higher.
 * <p>
 * Originally written by Santeri Paavolainen, Helsinki Finland 1996.<br>
 * (c) Santeri Paavolainen, Helsinki Finland 1996<br>
 * Many changes Copyright (c) 2002 - 2005 Timothy W Macinta<br>
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * <p>
 * See http://www.twmacinta.com/myjava/fast_md5.php for more information on this
 * file and the related files.
 * <p>
 * This was originally a rather straight re-implementation of the reference
 * implementation given in RFC1321 by RSA. It passes the MD5 test suite as
 * defined in RFC1321.
 * <p>
 * Many optimizations made by Timothy W Macinta. Reduced time to checksum a test
 * file in Java alone to roughly half the time taken compared with
 * java.security.MessageDigest (within an intepretter). Also added an optional
 * native method to reduce the time even further. See
 * http://www.twmacinta.com/myjava/fast_md5.php for further information on the
 * time improvements achieved.
 * <p>
 * Some bug fixes also made by Timothy W Macinta.
 * <p>
 * Please note: I (Timothy Macinta) have put this code in the com.twmacinta.util
 * package only because it came without a package. I was not the the original
 * author of the code, although I did optimize it (substantially) and fix some
 * bugs.
 * <p>
 * This Java class has been derived from the RSA Data Security, Inc. MD5
 * Message-Digest Algorithm and its reference implementation.
 * <p>
 * This class will attempt to use a native method to quickly compute checksums
 * when the appropriate native library is available. On Linux, this library
 * should be named "MD5.so" and on Windows it should be named "MD5.dll". The
 * code will attempt to locate the library in the following locations in the
 * order given:
 *
 * <ol>
 * <li>The path specified by the system property
 * "com.twmacinta.util.MD5.NATIVE_LIB_FILE" (be sure to include "MD5.so" or
 * "MD5.dll" as appropriate at the end of the path).
 * <li>A platform specific directory beneath the "lib/arch/" directory. On
 * Linux for x86, this is "lib/arch/linux_x86/". On Windows for x86, this is
 * "lib/arch/win32_x86/".
 * <li>Within the "lib/" directory.
 * <li>Within the current directory.
 * </ol>
 *
 * <p>
 * If the library is not found, the code will fall back to the default (slower)
 * Java code.
 * <p>
 * As a side effect of having the code search for the native library,
 * SecurityExceptions might be thrown on JVMs that have a restrictive
 * SecurityManager. The initialization code attempts to silently discard these
 * exceptions and continue, but many SecurityManagers will attempt to notify the
 * user directly of all SecurityExceptions thrown. Consequently, the code has
 * provisions for skipping the search for the native library. Any of these
 * provisions may be used to skip the search as long as they are performed
 * <i>before</i> the first instance of a com.twmacinta.util.MD5 object is
 * constructed (note that the convenience stream objects will implicitly create
 * an MD5 object).
 * <p>
 * The first option is to set the system property
 * "com.twmacinta.util.MD5.NO_NATIVE_LIB" to "true" or "1". Unfortunately,
 * SecurityManagers may also choose to disallow system property setting, so this
 * won't be of use in all cases.
 * <p>
 * The second option is to call com.twmacinta.util.MD5.initNativeLibrary(true)
 * before any MD5 objects are constructed.
 *
 * @author Santeri Paavolainen <sjpaavol@cc.helsinki.fi>
 * @author Timothy W Macinta (twm@alum.mit.edu) (optimizations and bug fixes)
 */

public class MD5 {
    private static class MD5State {
        /**
         * 128-bit state
         */
        int state[];
        
        /**
         * 64-bit character count
         */
        long count;
        
        /**
         * 64-byte buffer (512 bits) for storing to-be-hashed characters
         */
        byte buffer[];
        
        public MD5State() {
            buffer = new byte[64];
            count = 0;
            state = new int[4];
            
            state[0] = 0x67452301;
            state[1] = 0xefcdab89;
            state[2] = 0x98badcfe;
            state[3] = 0x10325476;
        }
        
        /** Create this State as a copy of another state */
        public MD5State(MD5State from) {
            this();
            int i;
            for (i = 0; i < buffer.length; i++)
                this.buffer[i] = from.buffer[i];
            for (i = 0; i < state.length; i++)
                this.state[i] = from.state[i];
            this.count = from.count;
        }
    };
    
    private MD5State state;
    private MD5State finals;
    
    private static final byte padding[]	=
        {(byte)0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0 };
    
    /*
     * len += shift; for (int i = 0; shift < len; i++, shift += 4) { out[i] =
     * ((int) (buffer[shift] & 0xff)) | (((int) (buffer[shift + 1] & 0xff)) <<
     * 8) | (((int) (buffer[shift + 2] & 0xff)) << 16) | (((int)
     * buffer[shift + 3]) << 24); }
     */

    
    private final void decode( final byte buffer[], final int shift, final int[] out) {
        out[0] = (buffer[shift] & 0xff) | ((buffer[shift + 1] & 0xff) << 8)
        | ((buffer[shift + 2] & 0xff) << 16)
        | (buffer[shift + 3] << 24);
        out[1] = (buffer[shift + 4] & 0xff) | ((buffer[shift + 5] & 0xff) << 8)
        | ((buffer[shift + 6] & 0xff) << 16)
        | (buffer[shift + 7] << 24);
        out[2] = (buffer[shift + 8] & 0xff) | ((buffer[shift + 9] & 0xff) << 8)
        | ((buffer[shift + 10] & 0xff) << 16)
        | (buffer[shift + 11] << 24);
        out[3] = (buffer[shift + 12] & 0xff)
        | ((buffer[shift + 13] & 0xff) << 8)
        | ((buffer[shift + 14] & 0xff) << 16)
        | (buffer[shift + 15] << 24);
        out[4] = (buffer[shift + 16] & 0xff)
        | ((buffer[shift + 17] & 0xff) << 8)
        | ((buffer[shift + 18] & 0xff) << 16)
        | (buffer[shift + 19] << 24);
        out[5] = (buffer[shift + 20] & 0xff)
        | ((buffer[shift + 21] & 0xff) << 8)
        | ((buffer[shift + 22] & 0xff) << 16)
        | (buffer[shift + 23] << 24);
        out[6] = (buffer[shift + 24] & 0xff)
        | ((buffer[shift + 25] & 0xff) << 8)
        | ((buffer[shift + 26] & 0xff) << 16)
        | (buffer[shift + 27] << 24);
        out[7] = (buffer[shift + 28] & 0xff)
        | ((buffer[shift + 29] & 0xff) << 8)
        | ((buffer[shift + 30] & 0xff) << 16)
        | (buffer[shift + 31] << 24);
        out[8] = (buffer[shift + 32] & 0xff)
        | ((buffer[shift + 33] & 0xff) << 8)
        | ((buffer[shift + 34] & 0xff) << 16)
        | (buffer[shift + 35] << 24);
        out[9] = (buffer[shift + 36] & 0xff)
        | ((buffer[shift + 37] & 0xff) << 8)
        | ((buffer[shift + 38] & 0xff) << 16)
        | (buffer[shift + 39] << 24);
        out[10] = (buffer[shift + 40] & 0xff)
        | ((buffer[shift + 41] & 0xff) << 8)
        | ((buffer[shift + 42] & 0xff) << 16)
        | (buffer[shift + 43] << 24);
        out[11] = (buffer[shift + 44] & 0xff)
        | ((buffer[shift + 45] & 0xff) << 8)
        | ((buffer[shift + 46] & 0xff) << 16)
        | (buffer[shift + 47] << 24);
        out[12] = (buffer[shift + 48] & 0xff)
        | ((buffer[shift + 49] & 0xff) << 8)
        | ((buffer[shift + 50] & 0xff) << 16)
        | (buffer[shift + 51] << 24);
        out[13] = (buffer[shift + 52] & 0xff)
        | ((buffer[shift + 53] & 0xff) << 8)
        | ((buffer[shift + 54] & 0xff) << 16)
        | (buffer[shift + 55] << 24);
        out[14] = (buffer[shift + 56] & 0xff)
        | ((buffer[shift + 57] & 0xff) << 8)
        | ((buffer[shift + 58] & 0xff) << 16)
        | (buffer[shift + 59] << 24);
        out[15] = (buffer[shift + 60] & 0xff)
        | ((buffer[shift + 61] & 0xff) << 8)
        | ((buffer[shift + 62] & 0xff) << 16)
        | (buffer[shift + 63] << 24);
    }
    
    private final void transform(MD5State state, byte buffer[], int shift, int[] decode_buf) {
        int a = state.state[0], b = state.state[1], c = state.state[2], d = state.state[3], x[] = decode_buf;
        
        decode(buffer, shift, decode_buf);
        
        /* Round 1 */
        a += ((b & c) | (~b & d)) + x[0] + 0xd76aa478; /* 1 */
        a = ((a << 7) | (a >>> 25)) + b;
        d += ((a & b) | (~a & c)) + x[1] + 0xe8c7b756; /* 2 */
        d = ((d << 12) | (d >>> 20)) + a;
        c += ((d & a) | (~d & b)) + x[2] + 0x242070db; /* 3 */
        c = ((c << 17) | (c >>> 15)) + d;
        b += ((c & d) | (~c & a)) + x[3] + 0xc1bdceee; /* 4 */
        b = ((b << 22) | (b >>> 10)) + c;
        
        a += ((b & c) | (~b & d)) + x[4] + 0xf57c0faf; /* 5 */
        a = ((a << 7) | (a >>> 25)) + b;
        d += ((a & b) | (~a & c)) + x[5] + 0x4787c62a; /* 6 */
        d = ((d << 12) | (d >>> 20)) + a;
        c += ((d & a) | (~d & b)) + x[6] + 0xa8304613; /* 7 */
        c = ((c << 17) | (c >>> 15)) + d;
        b += ((c & d) | (~c & a)) + x[7] + 0xfd469501; /* 8 */
        b = ((b << 22) | (b >>> 10)) + c;
        
        a += ((b & c) | (~b & d)) + x[8] + 0x698098d8; /* 9 */
        a = ((a << 7) | (a >>> 25)) + b;
        d += ((a & b) | (~a & c)) + x[9] + 0x8b44f7af; /* 10 */
        d = ((d << 12) | (d >>> 20)) + a;
        c += ((d & a) | (~d & b)) + x[10] + 0xffff5bb1; /* 11 */
        c = ((c << 17) | (c >>> 15)) + d;
        b += ((c & d) | (~c & a)) + x[11] + 0x895cd7be; /* 12 */
        b = ((b << 22) | (b >>> 10)) + c;
        
        a += ((b & c) | (~b & d)) + x[12] + 0x6b901122; /* 13 */
        a = ((a << 7) | (a >>> 25)) + b;
        d += ((a & b) | (~a & c)) + x[13] + 0xfd987193; /* 14 */
        d = ((d << 12) | (d >>> 20)) + a;
        c += ((d & a) | (~d & b)) + x[14] + 0xa679438e; /* 15 */
        c = ((c << 17) | (c >>> 15)) + d;
        b += ((c & d) | (~c & a)) + x[15] + 0x49b40821; /* 16 */
        b = ((b << 22) | (b >>> 10)) + c;
        
        /* Round 2 */
        a += ((b & d) | (c & ~d)) + x[1] + 0xf61e2562; /* 17 */
        a = ((a << 5) | (a >>> 27)) + b;
        d += ((a & c) | (b & ~c)) + x[6] + 0xc040b340; /* 18 */
        d = ((d << 9) | (d >>> 23)) + a;
        c += ((d & b) | (a & ~b)) + x[11] + 0x265e5a51; /* 19 */
        c = ((c << 14) | (c >>> 18)) + d;
        b += ((c & a) | (d & ~a)) + x[0] + 0xe9b6c7aa; /* 20 */
        b = ((b << 20) | (b >>> 12)) + c;
        
        a += ((b & d) | (c & ~d)) + x[5] + 0xd62f105d; /* 21 */
        a = ((a << 5) | (a >>> 27)) + b;
        d += ((a & c) | (b & ~c)) + x[10] + 0x02441453; /* 22 */
        d = ((d << 9) | (d >>> 23)) + a;
        c += ((d & b) | (a & ~b)) + x[15] + 0xd8a1e681; /* 23 */
        c = ((c << 14) | (c >>> 18)) + d;
        b += ((c & a) | (d & ~a)) + x[4] + 0xe7d3fbc8; /* 24 */
        b = ((b << 20) | (b >>> 12)) + c;
        
        a += ((b & d) | (c & ~d)) + x[9] + 0x21e1cde6; /* 25 */
        a = ((a << 5) | (a >>> 27)) + b;
        d += ((a & c) | (b & ~c)) + x[14] + 0xc33707d6; /* 26 */
        d = ((d << 9) | (d >>> 23)) + a;
        c += ((d & b) | (a & ~b)) + x[3] + 0xf4d50d87; /* 27 */
        c = ((c << 14) | (c >>> 18)) + d;
        b += ((c & a) | (d & ~a)) + x[8] + 0x455a14ed; /* 28 */
        b = ((b << 20) | (b >>> 12)) + c;
        
        a += ((b & d) | (c & ~d)) + x[13] + 0xa9e3e905; /* 29 */
        a = ((a << 5) | (a >>> 27)) + b;
        d += ((a & c) | (b & ~c)) + x[2] + 0xfcefa3f8; /* 30 */
        d = ((d << 9) | (d >>> 23)) + a;
        c += ((d & b) | (a & ~b)) + x[7] + 0x676f02d9; /* 31 */
        c = ((c << 14) | (c >>> 18)) + d;
        b += ((c & a) | (d & ~a)) + x[12] + 0x8d2a4c8a; /* 32 */
        b = ((b << 20) | (b >>> 12)) + c;
        
        /* Round 3 */
        a += (b ^ c ^ d) + x[5] + 0xfffa3942; /* 33 */
        a = ((a << 4) | (a >>> 28)) + b;
        d += (a ^ b ^ c) + x[8] + 0x8771f681; /* 34 */
        d = ((d << 11) | (d >>> 21)) + a;
        c += (d ^ a ^ b) + x[11] + 0x6d9d6122; /* 35 */
        c = ((c << 16) | (c >>> 16)) + d;
        b += (c ^ d ^ a) + x[14] + 0xfde5380c; /* 36 */
        b = ((b << 23) | (b >>> 9)) + c;
        
        a += (b ^ c ^ d) + x[1] + 0xa4beea44; /* 37 */
        a = ((a << 4) | (a >>> 28)) + b;
        d += (a ^ b ^ c) + x[4] + 0x4bdecfa9; /* 38 */
        d = ((d << 11) | (d >>> 21)) + a;
        c += (d ^ a ^ b) + x[7] + 0xf6bb4b60; /* 39 */
        c = ((c << 16) | (c >>> 16)) + d;
        b += (c ^ d ^ a) + x[10] + 0xbebfbc70; /* 40 */
        b = ((b << 23) | (b >>> 9)) + c;
        
        a += (b ^ c ^ d) + x[13] + 0x289b7ec6; /* 41 */
        a = ((a << 4) | (a >>> 28)) + b;
        d += (a ^ b ^ c) + x[0] + 0xeaa127fa; /* 42 */
        d = ((d << 11) | (d >>> 21)) + a;
        c += (d ^ a ^ b) + x[3] + 0xd4ef3085; /* 43 */
        c = ((c << 16) | (c >>> 16)) + d;
        b += (c ^ d ^ a) + x[6] + 0x04881d05; /* 44 */
        b = ((b << 23) | (b >>> 9)) + c;
        
        a += (b ^ c ^ d) + x[9] + 0xd9d4d039; /* 33 */
        a = ((a << 4) | (a >>> 28)) + b;
        d += (a ^ b ^ c) + x[12] + 0xe6db99e5; /* 34 */
        d = ((d << 11) | (d >>> 21)) + a;
        c += (d ^ a ^ b) + x[15] + 0x1fa27cf8; /* 35 */
        c = ((c << 16) | (c >>> 16)) + d;
        b += (c ^ d ^ a) + x[2] + 0xc4ac5665; /* 36 */
        b = ((b << 23) | (b >>> 9)) + c;
        
        /* Round 4 */
        a += (c ^ (b | ~d)) + x[0] + 0xf4292244; /* 49 */
        a = ((a << 6) | (a >>> 26)) + b;
        d += (b ^ (a | ~c)) + x[7] + 0x432aff97; /* 50 */
        d = ((d << 10) | (d >>> 22)) + a;
        c += (a ^ (d | ~b)) + x[14] + 0xab9423a7; /* 51 */
        c = ((c << 15) | (c >>> 17)) + d;
        b += (d ^ (c | ~a)) + x[5] + 0xfc93a039; /* 52 */
        b = ((b << 21) | (b >>> 11)) + c;
        
        a += (c ^ (b | ~d)) + x[12] + 0x655b59c3; /* 53 */
        a = ((a << 6) | (a >>> 26)) + b;
        d += (b ^ (a | ~c)) + x[3] + 0x8f0ccc92; /* 54 */
        d = ((d << 10) | (d >>> 22)) + a;
        c += (a ^ (d | ~b)) + x[10] + 0xffeff47d; /* 55 */
        c = ((c << 15) | (c >>> 17)) + d;
        b += (d ^ (c | ~a)) + x[1] + 0x85845dd1; /* 56 */
        b = ((b << 21) | (b >>> 11)) + c;
        
        a += (c ^ (b | ~d)) + x[8] + 0x6fa87e4f; /* 57 */
        a = ((a << 6) | (a >>> 26)) + b;
        d += (b ^ (a | ~c)) + x[15] + 0xfe2ce6e0; /* 58 */
        d = ((d << 10) | (d >>> 22)) + a;
        c += (a ^ (d | ~b)) + x[6] + 0xa3014314; /* 59 */
        c = ((c << 15) | (c >>> 17)) + d;
        b += (d ^ (c | ~a)) + x[13] + 0x4e0811a1; /* 60 */
        b = ((b << 21) | (b >>> 11)) + c;
        
        a += (c ^ (b | ~d)) + x[4] + 0xf7537e82; /* 61 */
        a = ((a << 6) | (a >>> 26)) + b;
        d += (b ^ (a | ~c)) + x[11] + 0xbd3af235; /* 62 */
        d = ((d << 10) | (d >>> 22)) + a;
        c += (a ^ (d | ~b)) + x[2] + 0x2ad7d2bb; /* 63 */
        c = ((c << 15) | (c >>> 17)) + d;
        b += (d ^ (c | ~a)) + x[9] + 0xeb86d391; /* 64 */
        b = ((b << 21) | (b >>> 11)) + c;
        
        state.state[0] += a;
        state.state[1] += b;
        state.state[2] += c;
        state.state[3] += d;
    }
    
    /**
     * Updates hash with the bytebuffer given (using at maximum length bytes
     * from that buffer)
     *
     * @param stat
     *            Which state is updated
     * @param buffer
     *            Array of bytes to be hashed
     * @param offset
     *            Offset to buffer array
     * @param length
     *            Use at maximum `length' bytes (absolute maximum is
     *            buffer.length)
     */
    private final void update(MD5State stat, byte buffer[], int offset, int length) {
        int index, partlen, i, start;
        finals = null;
        /* Length can be told to be shorter, but not inter */
        if ((length - offset) > buffer.length)
            length = buffer.length - offset;
        
        /* compute number of bytes mod 64 */
        
        index = (int) (stat.count & 0x3f);
        stat.count += length;
        
        partlen = 64 - index;
        
        if (length >= partlen) {
            // update state (using only Java) to reflect input
            int[] decode_buf = new int[16];
            if (partlen == 64) {
                partlen = 0;
            } else {
                for (i = 0; i < partlen; i++)
                    stat.buffer[i + index] = buffer[i + offset];
                transform(stat, stat.buffer, 0, decode_buf);
            }
            for (i = partlen; (i + 63) < length; i += 64) {
                transform(stat, buffer, i + offset, decode_buf);
            }
            index = 0;
        } else
            i = 0;
        /* buffer remaining input */
        if (i < length) {
            start = i;
            for (; i < length; i++) {
                stat.buffer[index + i - start] = buffer[i + offset];
            }
        }
    }
    
    private static final byte[] encode( final int input[], final int len) {
        int i, j;
        byte out[];
        out = new byte[len];
        for (i = j = 0; j < len; i++, j += 4) {
            out[j] = (byte) (input[i] & 0xff);
            out[j + 1] = (byte) ((input[i] >>> 8) & 0xff);
            out[j + 2] = (byte) ((input[i] >>> 16) & 0xff);
            out[j + 3] = (byte) ((input[i] >>> 24) & 0xff);
        }
        return out;
    }
    
    public MD5() {
        state = new MD5State();
        finals = null;
    }
    
    public void reset() {
        state = new MD5State();
        finals = null;
    }
    
    /**
     * Updates hash with given array of bytes
     *
     * @param buffer
     *            Array of bytes to use for updating the hash
     */
    public final void update( final byte buffer[]) {
        if( buffer == null )
            return;
        update( state, buffer,0, buffer.length );
    }
    
    /**
     * Returns array of bytes (16 bytes) representing hash as of the current
     * state of this object. Note: getting a hash does not invalidate the hash
     * object, it only creates a copy of the real state which is finalized.
     *
     * @return Array of 16 bytes, the hash of all updated bytes
     */
    public synchronized final byte[] getDigest() {
        byte bits[];
        int index, padlen;
        MD5State fin;
        if (finals == null) {
            fin = new MD5State(state);
            int[] count_ints = { (int) (fin.count << 3),(int) (fin.count >> 29) };
            bits = encode(count_ints, 8);
            index = (int) (fin.count & 0x3f);
            padlen = (index < 56) ? (56 - index) : (120 - index);
            update(fin, padding, 0, padlen);
            update(fin, bits, 0, 8);
            /* Update() sets finals to null */
            finals = fin;
        }
        
        return encode(finals.state, 16);
    }
}