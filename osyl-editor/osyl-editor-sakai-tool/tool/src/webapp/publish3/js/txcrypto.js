/*
 * Copyright (c) 2000-2004 Teximus Technologies, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Teximus Technologies, Inc. (Teximus).  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Teximus.
 *
 * Teximus MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. Teximus SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

/**
 * HexaEncodes and crypt a string using a RSA key pair.  
 * @param modulus 			modulus of the RSA public key  
 * @param publicExponent 	public exponent of the RSA public key
 * @param message 			message to hexaEncode and then crypt
 * @return hexaEncoded message that has been crypted   
 */
function hexaEncodesAndCrypt(modulus, publicExponent, message) {
    var n = str2bigInt(modulus, 16, 0);
    var y = str2bigInt(publicExponent, 16, 0);
    var hexaEncodedMessage = hexaEncode(message);
    var x = str2bigInt(hexaEncodedMessage, 16, n.length);
    powMod(x,y,n);
    return bigInt2str(x,16);
}

/**
 * Encodes a string in hexadecimal. 
 * Each character of the String translates to its character code (ISO88591) as a 2 character hexadecimal string.  
 * @param  s the string to encode
 * @return the encoded string
 */
function hexaEncode(s) {
    var result = "";
    var sl = s.length;
    var i = 0;
    while (i < sl) {
        result += digitToHex(s.charCodeAt(i));
        i++;
    }
    return result;
}

/**
 * Decodes a string encoded using hexaEncode(). 
 * @param  toDecode the string to encode
 * @return the decoded string
 */
function hexaDecode(s) {
    var result = "";
    var byte;
    var sl = Math.min(s.length);
    for (var i = 0; i < sl; i++) {
        byte = s.charAt(i);
        byte += s.charAt(++i);
        result += String.fromCharCode(hexToDigit(byte));
        result += "-";
    }
    return result;
}

/**
 * Transforms a digit to a hexadecimal string.
 */
function digitToHex(n) {
    var hexToChar = new Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                              'A', 'B', 'C', 'D', 'E', 'F');

    var mask = 0xf;
    var result = "";

    for (i = 0; i < 2; ++i) {
        result += hexToChar[n & mask];
        n >>>= 4;
    }
    return reverseStr(result);
}

/**
 * Transforms a hexadecimal string to a digit.
 */
function hexToDigit(s) {
    var result = 0;
    var sl = Math.min(s.length, 2);
    for (var i = 0; i < sl; ++i) {
        result <<= 4;
        result |= charToHex(s.charCodeAt(i))
    }
    return result;
}

/**
 * Transforms a char to a hexadecimal String.
 */
function charToHex(c) {
    var ZERO = 48;
    var NINE = ZERO + 9;
    var littleA = 97;
    var littleZ = littleA + 25;
    var bigA = 65;
    var bigZ = 65 + 25;
    var result;

    if (c >= ZERO && c <= NINE) {
        result = c - ZERO;
    } else if (c >= bigA && c <= bigZ) {
        result = 10 + c - bigA;
    } else if (c >= littleA && c <= littleZ) {
        result = 10 + c - littleA;
    } else {
        result = 0;
    }
    return result;
}


/**
 * Returns the reverse of a String.
 */
function reverseStr(s) {
    var result = "";
    for (var i = s.length - 1; i > -1; --i) {
        result += s.charAt(i);
    }
    return result;
}