from struct import pack
from binascii import hexlify

# Convert message to 32 bits words 
def make_words(byte_array):
    res = []
    for i in range(0, len(byte_array), 4):
        index = i/4
        res.append(byte_array[i+3])
        res[index] = (res[index] << 8) | byte_array[i+2]
        res[index] = (res[index] << 8) | byte_array[i+1]
        res[index] = (res[index] << 8) | byte_array[i]
    return res

def md4(message, mode_test=False):
    # Get the lentgh of the original message 
    original_length = len(message)
    message = [ord(c) for c in message]

    # add a '1' bit via a byte
    message += [0x80]
    
    mod_length = len(message) % 64

    # padding to 448 % 512 bits (56 % 64 byte)
    if mod_length < 56:
        message += [0x00] * (56 - mod_length)
    else:
        message += [0x00] * (120 - mod_length)

    # add the length as a 64 bit big endian, use lower order bits if length overflows 2^64
    length = [ord(c) for c in pack('>Q', (original_length * 8) & 0xFFFFFFFFFFFFFFFF)]

    # add the two words least significant first
    message.extend(length[::-1])

    if mode_test:
        #print("\nafter padding {}".format(hex(b) for b in message))
        print("\nMessage after padding ({1} bits) : \n  \"{0}\"".format(''.join(format(b, '02x') for b in message), len(message)*8))

        #print(message)
    # Initialization Vector(IV) {Registers} 
    A = 0x67452301
    B = 0xefcdab89
    C = 0x98badcfe
    D = 0x10325476

    def F(x, y, z): 
        return ((x & y) | ((~x) & z))
    def G(x, y, z): 
        return (x & y) | (x & z) | (y & z)
    def H(x, y, z): 
        return x ^ y ^ z

    # round functions
    # Selection Function 
    def selection_func(a, b, c, d, k, s): 
        return left_rotate((a + F(b, c, d) + X[k]) & 0xFFFFFFFF, s)
    # Majority Function 
    def majority_func(a, b, c, d, k, s): 
        return left_rotate((a + G(b, c, d) + X[k] + 0x5A827999) & 0xFFFFFFFF, s)
    # Final Function
    def final_func(a, b, c, d, k, s): 
        return left_rotate((a + H(b, c, d) + X[k] + 0x6ED9EBA1) & 0xFFFFFFFF, s)

    # define a 32-bit left-rotate function (<<< in the RFC)
    def left_rotate(x, n): 
        return ((x << n) & 0xFFFFFFFF) | (x >> (32-n))

    # turn the padded message into a list of 32-bit words
    M = make_words(message)

    # process each 16 word (64 byte) block
    for i in range(0, len(M), 16):

        X = M[i:i+16]

        # current value of registers 
        AA = A
        BB = B
        CC = C
        DD = D

        if mode_test:
            print("\nINITIAL REGISTERS \tA: {} | B: {} | C: {} | D: {} ".format(hex(A), hex(B), hex(C), hex(D)))

        # ROUND 1
        # perform the 16 operations
        A = selection_func(A, B, C, D, 0, 3)
        D = selection_func(D, A, B, C, 1, 7)
        C = selection_func(C, D, A, B, 2, 11)
        B = selection_func(B, C, D, A, 3, 19)

        A = selection_func(A, B, C, D, 4, 3)
        D = selection_func(D, A, B, C, 5, 7)
        C = selection_func(C, D, A, B, 6, 11)
        B = selection_func(B, C, D, A, 7, 19)

        A = selection_func(A, B, C, D, 8, 3)
        D = selection_func(D, A, B, C, 9, 7)
        C = selection_func(C, D, A, B, 10, 11)
        B = selection_func(B, C, D, A, 11, 19)

        A = selection_func(A, B, C, D, 12, 3)
        D = selection_func(D, A, B, C, 13, 7)
        C = selection_func(C, D, A, B, 14, 11)
        B = selection_func(B, C, D, A, 15, 19)

        if mode_test:
            print("ROUND 1 \t\tA: {} | B: {} | C: {} | D: {} ".format(hex(A), hex(B), hex(C), hex(D)))

        # ROUND 2

        # perform the 16 operations
        A = majority_func(A, B, C, D, 0, 3)
        D = majority_func(D, A, B, C, 4, 5)
        C = majority_func(C, D, A, B, 8, 9)
        B = majority_func(B, C, D, A, 12, 13)

        A = majority_func(A, B, C, D, 1, 3)
        D = majority_func(D, A, B, C, 5, 5)
        C = majority_func(C, D, A, B, 9, 9)
        B = majority_func(B, C, D, A, 13, 13)

        A = majority_func(A, B, C, D, 2, 3)
        D = majority_func(D, A, B, C, 6, 5)
        C = majority_func(C, D, A, B, 10, 9)
        B = majority_func(B, C, D, A, 14, 13)

        A = majority_func(A, B, C, D, 3, 3)
        D = majority_func(D, A, B, C, 7, 5)
        C = majority_func(C, D, A, B, 11, 9)
        B = majority_func(B, C, D, A, 15, 13)

        if mode_test:
            print("ROUND 2 \t\tA: {} | B: {} | C: {} | D: {} ".format(hex(A), hex(B), hex(C), hex(D)))

        # ROUND 3

        A = final_func(A, B, C, D, 0, 3)
        D = final_func(D, A, B, C, 8, 9)
        C = final_func(C, D, A, B, 4, 11)
        B = final_func(B, C, D, A, 12, 15)

        A = final_func(A, B, C, D, 2, 3)
        D = final_func(D, A, B, C, 10, 9)
        C = final_func(C, D, A, B, 6, 11)
        B = final_func(B, C, D, A, 14, 15)

        A = final_func(A, B, C, D, 1, 3)
        D = final_func(D, A, B, C, 9, 9)
        C = final_func(C, D, A, B, 5, 11)
        B = final_func(B, C, D, A, 13, 15)

        A = final_func(A, B, C, D, 3, 3)
        D = final_func(D, A, B, C, 11, 9)
        C = final_func(C, D, A, B, 7, 11)
        B = final_func(B, C, D, A, 15, 15)

        if mode_test:
            print("ROUND 3 \t\tA: {} | B: {} | C: {} | D: {} ".format(hex(A), hex(B), hex(C), hex(D)))

        # increment by previous values
        A = ((A + AA) & 0xFFFFFFFF)
        B = ((B + BB) & 0xFFFFFFFF)
        C = ((C + CC) & 0xFFFFFFFF)
        D = ((D + DD) & 0xFFFFFFFF)

        if mode_test:
            print("FINAL VALUE \t\tA: {} | B: {} | C: {} | D: {} ".format(hex(A), hex(B), hex(C), hex(D)))

    # convert endian-ness for output
    A = hexlify(pack('<L', A))
    B = hexlify(pack('<L', B))
    C = hexlify(pack('<L', C))
    D = hexlify(pack('<L', D))

    return A + B + C + D
