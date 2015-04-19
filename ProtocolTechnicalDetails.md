#protocol technical details

## Technical information ##
All that follow below is taken from java.io.DataInputStream and java,io.DataOutputStream.
### How works writeInt ###
Writes an int value, which is comprised of four bytes, to the output stream. The byte values to be written, in the order shown, are:
  * (byte)(0xff & (v >> 24))
  * (byte)(0xff & (v >> 16))
  * (byte)(0xff & (v >> 8))
  * (byte)(0xff & v)
### How works writeUTF ###
Each character in the string s is converted to a group of one, two, or three bytes, depending on the value of the character.

If a character c is in the range \u0001 through \u007f, it is represented by one byte:
  * (byte)c
If a character c is \u0000 or is in the range \u0080 through \u07ff, then it is represented by two bytes, to be written in the order shown:
    * (byte)(0xc0 | (0x1f & (c >> 6)))
    * (byte)(0x80 | (0x3f & c))

If a character c is in the range \u0800 through uffff, then it is represented by three bytes, to be written in the order shown:

  * (byte)(0xe0 | (0x0f & (c >> 12)))
  * (byte)(0x80 | (0x3f & (c >>  6)))
  * (byte)(0x80 | (0x3f & c))

**First, the total number of bytes needed to represent all the characters of s is calculated.** Note that it can`t be more than 65535. This length is written to the output stream as:
  * (byte)(0xff & (v >> 8))
  * (byte)(0xff & v)
And after this the string is writed as described above...